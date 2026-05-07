using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Services;
using Microsoft.AspNetCore.Mvc;

namespace CitiBankBankingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class TransactionsController : ControllerBase
{
    private readonly TransactionService _service;

    public TransactionsController(TransactionService service) => _service = service;

    [HttpGet("account/{accountId:int}")]
    public async Task<ActionResult<IEnumerable<TransactionDto>>> GetByAccountId(int accountId)
        => Ok(await _service.GetByAccountIdAsync(accountId));

    [HttpGet("recent")]
    public async Task<ActionResult<IEnumerable<TransactionDto>>> GetRecent([FromQuery] int count = 20)
        => Ok(await _service.GetRecentAsync(count));

    [HttpPost]
    public async Task<ActionResult<TransactionDto>> Create(CreateTransactionDto dto)
    {
        var txn = await _service.CreateAsync(dto);
        return txn is null
            ? BadRequest(new { error = "Invalid account ID or transaction type" })
            : Created($"/api/transactions/{txn.Id}", txn);
    }

    [HttpPost("transfer")]
    public async Task<ActionResult> Transfer(TransferDto dto)
    {
        var (debit, credit, error) = await _service.TransferAsync(dto);
        if (error is not null) return BadRequest(new { error });
        return Ok(new { debit, credit });
    }
}
