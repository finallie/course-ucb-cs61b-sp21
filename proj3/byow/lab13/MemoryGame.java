package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;

public class MemoryGame {
    /**
     * The width of the window of this game.
     */
    private int width;
    /**
     * The height of the window of this game.
     */
    private int height;
    /**
     * The current round the user is on.
     */
    private int round;
    /**
     * The Random object used to randomly generate Strings.
     */
    private Random rand;
    /**
     * Whether or not the game is over.
     */
    private boolean gameOver;
    /**
     * Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'.
     */
    private boolean playerTurn;
    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);

        //TODO: Initialize random number generator
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder stringBuilder = new StringBuilder();
        int length = CHARACTERS.length;
        for (int i = 0; i < n; i++) {
            stringBuilder.append(CHARACTERS[rand.nextInt(length)]);
        }
        return stringBuilder.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.black);
        //TODO: Take the string and display it in the center of the screen
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(this.width / 2.0, this.height / 2.0, s);
        //TODO: If game is not over, display relevant game information at the top of the screen
        if (!gameOver) {
            StdDraw.setFont(new Font("Monaco", Font.PLAIN, 20));
            StdDraw.line(0, height - 2, width, height - 2);
            StdDraw.textLeft(0, height - 1, "Round: " + round);
            StdDraw.text(width / 2.0, height - 1, playerTurn ? "Type!" : "Watch!");
            StdDraw.textRight(width, height - 1, ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(String.valueOf(letters.charAt(i)));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        //TODO: Read n letters of player input
        for (int i = 0; i < n; i++) {
            while (!StdDraw.hasNextKeyTyped()) {
                StdDraw.pause(100);
            }
            char c = StdDraw.nextKeyTyped();
            stringBuilder.append(c);
            drawFrame(stringBuilder.toString());
        }
        StdDraw.pause(500);
        return stringBuilder.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        //TODO: Establish Engine loop
        while (true) {
            drawFrame("Round: " + round);
            String s = generateRandomString(round);
            playerTurn = false;
            flashSequence(s);
            playerTurn = true;
            String answer = solicitNCharsInput(round);
            if (s.equals(answer)) {
                round++;
            } else {
                break;
            }
        }
        gameOver = true;
        drawFrame("Game Over! You made it to round:" + round);
    }


}
