package com.carissarhea.crhea.pushfour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {
    private int tileSize;
    private int offset;
    private GameActivity gameActivity;   // our owner

    public void setOwner(GameActivity ga) { this.gameActivity = ga; }

    public BoardView(Context context) {
        super(context);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        tileSize = (canvas.getWidth() / 13);
        int color;
        offset = (tileSize / 2);

        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                color = GameService.getInstance().boardPositions[x][y];
                drawTile(canvas, x*tileSize + offset, y*tileSize + offset, color);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (GameService.getInstance().endOfGame) {
            return true;
        }
        int coords[] = getBoardTile(event.getX(), event.getY());

        int action = event.getAction();
        int color = GameService.getInstance().playersTurn;
        switch (action) {
            case MotionEvent.ACTION_UP:
                GameService.getInstance().resetPreview();
                // Get where user touched down
                int x = GameService.getInstance().preX;
                int y = GameService.getInstance().preY;

                // If the user has moved away from the touched down square, cancel the move.
                if (x != coords[0] || y != coords[1]) {
                    invalidate();
                    return true;
                }

                // Finalize move
                GameService.getInstance().setMove(x, y, color);
                if (GameService.getInstance().endOfGame) {
                    gameActivity.gameEnded(GameService.getInstance().winningPlayer);
                }
                invalidate(); // redraw canvas

                break;
            case MotionEvent.ACTION_DOWN:
                // Preview play
                GameService.getInstance().setPreview(coords[0], coords[1], color);
                invalidate(); // redraw canvas
                break;
        }

        return true;
    }

    /**
     *  Returns x/y coordinates of the game board given device screen coordinates
     */
    private int[] getBoardTile(float x, float y) {
        int[] retVal = new int[2];

        int xPos = (int) Math.floor((x - offset) / tileSize);
        int yPos = (int) Math.floor((y - offset) / tileSize);

        retVal[0] = xPos;
        retVal[1] = yPos;

        return retVal;
    }

    /**
     * Draws a game tile at the given device screen coordinates.
     */
    private void drawTile(Canvas canvas, int xPos, int yPos, int fillColor) {
        Paint p = new Paint();

        // Draw outline of square.
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        p.setColor(Color.BLACK);
        canvas.drawRect(xPos, yPos, xPos + tileSize, yPos + tileSize, p);

        // Reset the stroke width to get ready to fill in the tile.
        p.setStrokeWidth(0);

        if (fillColor == Color.WHITE || fillColor == Color.BLACK || fillColor == Color.GRAY) {
            p.setColor(fillColor);
            canvas.drawRect(xPos + 3, yPos + 3, xPos + tileSize - 3, yPos + tileSize - 3, p);
        } else {
            // Draw the checker
            p.setColor(Color.WHITE);
            canvas.drawRect(xPos + 3, yPos + 3, xPos + tileSize - 3, yPos + tileSize - 3, p);
            p.setColor(fillColor);
            canvas.drawCircle(xPos + (tileSize / 2), yPos + (tileSize / 2), (tileSize / 2), p);
        }
    }

}
