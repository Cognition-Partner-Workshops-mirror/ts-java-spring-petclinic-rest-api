using CitiBankBankingApi.Data;
using CitiBankBankingApi.DTOs;
using Microsoft.EntityFrameworkCore;

namespace CitiBankBankingApi.Services;

public class DashboardService
{
    private readonly CitiBankDbContext _db;

    public DashboardService(CitiBankDbContext db) => _db = db;

    public async Task<DashboardDto> GetDashboardAsync()
    {
        var totalCustomers = await _db.Customers.CountAsync();
        var totalAccounts = await _db.Accounts.CountAsync();
        var totalDeposits = await _db.Accounts.SumAsync(a => a.Balance);
        var totalLoans = await _db.Loans.SumAsync(l => l.OutstandingBalance);
        var totalCards = await _db.CreditCards.CountAsync();
        var activeBranches = await _db.Branches.CountAsync(b => b.IsOpen);

        var customersByTier = await _db.Customers
            .GroupBy(c => c.Tier)
            .Select(g => new CustomerTierBreakdownDto(g.Key.ToString(), g.Count()))
            .ToListAsync();

        var accountsByType = await _db.Accounts
            .GroupBy(a => a.Type)
            .Select(g => new AccountTypeBreakdownDto(g.Key.ToString(), g.Count(), g.Sum(a => a.Balance)))
            .ToListAsync();

        var recentTxns = await _db.Transactions
            .OrderByDescending(t => t.TransactionDate)
            .Take(10)
            .Select(t => new TransactionDto(
                t.Id, t.TransactionId, t.Type.ToString(), t.Status.ToString(),
                t.Amount, t.BalanceAfter, t.Currency, t.Description,
                t.MerchantName, t.MerchantCategory, t.TransactionDate, t.PostedDate))
            .ToListAsync();

        return new DashboardDto(
            totalCustomers, totalAccounts, totalDeposits, totalLoans,
            totalCards, activeBranches, customersByTier, accountsByType, recentTxns);
    }
}
