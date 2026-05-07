using CitiBankBankingApi.Data;
using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Models;
using Microsoft.EntityFrameworkCore;

namespace CitiBankBankingApi.Services;

public class CustomerService
{
    private readonly CitiBankDbContext _db;

    public CustomerService(CitiBankDbContext db) => _db = db;

    public async Task<IEnumerable<CustomerListDto>> GetAllAsync()
    {
        return await _db.Customers
            .Include(c => c.Accounts)
            .Include(c => c.CreditCards)
            .Select(c => new CustomerListDto(
                c.Id, c.FirstName, c.LastName, c.Email, c.Phone,
                c.Tier.ToString(), c.Accounts.Count, c.CreditCards.Count))
            .ToListAsync();
    }

    public async Task<CustomerDetailDto?> GetByIdAsync(int id)
    {
        var c = await _db.Customers
            .Include(c => c.Accounts)
            .Include(c => c.CreditCards)
            .FirstOrDefaultAsync(c => c.Id == id);

        if (c is null) return null;

        return new CustomerDetailDto(
            c.Id, c.FirstName, c.LastName, c.Email, c.Phone,
            c.Address, c.City, c.State, c.ZipCode, c.DateOfBirth,
            c.Tier.ToString(), c.CreatedAt,
            c.Accounts.Select(a => new AccountSummaryDto(
                a.Id, a.AccountNumber, a.Type.ToString(), a.Status.ToString(),
                a.Balance, a.AvailableBalance, a.Currency)),
            c.CreditCards.Select(cc => new CreditCardSummaryDto(
                cc.Id, cc.MaskedNumber, cc.Type.ToString(), cc.Status.ToString(),
                cc.CreditLimit, cc.CurrentBalance, cc.AvailableCredit)));
    }

    public async Task<CustomerDetailDto> CreateAsync(CreateCustomerDto dto)
    {
        var customer = new Customer
        {
            FirstName = dto.FirstName,
            LastName = dto.LastName,
            Email = dto.Email,
            Phone = dto.Phone,
            Address = dto.Address,
            City = dto.City,
            State = dto.State,
            ZipCode = dto.ZipCode,
            SSNLastFour = dto.SSNLastFour,
            DateOfBirth = dto.DateOfBirth
        };

        _db.Customers.Add(customer);
        await _db.SaveChangesAsync();

        return new CustomerDetailDto(
            customer.Id, customer.FirstName, customer.LastName, customer.Email,
            customer.Phone, customer.Address, customer.City, customer.State,
            customer.ZipCode, customer.DateOfBirth, customer.Tier.ToString(),
            customer.CreatedAt, [], []);
    }
}
