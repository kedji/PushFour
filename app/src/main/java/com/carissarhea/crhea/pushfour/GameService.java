package com.carissarhea.crhea.pushfour;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;

public class GameService {
    private static GameService ourInstance = new GameService();

    public static GameService getInstance() {
        return ourInstance;
    }
    public int[][] boardPositions = new int[12][12];
    public int playersTurn;
    public boolean endOfGame = false;
    public String winningPlayer = "";
    int preX = 0;
    int preY = 0;

    private static int playerOneColor = Color.BLUE;
    private static int playerTwoColor = Color.RED;

    private static String PLAYER_ONE_NAME = "Blue";
    private static String PLAYER_TWO_NAME = "Red";


    private GameService() {
        resetGame();
    }

    /**
     * Wipes the game board of any preview tiles.
     */
    public void resetPreview() {
        for (int k = 1; k < 12; k++ ) {
            boardPositions[0][k] = Color.GRAY;
            boardPositions[k][0] = Color.GRAY;
            boardPositions[k][11] = Color.GRAY;
            boardPositions[11][k] = Color.GRAY;
        }
        boardPositions[0][0] = Color.WHITE;
        boardPositions[11][11] = Color.WHITE;
        boardPositions[0][11] = Color.WHITE;
        boardPositions[11][0] = Color.WHITE;

        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                if(boardPositions[i][j] != playerOneColor
                        && boardPositions[i][j] != playerTwoColor
                        && boardPositions[i][j] != Color.BLACK ) {
                    boardPositions[i][j] = Color.WHITE;
                }
            }
        }
    }

    /**
     * Sets a game piece at this position
     */
    public void setPosition(int x, int y, int color) {
        if (x < 0 || x > 11 || y < 0 || y > 11) {
            return;
        }
        boardPositions[x][y] = color;
    }

    /**
     * This method sets 'preview' pieces on the board
     * to show where a final move would land.
     */
    public void setPreview(int x, int y, int color) {
        if ((x == 0 && y > 0 && y < 11)
                || (y == 0 && x > 0 && x < 11)
                || (x == 11 && y > 0 && y < 11)
                || (y == 11 && x > 0 && x < 11)) {
            //TODO: Animate to final position

            // Place preview piece on border square
            setPosition(x, y, color);

            // Place lightened preview piece on final position
            int [] landed;
            landed = getLandingPosition(x,y);
            float factor = 0.6f;
            int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
            int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
            int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
            color = Color.argb(Color.alpha(color), red, green, blue);

            setPosition(landed[0], landed[1], color);

            this.preX = x;
            this.preY = y;
        }
    }

    /**
     *  Returns landing coordinates of a potential move.
     *  This expects the coordinates of a clickable edge tile.
     */
    public int[] getLandingPosition(int x, int y) {
        int lX = -1;
        int lY = -1;
        if (x == 0) {
            // Walk right
            for (int i = 0; i < 10; i++) {
                if ( boardPositions[i+1][y] != Color.WHITE ) {
                    lX = i;
                    lY = y;
                    break;
                }
            }
        } else if (y == 0) {
            // Walk down
            for (int i = 0; i < 10; i++) {
                if ( boardPositions[x][i+1] != Color.WHITE ) {
                    lX = x;
                    lY = i;
                    break;
                }
            }
        } else if (x == 11) {
            // Walk left
            for (int i = 11; i > 1; i--) {
                if ( boardPositions[i-1][y] != Color.WHITE ) {
                    lX = i;
                    lY = y;
                    break;
                }
            }
        } else if (y == 11) {
            // Walk up
            for (int i = 11; i > 1; i--) {
                if (boardPositions[x][i-1] != Color.WHITE ) {
                    lX = x;
                    lY = i;
                    break;
                }
            }
        }
        int[] coordinates = {lX, lY};
        return coordinates;
    }

    /**
     * Player has committed to move.
     * Input params is the clicked edge tile.
     * Finds and sets the final landing tile.
     */
    public void setMove(int x, int y, int color) {
        int[] landed = getLandingPosition(x,y);
        if (landed[0] == -1 || landed[1] == -1) {
            // Move landed out of bounds.
            return;
        }
        boardPositions[landed[0]][landed[1]] = color;
        endTurn();
        resetPreview();
    }

    /**
     * Reset the game board with newly randomized blocker tiles.
     */
    public void resetGame() {
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                boardPositions[x][y] = Color.WHITE;
            }
        }

        Random r = new Random();
        for (int j = 0; j < 10; j++) {
            boardPositions[r.nextInt(10) + 1][r.nextInt(10) + 1] = Color.BLACK;
        }

        playersTurn = playerOneColor;
        endOfGame = false;
        resetPreview();
    }

    /**
     * End the current player's turn, checking for winning conditions.
     */
    public void endTurn() {
        if (winningCondition(playersTurn)) {
            endGame(playersTurn);
        } else {
            playersTurn = (playersTurn == playerOneColor) ? playerTwoColor : playerOneColor;
        }
    }

    /**
     * True if there are 4 of the given player color in a row.
     * TODO: Check for diagonal 4 in a row
     */
    public boolean winningCondition(int color) {
        int inARow = 0;

        // Vertical
        for(int row = 1; row < 11; row++) {
            for (int col = 1; col < 11; col++) {
                if (boardPositions[row][col] == color) {
                    inARow = inARow + 1;
                } else {
                    inARow = 0;
                }
                if (inARow == 4) {
                    return true;
                }
            }
        }

        // Horizontal
        for(int row = 1; row < 11; row++) {
            for (int col = 1; col < 11; col++) {
                if (boardPositions[col][row] == color) {
                    inARow = inARow + 1;
                } else {
                    inARow = 0;
                }
                if (inARow == 4) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Sets the endOfGame flag, giving the win to the player with the given color.
     */
    public void endGame(int color) {
        winningPlayer = (color == playerOneColor) ? PLAYER_ONE_NAME : PLAYER_TWO_NAME;
        endOfGame = true;
    }

}
