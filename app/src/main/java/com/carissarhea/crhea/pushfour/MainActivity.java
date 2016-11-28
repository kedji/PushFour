package com.carissarhea.crhea.pushfour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * TODO: 1. AI for single player play.
         * TODO: 2. Two player network play.
         */
        //findViewById(R.id.btn_one_player).setOnClickListener(this);
        findViewById(R.id.btn_two_players).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        switch(v.getId()) {
            case R.id.btn_one_player:
                GameService.getInstance().resetGame();
                intent.putExtra("players", 1);
                break;
            case R.id.btn_two_players:
                GameService.getInstance().resetGame();
                intent.putExtra("players", 2);
                break;
        }
        startActivity(intent);
    }

}
