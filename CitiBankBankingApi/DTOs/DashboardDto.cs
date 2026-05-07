namespace CitiBankBankingApi.DTOs;

public record DashboardDto(
    int TotalCustomers,
    int TotalAccounts,
    decimal TotalDeposits,
    decimal TotalLoansOutstanding,
    int TotalCreditCards,
    int ActiveBranches,
    IEnumerable<CustomerTierBreakdownDto> CustomersByTier,
    IEnumerable<AccountTypeBreakdownDto> AccountsByType,
    IEnumerable<TransactionDto> RecentTransactions);

public record CustomerTierBreakdownDto(string Tier, int Count);

public record AccountTypeBreakdownDto(string Type, int Count, decimal TotalBalance);
