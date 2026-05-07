namespace CitiBankBankingApi.Models;

public class CreditCard
{
    public int Id { get; set; }
    public string CardNumber { get; set; } = string.Empty;
    public string MaskedNumber { get; set; } = string.Empty;
    public CardType Type { get; set; }
    public CardStatus Status { get; set; } = CardStatus.Active;
    public decimal CreditLimit { get; set; }
    public decimal CurrentBalance { get; set; }
    public decimal AvailableCredit { get; set; }
    public decimal AnnualPercentageRate { get; set; }
    public decimal MinimumPaymentDue { get; set; }
    public DateTime? PaymentDueDate { get; set; }
    public DateTime IssuedDate { get; set; } = DateTime.UtcNow;
    public DateTime ExpirationDate { get; set; }
    public decimal RewardsPoints { get; set; }
    public decimal CashBackEarned { get; set; }

    public int CustomerId { get; set; }
    public Customer Customer { get; set; } = null!;
}

public enum CardType
{
    CitiDoubleCash,
    CitiCustomCash,
    CitiDiamondPreferred,
    CitiPremier,
    CitiRewards,
    CitiSimplicity,
    CitiSecured,
    CitiBusinessPlatinum,
    CostcoAnywhere
}

public enum CardStatus
{
    Active,
    Inactive,
    Lost,
    Stolen,
    Expired,
    Cancelled
}
