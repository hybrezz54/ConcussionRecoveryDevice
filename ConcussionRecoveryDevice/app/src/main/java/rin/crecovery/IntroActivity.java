package rin.crecovery;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };

        handler.postDelayed(r, 5000);
    }
}
