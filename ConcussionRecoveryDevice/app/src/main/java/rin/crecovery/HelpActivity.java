package rin.crecovery;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;

public class HelpActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new HelpOneFragment());
        addSlide(new HelpTwoFragment());
        addSlide(new HelpThreeFragment());
    }

    @Override
    public void onSkipPressed() {
        finish();
    }

    @Override
    public void onDonePressed() {
        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }

}
