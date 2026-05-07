using CitiBankBankingApi.Data;
using CitiBankBankingApi.DTOs;
using CitiBankBankingApi.Models;
using Microsoft.EntityFrameworkCore;

namespace CitiBankBankingApi.Services;

public class TransactionService
{
    private readonly CitiBankDbContext _db;

    public TransactionService(CitiBankDbContext db) => _db = db;

    public async Task<IEnumerable<TransactionDto>> GetByAccountIdAsync(int accountId)
    {
        return await _db.Transactions
            .Where(t => t.AccountId == accountId)
            .OrderByDescending(t => t.TransactionDate)
            .Select(t => new TransactionDto(
                t.Id, t.TransactionId, t.Type.ToString(), t.Status.ToString(),
                t.Amount, t.BalanceAfter, t.Currency, t.Description,
                t.MerchantName, t.MerchantCategory, t.TransactionDate, t.PostedDate))
            .ToListAsync();
    }

    public async Task<IEnumerable<TransactionDto>> GetRecentAsync(int count = 20)
    {
        return await _db.Transactions
            .OrderByDescending(t => t.TransactionDate)
            .Take(count)
            .Select(t => new TransactionDto(
                t.Id, t.TransactionId, t.Type.ToString(), t.Status.ToString(),
                t.Amount, t.BalanceAfter, t.Currency, t.Description,
                t.MerchantName, t.MerchantCategory, t.TransactionDate, t.PostedDate))
            .ToListAsync();
    }

    public async Task<TransactionDto?> CreateAsync(CreateTransactionDto dto)
    {
        var account = await _db.Accounts.FindAsync(dto.AccountId);
        if (account is null) return null;

        if (!Enum.TryParse<TransactionType>(dto.Type, true, out var txnType))
            return null;

        var amount = dto.Amount;
        if (txnType is TransactionType.Withdrawal or TransactionType.Payment
            or TransactionType.ACHDebit or TransactionType.Fee or TransactionType.ATMWithdrawal)
        {
            amount = -Math.Abs(amount);
        }

        account.Balance += amount;
        account.AvailableBalance += amount;

        var transaction = new Transaction
        {
            Type = txnType,
            Amount = amount,
            BalanceAfter = account.Balance,
            Description = dto.Description,
            MerchantName = dto.MerchantName,
            MerchantCategory = dto.MerchantCategory,
            AccountId = dto.AccountId
        };

        _db.Transactions.Add(transaction);
        await _db.SaveChangesAsync();

        return new TransactionDto(
            transaction.Id, transaction.TransactionId, transaction.Type.ToString(),
            transaction.Status.ToString(), transaction.Amount, transaction.BalanceAfter,
            transaction.Currency, transaction.Description, transaction.MerchantName,
            transaction.MerchantCategory, transaction.TransactionDate, transaction.PostedDate);
    }

    public async Task<(TransactionDto? Debit, TransactionDto? Credit, string? Error)> TransferAsync(TransferDto dto)
    {
        var fromAccount = await _db.Accounts.FindAsync(dto.FromAccountId);
        var toAccount = await _db.Accounts.FindAsync(dto.ToAccountId);

        if (fromAccount is null || toAccount is null)
            return (null, null, "One or both accounts not found");

        if (fromAccount.AvailableBalance < dto.Amount)
            return (null, null, "Insufficient funds");

        fromAccount.Balance -= dto.Amount;
        fromAccount.AvailableBalance -= dto.Amount;
        toAccount.Balance += dto.Amount;
        toAccount.AvailableBalance += dto.Amount;

        var debit = new Transaction
        {
            Type = TransactionType.Transfer,
            Amount = -dto.Amount,
            BalanceAfter = fromAccount.Balance,
            Description = $"{dto.Description} to {toAccount.AccountNumber}",
            AccountId = dto.FromAccountId
        };

        var credit = new Transaction
        {
            Type = TransactionType.Transfer,
            Amount = dto.Amount,
            BalanceAfter = toAccount.Balance,
            Description = $"{dto.Description} from {fromAccount.AccountNumber}",
            AccountId = dto.ToAccountId
        };

        _db.Transactions.AddRange(debit, credit);
        await _db.SaveChangesAsync();

        return (
            new TransactionDto(debit.Id, debit.TransactionId, debit.Type.ToString(),
                debit.Status.ToString(), debit.Amount, debit.BalanceAfter, debit.Currency,
                debit.Description, null, null, debit.TransactionDate, debit.PostedDate),
            new TransactionDto(credit.Id, credit.TransactionId, credit.Type.ToString(),
                credit.Status.ToString(), credit.Amount, credit.BalanceAfter, credit.Currency,
                credit.Description, null, null, credit.TransactionDate, credit.PostedDate),
            null);
    }
}
