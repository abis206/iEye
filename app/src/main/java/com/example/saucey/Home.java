package com.example.saucey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity implements View.OnClickListener {
//import com.bookiebot.R;
//import com.bookiebot.helper.Const;
//import com.bookiebot.helper.SharedPref;

    private LinearLayout homeLLParentView;
    private RelativeLayout homeRLChildView, homeRLView;
    private LinearLayout homeLLParentRead, homeLLParentRecord, homeLLParentExplore, homeLLParentLearn;
    private RelativeLayout homeLLChildRead, homeLLChildExplore;
    private Intent intent;
    private TextView homeParentName, homeChildName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homee);
        init();
        initAction();
    }

    private void init() {
        homeLLParentView = findViewById(R.id.homeLLParentView);
        homeRLView = findViewById(R.id.homeRLView);

        homeLLParentRead = findViewById(R.id.homeLLParentRead);
        homeLLParentRecord = findViewById(R.id.homeLLParentRecord);
        homeLLParentExplore = findViewById(R.id.homeLLParentExplore);
      //  homeLLParentLearn = findViewById(R.id.homeLLParentLearn);

        homeParentName = findViewById(R.id.homeParentName);
    }

    private void initAction() {
        homeLLParentRead.setOnClickListener(this);
        homeLLParentRecord.setOnClickListener(this);
        homeLLParentExplore.setOnClickListener(this);
        homeLLParentLearn.setOnClickListener(this);
        //setUserData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeLLParentRead:
                intent = new Intent(Home.this, ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.homeLLParentRecord:
                intent = new Intent(Home.this, OptometristActivity.class);
                startActivity(intent);
                break;
            case R.id.homeLLParentExplore:
            case R.id.homeLLChildExplore:
                intent = new Intent(Home.this, ResultsActivity.class);
                startActivity(intent);
                break;
//            case R.id.homeLLParentLearn:
           //     break;
            case R.id.homeLLChildRead:
                intent = new Intent(Home.this, ResultsActivity.class);
                startActivity(intent);
                break;
        }
    }
//
//    @Override
//    protected boolean isHomeScreen() {
//        return true;
//    }
//
//    @Override
//    protected boolean useToolbar() {
//        return true;
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        setCurrentScreen(HOME);
//        setNavigationSelected(HOME);
//    }

//    private void setUserData() {
//        if (SharedPref.getPrefsHelper().getPref(Const.Var.NAME, "") != null && SharedPref.getPrefsHelper().getPref(Const.Var.NAME, "").length() > 0) {
//            homeParentName.setText(String.valueOf(SharedPref.getPrefsHelper().getPref(Const.Var.NAME, "") + "!"));
//        }
//        if (SharedPref.getPrefsHelper().getPref(Const.Var.CHILD_NAME, "") != null && SharedPref.getPrefsHelper().getPref(Const.Var.CHILD_NAME, "").length() > 0) {
//            homeChildName.setText(String.valueOf(SharedPref.getPrefsHelper().getPref(Const.Var.CHILD_NAME, "") + "!"));
//        }
//    }


}
