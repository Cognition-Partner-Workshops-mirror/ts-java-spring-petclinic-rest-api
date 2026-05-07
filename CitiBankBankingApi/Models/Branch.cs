namespace CitiBankBankingApi.Models;

public class Branch
{
    public int Id { get; set; }
    public string BranchCode { get; set; } = string.Empty;
    public string Name { get; set; } = string.Empty;
    public string Address { get; set; } = string.Empty;
    public string City { get; set; } = string.Empty;
    public string State { get; set; } = string.Empty;
    public string ZipCode { get; set; } = string.Empty;
    public string Phone { get; set; } = string.Empty;
    public string ManagerName { get; set; } = string.Empty;
    public bool IsOpen { get; set; } = true;
    public string Hours { get; set; } = "Mon-Fri 9:00 AM - 5:00 PM, Sat 9:00 AM - 1:00 PM";
    public bool HasATM { get; set; } = true;
    public bool HasSafeDepositBoxes { get; set; }
    public bool HasDriveThrough { get; set; }
    public double Latitude { get; set; }
    public double Longitude { get; set; }
}
