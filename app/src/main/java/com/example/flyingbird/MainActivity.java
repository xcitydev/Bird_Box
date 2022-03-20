package com.example.flyingbird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private boolean ismute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Gameactivity.class));
            }
        });
        TextView highscore = findViewById(R.id.highscore);

        final SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        highscore.setText("Highscore: "+ prefs.getInt("highsore", 0));

        ismute = prefs.getBoolean("ismute", false);

        final ImageView vlmctrl = findViewById(R.id.volumectrl);

        if(ismute)
            vlmctrl.setImageResource(R.drawable.ic_volume_off_black_24dp);
        else
            vlmctrl.setImageResource(R.drawable.ic_volume_up_black_24dp);

        vlmctrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ismute = !ismute;
                if(ismute)
                    vlmctrl.setImageResource(R.drawable.ic_volume_off_black_24dp);
                else
                    vlmctrl.setImageResource(R.drawable.ic_volume_up_black_24dp);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("ismute", ismute);
                editor.apply();
            }
        });


    }
}
