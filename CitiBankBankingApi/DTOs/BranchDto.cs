namespace CitiBankBankingApi.DTOs;

public record BranchDto(
    int Id,
    string BranchCode,
    string Name,
    string Address,
    string City,
    string State,
    string ZipCode,
    string Phone,
    string ManagerName,
    bool IsOpen,
    string Hours,
    bool HasATM,
    bool HasSafeDepositBoxes,
    bool HasDriveThrough,
    double Latitude,
    double Longitude);
