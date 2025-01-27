package br.com.compass;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import br.com.compass.models.Account;
import br.com.compass.models.User;
import br.com.compass.services.AccountService;
import br.com.compass.services.TransactionService;
import br.com.compass.services.UserService;

public class App {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);
        
        scanner.close();
        System.out.println("Application closed");
    }

    public static void mainMenu(Scanner scanner) {
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
                case 1:
                	 login(scanner);
                case 2:
                	accountOpening(scanner);
                    System.out.println("\nAccount Opening.\n");
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    
    public static void accountOpening(Scanner scanner) {
        UserService userService = new UserService();
        AccountService accountService = new AccountService();

        System.out.println("\n========= Account Opening =========");

        String name = "", birthDate = "", cpf = "", phone = "", password = "";
        boolean confirmed = false;

        scanner.nextLine(); 

        while (!confirmed) {
            
            name = validateName(scanner, name);

            birthDate = validateBirthDate(scanner, birthDate);

            cpf = validateCpf(scanner, cpf);

            phone = validatePhone(scanner, phone);

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

        User user = new User(null, name, cpf, LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd-MM-yyyy")), phone);

        if (!userService.registerUser(user)) {
            System.out.println("A user with this CPF already exists. Returning to the main menu...");
            return;
        }

        while (true) {
            System.out.print("Enter a 6-digit password (numbers only, no repeated digits): ");
            password = scanner.nextLine().replaceAll("[^0-9]", ""); 
            if (password.length() != 6 || hasRepeatedDigits(password)) {
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

        user.setPassword(password);

        String accountType = selectAccountType(scanner);

        Account account = accountService.openAccount(cpf, accountType);

        System.out.println("\nAccount created successfully!");
        System.out.println("Use your CPF to log in: " + cpf);
        System.out.println("Account ID: " + account.getAccountNumber());
        System.out.println("Account Type: " + account.getAccountType());
    }	
    
    private static boolean hasRepeatedDigits(String password) {
        for (int i = 0; i < password.length(); i++) {
            for (int j = i + 1; j < password.length(); j++) {
                if (password.charAt(i) == password.charAt(j)) {
                    return true;
                }
            }
        }
        return false;
    }


    private static String validateName(Scanner scanner, String name) {
        while (name.isEmpty()) {
            System.out.print("Enter Name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name is required. Please enter a valid name.");
            } else if (!name.matches("^[a-zA-Z\\s]+$")) {
                System.out.println("Name must not contain numbers. Please enter a valid name.");
                name = "";
            } else if (name.length() < 2 || name.length() > 100) {
                System.out.println("Name must be between 2 and 100 characters.");
                name = "";
            }
        }
        return name;
    }

    private static String validateBirthDate(Scanner scanner, String birthDate) {
        while (birthDate.isEmpty()) {
            System.out.print("Enter Birth Date (dd-mm-yyyy): ");
            birthDate = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                LocalDate today = LocalDate.now();
                if (date.isBefore(LocalDate.of(1900, 1, 1)) || date.isAfter(today.minusYears(18))) {
                    System.out.println("You must be at least 18 years old to open an account.");
                    birthDate = "";
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date as dd-mm-yyyy.");
                birthDate = "";
            }
        }
        return birthDate;
    }

    private static String validateCpf(Scanner scanner, String cpf) {
        while (cpf.isEmpty()) {
            System.out.print("Enter CPF (numbers only): ");
            cpf = scanner.nextLine().replaceAll("[^0-9]", ""); 
            if (!isValidCpf(cpf)) {
                System.out.println("Invalid CPF. Please enter a valid CPF.");
                cpf = "";
            }
        }
        return cpf;
    }

    private static String validatePhone(Scanner scanner, String phone) {
        while (phone.isEmpty()) {
            System.out.print("Enter Phone (DDD + 9 digits): ");
            phone = scanner.nextLine().replaceAll("[^0-9]", ""); 
            if (phone.length() != 11) {
                System.out.println("Phone number must contain exactly 11 digits.");
                phone = "";
            }
        }
        return phone;
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

    
    private static boolean isValidCpf(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) {
                firstDigit = 0;
            }
            if (firstDigit != (cpf.charAt(9) - '0')) {
                return false;
            }
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) {
                secondDigit = 0;
            }
            return secondDigit == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    public static void login(Scanner scanner) {
        UserService userService = new UserService();
        AccountService accountService = new AccountService();
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

                        bankMenu(scanner, user, account);
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

    public static void bankMenu(Scanner scanner, User user, Account account) {
    	AccountService accountService = new AccountService();
        TransactionService transactionService = new TransactionService(); 
        UserService userService = new UserService();
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
                        try {
                            double amount = Double.parseDouble(scanner.nextLine());
                            transactionService.deposit(account, amount);
                            System.out.println("Deposit successful. New balance: " + account.getBalance());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount. Please enter a valid number.");
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    case 2 -> { 
                        System.out.print("Enter the amount to withdraw: ");
                        try {
                            double amount = Double.parseDouble(scanner.nextLine());
                            
                            System.out.print("Enter your password: ");
                            String password = scanner.nextLine();

                            if (transactionService.isPasswordValid(account.getUserCpf(), password)) {
                                transactionService.withdraw(account, amount);
                                System.out.println("Withdrawal successful. New balance: " + account.getBalance());
                            } else {
                                System.out.println("Invalid password. Withdrawal cancelled.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount. Please enter a valid number.");
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    case 3 -> System.out.println("Your balance is: " + account.getBalance());

                    case 4 -> {
                        System.out.print("Enter the destination account number: ");
                        String targetAccountNumber = scanner.nextLine();
                        try {
                            Account targetAccount = accountService.getAccountByNumber(targetAccountNumber);

                            String recipientCpf = targetAccount.getUserCpf();
                            User recipientUser = userService.getUserByCpf(recipientCpf)
                                    .orElseThrow(() -> new IllegalArgumentException("Account not found."));
                            String recipientName = recipientUser.getName();

                            System.out.println("Account: " + recipientName);
                            System.out.print("Do you confirm the transfer to this person? (y/n): ");
                            String confirmation = scanner.nextLine().toLowerCase();

                            if (confirmation.equals("y")) {
                                System.out.print("Enter the amount to transfer: ");
                                double amount = Double.parseDouble(scanner.nextLine());

                                System.out.print("Enter your password: ");
                                String password = scanner.nextLine();

                                if (!transactionService.isPasswordValid(account.getUserCpf(), password)) {
                                    System.out.println("Invalid password. Transfer cancelled.");
                                } else {
                                    transactionService.transfer(account, targetAccount, amount, recipientName);
                                    System.out.println("Transfer successful. New balance: " + account.getBalance());
                                }
                            } else {
                                System.out.println("Transfer cancelled.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount. Please enter a valid number.");
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    case 5 -> {
                        System.out.println("Bank Statement:");
                        if (account.getTransactionHistory().isEmpty()) {
                            System.out.println("No transactions found.");
                        } else {
                            account.getTransactionHistory().forEach(System.out::println);
                        }
                    }


                    case 0 -> {
                        System.out.println("Returning to the main menu...");
                        mainMenu(scanner);
                        return; 
                    }
                    default -> System.out.println("Invalid option! Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid option.");
            }
        }
    }

}