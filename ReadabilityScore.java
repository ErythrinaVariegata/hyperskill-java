package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Analyser analyser = new Analyser();

    public static void main(String[] args) {
        String fileName = args[0];
        analyser.analyse(Objects.requireNonNull(sentenceReader(fileName)));
        System.out.println("Words: " + analyser.getWordNum());
        System.out.println("Sentences: " + analyser.getSentenceNum());
        System.out.println("Characters: " + analyser.getCharacterNum());
        System.out.println("Syllables: " + analyser.getSyllableNum());
        System.out.println("Polysyllables: " + analyser.getPolysyllableNum());
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String option = scanner.nextLine();
        System.out.println();
        displayScore(option);
    }

    private static String[] sentenceReader(String fileName) {
        File file = new File(fileName);
        try (Scanner reader = new Scanner(file)) {
            StringBuilder sb = new StringBuilder();
            while (reader.hasNext()) {
                sb.append(reader.nextLine().toLowerCase());
            }
            if ('.' != sb.charAt(sb.length() - 1)) {
                analyser.setEndWithDot(false);
            }
            return sb.toString().split("[\\.\\?!]\\s*");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        return null;
    }

    private static void displayScore(String option) {
        switch (option) {
            case "ARI":
                double scoreARI = analyser.getScoreARI();
                System.out.printf("Automated Readability Index: %.2f ", scoreARI);
                System.out.println("(about " + getAge(scoreARI) + " year olds).");
                break;
            case "FK":
                double scoreFK = analyser.getScoreFK();
                System.out.printf("Flesch-Kincaid readability tests: %.2f ", scoreFK);
                System.out.println("(about " + getAge(scoreFK) + " year olds).");
                break;
            case "SMOG":
                double scoreSMOG = analyser.getScoreSMOG();
                System.out.printf("Simple Measure of Gobbledgook: %.2f ", scoreSMOG);
                System.out.println("(about " + getAge(scoreSMOG) + " year olds).");
                break;
            case "CL":
                double scoreCL = analyser.getScoreCL();
                System.out.printf("Coleman-Liau index: %.2f ", scoreCL);
                System.out.println("(about " + getAge(scoreCL) + " year olds).");
                break;
            case "all":
                double scoreA = analyser.getScoreARI();
                int ageA = getAge(scoreA);
                System.out.printf("Automated Readability Index: %.2f ", scoreA);
                System.out.println("(about " + ageA + " year olds).");

                double scoreF = analyser.getScoreFK();
                int ageF = getAge(scoreF);
                System.out.printf("Flesch–Kincaid readability tests: %.2f ", scoreF);
                System.out.println("(about " + ageF + " year olds).");

                double scoreS = analyser.getScoreSMOG();
                int ageS = getAge(scoreS);
                System.out.printf("Simple Measure of Gobbledygook: %.2f ", scoreS);
                System.out.println("(about " + ageS + " year olds).");

                double scoreC = analyser.getScoreCL();
                int ageC = getAge(scoreC);
                System.out.printf("Coleman–Liau index: %.2f ", scoreC);
                System.out.println("(about " + ageC + " year olds).");

                double avgAge = (ageA + ageF + ageS + ageC) / 4.0;
                System.out.println();
                System.out.printf("This text should be understood in average by %.2f year olds.\n", avgAge);
                break;
        }
    }

    private static int getAge(double indexScore) {
        int score = Integer.parseInt(String.valueOf(Math.round(indexScore)));
        return Objects.requireNonNull(ReadabilityLevel.findByScore(score)).age;
    }
}

class Analyser {
    private int characterNum = 0;
    private int wordNum = 0;
    private int sentenceNum = 0;
    private boolean endWithDot = true;
    private int syllableNum = 0;
    private int polysyllableNum = 0;
    private double scoreARI = 0;
    private double scoreFK = 0;
    private double scoreSMOG = 0;
    private double scoreCL = 0;

    public void analyse(String[] sentences) {
        this.sentenceNum = sentences.length;
        for (String sentence : sentences) {
            String[] sentenceWords = sentence.split("\\s+");
            for (String word : sentenceWords) {
                this.characterNum += word.length();
                int currentSyllables = getSyllablesOfOneWord(word);
                currentSyllables = currentSyllables > 0 ? currentSyllables : 1;
                this.syllableNum += currentSyllables;
                if (currentSyllables > 2) {
                    this.polysyllableNum += 1;
                }
            }
            this.wordNum += sentenceWords.length;
            if ("".equals(sentenceWords[0])) {
                this.wordNum -= 1;
            }
        }
        this.characterNum += this.sentenceNum;
        if (!this.endWithDot) {
            this.characterNum -= 1;
        }
        calARI();
        calFK();
        calSMOG();
        calCL();
    }

    private int getSyllablesOfOneWord(String word) {
        String vowels = "aeiouy";

        int syllables = 0;
        boolean flag = false;
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);

            if (i == word.length() - 1 && word.charAt(i) == 'e') {
                break; // if last char is 'e' it is not counted
            }
            // the below if else ensure more than one consecutive vowels are counted as one syllable
            if (vowels.indexOf(currentChar) >= 0) {
                if (!flag) {
                    syllables++;
                }
                flag = true;
            } else {
                flag = false;
            }
        }
        return syllables;
    }

    private void calARI() {
        this.scoreARI =  4.71 * characterNum / wordNum + 0.5 * wordNum / sentenceNum - 21.43;
    }

    private void calFK() {
        this.scoreFK = 0.39 * wordNum / sentenceNum + 11.8 * syllableNum / wordNum - 15.59;
    }

    private void calSMOG() {
        this.scoreSMOG = 1.043 * Math.sqrt((double) polysyllableNum * 30 / sentenceNum) + 3.1291;
    }

    private void calCL() {
        this.scoreCL = (0.0588 * characterNum  - 0.296 * sentenceNum) * 100 / wordNum - 15.8;
    }

    public int getWordNum() {
        return wordNum;
    }

    public int getSentenceNum() {
        return sentenceNum;
    }

    public int getCharacterNum() {
        return characterNum;
    }

    public int getSyllableNum() {
        return syllableNum;
    }

    public int getPolysyllableNum() {
        return polysyllableNum;
    }

    public void setEndWithDot(boolean dot) {
        endWithDot = dot;
    }

    public double getScoreARI() {
        return scoreARI;
    }

    public double getScoreFK() {
        return scoreFK;
    }

    public double getScoreSMOG() {
        return scoreSMOG;
    }

    public double getScoreCL() {
        return scoreCL;
    }
}

enum ReadabilityLevel {
    KINDERGARTEN(1, 6),
    FIRST_SECOND(2, 7),
    THIRD(3, 9),
    FOURTH(4, 10),
    FIFTH(5, 11),
    SIXTH(6, 12),
    SEVENTH(7, 13),
    EIGHTH(8, 14),
    NINTH(9, 15),
    TENTH(10, 16),
    ELEVENTH(11, 17),
    TWELFTH(12, 18),
    COLLEGE(13, 24),
    PROFESSOR(14, -1);

    int score;
    int age;

    ReadabilityLevel(int score, int age) {
        this.score = score;
        this.age = age;
    }

    public static ReadabilityLevel findByScore(int score) {
        for (ReadabilityLevel value: values()) {
            if (score == value.score) {
                return value;
            }
        }
        return null;
    }
}