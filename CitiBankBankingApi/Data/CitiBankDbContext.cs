using CitiBankBankingApi.Models;
using Microsoft.EntityFrameworkCore;

namespace CitiBankBankingApi.Data;

public class CitiBankDbContext : DbContext
{
    public CitiBankDbContext(DbContextOptions<CitiBankDbContext> options) : base(options) { }

    public DbSet<Customer> Customers => Set<Customer>();
    public DbSet<Account> Accounts => Set<Account>();
    public DbSet<Transaction> Transactions => Set<Transaction>();
    public DbSet<CreditCard> CreditCards => Set<CreditCard>();
    public DbSet<Branch> Branches => Set<Branch>();
    public DbSet<Loan> Loans => Set<Loan>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Customer>(e =>
        {
            e.HasKey(c => c.Id);
            e.Property(c => c.Email).HasMaxLength(255);
            e.HasIndex(c => c.Email).IsUnique();
        });

        modelBuilder.Entity<Account>(e =>
        {
            e.HasKey(a => a.Id);
            e.HasIndex(a => a.AccountNumber).IsUnique();
            e.Property(a => a.Balance).HasPrecision(18, 2);
            e.Property(a => a.AvailableBalance).HasPrecision(18, 2);
            e.Property(a => a.InterestRate).HasPrecision(6, 4);
            e.HasOne(a => a.Customer).WithMany(c => c.Accounts).HasForeignKey(a => a.CustomerId);
        });

        modelBuilder.Entity<Transaction>(e =>
        {
            e.HasKey(t => t.Id);
            e.HasIndex(t => t.TransactionId).IsUnique();
            e.Property(t => t.Amount).HasPrecision(18, 2);
            e.Property(t => t.BalanceAfter).HasPrecision(18, 2);
            e.HasOne(t => t.Account).WithMany(a => a.Transactions).HasForeignKey(t => t.AccountId);
        });

        modelBuilder.Entity<CreditCard>(e =>
        {
            e.HasKey(cc => cc.Id);
            e.Property(cc => cc.CreditLimit).HasPrecision(18, 2);
            e.Property(cc => cc.CurrentBalance).HasPrecision(18, 2);
            e.Property(cc => cc.AvailableCredit).HasPrecision(18, 2);
            e.Property(cc => cc.AnnualPercentageRate).HasPrecision(6, 4);
            e.Property(cc => cc.MinimumPaymentDue).HasPrecision(18, 2);
            e.Property(cc => cc.RewardsPoints).HasPrecision(18, 2);
            e.Property(cc => cc.CashBackEarned).HasPrecision(18, 2);
            e.HasOne(cc => cc.Customer).WithMany(c => c.CreditCards).HasForeignKey(cc => cc.CustomerId);
        });

        modelBuilder.Entity<Branch>(e =>
        {
            e.HasKey(b => b.Id);
            e.HasIndex(b => b.BranchCode).IsUnique();
        });

        modelBuilder.Entity<Loan>(e =>
        {
            e.HasKey(l => l.Id);
            e.HasIndex(l => l.LoanNumber).IsUnique();
            e.Property(l => l.PrincipalAmount).HasPrecision(18, 2);
            e.Property(l => l.OutstandingBalance).HasPrecision(18, 2);
            e.Property(l => l.InterestRate).HasPrecision(6, 4);
            e.Property(l => l.MonthlyPayment).HasPrecision(18, 2);
            e.HasOne(l => l.Customer).WithMany().HasForeignKey(l => l.CustomerId);
        });
    }
}
