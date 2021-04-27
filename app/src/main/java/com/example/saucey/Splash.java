package com.example.saucey;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class Splash extends AwesomeSplash {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    @Override
    public void initSplash(ConfigSplash configSplash) {
        ActionBar actionBar = getSupportActionBar();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        configSplash.setBackgroundColor(R.color.colorPrimary);
        configSplash.setAnimCircularRevealDuration(500);
        configSplash.setRevealFlagX(Flags.REVEAL_LEFT);
        configSplash.setRevealFlagX(Flags.REVEAL_BOTTOM);

        configSplash.setLogoSplash(R.drawable.splash);
        configSplash.setAnimLogoSplashDuration(200);

        configSplash.setTitleSplash("iEYE");
        configSplash.setTitleTextColor(R.color.splashColor);
        configSplash.setTitleTextSize(50f);
        //configSplash.setTitleFont("Fonts/redhatdisplaybold.ttf");
        configSplash.setAnimTitleDuration(200);
        configSplash.setAnimTitleTechnique(Techniques.FadeIn);
    }

    @Override
    public void animationsFinished() {
      startActivity(new Intent(Splash.this,MainActivity.class));
    }
}
