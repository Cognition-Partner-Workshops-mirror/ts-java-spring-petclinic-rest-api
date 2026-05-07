namespace CitiBankBankingApi.Models;

public class Customer
{
    public int Id { get; set; }
    public string FirstName { get; set; } = string.Empty;
    public string LastName { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Phone { get; set; } = string.Empty;
    public string Address { get; set; } = string.Empty;
    public string City { get; set; } = string.Empty;
    public string State { get; set; } = string.Empty;
    public string ZipCode { get; set; } = string.Empty;
    public string SSNLastFour { get; set; } = string.Empty;
    public DateTime DateOfBirth { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public CustomerTier Tier { get; set; } = CustomerTier.Basic;

    public ICollection<Account> Accounts { get; set; } = new List<Account>();
    public ICollection<CreditCard> CreditCards { get; set; } = new List<CreditCard>();
}

public enum CustomerTier
{
    Basic,
    Gold,
    Platinum,
    CitiPriority,
    Citigold,
    CitigoldPrivateClient
}
