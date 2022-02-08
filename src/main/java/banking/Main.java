package banking;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;

public class Main {

    final static String BIN = "400000";
    static String[] cardData = new String[2];
    static boolean loggedIn = false;
    static boolean exit = false;
    static String filename;
    static String currentCard;
    public static void main(String[] args) {
        if (args[0].equals("-fileName")) {
            filename = args[1];
        }
        // SQL.createNewDatabase(filename);
        SQL.createNewTable(filename);
        start();
    }
    public static void start() {
        Scanner sc = new Scanner(System.in);
        showStartingMenu();
        do {
            int operation = -1;
            try {
                operation = sc.nextInt();
                if (operation >= 3) {
                    System.out.println("Invalid Input");
                    start();
                }
            } catch (InputMismatchException e) {

                exit = true;
                System.out.println("Invalid Input");
            }

            switch(operation) {
                case 0:
                    System.out.println("Bye!");
                    exit = true;
                    break;
                case 1:
                    createAccount();
                    break;
                case 2:
                    logIn();
                    break;
            }
        } while (!exit);
    }
    public static void showStartingMenu() {
        System.out.println(
                "1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit"
        );
    }

    public static void showLogInMenu() {
        System.out.println(
                "1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit"
        );
    }
    public static String luhnCheck(String card) {
        card += "0";
        long cardNumber = Long.parseLong(card);
        int checksum;
        int sum_of_odd = 0;
        int sum = 0;
        int sum_of_doubled = 0;
        long number;
        for (long i = 10; i < cardNumber; i*=100) {
            number = ((cardNumber / i) % 10)*2;
            if (number > 9) {
                sum += number % 10 + (number/10)%10;
            } else {
                sum_of_doubled += number;
            }
        }
        sum_of_doubled += sum;
        for (long i = 1; i < cardNumber; i*=100) {
            sum_of_odd += (cardNumber / i)%10;
        }
        int total = sum_of_odd + sum_of_doubled;

        if (total % 10 == 0) {
            return "" + cardNumber;
        } else {
            checksum = 10 - (total % 10);
            cardNumber /= 10;
            return "" + cardNumber + checksum;
        }
    }
    public static boolean luhnAlg(String card) {
        int result = 0;
        for (int i = 0; i < card.length(); i++) {
            int digit = Character.getNumericValue(card.charAt(i));
            if (i % 2 == 0) {
                int doubleDigit = digit * 2 > 9 ? digit * 2 - 9 : digit * 2;
                result += doubleDigit;
                continue;
            }
            result += digit;
        }
        return result % 10 == 0;
    }
    public static void createAccount() {
        StringBuilder cardNumber = new StringBuilder(BIN + "");
        StringBuilder PIN = new StringBuilder();
        System.out.println("Your card has been created");
        Random rand = new Random();

        for (int i = 0; i < 9; i++) {
            cardNumber.append(rand.nextInt(10));
        }

        for (int i = 0; i < 4; i++) {
            PIN.append(rand.nextInt(10));
        }
        String validCardNumber = "" + luhnCheck(cardNumber.toString());
        InsertApp app = new InsertApp();
        app.insert(validCardNumber, PIN.toString());
        cardData[0] = validCardNumber.trim();
        cardData[1] = PIN.toString().trim();
        System.out.println("Your card number: \n" + validCardNumber + "\nYour card PIN: \n" + PIN);
        if (!exit) {
            showStartingMenu();
        }

    }

    public static void logIn() {
        Scanner scanner = new Scanner(System.in);
        LogIn log = new LogIn();
        System.out.println("Enter your card number:");
        String logCard = scanner.nextLine();
        log.getLogInInfo(logCard);
        System.out.println("Enter your PIN:");
        String logPin = scanner.nextLine();
        if (log.getState(logCard, logPin)) {
            loggedIn = true;
            System.out.println("You have successfully logged in!");
            currentCard = logCard;
            loggedInMenu();
        } else {
            System.out.println("Wrong card number or PIN!");
            showStartingMenu();
        }
    }

    public static void loggedInMenu() {
        Scanner sc = new Scanner(System.in);
        showLogInMenu();
        while (loggedIn && !exit) {
            int operation = sc.nextInt();
            SQLQueries queries = new SQLQueries();
            switch(operation) {
                case 0:
                    System.out.println("Bye!");
                    exit = true;
                    break;
                case 1:
                    System.out.println("Balance: " + queries.getBalance(currentCard));
                    showLogInMenu();
                    break;
                case 2:
                    System.out.println("Enter income:");
                    int income = sc.nextInt();
                    queries.setBalance(currentCard, income);
                    System.out.println("Income was added!");
                    showLogInMenu();
                    break;
                case 3:
                    System.out.println("Transfer\n" +
                            "Enter card number:");
                    String card = sc.next();
                    int errors = 0;
                    if (!luhnAlg(card)) {
                        errors++;
                        System.out.println("Probably you made a mistake in the card number. Please try again!");
                        showLogInMenu();
                    } else if (card.equals(currentCard)) {
                        errors++;
                        System.out.println("You can't transfer money to the same account!");
                        showLogInMenu();
                    } else if (!queries.cardExists(card)) {
                        errors++;
                        System.out.println("Such a card does not exist.");
                        showLogInMenu();
                    }
                    if (errors == 0) {
                        System.out.println("Enter how much money you want to transfer:");
                        int tran = sc.nextInt();
                        if (queries.getBalance(currentCard) - tran < 0) {
                            System.out.println("Not enough money!");
                            showLogInMenu();
                        } else {
                            queries.transfer(currentCard, tran);
                            queries.transfer(card, -tran);
                            System.out.println("Success!");
                            showLogInMenu();
                        }
                    }
                    break;
                case 4:
                    queries.deleteCard(currentCard);
                    System.out.println("The account has been closed!");
                    loggedIn = false;
                    showStartingMenu();
                    break;
                case 5:
                    System.out.println("You have successfully logged out!");
                    loggedIn = false;
                    showStartingMenu();
                    break;
                default:
                    showStartingMenu();
            }
        }
    }
}
