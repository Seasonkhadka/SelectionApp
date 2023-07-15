package com.example.selectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.AlgoSdk.NskAlgoDataType;

import java.util.Arrays;

public class OptionActivity extends AppCompatActivity {
    ImageButton dailyActivity, sick, command, entertainment, imgSubBtn1, imgSubBtn2, imgSubBtn3, imgSubBtn4;
    TextView subTxt1, subTxt2, subTxt3, subTxt4;
    LinearLayout linearLayout;
    MainActivity main;
    Intent serviceIntent = new Intent(this, BrainWave.class);
    double realtimerange;
    int currentIndex = 0;
    boolean firstButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        dailyActivity = findViewById(R.id.DailyActivity);
        sick = findViewById(R.id.Sick);
        command = findViewById(R.id.Command);
        entertainment = findViewById(R.id.Entertainment);
        dailyActivity.setImageResource(R.drawable.dailyactivity);
        sick.setImageResource(R.drawable.sick);
        command.setImageResource(R.drawable.help);
        entertainment.setImageResource(R.drawable.entertainment);
        main = new MainActivity();
        subTxt1 = findViewById(R.id.txtsub1);
        subTxt2 = findViewById(R.id.txtsub2);
        subTxt3 = findViewById(R.id.txtsub3);
        subTxt4 = findViewById(R.id.txtsub4);
        imgSubBtn1 = findViewById(R.id.imgsub1);
        imgSubBtn2 = findViewById(R.id.imgSub2);
        imgSubBtn3 = findViewById(R.id.imgSub3);
        imgSubBtn4 = findViewById(R.id.imgSub4);
        linearLayout = findViewById(R.id.LinearLayout);

/*
        if (raw_data_index == 2560) {
            nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_EEG.value, raw_data, raw_data_index);
            raw_data_index = 0;

        }


        Arrays.sort(raw_data);
        realtimerange = raw_data[raw_data.length - 1] - raw_data[0];

        if (!firstButtonClicked) {

            if ((realtimerange <= main.goodmax) && ((main.goodmin) < realtimerange)) {
                buttonChecked(currentIndex);
                                /*&Log.e("TAG", "realtimerange1=" + realtimerange);
                                showToast("you like the image ", Toast.LENGTH_SHORT);
                                ImageButton currentButton = imageButtons.get(currentIndex);
                                Log.e("TAG", "current Index" + imageButtons.get(currentIndex));
                                currentButton.performClick();
                                Log.e("TAG", "buttonclicked");


            } else if ((realtimerange <= main.badmax) && ((main.badmin) < realtimerange)) {
                Log.e("TAG1", "realtimerange2=" + realtimerange);
                showToast("you dont like the image ", Toast.LENGTH_SHORT);
                currentIndex++;
                if (currentIndex >= 4) {
                    currentIndex = 0; // Reset to the first button if the end is reached
                }
            } else {
                Log.e("TAG1", "realtimerange3=" + realtimerange);

                showToast("no value matched", Toast.LENGTH_SHORT);
            }
        }

        dailyActivity.setOnClickListener(dailyView -> {
            dailyAction();
        });


        sick.setOnClickListener(sickView -> {
            Sick();
        });

        command.setOnClickListener(commandView -> {
            command();
        });

        entertainment.setOnClickListener(entertainmentView -> {
            entertainment();
        });


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
        subTxt2.setText("겔임");
        subTxt3.setText("노래");
        subTxt4.setText("TV");

    }

    private BroadcastReceiver arrayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BrainWave.ACTION_ARRAY_UPDATED)) {
                String[] array = intent.getStringArrayExtra("array");
                // Handle the updated array here
            }
        }
    };


    private void buttonChecked(int num) {
        firstButtonClicked = true;
        if (num == 1) {
            dailyAction();
        } else if (num == 2) {
            Sick();
        } else if (num == 3) {
            command();

        } else if (num == 4) {
            entertainment();
        }

    }


    private void showToast(final String msg, final int timeStyle) {
    }
        */}

}