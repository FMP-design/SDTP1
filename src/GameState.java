import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;

public class GameState {
    private String secretWord;
    private char[] mask;
    private int tries = 0;
    private List<Character> wrongLetters = new ArrayList<>();

    public String setupGame() {
        String fileName = "words.txt";
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            if (lines.isEmpty())
                return "Error - Empty File";

            Random generator = new Random();
            this.secretWord = lines.get(generator.nextInt(lines.size())).trim().toUpperCase();

            this.mask = new char[secretWord.length()];
            for (int i = 0; i < secretWord.length(); i++) {
                mask[i] = '_';
            }

            this.tries = 0;
            this.wrongLetters.clear();

            return getMaskDisplay();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public boolean guessLetter(char letter) {
        letter = Character.toUpperCase(letter);
        boolean guess = false;

        if(wrongLetters.contains(letter)) {
            System.out.println("You already tried that letter!");
            return false;
        }

        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                mask[i] = letter;
                guess = true;
            }
        }

        if (!guess) {
            tries++;
            wrongLetters.add(letter);
            System.out.println("Worng!! Tries left: " + tries + "/6");
        }

        if (tries >= 6) {
            System.out.println("YOUUUUUU LOSSSSSSSE - Correct word was: " + secretWord);
        }else{
            System.out.println("YOU WIN!!");
        }
        return guess;
    }

    public String getWrongLettersDisplay() {
        StringBuilder sb = new StringBuilder();
        for (char c : wrongLetters) {
            sb.append(c).append(" ");
        }
        return sb.toString().trim();
    }

    public int getTries() {
        return tries;
    }

    public String getMaskDisplay() {
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < mask.length; i++) {
            display.append(mask[i]).append(" ");
        }
        return display.toString().trim();
    }

    public String getSecretWord() {
        return secretWord;
    }
}


