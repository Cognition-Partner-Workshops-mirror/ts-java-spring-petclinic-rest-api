using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CitiBankBankingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class LoansController : ControllerBase
{
    private readonly CitiBankDbContext _db;

    public LoansController(CitiBankDbContext db) => _db = db;

    [HttpGet]
    public async Task<ActionResult<IEnumerable<LoanSummaryDto>>> GetAll()
    {
        var loans = await _db.Loans
            .Select(l => new LoanSummaryDto(
                l.Id, l.LoanNumber, l.Type.ToString(), l.Status.ToString(),
                l.OutstandingBalance, l.MonthlyPayment, l.NextPaymentDate))
            .ToListAsync();
        return Ok(loans);
    }

    [HttpGet("{id:int}")]
    public async Task<ActionResult<LoanDetailDto>> GetById(int id)
    {
        var l = await _db.Loans
            .Include(l => l.Customer)
            .FirstOrDefaultAsync(l => l.Id == id);

        if (l is null) return NotFound();

        return Ok(new LoanDetailDto(
            l.Id, l.LoanNumber, l.Type.ToString(), l.Status.ToString(),
            l.PrincipalAmount, l.OutstandingBalance, l.InterestRate,
            l.MonthlyPayment, l.TermMonths, l.OriginationDate,
            l.MaturityDate, l.NextPaymentDate, l.CustomerId,
            $"{l.Customer.FirstName} {l.Customer.LastName}"));
    }

    [HttpGet("customer/{customerId:int}")]
    public async Task<ActionResult<IEnumerable<LoanSummaryDto>>> GetByCustomerId(int customerId)
    {
        var loans = await _db.Loans
            .Where(l => l.CustomerId == customerId)
            .Select(l => new LoanSummaryDto(
                l.Id, l.LoanNumber, l.Type.ToString(), l.Status.ToString(),
                l.OutstandingBalance, l.MonthlyPayment, l.NextPaymentDate))
            .ToListAsync();
        return Ok(loans);
    }
}
