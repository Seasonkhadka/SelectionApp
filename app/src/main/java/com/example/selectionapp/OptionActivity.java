package com.example.selectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OptionActivity extends AppCompatActivity {
    ImageButton dailyActivity,sick,command,entertainment,imgSubBtn1,imgSubBtn2,imgSubBtn3,imgSubBtn4;
    TextView subTxt1,subTxt2,subTxt3,subTxt4;
    LinearLayout linearLayout;
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
        subTxt1 = findViewById(R.id.txtsub1);
        subTxt2 = findViewById(R.id.txtsub2);
        subTxt3 = findViewById(R.id.txtsub3);
        subTxt4 = findViewById(R.id.txtsub4);
        imgSubBtn1 = findViewById(R.id.imgsub1);
        imgSubBtn2 = findViewById(R.id.imgSub2);
        imgSubBtn3 = findViewById(R.id.imgSub3);
        imgSubBtn4 = findViewById(R.id.imgSub4);
        linearLayout = findViewById(R.id.LinearLayout);
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
    public void Sick(){
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
    public void command(){
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
    public void  entertainment(){
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
}