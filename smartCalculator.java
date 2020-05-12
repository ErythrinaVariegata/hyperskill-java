package calculator;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final HashMap<String, String> varDict = new HashMap<>();
    private static final Deque<String> symStack = new ArrayDeque<>();
    private static final Deque<String> numStack = new ArrayDeque<>();

    public static void main(String[] args) {

        // put your code here
        boolean foward = true;
        while (foward) {
            String userInput = scanner.nextLine();
            if ("".equals(userInput)) {
                continue;
            }
            if ('/' == userInput.charAt(0)) {
                switch (userInput) {
                    case "/exit":
                        System.out.println("Bye!");
                        foward = false;
                        break;
                    case "/help":
                        System.out.println("The program support parenthesis now! But still strict with spaces...");
                        break;
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            } else {
                String mode = switchMode(userInput);
                switch (mode) {
                    case "var":
                        storeVariable(userInput);
                        break;
                    case "cal":
                        userInput = userInput.replaceAll("\\("," ( ");
                        userInput = userInput.replaceAll("\\)", " ) ");
                        String result = calculate(reversePolishNotation(parseInput(userInput)));
                        System.out.println(result);
                        break;
                }
            }
        }
    }

    private static String switchMode(String userInput) {
        if (userInput.matches("^[^=]+=[^=]+$")) {
            return "var";
        } else if (userInput.matches(".*=.*")) {
            System.out.println("Invalid assignment");
            return "err";
        } else {
            return "cal";
        }
    }

    private static void storeVariable(String userInput) {
        String[] nameWithVal = userInput.split("\\s*=\\s*");
        if (nameWithVal[0].matches("[A-Za-z]+")) {
            if (nameWithVal.length == 2 && nameWithVal[1].matches("(-?[0-9][0-9]*)|([A-Za-z]+)")) {
                if (nameWithVal[1].matches("[A-Za-z]+") && varDict.containsKey(nameWithVal[1])) {
                    varDict.put(nameWithVal[0], varDict.get(nameWithVal[1]));
                } else if (nameWithVal[1].matches("-?[0-9][0-9]*")) {
                    varDict.put(nameWithVal[0], nameWithVal[1]);
                } else {
                    System.out.println("Unknown variable");
                }
            } else {
                System.out.println("Invalid assignment");
            }
        } else {
            System.out.println("Invalid identifier");
        }
    }

    private static String reversePolishNotation(String userInput) {
        if ("Invalid expression".equals(userInput)) {
            return "Invalid expression";
        }
        String[] strings = userInput.replaceAll("\\s+", " ").split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            if (s.matches("(-?([1-9][0-9]*)|([a-zA-Z]*))")) {
                sb.append(s).append(" ");
            } else {
                switch (s) {
                    case "(":
                        symStack.offerFirst(s);
                        break;
                    case ")":
                        while (!"(".equals(symStack.peekFirst())) {
                            sb.append(symStack.pollFirst()).append(" ");
                        }
                        symStack.pollFirst();
                        break;
                    case "+":
                    case "-":
                        while (!symStack.isEmpty()) {
                            String symbol = symStack.peekFirst();
                            if ("(".equals(symbol)) {
                                break;
                            } else {
                                sb.append(symStack.pollFirst()).append(" ");
                            }
                        }
                        symStack.offerFirst(s);
                        break;
                    case "*":
                    case "/":
                        while (!symStack.isEmpty()) {
                            String symbol = symStack.peekFirst();
                            if ("*".equals(symbol) || "/".equals(symbol) || "^".equals(symbol)) {
                                sb.append(symStack.pollFirst()).append(" ");
                            } else {
                                break;
                            }
                        }
                        symStack.offerFirst(s);
                        break;
                    case "^":
                        while (!symStack.isEmpty()) {
                            String symbol = symStack.peekFirst();
                            if ("^".equals(symbol)) {
                                sb.append(symStack.pollFirst()).append(" ");
                            } else {
                                break;
                            }
                        }
                        symStack.offerFirst(s);
                        break;
                }
            }
        }
        while (!symStack.isEmpty()) {
            sb.append(symStack.pollFirst()).append(" ");
        }
        return sb.toString();
    }

    private static void refreshStack() {
        symStack.clear();
        numStack.clear();
    }

    private static boolean symbolCheck(String userInput) {
        Pattern validPattern = Pattern.compile("(\\s*[0-9a-zA-Z\\+\\-\\*\\/\\^\\(\\)]\\s*)*[0-9a-zA-Z\\)]\\s*$");
        Matcher validMatcher = validPattern.matcher(userInput);
        if (validMatcher.matches() && !userInput.matches(".*(\\s*[\\*\\/\\^]\\s*){2,}.*")) {
            return !userInput.matches(".*\\([^)]*") && !userInput.matches("[^(]*\\).*");
        }
        return false;
    }

    private static String parseInput(String userInput) {
        if (userInput.matches("^[a-zA-Z]+$")) {
            return userInput;
        }
        if (symbolCheck(userInput)) {
            Pattern pattern = Pattern.compile("(\\+-)|(-\\+)|(--)|(\\+\\+)");
            Matcher matcher = pattern.matcher(userInput);
            while (matcher.find()) {
                userInput = userInput.replaceAll("(\\++)|(--)", "+");
                userInput = userInput.replaceAll("(\\+-)|(-\\+)", "-");
                matcher = pattern.matcher(userInput);
            }
        } else {
            userInput = "Invalid expression";
        }
        return userInput;
    }

    private static String parseNum(String num) {
        return num.matches("-?[1-9][0-9]*") ? num : varDict.containsKey(num) ?
                String.valueOf(varDict.get(num)) : "Unknown variable";
    }

    private static String calculate(String userInput) {
        if ("Invalid expression".equals(userInput)) {
            return userInput;
        }
        refreshStack();
        String[] splitUserInput = userInput.split(" ");
        for (String s : splitUserInput) {
            if (s.matches("-?[1-9][0-9]*")) {
                numStack.offerFirst(s);
            } else if (s.matches("^[a-zA-Z]+$")) {
                numStack.offerFirst(parseNum(s));
            } else {
                if (!numStack.isEmpty()) {
                    String b = parseNum(numStack.pollFirst());
                    String a = parseNum(numStack.pollFirst());
                    if ("Unknown variable".equals(a) || "Unknown variable".equals(b)) {
                        return "Unknown variable";
                    }
                    if (a.length() > 8 || b.length() > 8) {
                        BigDecimal numA = new BigDecimal(a);
                        BigDecimal numB = new BigDecimal(b);
                        switch (s) {
                            case "+":
                                numStack.offerFirst(numA.add(numB).toPlainString());
                                break;
                            case "-":
                                numStack.offerFirst(numA.subtract(numB).toPlainString());
                                break;
                            case "*":
                                numStack.offerFirst(numA.multiply(numB).toPlainString());
                                break;
                            case "/":
                                numStack.offerFirst(numA.divide(numB).toPlainString());
                                break;
                            default:
                                return "Big numbers only support + - * /";
                        }
                    } else {
                        switch (s) {
                            case "+":
                                numStack.offerFirst(String.valueOf(Long.parseLong(a)+ Long.parseLong(b)));
                                break;
                            case "-":
                                numStack.offerFirst(String.valueOf(Long.parseLong(a) - Long.parseLong(b)));
                                break;
                            case "*":
                                numStack.offerFirst(String.valueOf(Long.parseLong(a) * Long.parseLong(b)));
                                break;
                            case "/":
                                numStack.offerFirst(String.valueOf(Long.parseLong(a) / Long.parseLong(b)));
                                break;
                            case "^":
                                numStack.offerFirst(String.valueOf(Math.round(Math.pow(Double.parseDouble(a), Double.parseDouble(b)))));
                                break;
                        }
                    }
                }

            }
        }
        if (symStack.isEmpty()) {
            return numStack.peekFirst();
        } else {
            return "Invalid expression";
        }
    }
}
