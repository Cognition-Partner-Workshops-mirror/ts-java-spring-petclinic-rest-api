using CitiBankBankingApi.Data;
using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Models;
using Microsoft.EntityFrameworkCore;

namespace CitiBankBankingApi.Services;

public class AccountService
{
    private readonly CitiBankDbContext _db;

    public AccountService(CitiBankDbContext db) => _db = db;

    public async Task<IEnumerable<AccountSummaryDto>> GetAllAsync()
    {
        return await _db.Accounts
            .Select(a => new AccountSummaryDto(
                a.Id, a.AccountNumber, a.Type.ToString(), a.Status.ToString(),
                a.Balance, a.AvailableBalance, a.Currency))
            .ToListAsync();
    }

    public async Task<AccountDetailDto?> GetByIdAsync(int id)
    {
        var a = await _db.Accounts
            .Include(a => a.Customer)
            .Include(a => a.Transactions.OrderByDescending(t => t.TransactionDate).Take(10))
            .FirstOrDefaultAsync(a => a.Id == id);

        if (a is null) return null;

        return new AccountDetailDto(
            a.Id, a.AccountNumber, a.Type.ToString(), a.Status.ToString(),
            a.Balance, a.AvailableBalance, a.Currency, a.InterestRate,
            a.RoutingNumber, a.BranchCode, a.OpenedDate, a.CustomerId,
            $"{a.Customer.FirstName} {a.Customer.LastName}",
            a.Transactions.Select(t => new TransactionDto(
                t.Id, t.TransactionId, t.Type.ToString(), t.Status.ToString(),
                t.Amount, t.BalanceAfter, t.Currency, t.Description,
                t.MerchantName, t.MerchantCategory, t.TransactionDate, t.PostedDate)));
    }

    public async Task<IEnumerable<AccountSummaryDto>> GetByCustomerIdAsync(int customerId)
    {
        return await _db.Accounts
            .Where(a => a.CustomerId == customerId)
            .Select(a => new AccountSummaryDto(
                a.Id, a.AccountNumber, a.Type.ToString(), a.Status.ToString(),
                a.Balance, a.AvailableBalance, a.Currency))
            .ToListAsync();
    }

    public async Task<AccountDetailDto?> CreateAsync(CreateAccountDto dto)
    {
        var customer = await _db.Customers.FindAsync(dto.CustomerId);
        if (customer is null) return null;

        if (!Enum.TryParse<AccountType>(dto.Type, true, out var accountType))
            return null;

        var account = new Account
        {
            AccountNumber = $"CITI-{dto.Type[..3].ToUpperInvariant()}-{Random.Shared.Next(100000, 999999)}",
            Type = accountType,
            Balance = dto.InitialDeposit,
            AvailableBalance = dto.InitialDeposit,
            Currency = dto.Currency,
            InterestRate = accountType switch
            {
                AccountType.Savings => 4.35m,
                AccountType.MoneyMarket => 4.75m,
                AccountType.CertificateOfDeposit => 5.00m,
                _ => 0.01m
            },
            CustomerId = dto.CustomerId
        };

        _db.Accounts.Add(account);
        await _db.SaveChangesAsync();

        return new AccountDetailDto(
            account.Id, account.AccountNumber, account.Type.ToString(),
            account.Status.ToString(), account.Balance, account.AvailableBalance,
            account.Currency, account.InterestRate, account.RoutingNumber,
            account.BranchCode, account.OpenedDate, account.CustomerId,
            $"{customer.FirstName} {customer.LastName}", []);
    }
}
