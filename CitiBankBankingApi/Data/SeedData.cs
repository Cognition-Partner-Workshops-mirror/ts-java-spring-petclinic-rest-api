using CitiBankBankingApi.Models;

namespace CitiBankBankingApi.Data;

public static class SeedData
{
    public static void Initialize(CitiBankDbContext context)
    {
        if (context.Customers.Any()) return;

        var customers = new List<Customer>
        {
            new()
            {
                Id = 1, FirstName = "James", LastName = "Morgan", Email = "james.morgan@email.com",
                Phone = "212-555-0101", Address = "388 Greenwich St", City = "New York", State = "NY",
                ZipCode = "10013", SSNLastFour = "4521", DateOfBirth = new DateTime(1978, 3, 15),
                Tier = CustomerTier.Citigold
            },
            new()
            {
                Id = 2, FirstName = "Sarah", LastName = "Chen", Email = "sarah.chen@email.com",
                Phone = "212-555-0102", Address = "153 E 53rd St", City = "New York", State = "NY",
                ZipCode = "10022", SSNLastFour = "7893", DateOfBirth = new DateTime(1985, 7, 22),
                Tier = CustomerTier.CitigoldPrivateClient
            },
            new()
            {
                Id = 3, FirstName = "Michael", LastName = "Williams", Email = "michael.williams@email.com",
                Phone = "305-555-0103", Address = "1395 Brickell Ave", City = "Miami", State = "FL",
                ZipCode = "33131", SSNLastFour = "2156", DateOfBirth = new DateTime(1990, 11, 8),
                Tier = CustomerTier.CitiPriority
            },
            new()
            {
                Id = 4, FirstName = "Emily", LastName = "Rodriguez", Email = "emily.rodriguez@email.com",
                Phone = "415-555-0104", Address = "1 Sansome St", City = "San Francisco", State = "CA",
                ZipCode = "94104", SSNLastFour = "6734", DateOfBirth = new DateTime(1982, 1, 30),
                Tier = CustomerTier.Platinum
            },
            new()
            {
                Id = 5, FirstName = "Robert", LastName = "Patel", Email = "robert.patel@email.com",
                Phone = "312-555-0105", Address = "500 W Madison St", City = "Chicago", State = "IL",
                ZipCode = "60661", SSNLastFour = "9012", DateOfBirth = new DateTime(1995, 5, 12),
                Tier = CustomerTier.Gold
            },
            new()
            {
                Id = 6, FirstName = "Jennifer", LastName = "Kim", Email = "jennifer.kim@email.com",
                Phone = "213-555-0106", Address = "444 S Flower St", City = "Los Angeles", State = "CA",
                ZipCode = "90071", SSNLastFour = "3456", DateOfBirth = new DateTime(1988, 9, 25),
                Tier = CustomerTier.Basic
            },
            new()
            {
                Id = 7, FirstName = "David", LastName = "Thompson", Email = "david.thompson@email.com",
                Phone = "202-555-0107", Address = "1101 Pennsylvania Ave NW", City = "Washington", State = "DC",
                ZipCode = "20004", SSNLastFour = "8901", DateOfBirth = new DateTime(1975, 12, 3),
                Tier = CustomerTier.Citigold
            },
            new()
            {
                Id = 8, FirstName = "Lisa", LastName = "Nakamura", Email = "lisa.nakamura@email.com",
                Phone = "206-555-0108", Address = "1420 5th Ave", City = "Seattle", State = "WA",
                ZipCode = "98101", SSNLastFour = "5678", DateOfBirth = new DateTime(1992, 4, 18),
                Tier = CustomerTier.CitiPriority
            }
        };
        context.Customers.AddRange(customers);
        context.SaveChanges();

        var accounts = new List<Account>
        {
            new() { Id = 1, AccountNumber = "CITI-CHK-100001", Type = AccountType.Checking, Balance = 45230.75m, AvailableBalance = 45230.75m, InterestRate = 0.01m, BranchCode = "NYC-001", CustomerId = 1 },
            new() { Id = 2, AccountNumber = "CITI-SAV-100002", Type = AccountType.Savings, Balance = 128500.00m, AvailableBalance = 128500.00m, InterestRate = 4.35m, BranchCode = "NYC-001", CustomerId = 1 },
            new() { Id = 3, AccountNumber = "CITI-CHK-100003", Type = AccountType.Checking, Balance = 312750.50m, AvailableBalance = 310000.00m, InterestRate = 0.04m, BranchCode = "NYC-002", CustomerId = 2 },
            new() { Id = 4, AccountNumber = "CITI-MMA-100004", Type = AccountType.MoneyMarket, Balance = 525000.00m, AvailableBalance = 525000.00m, InterestRate = 4.75m, BranchCode = "NYC-002", CustomerId = 2 },
            new() { Id = 5, AccountNumber = "CITI-CHK-100005", Type = AccountType.Checking, Balance = 18420.30m, AvailableBalance = 18420.30m, InterestRate = 0.01m, BranchCode = "MIA-001", CustomerId = 3 },
            new() { Id = 6, AccountNumber = "CITI-SAV-100006", Type = AccountType.Savings, Balance = 67800.00m, AvailableBalance = 67800.00m, InterestRate = 4.35m, BranchCode = "MIA-001", CustomerId = 3 },
            new() { Id = 7, AccountNumber = "CITI-CHK-100007", Type = AccountType.Checking, Balance = 92150.25m, AvailableBalance = 92150.25m, InterestRate = 0.02m, BranchCode = "SFO-001", CustomerId = 4 },
            new() { Id = 8, AccountNumber = "CITI-BUS-100008", Type = AccountType.BusinessChecking, Balance = 245000.00m, AvailableBalance = 240000.00m, InterestRate = 0.03m, BranchCode = "SFO-001", CustomerId = 4 },
            new() { Id = 9, AccountNumber = "CITI-CHK-100009", Type = AccountType.Checking, Balance = 8750.60m, AvailableBalance = 8750.60m, InterestRate = 0.01m, BranchCode = "CHI-001", CustomerId = 5 },
            new() { Id = 10, AccountNumber = "CITI-SAV-100010", Type = AccountType.Savings, Balance = 22400.00m, AvailableBalance = 22400.00m, InterestRate = 4.35m, BranchCode = "CHI-001", CustomerId = 5 },
            new() { Id = 11, AccountNumber = "CITI-CHK-100011", Type = AccountType.Checking, Balance = 5230.40m, AvailableBalance = 5230.40m, InterestRate = 0.01m, BranchCode = "LAX-001", CustomerId = 6 },
            new() { Id = 12, AccountNumber = "CITI-CD-100012", Type = AccountType.CertificateOfDeposit, Balance = 50000.00m, AvailableBalance = 0.00m, InterestRate = 5.00m, BranchCode = "DC-001", CustomerId = 7 },
            new() { Id = 13, AccountNumber = "CITI-CHK-100013", Type = AccountType.Checking, Balance = 156800.00m, AvailableBalance = 156800.00m, InterestRate = 0.04m, BranchCode = "DC-001", CustomerId = 7 },
            new() { Id = 14, AccountNumber = "CITI-CHK-100014", Type = AccountType.Checking, Balance = 34500.80m, AvailableBalance = 34500.80m, InterestRate = 0.02m, BranchCode = "SEA-001", CustomerId = 8 },
            new() { Id = 15, AccountNumber = "CITI-SAV-100015", Type = AccountType.Savings, Balance = 89200.00m, AvailableBalance = 89200.00m, InterestRate = 4.50m, BranchCode = "SEA-001", CustomerId = 8 }
        };
        context.Accounts.AddRange(accounts);
        context.SaveChanges();

        var transactions = new List<Transaction>
        {
            new() { Id = 1, TransactionId = "TXN-A001-001", Type = TransactionType.DirectDeposit, Amount = 8500.00m, BalanceAfter = 45230.75m, Description = "Payroll - Citi Employee", AccountId = 1, TransactionDate = DateTime.UtcNow.AddDays(-1) },
            new() { Id = 2, TransactionId = "TXN-A001-002", Type = TransactionType.Payment, Amount = -1250.00m, BalanceAfter = 43980.75m, Description = "Mortgage Payment", MerchantName = "CitiMortgage", MerchantCategory = "Financial Services", AccountId = 1, TransactionDate = DateTime.UtcNow.AddDays(-3) },
            new() { Id = 3, TransactionId = "TXN-A001-003", Type = TransactionType.PointOfSale, Amount = -89.50m, BalanceAfter = 43891.25m, Description = "Restaurant Purchase", MerchantName = "Le Bernardin", MerchantCategory = "Dining", AccountId = 1, TransactionDate = DateTime.UtcNow.AddDays(-4) },
            new() { Id = 4, TransactionId = "TXN-A001-004", Type = TransactionType.Transfer, Amount = -5000.00m, BalanceAfter = 38891.25m, Description = "Transfer to Savings", AccountId = 1, TransactionDate = DateTime.UtcNow.AddDays(-5) },
            new() { Id = 5, TransactionId = "TXN-A002-001", Type = TransactionType.Transfer, Amount = 5000.00m, BalanceAfter = 128500.00m, Description = "Transfer from Checking", AccountId = 2, TransactionDate = DateTime.UtcNow.AddDays(-5) },
            new() { Id = 6, TransactionId = "TXN-A002-002", Type = TransactionType.Interest, Amount = 465.50m, BalanceAfter = 123965.50m, Description = "Monthly Interest Payment", AccountId = 2, TransactionDate = DateTime.UtcNow.AddDays(-30) },
            new() { Id = 7, TransactionId = "TXN-A003-001", Type = TransactionType.WireTransfer, Amount = 50000.00m, BalanceAfter = 312750.50m, Description = "Incoming Wire - Investment Returns", AccountId = 3, TransactionDate = DateTime.UtcNow.AddDays(-2) },
            new() { Id = 8, TransactionId = "TXN-A003-002", Type = TransactionType.ACHDebit, Amount = -15000.00m, BalanceAfter = 297750.50m, Description = "Property Tax Payment", MerchantName = "NYC Tax Commission", MerchantCategory = "Government", AccountId = 3, TransactionDate = DateTime.UtcNow.AddDays(-7) },
            new() { Id = 9, TransactionId = "TXN-A005-001", Type = TransactionType.DirectDeposit, Amount = 3200.00m, BalanceAfter = 18420.30m, Description = "Payroll Deposit", AccountId = 5, TransactionDate = DateTime.UtcNow.AddDays(-1) },
            new() { Id = 10, TransactionId = "TXN-A005-002", Type = TransactionType.ATMWithdrawal, Amount = -200.00m, BalanceAfter = 18220.30m, Description = "ATM Withdrawal - Brickell", AccountId = 5, TransactionDate = DateTime.UtcNow.AddDays(-2) },
            new() { Id = 11, TransactionId = "TXN-A005-003", Type = TransactionType.PointOfSale, Amount = -45.80m, BalanceAfter = 18174.50m, Description = "Grocery Purchase", MerchantName = "Whole Foods", MerchantCategory = "Groceries", AccountId = 5, TransactionDate = DateTime.UtcNow.AddDays(-3) },
            new() { Id = 12, TransactionId = "TXN-A007-001", Type = TransactionType.ACHCredit, Amount = 12000.00m, BalanceAfter = 92150.25m, Description = "Consulting Payment", AccountId = 7, TransactionDate = DateTime.UtcNow.AddDays(-1) },
            new() { Id = 13, TransactionId = "TXN-A009-001", Type = TransactionType.DirectDeposit, Amount = 2800.00m, BalanceAfter = 8750.60m, Description = "Payroll Deposit", AccountId = 9, TransactionDate = DateTime.UtcNow.AddDays(-1) },
            new() { Id = 14, TransactionId = "TXN-A009-002", Type = TransactionType.PointOfSale, Amount = -125.30m, BalanceAfter = 8625.30m, Description = "Online Purchase", MerchantName = "Amazon", MerchantCategory = "Online Retail", AccountId = 9, TransactionDate = DateTime.UtcNow.AddDays(-2) },
            new() { Id = 15, TransactionId = "TXN-A013-001", Type = TransactionType.WireTransfer, Amount = -25000.00m, BalanceAfter = 156800.00m, Description = "Investment Transfer - Citi Private Bank", AccountId = 13, TransactionDate = DateTime.UtcNow.AddDays(-1) },
            new() { Id = 16, TransactionId = "TXN-A014-001", Type = TransactionType.DirectDeposit, Amount = 6500.00m, BalanceAfter = 34500.80m, Description = "Payroll - Tech Company", AccountId = 14, TransactionDate = DateTime.UtcNow.AddDays(-1) },
            new() { Id = 17, TransactionId = "TXN-A014-002", Type = TransactionType.Payment, Amount = -1800.00m, BalanceAfter = 32700.80m, Description = "Rent Payment", MerchantName = "Equity Residential", MerchantCategory = "Housing", AccountId = 14, TransactionDate = DateTime.UtcNow.AddDays(-5) },
            new() { Id = 18, TransactionId = "TXN-A011-001", Type = TransactionType.DirectDeposit, Amount = 2200.00m, BalanceAfter = 5230.40m, Description = "Payroll Deposit", AccountId = 11, TransactionDate = DateTime.UtcNow.AddDays(-1) },
            new() { Id = 19, TransactionId = "TXN-A011-002", Type = TransactionType.Fee, Amount = -12.00m, BalanceAfter = 5218.40m, Description = "Monthly Service Fee", AccountId = 11, TransactionDate = DateTime.UtcNow.AddDays(-30) },
            new() { Id = 20, TransactionId = "TXN-A004-001", Type = TransactionType.Transfer, Amount = -100000.00m, BalanceAfter = 525000.00m, Description = "Investment Transfer", AccountId = 4, TransactionDate = DateTime.UtcNow.AddDays(-10) }
        };
        context.Transactions.AddRange(transactions);
        context.SaveChanges();

        var creditCards = new List<CreditCard>
        {
            new() { Id = 1, CardNumber = "4128-XXXX-XXXX-9012", MaskedNumber = "****9012", Type = CardType.CitiDoubleCash, CreditLimit = 25000m, CurrentBalance = 3245.67m, AvailableCredit = 21754.33m, AnnualPercentageRate = 18.24m, MinimumPaymentDue = 65.00m, PaymentDueDate = DateTime.UtcNow.AddDays(15), ExpirationDate = DateTime.UtcNow.AddYears(3), RewardsPoints = 0, CashBackEarned = 1245.80m, CustomerId = 1 },
            new() { Id = 2, CardNumber = "4128-XXXX-XXXX-3456", MaskedNumber = "****3456", Type = CardType.CitiPremier, CreditLimit = 50000m, CurrentBalance = 12890.00m, AvailableCredit = 37110.00m, AnnualPercentageRate = 20.99m, MinimumPaymentDue = 258.00m, PaymentDueDate = DateTime.UtcNow.AddDays(20), ExpirationDate = DateTime.UtcNow.AddYears(4), RewardsPoints = 87500, CashBackEarned = 0, CustomerId = 2 },
            new() { Id = 3, CardNumber = "4128-XXXX-XXXX-7890", MaskedNumber = "****7890", Type = CardType.CitiCustomCash, CreditLimit = 10000m, CurrentBalance = 1560.25m, AvailableCredit = 8439.75m, AnnualPercentageRate = 17.99m, MinimumPaymentDue = 35.00m, PaymentDueDate = DateTime.UtcNow.AddDays(12), ExpirationDate = DateTime.UtcNow.AddYears(2), RewardsPoints = 0, CashBackEarned = 423.50m, CustomerId = 3 },
            new() { Id = 4, CardNumber = "4128-XXXX-XXXX-2345", MaskedNumber = "****2345", Type = CardType.CitiDiamondPreferred, CreditLimit = 15000m, CurrentBalance = 0m, AvailableCredit = 15000m, AnnualPercentageRate = 16.49m, MinimumPaymentDue = 0m, ExpirationDate = DateTime.UtcNow.AddYears(3), RewardsPoints = 12300, CashBackEarned = 0, CustomerId = 4 },
            new() { Id = 5, CardNumber = "4128-XXXX-XXXX-6789", MaskedNumber = "****6789", Type = CardType.CitiRewards, CreditLimit = 8000m, CurrentBalance = 2340.50m, AvailableCredit = 5659.50m, AnnualPercentageRate = 19.49m, MinimumPaymentDue = 47.00m, PaymentDueDate = DateTime.UtcNow.AddDays(8), ExpirationDate = DateTime.UtcNow.AddYears(2), RewardsPoints = 34200, CashBackEarned = 0, CustomerId = 5 },
            new() { Id = 6, CardNumber = "4128-XXXX-XXXX-0123", MaskedNumber = "****0123", Type = CardType.CitiSecured, CreditLimit = 2500m, CurrentBalance = 890.30m, AvailableCredit = 1609.70m, AnnualPercentageRate = 24.99m, MinimumPaymentDue = 25.00m, PaymentDueDate = DateTime.UtcNow.AddDays(18), ExpirationDate = DateTime.UtcNow.AddYears(1), RewardsPoints = 0, CashBackEarned = 0, CustomerId = 6 },
            new() { Id = 7, CardNumber = "4128-XXXX-XXXX-4567", MaskedNumber = "****4567", Type = CardType.CostcoAnywhere, CreditLimit = 30000m, CurrentBalance = 5670.00m, AvailableCredit = 24330.00m, AnnualPercentageRate = 17.99m, MinimumPaymentDue = 114.00m, PaymentDueDate = DateTime.UtcNow.AddDays(22), ExpirationDate = DateTime.UtcNow.AddYears(4), RewardsPoints = 0, CashBackEarned = 2890.40m, CustomerId = 7 },
            new() { Id = 8, CardNumber = "4128-XXXX-XXXX-8901", MaskedNumber = "****8901", Type = CardType.CitiSimplicity, CreditLimit = 12000m, CurrentBalance = 4200.00m, AvailableCredit = 7800.00m, AnnualPercentageRate = 18.74m, MinimumPaymentDue = 84.00m, PaymentDueDate = DateTime.UtcNow.AddDays(10), ExpirationDate = DateTime.UtcNow.AddYears(3), RewardsPoints = 0, CashBackEarned = 0, CustomerId = 8 }
        };
        context.CreditCards.AddRange(creditCards);
        context.SaveChanges();

        var branches = new List<Branch>
        {
            new() { Id = 1, BranchCode = "NYC-001", Name = "Citi Flagship - Greenwich Street", Address = "388 Greenwich St", City = "New York", State = "NY", ZipCode = "10013", Phone = "212-559-1000", ManagerName = "Patricia Walsh", HasSafeDepositBoxes = true, HasDriveThrough = false, Latitude = 40.7205, Longitude = -74.0112 },
            new() { Id = 2, BranchCode = "NYC-002", Name = "Citi - Park Avenue", Address = "153 E 53rd St", City = "New York", State = "NY", ZipCode = "10022", Phone = "212-559-2000", ManagerName = "Thomas Lee", HasSafeDepositBoxes = true, HasDriveThrough = false, Latitude = 40.7589, Longitude = -73.9710 },
            new() { Id = 3, BranchCode = "MIA-001", Name = "Citi - Brickell", Address = "1395 Brickell Ave", City = "Miami", State = "FL", ZipCode = "33131", Phone = "305-347-1000", ManagerName = "Carlos Gutierrez", HasSafeDepositBoxes = true, HasDriveThrough = true, Latitude = 25.7559, Longitude = -80.1930 },
            new() { Id = 4, BranchCode = "SFO-001", Name = "Citi - Financial District SF", Address = "1 Sansome St", City = "San Francisco", State = "CA", ZipCode = "94104", Phone = "415-627-1000", ManagerName = "Michelle Tanaka", HasSafeDepositBoxes = false, HasDriveThrough = false, Latitude = 37.7900, Longitude = -122.4010 },
            new() { Id = 5, BranchCode = "CHI-001", Name = "Citi - West Loop", Address = "500 W Madison St", City = "Chicago", State = "IL", ZipCode = "60661", Phone = "312-627-1000", ManagerName = "Andrew Brooks", HasSafeDepositBoxes = true, HasDriveThrough = true, Latitude = 41.8822, Longitude = -87.6414 },
            new() { Id = 6, BranchCode = "LAX-001", Name = "Citi - Downtown LA", Address = "444 S Flower St", City = "Los Angeles", State = "CA", ZipCode = "90071", Phone = "213-239-1000", ManagerName = "Sandra Kim", HasSafeDepositBoxes = false, HasDriveThrough = false, Latitude = 34.0531, Longitude = -118.2575 },
            new() { Id = 7, BranchCode = "DC-001", Name = "Citi - Pennsylvania Avenue", Address = "1101 Pennsylvania Ave NW", City = "Washington", State = "DC", ZipCode = "20004", Phone = "202-879-1000", ManagerName = "Richard Evans", HasSafeDepositBoxes = true, HasDriveThrough = false, Latitude = 38.8955, Longitude = -77.0283 },
            new() { Id = 8, BranchCode = "SEA-001", Name = "Citi - Downtown Seattle", Address = "1420 5th Ave", City = "Seattle", State = "WA", ZipCode = "98101", Phone = "206-344-1000", ManagerName = "Karen Yamamoto", HasSafeDepositBoxes = true, HasDriveThrough = false, Latitude = 47.6113, Longitude = -122.3365 }
        };
        context.Branches.AddRange(branches);
        context.SaveChanges();

        var loans = new List<Loan>
        {
            new() { Id = 1, LoanNumber = "CITI-MTG-500001", Type = LoanType.Mortgage, PrincipalAmount = 750000m, OutstandingBalance = 682500m, InterestRate = 6.875m, MonthlyPayment = 4925.50m, TermMonths = 360, OriginationDate = DateTime.UtcNow.AddYears(-2), MaturityDate = DateTime.UtcNow.AddYears(28), NextPaymentDate = DateTime.UtcNow.AddDays(15), CustomerId = 1 },
            new() { Id = 2, LoanNumber = "CITI-HEQ-500002", Type = LoanType.HomeEquity, PrincipalAmount = 200000m, OutstandingBalance = 145000m, InterestRate = 8.50m, MonthlyPayment = 2100.00m, TermMonths = 120, OriginationDate = DateTime.UtcNow.AddYears(-3), MaturityDate = DateTime.UtcNow.AddYears(7), NextPaymentDate = DateTime.UtcNow.AddDays(20), CustomerId = 2 },
            new() { Id = 3, LoanNumber = "CITI-AUT-500003", Type = LoanType.Auto, PrincipalAmount = 45000m, OutstandingBalance = 32800m, InterestRate = 5.49m, MonthlyPayment = 870.25m, TermMonths = 60, OriginationDate = DateTime.UtcNow.AddMonths(-14), MaturityDate = DateTime.UtcNow.AddMonths(46), NextPaymentDate = DateTime.UtcNow.AddDays(10), CustomerId = 3 },
            new() { Id = 4, LoanNumber = "CITI-PER-500004", Type = LoanType.Personal, PrincipalAmount = 25000m, OutstandingBalance = 18750m, InterestRate = 10.99m, MonthlyPayment = 545.80m, TermMonths = 60, OriginationDate = DateTime.UtcNow.AddMonths(-12), MaturityDate = DateTime.UtcNow.AddMonths(48), NextPaymentDate = DateTime.UtcNow.AddDays(5), CustomerId = 5 },
            new() { Id = 5, LoanNumber = "CITI-SBL-500005", Type = LoanType.SmallBusiness, PrincipalAmount = 500000m, OutstandingBalance = 425000m, InterestRate = 7.25m, MonthlyPayment = 6125.00m, TermMonths = 120, OriginationDate = DateTime.UtcNow.AddMonths(-10), MaturityDate = DateTime.UtcNow.AddMonths(110), NextPaymentDate = DateTime.UtcNow.AddDays(12), CustomerId = 4 },
            new() { Id = 6, LoanNumber = "CITI-STU-500006", Type = LoanType.StudentLoan, PrincipalAmount = 85000m, OutstandingBalance = 72000m, InterestRate = 4.99m, MonthlyPayment = 900.50m, TermMonths = 120, OriginationDate = DateTime.UtcNow.AddYears(-1), MaturityDate = DateTime.UtcNow.AddYears(9), NextPaymentDate = DateTime.UtcNow.AddDays(8), CustomerId = 8 }
        };
        context.Loans.AddRange(loans);
        context.SaveChanges();
    }
}
