package com.carissarhea.crhea.pushfour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends Activity
        implements View.OnClickListener, BoardView.Listener {

    int mPlayers;
    BoardView boardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        mPlayers = intent.getIntExtra("players", 1);

        boardView = (BoardView) findViewById(R.id.bview);
        boardView.setListener(this);

        findViewById(R.id.btn_new_game).setOnClickListener(this);
    }

    /**
     * BoardView callback for when the game has ended.
     */
    @Override
    public void gameEnded(String winningPlayer) {
        Button btn = (Button) findViewById(R.id.btn_new_game);
        btn.setVisibility(View.VISIBLE);

        TextView textView = (TextView) findViewById(R.id.txt_winner);
        String winner = winningPlayer + " has won!";
        textView.setText(winner);
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_new_game:
                GameService.getInstance().resetGame();

                Button btn = (Button) findViewById(R.id.btn_new_game);
                btn.setVisibility(View.GONE);

                TextView textView = (TextView) findViewById(R.id.txt_winner);
                textView.setVisibility(View.GONE);

                boardView.invalidate();
                break;
        }
    }
}
