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
        try {
            //Carrega lista de palavras
            List<String> lines = Files.readAllLines(Paths.get("words.txt"));
            if (lines.isEmpty())
                throw new RuntimeException("Empty file");

            //Escolhe aleatoriamente uma palavra da lista
            Random generator = new Random();
            this.secretWord = lines.get(generator.nextInt(lines.size())).trim().toUpperCase();

            //Criação da máscara da palavra (_ _ _ _)
            this.mask = new char[secretWord.length()];
            for (int i = 0; i < secretWord.length(); i++) {
                mask[i] = '_';
            }

            //Número fixo de tentativas por jogo
            this.tries = 6;
            this.usedLetters.clear();

        } catch (IOException e) {
            throw new RuntimeException("Error reading file. " + e.getMessage());
        }
    }

    //Função para avaliar quando jogador tenta uma letra
    public synchronized boolean guessLetter(char letter) {
        if (gameOver())
            return false;

        letter = Character.toUpperCase(letter);
        boolean guessC = false;

        //Verifica se a letra já foi usada para evitar repetição
        if (usedLetters.contains(letter))
            return false;

        //Adicionamos a letra a lista de letras usadas
        usedLetters.add(letter);

        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                //Atualiza a mascara quando letra -> correta
                mask[i] = letter;
                guessC = true;
            }
        }
        if (!guessC)
            //Penalização quando letra -> errada
            tries--;

        return guessC;
    }

    //Função para avaliar quando jogador tenta uma palavra
    public synchronized boolean guessWord(String word) {
        if (gameOver())
            return false;

        word = word.toUpperCase();

        if (word.equals(secretWord)) {
            for (int i = 0; i < secretWord.length(); i++) {
                mask[i] = secretWord.charAt(i);
            }
            return true;
        } else {
            tries--;
            return false;
        }
    }

    //Função que decide se a tentativa é letra ou palavra
    // e qual função de verificar se esta correta
    public synchronized boolean guess(String wORl) {
        //Se for uma letra -> guessLetter
        if (wORl.length() == 1) {
            return guessLetter(wORl.charAt(0));
        } else {
            return guessWord(wORl);
        }
    }

    //Função para mostrar quais as letras já usadas (Certas ou Erradas)
    public synchronized String getWrongLetter() {
        return usedLetters.toString();
    }

    //Função para mostrar estado atual da palavra com máscara aplicada
    public synchronized String getMaskDisplay() {
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < mask.length; i++) {
            display.append(mask[i]).append(" ");
        }
        return display.toString().trim();
    }

    public String getSecretWord() {
        return secretWord;
    }

    public synchronized int getTriesLeft() {
        return tries;
    }

    public synchronized boolean won() {
        return secretWord.equals(new String(mask));
    }

    public synchronized boolean lost() {
        return tries <= 0;
    }

    public synchronized boolean gameOver() {
        return won() || lost();
    }

    public synchronized void wrongMove() {
        tries--;
    }

    public String getHangman() {
        return switch (tries) {
            case 6 -> """
                        _____
                       |     |
                       |     
                       |    \s
                       |   \s
                     __|_______ 
                    """ +tries+ " TRIES LEFT!";
            case 5 -> """
                       _____
                      |     |
                      |     O
                      |     
                      |   \s
                    __|_______
                    """ +tries+ " TRIES LEFT!";
            case 4 -> """
                       _____
                      |     |
                      |     O
                      |     |
                      |   \s
                    __|_______
                    """ +tries+ " TRIES LEFT!";
            case 3 -> """
                       _____
                      |     |
                      |     O
                      |    /|
                      |   \s
                    __|_______
                    """ +tries+ " TRIES LEFT!";
            case 2 -> """
                       _____
                      |     |
                      |     O
                      |    /|\\
                      |    \s
                    __|_______
                    """ +tries+ " TRIES LEFT!";
            case 1 -> """
                       _____
                      |     |
                      |     O
                      |    /|\\
                      |    / 
                    __|_______
                    """ +tries+ " TRIES LEFT!";
            case 0 -> """
                       _____
                      |     |
                      |     O
                      |    /|\\
                      |    / \\
                    __|_______
                    GAME OVER!
                    """ +tries+ " TRIES LEFT!";
            default -> "";
        };
    }
}


