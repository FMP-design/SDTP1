import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;

public class GameState {
    private String secretWord;
    private char[] mask;
    private int tries;
    private List<Character> usedLetters = new ArrayList<>();

    public void setupGame() {
        String fileName = "words.txt";
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            if (lines.isEmpty())
                throw new RuntimeException("Empty file");

            Random generator = new Random();
            this.secretWord = lines.get(generator.nextInt(lines.size())).trim().toUpperCase();

            this.mask = new char[secretWord.length()];
            for (int i = 0; i < secretWord.length(); i++) {
                mask[i] = '_';
            }

            this.tries = 6;
            this.usedLetters.clear();

        } catch (IOException e) {
            throw new RuntimeException("Error reading file. " +e.getMessage());
        }
    }

    public boolean guessLetter(char letter) {
        if(gameOver())
            return false;

        letter = Character.toUpperCase(letter);
        boolean guessC = false;

        if(usedLetters.contains(letter))
            return false;

        usedLetters.add(letter);

        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                mask[i] = letter;
                guessC = true;
            }
        }
        if (!guessC)
            tries--;

        return guessC;
    }

    public boolean guessWord(String word) {
        if(gameOver())
            return false;

        word = word.toUpperCase();

        if(word.equals(secretWord)){
            for(int i = 0; i < secretWord.length(); i++){
                mask[i] = secretWord.charAt(i);
            }
            return true;
        }else {
            tries--;
            return false;
        }
    }

    public String getWrongLetter() {
        StringBuilder sb = new StringBuilder();
        for (char c : usedLetters) {
            sb.append(c).append(" ");
        }
        return sb.toString().trim();
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

    public int getTriesLeft() {
        return tries;
    }

    public boolean won(){
        return secretWord.equals(new String(mask));
    }

    public boolean lost(){
        return tries <= 0;
    }

    public boolean gameOver(){
        return won() || lost();
    }

}


