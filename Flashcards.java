package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    private static final Logger logger = new Logger();
    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Statistic stats = new Statistic();
    private static final Map<String, String> flashCards = new HashMap<>();
    private static final Map<String, String> reverseFlashCards = new HashMap<>();
    private static boolean display = true;
    private static String importPath = "";
    private static String exportPath = "";

    public static void main(String[] args) {
        for (int i = 0; i < args.length;) {
            if ("-import".toLowerCase().equals(args[i])) {
                importPath = args[i + 1];
                i += 2;
            } else if ("-export".toLowerCase().equals(args[i])) {
                exportPath = args[i + 1];
                i += 2;
            } else {
                i += 1;
            }
        }
        if (!importPath.equals("")){
            importCards(importPath);
        }
        while (display) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            logger.write("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String option = scanner.nextLine().toLowerCase();
            logger.write("> " + option);
            switch (option) {
                case "add":
                    addCards();
                    break;
                case "remove":
                    removeCards();
                    break;
                case "import":
                    System.out.println("File name:");
                    logger.write("File name:");
                    String pathToImport = scanner.nextLine();
                    logger.write("> " + pathToImport);
                    importCards(pathToImport);
                    break;
                case "export":
                    System.out.println("File name:");
                    logger.write("File name:");
                    String pathToExport = scanner.nextLine();
                    logger.write("> " + pathToExport);
                    exportCards(pathToExport);
                    break;
                case "ask":
                    askQuestions();
                    break;
                case "exit":
                    exit();
                    break;
                case "log":
                    exportLogs();
                    break;
                case "hardest card":
                    getHardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                default:
                    logger.write("Not found.");
                    break;
            }
            logger.writeReturn("UNIX");
        }
    }

    public static void addCards() {
        System.out.println("The card:");
        logger.write("The card:");
        String card = scanner.nextLine();
        logger.write("> " + card);
        if (flashCards.containsKey(card)) {
            System.out.println("The card \"" + card + "\" already exists.");
            logger.write("The card \"" + card + "\" already exists.");
            return;
        }
        System.out.println("The definition of the card:");
        logger.write("The definition of the card:");
        String definition = scanner.nextLine();
        logger.write("> " + definition);
        if (reverseFlashCards.containsKey(definition)) {
            System.out.println("The definition \"" + definition + "\" already exists.");
            logger.write("The definition \"" + definition + "\" already exists.");
            return;
        }
        flashCards.put(card, definition);
        reverseFlashCards.put(definition, card);
        stats.loadCards(card);
        System.out.println("The pair (\"" + card + "\":\"" + definition + "\") has been added.");
        logger.write("The pair (\"" + card + "\":\"" + definition + "\") has been added.");
    }

    public static void removeCards() {
        System.out.println("The card:");
        logger.write("The card:");
        String card = scanner.nextLine();
        logger.write("> " + card);
        if (!flashCards.containsKey(card)) {
            System.out.println("Can't remove \"" + card + "\": there is no such card.");
            logger.write("Can't remove \"" + card + "\": there is no such card.");
            return;
        }
        String definition = flashCards.get(card);
        flashCards.remove(card);
        reverseFlashCards.remove(definition);
        stats.remove(card);
        System.out.println("Card \"" + card + "\" has been removed.");
        logger.write("Card \"" + card + "\" has been removed.");
    }

    public static void importCards(String pathToFile) {
        File file = new File(pathToFile);
        try (Scanner reader = new Scanner(file)) {
            int cardNum = 0;
            while (reader.hasNext()) {
                String[] line = reader.nextLine().split("\\s+");
                if (flashCards.containsKey(line[0])) {
                    reverseFlashCards.remove(flashCards.get(line[0]));
                }
                flashCards.put(line[0], line[1]);
                reverseFlashCards.put(line[1], line[0]);
                stats.loadCards(line[0], Integer.parseInt(line[2]));
                cardNum++;
            }
            System.out.println(cardNum + " cards have been loaded.");
            logger.write(cardNum + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            logger.write("File not found.");
        }
    }

    public static void exportCards(String pathToFile) {
        try (PrintWriter writer = new PrintWriter(pathToFile)) {
            for (String key : flashCards.keySet()) {
                writer.println(key + "    " + flashCards.get(key) + "    " + stats.getWrongCounts(key));
            }
            System.out.println(flashCards.size() + " cards have been saved.");
            logger.write(flashCards.size() + " cards have been saved.");
        } catch (IOException e) {
            System.out.println("0 cards have been saved.");
            logger.write("0 cards have been saved.");
        }
    }

    public static void askQuestions() {
        System.out.println("How many times to ask?");
        logger.write("How many times to ask?");
        int count = Integer.parseInt(scanner.nextLine());
        logger.write("> " + count);
        String[] keys = flashCards.keySet().toArray(new String[0]);
        for (int i = 0; i < count; i++) {
            String randomCard = keys[random.nextInt(keys.length)];
            System.out.println("Print the definition of \"" + randomCard + "\":");
            logger.write("Print the definition of \"" + randomCard + "\":");
            String answer = scanner.nextLine();
            logger.write("> " + answer);
            if (answer.equals(flashCards.get(randomCard))) {
                System.out.println("Correct answer.");
                logger.write("Correct answer.");
            } else {
                stats.wrongAnswer(randomCard);
                if (reverseFlashCards.containsKey(answer)) {
                    System.out.println("Wrong answer. The correct one is \"" + flashCards.get(randomCard) + "\", you've just written the definition of \"" + reverseFlashCards.get(answer) + "\".");
                    logger.write("Wrong answer. The correct one is \"" + flashCards.get(randomCard) + "\", you've just written the definition of \"" + reverseFlashCards.get(answer) + "\".");
                } else {
                    System.out.println("Wrong answer. The correct one is \"" + flashCards.get(randomCard) + "\".");
                    logger.write("Wrong answer. The correct one is \"" + flashCards.get(randomCard) + "\".");
                }
            }
        }
    }

    public static void exit() {
        display = false;
        System.out.println("Bye bye!");
        logger.write("Bye bye!");
        if (!exportPath.equals("")) {
            exportCards(exportPath);
        }
    }

    public static void exportLogs() {
        System.out.println("File name:");
        logger.write("File name:");
        String pathToFile = scanner.nextLine();
        logger.write("> " + pathToFile);
        logger.exportToFile(pathToFile);
    }

    public static void getHardestCard() {
        ArrayList<String> hardestCards = stats.getHardestCard();
        if (hardestCards.size() == 1) {
            System.out.println("There are no cards with errors.");
            logger.write("There are no cards with errors.");
        } else {
            int wrongCounts = Integer.parseInt(hardestCards.get(hardestCards.size()-1));
            hardestCards.remove(hardestCards.size()-1);
            StringBuilder sb = new StringBuilder("The hardest card ");
            if (hardestCards.size() == 1) {
                sb.append("is ");
            } else {
                sb.append("are ");
            }
            hardestCards.forEach(card -> sb.append("\"").append(card).append("\", "));
            sb.replace(sb.length()-2, sb.length(), ". ");
            sb.append("You have ").append(wrongCounts).append(" errors answering them");
            System.out.println(sb.toString());
            logger.write(sb.toString());
        }
    }

    public static void resetStats() {
        stats.resetStats();
        System.out.println("Card Statistics has been reset.");
        logger.write("Card Statistics has been reset.");
    }
}

