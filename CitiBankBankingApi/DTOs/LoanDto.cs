namespace CitiBankBankingApi.DTOs;

public record LoanSummaryDto(
    int Id,
    string LoanNumber,
    string Type,
    string Status,
    decimal OutstandingBalance,
    decimal MonthlyPayment,
    DateTime? NextPaymentDate);

public record LoanDetailDto(
    int Id,
    string LoanNumber,
    string Type,
    string Status,
    decimal PrincipalAmount,
    decimal OutstandingBalance,
    decimal InterestRate,
    decimal MonthlyPayment,
    int TermMonths,
    DateTime OriginationDate,
    DateTime MaturityDate,
    DateTime? NextPaymentDate,
    int CustomerId,
    string CustomerName);
