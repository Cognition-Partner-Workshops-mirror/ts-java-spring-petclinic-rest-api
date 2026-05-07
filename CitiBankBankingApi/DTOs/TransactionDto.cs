namespace CitiBankBankingApi.DTOs;

public record TransactionDto(
    int Id,
    string TransactionId,
    string Type,
    string Status,
    decimal Amount,
    decimal BalanceAfter,
    string Currency,
    string Description,
    string? MerchantName,
    string? MerchantCategory,
    DateTime TransactionDate,
    DateTime PostedDate);

public record CreateTransactionDto(
    int AccountId,
    string Type,
    decimal Amount,
    string Description,
    string? MerchantName = null,
    string? MerchantCategory = null);

public record TransferDto(
    int FromAccountId,
    int ToAccountId,
    decimal Amount,
    string Description = "Transfer");
