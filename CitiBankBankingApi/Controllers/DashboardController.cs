using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Services;
using Microsoft.AspNetCore.Mvc;

namespace CitiBankBankingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class DashboardController : ControllerBase
{
    private readonly DashboardService _service;

    public DashboardController(DashboardService service) => _service = service;

    [HttpGet]
    public async Task<ActionResult<DashboardDto>> GetDashboard()
        => Ok(await _service.GetDashboardAsync());
}
