using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Services;
using Microsoft.AspNetCore.Mvc;

namespace CitiBankBankingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AccountsController : ControllerBase
{
    private readonly AccountService _service;

    public AccountsController(AccountService service) => _service = service;

    [HttpGet]
    public async Task<ActionResult<IEnumerable<AccountSummaryDto>>> GetAll()
        => Ok(await _service.GetAllAsync());

    [HttpGet("{id:int}")]
    public async Task<ActionResult<AccountDetailDto>> GetById(int id)
    {
        var account = await _service.GetByIdAsync(id);
        return account is null ? NotFound() : Ok(account);
    }

    [HttpGet("customer/{customerId:int}")]
    public async Task<ActionResult<IEnumerable<AccountSummaryDto>>> GetByCustomerId(int customerId)
        => Ok(await _service.GetByCustomerIdAsync(customerId));

    [HttpPost]
    public async Task<ActionResult<AccountDetailDto>> Create(CreateAccountDto dto)
    {
        var account = await _service.CreateAsync(dto);
        return account is null
            ? BadRequest(new { error = "Invalid customer ID or account type" })
            : CreatedAtAction(nameof(GetById), new { id = account.Id }, account);
    }
}
