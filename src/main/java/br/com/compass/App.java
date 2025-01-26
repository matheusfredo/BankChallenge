package br.com.compass;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import br.com.compass.models.Account;
import br.com.compass.models.User;
import br.com.compass.services.AccountService;
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
                    bankMenu(scanner);
                    return;
                case 2:
                	accountOpening(scanner);
                    System.out.println("Account Opening.");
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

        System.out.println("=== Account Opening ===");

        String name = "", birthDate = "", cpf = "", phone = "";
        boolean confirmed = false;
        scanner.nextLine();

        while (!confirmed) {
        	while (true) {
        	    System.out.print("Enter Name (2-100 characters, no numbers): ");
        	    name = scanner.nextLine().trim(); 

        	    if (name.isEmpty()) {
        	        System.out.println("Name is required. Please enter a valid name.");
        	        continue;
        	    }

        	    if (!name.matches("^[a-zA-Z\\s]+$")) {
        	        System.out.println("Name must not contain numbers. Please enter a valid name.");
        	        continue; 
        	    }

        	    if (name.length() < 2 || name.length() > 100) {
        	        System.out.println("Name must be between 2 and 100 characters.");
        	        continue; 
        	    }
        	    
        	    break;
        	}

            if (birthDate.isEmpty()) {
                while (true) {
                    System.out.print("Enter Birth Date (dd-mm-yyyy): ");
                    birthDate = scanner.nextLine();
                    try {
                        LocalDate date = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        LocalDate today = LocalDate.now();
                        if (date.isBefore(LocalDate.of(1900, 1, 1)) || date.isAfter(today.minusYears(18))) {
                            System.out.println("You must be at least 18 years old to open an account. Returning to the main menu...");
                            return; 
                        }
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Please enter the date as dd-mm-yyyy.");
                    }
                }
            }

            if (cpf.isEmpty()) {
                while (true) {
                    System.out.print("Enter CPF (numbers only): ");
                    cpf = scanner.nextLine().replaceAll("[^0-9]", "");
                    if (isValidCpf(cpf)) {
                        break;
                    } else {
                        System.out.println("Invalid CPF. Please enter a valid CPF.");
                    }
                }
            }

            if (phone.isEmpty()) {
                while (true) {
                    System.out.print("Enter Phone (DDD + 9 digits): ");
                    phone = scanner.nextLine().replaceAll("[^0-9]", "");
                    if (phone.length() == 11) {
                        break; 
                    } else {
                        System.out.println("Phone number must contain exactly 11 digits.");
                    }
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
                    case 1:
                        name = ""; 
                        break;
                    case 2:
                        birthDate = ""; 
                        break;
                    case 3:
                        cpf = ""; 
                        break;
                    case 4:
                        phone = ""; 
                        break;
                    default:
                        System.out.println("Invalid option. Returning to review...");
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

        System.out.println("Choose Account Type:");
        System.out.println("1. Checking Account");
        System.out.println("2. Salary Account");
        System.out.println("3. Savings Account");
        System.out.print("Enter option (1-3): ");
        int accountOption = scanner.nextInt();
        scanner.nextLine(); 

        String accountType;
        switch (accountOption) {
            case 1:
                accountType = "Checking Account";
                break;
            case 2:
                accountType = "Salary Account";
                break;
            case 3:
                accountType = "Savings Account";
                break;
            default:
                System.out.println("Invalid option. Defaulting to Checking Account.");
                accountType = "Checking Account";
        }

        Account account = accountService.openAccount(user.getId(), accountType);

        System.out.println("\nAccount created successfully!");
        System.out.println("User ID: " + user.getId());
        System.out.println("Account ID: " + account.getId());
        System.out.println("Account Type: " + account.getAccountType());
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


    
    public static void bankMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    // ToDo...
                    System.out.println("Deposit.");
                    break;
                case 2:
                    // ToDo...
                    System.out.println("Withdraw.");
                    break;
                case 3:
                    // ToDo...
                    System.out.println("Check Balance.");
                    break;
                case 4:
                    // ToDo...
                    System.out.println("Transfer.");
                    break;
                case 5:
                    // ToDo...
                    System.out.println("Bank Statement.");
                    break;
                case 0:
                    // ToDo...
                    System.out.println("Exiting...");
                    running = false;
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
    public static void registerUser(Scanner scanner) {
        UserService userService = new UserService();

        System.out.println("=== Register User ===");
        System.out.print("Enter Name: ");
        String name = scanner.next();
        System.out.print("Enter CPF: ");
        String cpf = scanner.next();
        System.out.print("Enter Birth Date (yyyy-mm-dd): ");
        String birthDate = scanner.next();
        System.out.print("Enter Phone: ");
        String phone = scanner.next();

        User user = new User(null, name, cpf, java.time.LocalDate.parse(birthDate), phone);

        userService.registerUser(user);

        System.out.println("User registered successfully! User ID: " + user.getId());
    }

    
}
