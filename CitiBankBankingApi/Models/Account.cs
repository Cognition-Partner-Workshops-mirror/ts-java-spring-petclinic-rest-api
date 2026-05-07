namespace CitiBankBankingApi.Models;

public class Account
{
    public int Id { get; set; }
    public string AccountNumber { get; set; } = string.Empty;
    public AccountType Type { get; set; }
    public AccountStatus Status { get; set; } = AccountStatus.Active;
    public decimal Balance { get; set; }
    public decimal AvailableBalance { get; set; }
    public string Currency { get; set; } = "USD";
    public decimal InterestRate { get; set; }
    public DateTime OpenedDate { get; set; } = DateTime.UtcNow;
    public DateTime? ClosedDate { get; set; }
    public string RoutingNumber { get; set; } = "021000089";
    public string BranchCode { get; set; } = string.Empty;

    public int CustomerId { get; set; }
    public Customer Customer { get; set; } = null!;

    public ICollection<Transaction> Transactions { get; set; } = new List<Transaction>();
}

public enum AccountType
{
    Checking,
    Savings,
    MoneyMarket,
    CertificateOfDeposit,
    BusinessChecking,
    BusinessSavings
}

public enum AccountStatus
{
    Active,
    Inactive,
    Frozen,
    Closed
}
