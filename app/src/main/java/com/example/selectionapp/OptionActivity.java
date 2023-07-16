package com.example.selectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.selectionapp.BrainWave.BrainWaveListener;

public class OptionActivity extends AppCompatActivity implements BrainWaveListener {
    ImageButton dailyActivity, sick, command, entertainment, imgSubBtn1, imgSubBtn2, imgSubBtn3, imgSubBtn4;
    TextView subTxt1, subTxt2, subTxt3, subTxt4;
    LinearLayout linearLayout;
    double realtimerange;
    int currentIndex = 0;
    boolean firstButtonClicked = false;

    private BrainWave brainWave;

    private int goodMin, goodMax, badMin, badMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Intent intent = getIntent();
        goodMin = intent.getIntExtra("goodMin", 0);
        goodMax = intent.getIntExtra("goodMax", 0);
        badMin = intent.getIntExtra("badMin", 0);
        badMax = intent.getIntExtra("badMax", 0);

        Intent serviceIntent = new Intent(this, BrainWave.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        init();
    }

    private void init() {
        dailyActivity = findViewById(R.id.DailyActivity);
        sick = findViewById(R.id.Sick);
        command = findViewById(R.id.Command);
        entertainment = findViewById(R.id.Entertainment);
        dailyActivity.setImageResource(R.drawable.dailyactivity);
        sick.setImageResource(R.drawable.sick);
        command.setImageResource(R.drawable.help);
        entertainment.setImageResource(R.drawable.entertainment);

        subTxt1 = findViewById(R.id.txtsub1);
        subTxt2 = findViewById(R.id.txtsub2);
        subTxt3 = findViewById(R.id.txtsub3);
        subTxt4 = findViewById(R.id.txtsub4);
        imgSubBtn1 = findViewById(R.id.imgsub1);
        imgSubBtn2 = findViewById(R.id.imgSub2);
        imgSubBtn3 = findViewById(R.id.imgSub3);
        imgSubBtn4 = findViewById(R.id.imgSub4);
        linearLayout = findViewById(R.id.LinearLayout);
    }

    @Override
    public void getData(short data) {
        Log.e("TAG", "Data: " + data);
    }

    @Override
    public void resetData() {
        Log.e("TAG", "resetData");
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BrainWave.LocalBinder binder = (BrainWave.LocalBinder) iBinder;
            brainWave = binder.getBrainWave();
            brainWave.setBrainWaveListener(OptionActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            brainWave = null;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the BrainWave service
        unbindService(serviceConnection);
    }

    public void dailyAction() {
        linearLayout.setVisibility(View.VISIBLE);
        imgSubBtn1.setImageResource(R.drawable.food);
        imgSubBtn2.setImageResource(R.drawable.bathroom);
        imgSubBtn3.setImageResource(R.drawable.cloth);
        imgSubBtn4.setImageResource(R.drawable.temperature);
        subTxt1.setText("음식");
        subTxt2.setText("화장실");
        subTxt3.setText("옷");
        subTxt4.setText("온도");
    }

    public void Sick() {
        linearLayout.setVisibility(View.VISIBLE);
        imgSubBtn1.setImageResource(R.drawable.headache);
        imgSubBtn2.setImageResource(R.drawable.leg);
        imgSubBtn3.setImageResource(R.drawable.heart);
        imgSubBtn4.setImageResource(R.drawable.throat);
        subTxt1.setText("머리");
        subTxt2.setText("다리");
        subTxt3.setText("심장");
        subTxt4.setText("목");
    }

    public void command() {
        linearLayout.setVisibility(View.VISIBLE);
        imgSubBtn1.setImageResource(R.drawable.light);
        imgSubBtn2.setImageResource(R.drawable.clean);
        imgSubBtn3.setImageResource(R.drawable.window);
        imgSubBtn4.setImageResource(R.drawable.bug);
        subTxt1.setText("불");
        subTxt2.setText("정소");
        subTxt3.setText("장문");
        subTxt4.setText("벌레");
    }

    public void entertainment() {
        linearLayout.setVisibility(View.VISIBLE);
        imgSubBtn1.setImageResource(R.drawable.walk);
        imgSubBtn2.setImageResource(R.drawable.game);
        imgSubBtn3.setImageResource(R.drawable.music);
        imgSubBtn4.setImageResource(R.drawable.tv);
        subTxt1.setText("산책");
        subTxt2.setText("게임");
        subTxt3.setText("노래");
        subTxt4.setText("TV");
    }
}
