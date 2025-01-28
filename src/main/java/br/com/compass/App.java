package br.com.compass;

import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import br.com.compass.database.DatabaseConnection;
import br.com.compass.models.Account;
import br.com.compass.models.User;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.TransactionRepository;
import br.com.compass.repositories.UserRepository;
import br.com.compass.services.AccountService;
import br.com.compass.services.TransactionService;
import br.com.compass.services.UserService;
import br.com.compass.utils.ValidationUtils;

public class App {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);

            UserRepository userRepository = new UserRepository(connection);
            AccountRepository accountRepository = new AccountRepository(connection);
            TransactionRepository transactionRepository = new TransactionRepository(connection);

            UserService userService = new UserService(userRepository);
            AccountService accountService = new AccountService(accountRepository, userRepository);
            TransactionService transactionService = new TransactionService(accountRepository, userRepository, transactionRepository);

            mainMenu(scanner, userService, accountService, transactionService);
            scanner.nextLine(); 
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mainMenu(Scanner scanner, UserService userService, AccountService accountService, TransactionService transactionService) {
        boolean running = true;

        while (running) {
            System.out.println("========= Main Menu =========");
            System.out.println("|| 1. Login                ||");
            System.out.println("|| 2. Account Opening      ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1 -> login(scanner, userService, accountService, transactionService);
                case 2 -> accountOpening(scanner, userService, accountService);
                case 0 -> {
                    System.exit(0);
                    break;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
    }

    public static void login(Scanner scanner, UserService userService, AccountService accountService, TransactionService transactionService) {
        scanner.nextLine(); 

        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter your CPF (numbers only): ");
            String cpf = scanner.nextLine().replaceAll("[^0-9]", "");

            if (userService.isCpfRegistered(cpf)) {
                System.out.print("Enter your 6-digit password: ");
                String password = scanner.nextLine();

                if (userService.isPasswordValid(cpf, password)) {
                    try {
                        var user = userService.getUserByCpf(cpf).orElseThrow(() -> new IllegalArgumentException("User not found."));
                        var account = accountService.getAccountByCpf(cpf);

                        System.out.println("\nWelcome to Bank Compass, " + user.getName() + "!");
                        System.out.println("Account Type: " + account.getAccountType());
                        System.out.println("Account Number: " + account.getAccountNumber());

                        loggedIn = true;

                        bankMenu(scanner, user, account, userService, accountService, transactionService);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        System.out.println("Returning to the main menu...");
                    }
                } else {
                    System.out.println("Invalid password. Would you like to try again? (y/n): ");
                    String choice = scanner.nextLine().toLowerCase();

                    if (choice.equals("n")) {
                        System.out.println("Returning to the main menu...");
                        return;
                    }
                }
            } else {
                System.out.println("Invalid CPF. Would you like to try again? (y/n): ");
                String choice = scanner.nextLine().toLowerCase();

                if (choice.equals("n")) {
                    System.out.println("Returning to the main menu...");
                    return;
                }
            }
        }
    }

    public static void bankMenu(Scanner scanner, User user, Account account, UserService userService, AccountService accountService, TransactionService transactionService) {
        boolean running = true;

        while (running) {
            System.out.println("\n========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            try {
                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1 -> {
                        System.out.print("Enter the amount to deposit: ");
                        double amount = Double.parseDouble(scanner.nextLine());
                        transactionService.deposit(account, amount);
                        System.out.println("Deposit successful. New balance: " + account.getBalance());
                    }
                    case 2 -> {
                        System.out.print("Enter the amount to withdraw: ");
                        double amount = Double.parseDouble(scanner.nextLine());

                        System.out.print("Enter your password: ");
                        String password = scanner.nextLine();

                        if (transactionService.isPasswordValid(user.getCpf(), password)) {
                            transactionService.withdraw(account, amount);
                            System.out.println("Withdrawal successful. New balance: " + account.getBalance());
                        } else {
                            System.out.println("Invalid password. Withdrawal cancelled.");
                        }
                    }
                    case 3 -> System.out.println("Your balance is: " + account.getBalance());
                    case 4 -> handleTransfer(scanner, user, account, userService, accountService, transactionService);
                    case 5 -> showBankStatement(account, transactionService);
                    case 0 -> {
                        System.out.println("Returning to the main menu...");
                        mainMenu(scanner, userService, accountService, transactionService);
                        return;
                    }
                    default -> System.out.println("Invalid option! Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid option.");
            }
        }
    }

    private static void handleTransfer(Scanner scanner, User user, Account account, UserService userService, AccountService accountService, TransactionService transactionService) {
        try {
            System.out.print("Enter the destination account number: ");
            String targetAccountNumber = scanner.nextLine();
            Account targetAccount = accountService.getAccountByNumber(targetAccountNumber);

            if (targetAccount == null) {
                System.out.println("Target account not found. Transfer cancelled.");
                return;
            }

            // Obtém o nome do destinatário
            String recipientCpf = targetAccount.getUserCpf();
            String recipientName = userService.getUserByCpf(recipientCpf)
                    .map(User::getName)
                    .orElse("Unknown");

            // Exibe o destinatário e pede confirmação
            System.out.println("Person: " + recipientName);
            System.out.print("Do you confirm the transfer to this person? (y/n): ");
            String confirmation = scanner.nextLine().toLowerCase();

            if (!confirmation.equals("y")) {
                System.out.println("Transfer cancelled. Returning to the bank menu...");
                return; // Retorna para o menu sem fazer a transferência
            }

            System.out.print("Enter the amount to transfer: ");
            double amount = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            if (transactionService.isPasswordValid(user.getCpf(), password)) {
                transactionService.transfer(account, targetAccount, amount);
                System.out.println("Transfer successful to " + recipientName + ". New balance: " + account.getBalance());
            } else {
                System.out.println("Invalid password. Transfer cancelled.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during the transfer: " + e.getMessage());
        }
    }


    private static void showBankStatement(Account account, TransactionService transactionService) {
        System.out.println("\n========== Bank Statement ==========");
        List<String> history = transactionService.getTransactionHistory(account.getAccountNumber());
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.printf("%-25s %-10s %-10s %-30s%n", "Timestamp", "Type", "Amount", "Details");
            System.out.println("------------------------------------------------------------");
            history.forEach(System.out::println);
        }
        System.out.println("=====================================\n");
    }


    
    private static String selectAccountType(Scanner scanner) {
        String accountType = "";
        while (true) {
            System.out.println("\nChoose Account Type:");
            System.out.println("1. Checking Account");
            System.out.println("2. Salary Account");
            System.out.println("3. Savings Account");
            System.out.print("Enter option (1-3): ");
            int accountOption = scanner.nextInt();
            scanner.nextLine();

            switch (accountOption) {
                case 1 -> accountType = "Checking Account";
                case 2 -> accountType = "Salary Account";
                case 3 -> accountType = "Savings Account";
                default -> {
                    System.out.println("Invalid option. Please select a valid account type.");
                    continue;
                }
            }

            System.out.println("You selected: " + accountType);
            System.out.print("Do you confirm this account type? (s/n): ");
            String confirmation = scanner.nextLine().toLowerCase();

            if (confirmation.equals("s")) {
                break; 
            } else if (confirmation.equals("n")) {
                System.out.println("Let's choose the account type again.");
            } else {
                System.out.println("Invalid input. Returning to account type selection.");
            }
        }
        return accountType;
    }


    public static void accountOpening(Scanner scanner, UserService userService, AccountService accountService) {
        System.out.println("\n========= Account Opening =========");

        String name = "", birthDate = "", cpf = "", phone = "", password = "";
        boolean confirmed = false;

        scanner.nextLine();

        while (cpf.isEmpty()) {
            System.out.print("Enter CPF (numbers only): ");
            cpf = scanner.nextLine().replaceAll("[^0-9]", "");

            if (!ValidationUtils.isValidCpf(cpf)) {
                System.out.println("Invalid CPF. Please enter a valid CPF.");
                cpf = "";
            } else if (userService.isCpfRegistered(cpf)) {
                System.out.println("A user with this CPF already exists. Returning to the main menu...");
                return; // Retorna imediatamente ao menu principal se o CPF já estiver cadastrado
            }
        }

        while (!confirmed) {
            while (name.isEmpty()) {
                System.out.print("Enter Name: ");
                name = scanner.nextLine().trim();
                if (!ValidationUtils.isValidName(name)) {
                    System.out.println("Invalid name. Please enter a valid name with letters only (2-100 characters).");
                    name = "";
                }
            }
            while (birthDate.isEmpty()) {
                System.out.print("Enter Birth Date (dd-mm-yyyy): ");
                birthDate = scanner.nextLine().trim();
                if (!ValidationUtils.isValidBirthDate(birthDate)) {
                    System.out.println("Invalid birth date. You must be at least 18 years old.");
                    birthDate = "";
                }
            }
            while (phone.isEmpty()) {
                System.out.print("Enter Phone (DDD + 9 digits): ");
                phone = scanner.nextLine().replaceAll("[^0-9]", "");
                if (!ValidationUtils.isValidPhone(phone)) {
                    System.out.println("Invalid phone number. Please enter a valid 11-digit phone number.");
                    phone = "";
                }
            }

            System.out.println("\nReview your details:");
            System.out.println("1. Name: " + name);
            System.out.println("2. Birth Date: " + birthDate);
            System.out.println("3. CPF: " + cpf);
            System.out.println("4. Phone: " + phone);
            System.out.print("Are these details correct? (s/n): ");
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("s")) {
                confirmed = true;
            } else if (choice.equals("n")) {
                System.out.print("Enter the number of the field you want to edit (1-4): ");
                int field = scanner.nextInt();
                scanner.nextLine();
                switch (field) {
                    case 1 -> name = "";
                    case 2 -> birthDate = "";
                    case 3 -> cpf = "";
                    case 4 -> phone = "";
                    default -> System.out.println("Invalid option. Returning to review...");
                }
            } else {
                System.out.println("Invalid input. Returning to review...");
            }
        }

        // Solicitação da senha
        while (true) {
            System.out.print("Enter a 6-digit password (numbers only, no repeated digits): ");
            password = scanner.nextLine().replaceAll("[^0-9]", "");
            if (password.length() != 6 || ValidationUtils.hasRepeatedDigits(password)) {
                System.out.println("Invalid password. It must be 6 digits with no repeated numbers.");
                continue;
            }

            System.out.print("Confirm your password: ");
            String confirmPassword = scanner.nextLine().replaceAll("[^0-9]", "");
            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again.");
            } else {
                break;
            }
        }

        try {
            User user = new User(name, cpf, LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")), phone, password);
            userService.registerUser(user);

            String accountType = selectAccountType(scanner);
            Account account = accountService.openAccount(cpf, accountType);

            System.out.println("\nAccount created successfully!");
            System.out.println("Use your CPF to log in: " + cpf);
            System.out.println("Account ID: " + account.getAccountNumber());
            System.out.println("Account Type: " + account.getAccountType());
        } catch (Exception e) {
            System.out.println("An error occurred while creating the account: " + e.getMessage());
            System.out.println("Returning to the main menu...");
        }
    }
}
