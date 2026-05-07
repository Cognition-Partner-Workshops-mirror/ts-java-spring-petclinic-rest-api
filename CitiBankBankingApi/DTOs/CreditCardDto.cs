namespace CitiBankBankingApi.DTOs;

public record CreditCardSummaryDto(
    int Id,
    string MaskedNumber,
    string Type,
    string Status,
    decimal CreditLimit,
    decimal CurrentBalance,
    decimal AvailableCredit);

public record CreditCardDetailDto(
    int Id,
    string MaskedNumber,
    string Type,
    string Status,
    decimal CreditLimit,
    decimal CurrentBalance,
    decimal AvailableCredit,
    decimal AnnualPercentageRate,
    decimal MinimumPaymentDue,
    DateTime? PaymentDueDate,
    DateTime ExpirationDate,
    decimal RewardsPoints,
    decimal CashBackEarned,
    int CustomerId,
    string CustomerName);

public record CreateCreditCardDto(
    int CustomerId,
    string Type);
