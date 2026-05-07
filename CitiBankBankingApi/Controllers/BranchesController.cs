using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CitiBankBankingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class BranchesController : ControllerBase
{
    private readonly CitiBankDbContext _db;

    public BranchesController(CitiBankDbContext db) => _db = db;

    [HttpGet]
    public async Task<ActionResult<IEnumerable<BranchDto>>> GetAll()
    {
        var branches = await _db.Branches
            .Select(b => new BranchDto(
                b.Id, b.BranchCode, b.Name, b.Address, b.City, b.State,
                b.ZipCode, b.Phone, b.ManagerName, b.IsOpen, b.Hours,
                b.HasATM, b.HasSafeDepositBoxes, b.HasDriveThrough,
                b.Latitude, b.Longitude))
            .ToListAsync();
        return Ok(branches);
    }

    [HttpGet("{id:int}")]
    public async Task<ActionResult<BranchDto>> GetById(int id)
    {
        var b = await _db.Branches.FindAsync(id);
        if (b is null) return NotFound();

        return Ok(new BranchDto(
            b.Id, b.BranchCode, b.Name, b.Address, b.City, b.State,
            b.ZipCode, b.Phone, b.ManagerName, b.IsOpen, b.Hours,
            b.HasATM, b.HasSafeDepositBoxes, b.HasDriveThrough,
            b.Latitude, b.Longitude));
    }

    [HttpGet("search")]
    public async Task<ActionResult<IEnumerable<BranchDto>>> Search([FromQuery] string? city, [FromQuery] string? state)
    {
        var query = _db.Branches.AsQueryable();
        if (!string.IsNullOrEmpty(city)) query = query.Where(b => b.City.Contains(city));
        if (!string.IsNullOrEmpty(state)) query = query.Where(b => b.State == state);

        var branches = await query
            .Select(b => new BranchDto(
                b.Id, b.BranchCode, b.Name, b.Address, b.City, b.State,
                b.ZipCode, b.Phone, b.ManagerName, b.IsOpen, b.Hours,
                b.HasATM, b.HasSafeDepositBoxes, b.HasDriveThrough,
                b.Latitude, b.Longitude))
            .ToListAsync();
        return Ok(branches);
    }
}
