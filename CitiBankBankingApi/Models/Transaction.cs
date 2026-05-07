namespace CitiBankBankingApi.Models;

public class Transaction
{
    public int Id { get; set; }
    public string TransactionId { get; set; } = Guid.NewGuid().ToString("N")[..12].ToUpperInvariant();
    public TransactionType Type { get; set; }
    public TransactionStatus Status { get; set; } = TransactionStatus.Completed;
    public decimal Amount { get; set; }
    public decimal BalanceAfter { get; set; }
    public string Currency { get; set; } = "USD";
    public string Description { get; set; } = string.Empty;
    public string? ReferenceNumber { get; set; }
    public string? MerchantName { get; set; }
    public string? MerchantCategory { get; set; }
    public DateTime TransactionDate { get; set; } = DateTime.UtcNow;
    public DateTime PostedDate { get; set; } = DateTime.UtcNow;

    public int AccountId { get; set; }
    public Account Account { get; set; } = null!;
}

public enum TransactionType
{
    Deposit,
    Withdrawal,
    Transfer,
    Payment,
    DirectDeposit,
    ACHDebit,
    ACHCredit,
    WireTransfer,
    Fee,
    Interest,
    ATMWithdrawal,
    PointOfSale
}

public enum TransactionStatus
{
    Pending,
    Completed,
    Failed,
    Reversed,
    OnHold
}
