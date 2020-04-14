package machine;

import java.util.Scanner;

public class CoffeeMachine {
    final static Scanner scanner = new Scanner(System.in);
    private static int water = 400;
    private static int milk = 540;
    private static int coffeeBeans = 120;
    private static int cups = 9;
    private static int money = 550;

    public static void main(String[] args) {
        boolean switchOn = true;
        while (switchOn) {
            System.out.println("Write action (buy, fill, take, remaining, exit):");
            String action = scanner.next();
            System.out.println();
            switch (action) {
                case "buy":
                    buy();
                    break;
                case "fill":
                    fill();
                    break;
                case "take":
                    take();
                    break;
                case "remaining":
                    status();
                    break;
                case "exit":
                    switchOn = false;
                    break;
                default:
                    break;
            }
            System.out.println();
        }
    }

    public static void buy() {
        System.out.println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:");
        String coffeeType = scanner.next();
        switch (coffeeType) {
            case "1":
                // espresso
                makeCoffee(250, 0, 16, 4);
                break;
            case "2":
                // latte
                makeCoffee(350, 75, 20, 7);
                break;
            case "3":
                // cappuccino
                makeCoffee(200, 100, 12, 6);
                break;
            case "back":
                return;
            default:
                break;
        }
        System.out.println();
    }

    public static void fill() {
        System.out.println("Write how many ml of water do you want to add:");
        int water_ = scanner.nextInt();
        System.out.println("Write how many ml of milk do you want to add:");
        int milk_ = scanner.nextInt();
        System.out.println("Write how many grams of coffee beans do you want to add:");
        int coffeeBeans_ = scanner.nextInt();
        System.out.println("Write how many disposable cups of coffee do you want to add:");
        int cups_ = scanner.nextInt();

        addWater(water_);
        addMilk(milk_);
        addCoffeeBeans(coffeeBeans_);
        addCups(cups_);
    }

    public static void take() {
        System.out.println("I gave you $" + money);
        giveMoney();
    }

    private static void status() {
        System.out.println("The coffee machine has:");
        System.out.println(water + " of water");
        System.out.println(milk + " of milk");
        System.out.println(coffeeBeans + " coffee beans");
        System.out.println(cups + " of disposable cups");
        System.out.println(money + " of money");
    }

    private static void makeCoffee(int water_, int milk_, int coffeeBeans_, int money_) {
        water -= water_;
        milk -= milk_;
        coffeeBeans -= coffeeBeans_;
        cups -= 1;
        if(!checkStatus()) {
            addWater(water_);
            addMilk(milk_);
            addCoffeeBeans(coffeeBeans_);
            addCups(1);
        } else {
            System.out.println("I have enough resources, making you a coffee!");
            money += money_;
        }
    }

    private static boolean checkStatus() {
        if (water < 0) {
            System.out.println("Sorry, not enough water");
            return false;
        }
        if (milk < 0) {
            System.out.println("Sorry, not enough milk");
            return false;
        }
        if (coffeeBeans < 0) {
            System.out.println("Sorry, not enough coffee beans");
            return false;
        }
        if (cups < 0) {
            System.out.println("Sorry, not enough disposable cups");
            return false;
        }
        return true;
    }

    private static void addWater(int water_) {
        water += water_;
    }

    private static void addMilk(int milk_) {
        milk += milk_;
    }

    private static void addCoffeeBeans(int coffeeBeans_) {
        coffeeBeans += coffeeBeans_;
    }

    private static void addCups(int cups_) {
        cups += cups_;
    }

    private static void giveMoney() {
        money -= money;
    }
}
