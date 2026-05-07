namespace CitiBankBankingApi.DTOs;

public record CustomerListDto(
    int Id,
    string FirstName,
    string LastName,
    string Email,
    string Phone,
    string Tier,
    int AccountCount,
    int CreditCardCount);

public record CustomerDetailDto(
    int Id,
    string FirstName,
    string LastName,
    string Email,
    string Phone,
    string Address,
    string City,
    string State,
    string ZipCode,
    DateTime DateOfBirth,
    string Tier,
    DateTime CreatedAt,
    IEnumerable<AccountSummaryDto> Accounts,
    IEnumerable<CreditCardSummaryDto> CreditCards);

public record CreateCustomerDto(
    string FirstName,
    string LastName,
    string Email,
    string Phone,
    string Address,
    string City,
    string State,
    string ZipCode,
    string SSNLastFour,
    DateTime DateOfBirth);
