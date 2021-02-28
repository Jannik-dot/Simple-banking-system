package banking;

import banking.creditcard.CreditCard;
import banking.creditcard.CreditCardGenerator;
import banking.database.DataBase;

import java.util.Objects;
import java.util.Scanner;

/**
 * Handles interactions with the user via console
 */
public class UserInterface {
    private CreditCardGenerator generator;
    private CreditCard card;
    private DataBase dataBase;
    private Scanner scanner = new Scanner(System.in);
    boolean loggedIn = false;
    boolean exit = false;

    public UserInterface(String[] args) {
        dataBase = new DataBase(args[1]);
        this.generator = new CreditCardGenerator();
        init();
    }

    private void init() {


        while (!exit) {
            menu();

            String command = scanner.nextLine();

            System.out.println();

            if (loggedIn) {
                card = dataBase.getCard(card.getCardNumber());
                switch (command) {
                    case "1":
                        System.out.println(card.getBalance());
                        break;

                    case "2":
                        addIncome();
                        break;

                    case "3":
                        transfer();
                        break;

                    case "4":
                        dataBase.deleteCard(card);
                    case "5":
                        logOut();
                        break;

                    case "0":
                        System.out.println("Bye");
                        exit = true;
                        break;

                    default:
                        System.out.println("I do not know this command!");
                }

            } else {
                switch (command) {
                    case "1":
                        createAccount();
                        break;

                    case "2":
                        logIn();
                        break;

                    case "0":
                        System.out.println("Bye");
                        exit = true;
                        break;

                    default:
                        System.out.println("I do not know this command!");
                }
            }
            System.out.println();
        }
    }

    private void menu() {
        if (loggedIn) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close Account");
            System.out.println("5. Log out");
        } else {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
        }

        System.out.println("0. Exit");
    }

    private void createAccount() {
        card = generator.create();
        dataBase.insertCardQuery(card);
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(card.getCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(card.getPassword());
    }

    private void logIn() {
        System.out.println("Enter your card number:");
        String userCardInput = scanner.nextLine();
        try {
            card = dataBase.getCard(userCardInput);
            System.out.println("Enter your PIN:");
            String userPassInput = scanner.nextLine();

            if (userCardInput.equals(card.getCardNumber()) && userPassInput.equals(card.getPassword())) {
                loggedIn = true;
                System.out.println("\nYou have successfully logged in!");
            } else {
                System.out.println("\nWrong card number or PIN!");
            }

        } catch (Exception e) {
            System.out.println("\nWrong card number or PIN!");
        }
    }

    private void addIncome() {
        System.out.println("Enter income: ");
        int amount = scanner.nextInt();
        scanner.nextLine();
        dataBase.addIncomeToCard(amount, card);
        System.out.println("Income was added!");
    }

    private void logOut() {
        loggedIn = false;
        System.out.println("You have successfully logged out!");
    }

    private void transfer() {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String receiverNumber = scanner.nextLine();
        if (!receiverNumber.endsWith(generator
                .createChecksum(receiverNumber.substring(0, receiverNumber.length() - 1)))) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (receiverNumber.equals(card.getCardNumber())) {
            System.out.println("You can't transfer money to the same account!");
        } else if (Objects.isNull(dataBase.getCard(receiverNumber).getCardNumber())) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int amount = scanner.nextInt();
            scanner.nextLine();

            if (card.getBalance() < amount) {
                System.out.println("Not enough money!");
            } else {
                dataBase.makeTransaction(card, receiverNumber, amount);
                System.out.println("Success!");
            }
        }

    }


}
