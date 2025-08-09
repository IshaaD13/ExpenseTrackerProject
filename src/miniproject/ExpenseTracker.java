// ExpenseTracker.java (Simplified Version with All Concepts)
package miniproject;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Parent Class
class Transaction {
    private LocalDate date;
    private LocalTime time;
    private String description;
    private double amount;

    public Transaction(LocalDate date, LocalTime time, String description, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.amount = amount;
    }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
}

// Child Class
class Expense extends Transaction {
    public Expense(LocalDate date, LocalTime time, String description, double amount) {
        super(date, time, description, amount);
    }
}

// Main Class
public class ExpenseTracker {
    ArrayList<Expense> expenses = new ArrayList<>();
    static double monthlyIncome = 0.0;
    static double yearlyIncome = 0.0;
    double totalExpenses = 0.0, monthlySavings = 0.0, yearlySavings = 0.0, minimumExpense = 100.0;

    // Encapsulation
    public void setMonthlyIncome(double income) {
        monthlyIncome = income;
        yearlyIncome = income * 12;
        updateSavings();
        System.out.println("Monthly Income set.");
    }
    public void setMinimumExpense(double min) {
        minimumExpense = min;
        System.out.println("Minimum balance set to: " + min);
    }

    public void addExpense(String desc, double amt) {
        Expense e = new Expense(LocalDate.now(), LocalTime.now(), desc, amt);
        expenses.add(e);
        totalExpenses += amt;
        updateSavings();
        System.out.println("\nExpense added.");
        if (monthlyIncome - totalExpenses < minimumExpense)
            System.out.println("Warning: Below minimum balance!");
    }

    public void viewExpenses() {
        System.out.printf("\n| %-12s | %-5s | %-12s | %8s |%n", "DATE", "TIME", "DESCRIPTION", "AMOUNT");
        for (Expense e : expenses) {
            System.out.printf("| %-12s | %-5s | %-12s | %8.2f |%n",
                    e.getDate(), e.getTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    e.getDescription(), e.getAmount());
        }
        System.out.printf("Total: %.2f\nToday: %.2f\nThis Week: %.2f\nThis Month: %.2f\n",
                totalExpenses, getTotalForDay(), getTotalForWeek(), getTotalForMonth());
    }

    public void deleteExpense(String desc) {
        for (Expense e : expenses) {
            if (e.getDescription().equalsIgnoreCase(desc)) {
                totalExpenses -= e.getAmount();
                expenses.remove(e);
                updateSavings();
                System.out.println("\nExpense deleted.");
                return;
            }
        }
        System.out.println("\nExpense not found.");
    }

    private void updateSavings() {
        monthlySavings = monthlyIncome - getTotalForMonth();
        yearlySavings = yearlyIncome - getTotalForYear(LocalDate.now().getYear());
    }

    public double getTotalForDay() {
        return expenses.stream().filter(e -> e.getDate().isEqual(LocalDate.now())).mapToDouble(Expense::getAmount).sum();
    }
    public double getTotalForWeek() {
        LocalDate now = LocalDate.now(), start = now.minusDays(now.getDayOfWeek().getValue() - 1);
        return expenses.stream().filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(start.plusDays(6)))
                .mapToDouble(Expense::getAmount).sum();
    }
    public double getTotalForMonth() {
        LocalDate now = LocalDate.now();
        return getTotalForMonth(now.getYear(), now.getMonthValue());
    }
    public double getTotalForMonth(int y, int m) {
        return expenses.stream().filter(e -> e.getDate().getYear() == y && e.getDate().getMonthValue() == m)
                .mapToDouble(Expense::getAmount).sum();
    }
    public double getTotalForYear(int y) {
        return expenses.stream().filter(e -> e.getDate().getYear() == y).mapToDouble(Expense::getAmount).sum();
    }

    public void suggestInvestmentOpportunity() {
        double invest = monthlySavings * 0.2;
        if (invest >= 5000)
            System.out.println("Consider stocks or mutual funds.");
        else if (invest >= 1000)
            System.out.println("Try a high-yield savings account.");
        else
            System.out.println("Too low for investing right now.");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ExpenseTracker et = new ExpenseTracker();
        while (true) {
            try {
                System.out.println("\nMenu:\n1. Set Minimum Balance\n2. Add Income\n3. Add Expense\n4. View Expenses\n5. Delete Expense\n6. View Month/Year Expense\n7. View Savings\n8. Investment Tips\n9. Exit");
                System.out.print("Choice: ");
                int ch = sc.nextInt(); sc.nextLine();

                switch (ch) {
                    case 1 -> et.setMinimumExpense(sc.nextDouble());
                    case 2 -> et.setMonthlyIncome(sc.nextDouble());
                    case 3 -> {
                        System.out.print("Description: ");
                        String d = sc.nextLine();
                        System.out.print("Amount: ");
                        double a = sc.nextDouble();
                        et.addExpense(d, a);
                    }
                    case 4 -> et.viewExpenses();
                    case 5 -> {
                        System.out.print("Description to delete: ");
                        et.deleteExpense(sc.nextLine());
                    }
                    case 6 -> {
                        System.out.println("1. Month\n2. Year");
                        int opt = sc.nextInt();
                        if (opt == 1) {
                            System.out.print("Month (1-12): ");
                            int m = sc.nextInt();
                            System.out.print("Year: ");
                            int y = sc.nextInt();
                            System.out.printf("Total: %.2f\n", et.getTotalForMonth(y, m));
                        } else if (opt == 2) {
                            System.out.print("Year: ");
                            int y = sc.nextInt();
                            System.out.printf("Total: %.2f\n", et.getTotalForYear(y));
                        } else System.out.println("Invalid choice.");
                    }
                    case 7 -> {
                        System.out.printf("Monthly Savings: %.2f\nYearly Savings: %.2f\n", et.monthlySavings, et.yearlySavings);
                    }
                    case 8 -> et.suggestInvestmentOpportunity();
                    case 9 -> {
                        System.out.println("Thank you!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Invalid input. Try again.");
                sc.nextLine();
            }
        }
    }
}
