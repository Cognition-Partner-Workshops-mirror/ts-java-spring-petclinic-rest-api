using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CitiBankBankingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class CreditCardsController : ControllerBase
{
    private readonly CitiBankDbContext _db;

    public CreditCardsController(CitiBankDbContext db) => _db = db;

    [HttpGet]
    public async Task<ActionResult<IEnumerable<CreditCardSummaryDto>>> GetAll()
    {
        var cards = await _db.CreditCards
            .Select(cc => new CreditCardSummaryDto(
                cc.Id, cc.MaskedNumber, cc.Type.ToString(), cc.Status.ToString(),
                cc.CreditLimit, cc.CurrentBalance, cc.AvailableCredit))
            .ToListAsync();
        return Ok(cards);
    }

    [HttpGet("{id:int}")]
    public async Task<ActionResult<CreditCardDetailDto>> GetById(int id)
    {
        var cc = await _db.CreditCards
            .Include(c => c.Customer)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (cc is null) return NotFound();

        return Ok(new CreditCardDetailDto(
            cc.Id, cc.MaskedNumber, cc.Type.ToString(), cc.Status.ToString(),
            cc.CreditLimit, cc.CurrentBalance, cc.AvailableCredit,
            cc.AnnualPercentageRate, cc.MinimumPaymentDue, cc.PaymentDueDate,
            cc.ExpirationDate, cc.RewardsPoints, cc.CashBackEarned,
            cc.CustomerId, $"{cc.Customer.FirstName} {cc.Customer.LastName}"));
    }

    [HttpGet("customer/{customerId:int}")]
    public async Task<ActionResult<IEnumerable<CreditCardSummaryDto>>> GetByCustomerId(int customerId)
    {
        var cards = await _db.CreditCards
            .Where(cc => cc.CustomerId == customerId)
            .Select(cc => new CreditCardSummaryDto(
                cc.Id, cc.MaskedNumber, cc.Type.ToString(), cc.Status.ToString(),
                cc.CreditLimit, cc.CurrentBalance, cc.AvailableCredit))
            .ToListAsync();
        return Ok(cards);
    }
}
