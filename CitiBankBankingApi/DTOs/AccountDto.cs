namespace CitiBankBankingApi.DTOs;

public record AccountSummaryDto(
    int Id,
    string AccountNumber,
    string Type,
    string Status,
    decimal Balance,
    decimal AvailableBalance,
    string Currency);

public record AccountDetailDto(
    int Id,
    string AccountNumber,
    string Type,
    string Status,
    decimal Balance,
    decimal AvailableBalance,
    string Currency,
    decimal InterestRate,
    string RoutingNumber,
    string BranchCode,
    DateTime OpenedDate,
    int CustomerId,
    string CustomerName,
    IEnumerable<TransactionDto> RecentTransactions);

public record CreateAccountDto(
    int CustomerId,
    string Type,
    decimal InitialDeposit,
    string Currency = "USD");
