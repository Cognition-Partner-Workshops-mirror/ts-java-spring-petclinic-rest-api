namespace CitiBankBankingApi.Models;

public class Loan
{
    public int Id { get; set; }
    public string LoanNumber { get; set; } = string.Empty;
    public LoanType Type { get; set; }
    public LoanStatus Status { get; set; } = LoanStatus.Active;
    public decimal PrincipalAmount { get; set; }
    public decimal OutstandingBalance { get; set; }
    public decimal InterestRate { get; set; }
    public decimal MonthlyPayment { get; set; }
    public int TermMonths { get; set; }
    public DateTime OriginationDate { get; set; }
    public DateTime MaturityDate { get; set; }
    public DateTime? NextPaymentDate { get; set; }

    public int CustomerId { get; set; }
    public Customer Customer { get; set; } = null!;
}

public enum LoanType
{
    Personal,
    Mortgage,
    HomeEquity,
    Auto,
    StudentLoan,
    SmallBusiness
}

public enum LoanStatus
{
    Active,
    PaidOff,
    Delinquent,
    InDefault,
    InForbearance
}
