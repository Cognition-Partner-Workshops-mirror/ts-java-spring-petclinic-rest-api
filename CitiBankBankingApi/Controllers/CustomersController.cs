using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Services;
using Microsoft.AspNetCore.Mvc;

namespace CitiBankBankingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class CustomersController : ControllerBase
{
    private readonly CustomerService _service;

    public CustomersController(CustomerService service) => _service = service;

    [HttpGet]
    public async Task<ActionResult<IEnumerable<CustomerListDto>>> GetAll()
        => Ok(await _service.GetAllAsync());

    [HttpGet("{id:int}")]
    public async Task<ActionResult<CustomerDetailDto>> GetById(int id)
    {
        var customer = await _service.GetByIdAsync(id);
        return customer is null ? NotFound() : Ok(customer);
    }

    [HttpPost]
    public async Task<ActionResult<CustomerDetailDto>> Create(CreateCustomerDto dto)
    {
        var customer = await _service.CreateAsync(dto);
        return CreatedAtAction(nameof(GetById), new { id = customer.Id }, customer);
    }
}
