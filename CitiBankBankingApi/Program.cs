using CitiBankBankingApi.Data;
using CitiBankBankingApi.Services;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddOpenApi();

builder.Services.AddDbContext<CitiBankDbContext>(options =>
    options.UseInMemoryDatabase("CitiBankDb"));

builder.Services.AddScoped<CustomerService>();
builder.Services.AddScoped<AccountService>();
builder.Services.AddScoped<TransactionService>();
builder.Services.AddScoped<DashboardService>();

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
        policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<CitiBankDbContext>();
    SeedData.Initialize(db);
}

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseCors();
app.MapControllers();

app.MapGet("/", () => Results.Ok(new
{
    application = "CitiBank Banking API",
    version = "1.0.0",
    description = "RESTful API showcasing CitiBank banking operations",
    endpoints = new
    {
        dashboard = "/api/dashboard",
        customers = "/api/customers",
        accounts = "/api/accounts",
        transactions = "/api/transactions",
        creditCards = "/api/creditcards",
        branches = "/api/branches",
        loans = "/api/loans",
        openApi = "/openapi/v1.json"
    }
}));

app.Run();
