# CitiBank Banking API

A .NET Core Web API showcasing banking data relevant to CitiBank operations. Built with ASP.NET Core, Entity Framework Core (In-Memory), and OpenAPI/Swagger.

## Features

- **Customer Management** — CRUD operations for bank customers with tiered membership (Basic, Gold, Platinum, CitiPriority, Citigold, Citigold Private Client)
- **Account Management** — Checking, Savings, Money Market, CD, and Business accounts with real-time balances
- **Transaction Processing** — Deposits, withdrawals, transfers, ACH, wire transfers, POS, and ATM transactions
- **Credit Cards** — Citi Double Cash, Premier, Custom Cash, Diamond Preferred, Rewards, Simplicity, Secured, and Costco Anywhere cards
- **Loan Tracking** — Mortgages, Home Equity, Auto, Personal, Student, and Small Business loans
- **Branch Locator** — Branch information with geolocation, hours, and amenities
- **Executive Dashboard** — Aggregated metrics across all banking operations

## Tech Stack

- .NET 10 / ASP.NET Core
- Entity Framework Core (In-Memory Database)
- OpenAPI 3.x documentation
- RESTful API design

## Getting Started

### Prerequisites

- [.NET 10 SDK](https://dotnet.microsoft.com/download)

### Run the API

```bash
cd CitiBankBankingApi
dotnet run
```

The API will start at `http://localhost:5000` (or the port configured in `launchSettings.json`).

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | API info and available endpoints |
| GET | `/api/dashboard` | Executive dashboard with aggregated metrics |
| GET | `/api/customers` | List all customers |
| GET | `/api/customers/{id}` | Customer details with accounts and cards |
| POST | `/api/customers` | Create a new customer |
| GET | `/api/accounts` | List all accounts |
| GET | `/api/accounts/{id}` | Account details with recent transactions |
| GET | `/api/accounts/customer/{customerId}` | Accounts by customer |
| POST | `/api/accounts` | Open a new account |
| GET | `/api/transactions/account/{accountId}` | Transactions by account |
| GET | `/api/transactions/recent?count=20` | Recent transactions across all accounts |
| POST | `/api/transactions` | Create a transaction |
| POST | `/api/transactions/transfer` | Transfer between accounts |
| GET | `/api/creditcards` | List all credit cards |
| GET | `/api/creditcards/{id}` | Credit card details |
| GET | `/api/creditcards/customer/{customerId}` | Cards by customer |
| GET | `/api/loans` | List all loans |
| GET | `/api/loans/{id}` | Loan details |
| GET | `/api/loans/customer/{customerId}` | Loans by customer |
| GET | `/api/branches` | List all branches |
| GET | `/api/branches/{id}` | Branch details |
| GET | `/api/branches/search?city=&state=` | Search branches by location |

### Sample Data

The API comes pre-loaded with realistic banking data:

- **8 customers** across all Citi membership tiers
- **15 accounts** (checking, savings, money market, CD, business)
- **20 transactions** (payroll, transfers, purchases, wire transfers)
- **8 credit cards** (various Citi card products)
- **8 branches** across major US cities (NYC, Miami, SF, Chicago, LA, DC, Seattle)
- **6 loans** (mortgage, home equity, auto, personal, small business, student)

## Project Structure

```
CitiBankBankingApi/
├── Controllers/        # API endpoints
│   ├── AccountsController.cs
│   ├── BranchesController.cs
│   ├── CreditCardsController.cs
│   ├── CustomersController.cs
│   ├── DashboardController.cs
│   ├── LoansController.cs
│   └── TransactionsController.cs
├── Data/
│   ├── CitiBankDbContext.cs   # EF Core context
│   └── SeedData.cs            # Sample banking data
├── DTOs/               # Data transfer objects
├── Models/             # Domain entities
├── Services/           # Business logic
├── Program.cs          # Application entry point
└── README.md
```