class Logger {
    private final ArrayList<String> log = new ArrayList<>();

    public void write(String str) {
        log.add(str);
    }

    public void writeReturn(String systemType) {
        if (systemType.equals("UNIX")) {
            log.add("\n");
        } else {
            log.add("\r\n");
        }
    }

    public void exportToFile(String pathToFile) {
        try (PrintWriter writer = new PrintWriter(pathToFile)) {
            for (String line : log) {
                writer.println(line);
            }
            System.out.println("The log has been saved.");
            log.add("The log has been saved.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            log.add("File not found.");
        }
    }
}

class Statistic {
    private static final Map<String, Integer> stats = new HashMap<>();

    public void loadCards(String card) {
        if (stats.containsKey(card)) {
            return;
        }
        stats.put(card, 0);
    }

    public void loadCards(String card, Integer wrongCounts) {
        stats.put(card, wrongCounts);
    }

    public void wrongAnswer(String card) {
        Integer cardStat = stats.get(card);
        cardStat += 1;
        stats.put(card, cardStat);
    }

    public ArrayList<String> getHardestCard() {
        ArrayList<String> hardestCards = new ArrayList<>();
        int maxWrongCounts = 1;
        for (String card : stats.keySet()) {
            int cardWrongCounts = stats.get(card);
            if (cardWrongCounts > maxWrongCounts) {
                maxWrongCounts = cardWrongCounts;
                hardestCards.clear();
                hardestCards.add(card);
            } else if (cardWrongCounts == maxWrongCounts) {
                hardestCards.add(card);
            }
        }
        hardestCards.add(String.valueOf(maxWrongCounts));
        return hardestCards;
    }

    public int getWrongCounts(String card) {
        return stats.get(card);
    }

    public void remove(String card) {
        stats.remove(card);
    }

    public void resetStats() {
        stats.replaceAll((c, v) -> 0);
    }
}
