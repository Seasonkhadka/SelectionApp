package com.example.selectionapp;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoState;
import com.neurosky.AlgoSdk.NskAlgoType;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NskAlgoSdk nskAlgoSdk;
    private BluetoothAdapter bluetoothAdapter;
    private TgStreamReader tgStreamReader;
    InputStream badAvg, goodAvg;
    List<Double> goodrange = new ArrayList<>();
    List<Double> badrange = new ArrayList<>();
    String[] goodrage;
    String[] badrage;
    Button mainImage;
    private boolean buttonClicked = false;
    private boolean startAction = false;
    private boolean firstButtonClicked= false;
    private short raw_data[] = {0};
    private int raw_data_index = 0;
    private TextView connectionStatus;
    double realtimerange, goodmin, goodmax, badmin, badmax;
    List<ImageButton> imageButtons = new ArrayList<>();
    int currentIndex = 0;
    LinearLayout linearLayout;
    ImageButton dailyActivity , sick,command, entertainment,imgSubBtn3, imgSubBtn1,imgSubBtn2,imgSubBtn4;
    TextView subTxt1,subTxt2, subTxt3,subTxt4;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainImage = findViewById(R.id.button);
        imgSubBtn1 = findViewById(R.id.imgsub1);
        imgSubBtn2 = findViewById(R.id.imgSub2);
        imgSubBtn3 = findViewById(R.id.imgSub3);
        imgSubBtn4 = findViewById(R.id.imgSub4);
        subTxt1 = findViewById(R.id.txtsub1);
        subTxt2 = findViewById(R.id.txtsub2);
        subTxt3 = findViewById(R.id.txtsub3);
        subTxt4 = findViewById(R.id.txtsub4);
         linearLayout = findViewById(R.id.LinearLayout);
        connectionStatus = findViewById(R.id.connectionStatus);
        goodAvg = getResources().openRawResource(R.raw.good);
        BufferedReader reader = new BufferedReader(new InputStreamReader(goodAvg));

        try {
            String csvline;
            while ((csvline = reader.readLine()) != null) {
                goodrage = csvline.split(",");
                try {
                    //double[] doubleArray = Arrays.stream(goodrage).mapToDouble(Double::parseDouble).toArray();
                    goodrange.add(Double.valueOf(goodrage[0] + ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (goodrange.size() > 0) {
                Collections.sort(goodrange);
                goodmin = goodrange.get(0);
                goodmax = goodrange.get(goodrange.size() - 1);
                Log.e("TAG2", "good min : " + goodmin);
                Log.e("TAG2", "good max : " + goodmax);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        badAvg = getResources().openRawResource(R.raw.bad);
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(badAvg));
        Log.e("TAG2", "start");
        try {
            String csvline1;
            while ((csvline1 = reader1.readLine()) != null) {
                badrage = csvline1.split(",");
                try {
                    //double[] doubleArray = Arrays.stream(goodrage).mapToDouble(Double::parseDouble).toArray();
                    badrange.add(Double.valueOf(badrage[0] + ""));
                } catch (Exception e) {
                    Log.e("TAG2", "error : " + e);
                    e.printStackTrace();
                }
            }

            if (badrange.size() > 0) {
                Collections.sort(badrange);
                badmin = badrange.get(0);
                badmax = badrange.get(badrange.size() - 1);
                Log.e("TAG2", "bad min : " + badmin);
                Log.e("TAG2", "bad max : " + badmax);
            }


        } catch (Exception e) {
            Log.e("TAG2", "error 2 : " + e);
            e.printStackTrace();
        }


        try {
            // (1) Make sure that the device supports Bluetooth and Bluetooth is on
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Toast.makeText(
                        this,
                        "Please enable your Bluetooth and re-run this program !",
                        Toast.LENGTH_LONG).show();
                //finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "error:" + e.getMessage());
            return;
        }

        init();
    }

    @Override
    protected void onResume() {
        connect();
        super.onResume();
    }

    private void init() {
        nskAlgoSdk = new NskAlgoSdk();

        nskAlgoSdk.setOnStateChangeListener((state, reason) -> {
            String stateStr = "";
            String reasonStr = "";
            for (NskAlgoState s : NskAlgoState.values()) {
                if (s.value == state) {
                    stateStr = s.toString();
                }
            }
            for (NskAlgoState r : NskAlgoState.values()) {
                if (r.value == reason) {
                    reasonStr = r.toString();
                }
            }
            Log.e("TAG", "NskAlgoSdkStateChangeListener: state: " + stateStr + ", reason: " + reasonStr);

            final int[] finalState = {state};
            runOnUiThread(() -> {
                // change UI elements here
                if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_RUNNING.value || finalState[0] == NskAlgoState.NSK_ALGO_STATE_COLLECTING_BASELINE_DATA.value) {
                    connectionStatus.setText("running");
                } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_STOP.value) {
                    raw_data = null;
                    raw_data_index = 0;
                    connectionStatus.setText("Stopped");
                    if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

                        // Prepare for connecting
                        tgStreamReader.stop();
                        tgStreamReader.close();
                    }

                    System.gc();
                } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_PAUSE.value) {
                    connectionStatus.setText("paused");

                } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_ANALYSING_BULK_DATA.value) {
                    connectionStatus.setText("analyzing");
                } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_INITED.value || finalState[0] == NskAlgoState.NSK_ALGO_STATE_UNINTIED.value) {
                    connectionStatus.setText("inited");

                }
            });
        });


        mainImage.setOnClickListener(view -> {
            setContentView(R.layout.home);
            buttonClicked = true;
            raw_data_index = 0;
            dailyActivity = findViewById(R.id.DailyActivity);
            sick = findViewById(R.id.Sick);
            command = findViewById(R.id.Command);
            entertainment = findViewById(R.id.Entertainment);

            dailyActivity.setImageResource(R.drawable.dailyactivity);
            sick.setImageResource(R.drawable.sick);
            command.setImageResource(R.drawable.help);
            entertainment.setImageResource(R.drawable.entertainment);



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
        });


    }

    private void dailyAction() {
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

    private void connect() {
        raw_data = new short[2560];
        raw_data_index = 0;

        tgStreamReader = new TgStreamReader(bluetoothAdapter, callback);

        if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

            // Prepare for connecting
            tgStreamReader.stop();
            tgStreamReader.close();
        }

        // (4) Demo of  using connect() and start() to replace connectAndStart(),
        // please call start() when the state is changed to STATE_CONNECTED
        tgStreamReader.connect();
    }


    private TgStreamHandler callback = new TgStreamHandler() {

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            // You can handle the received data here
            // You can feed the raw data to algo sdk here if necessary.
            switch (datatype) {
                case MindDataType.CODE_ATTENTION:
                    short attValue[] = {(short) data};
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ATT.value, attValue, 1);
                    break;
                case MindDataType.CODE_MEDITATION:
                    short medValue[] = {(short) data};
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_MED.value, medValue, 1);
                    break;
                case MindDataType.CODE_POOR_SIGNAL:
                    short pqValue[] = {(short) data};
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_PQ.value, pqValue, 1);
                    break;
                case MindDataType.CODE_RAW:
                    raw_data[raw_data_index++] = (short) data;
                    // Log.e("TAG1", "rawdata :" +(short) data);
                    if (buttonClicked && !startAction) {
                        startAction = true;
                        raw_data_index = 0;
                        return;
                    }
                    if (raw_data_index == 2560) {
                        nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_EEG.value, raw_data, raw_data_index);
                        raw_data_index = 0;

                        if (startAction && !firstButtonClicked) {

                            Arrays.sort(raw_data);
                            realtimerange = raw_data[raw_data.length - 1] - raw_data[0];

                            if ((realtimerange <= goodmax) && ((goodmin) < realtimerange)) {
                                buttonChecked(currentIndex);
                                /*&Log.e("TAG", "realtimerange1=" + realtimerange);
                                showToast("you like the image ", Toast.LENGTH_SHORT);
                                ImageButton currentButton = imageButtons.get(currentIndex);
                                Log.e("TAG", "current Index" + imageButtons.get(currentIndex));
                                currentButton.performClick();
                                Log.e("TAG", "buttonclicked");*/


                            } else if ((realtimerange <= badmax) && ((badmin) < realtimerange)) {
                                Log.e("TAG1", "realtimerange2=" + realtimerange);
                                showToast("you dont like the image ", Toast.LENGTH_SHORT);
                                currentIndex++;
                                if (currentIndex >= imageButtons.size()) {
                                    currentIndex = 0; // Reset to the first button if the end is reached
                                }
                            } else {
                                Log.e("TAG1", "realtimerange3=" + realtimerange);

                                showToast("no value matched", Toast.LENGTH_SHORT);
                            }


                        }


                    }

                    break;
                default:
                    break;
            }
        }


        @Override
        public void onChecksumFail(byte[] bytes, int i, int i1) {

        }

        @Override
        public void onRecordFail(int flag) {
            Log.e("TAG", "onRecordFail: " + flag);
        }


        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
            Log.d("TAG", "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    showToast("Connecting", Toast.LENGTH_SHORT);
                    Log.e("TAG","ConNECTING");
                    // Do something when connecting
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    tgStreamReader.start();
                    Log.e("TAG","Connected");
                    showToast("Connected", Toast.LENGTH_SHORT);
                    int algoTypes = NskAlgoType.NSK_ALGO_TYPE_ATT.value;

                    int ret = nskAlgoSdk.NskAlgoInit(algoTypes, getFilesDir().getAbsolutePath());
                    if (ret == 0) {
                        Log.e("TAG","receiving data");
                        showToast("Receiving data ", Toast.LENGTH_LONG);
                        nskAlgoSdk.NskAlgoStart(false);
                    }
                    break;
                case ConnectionStates.STATE_WORKING:
                    // Do something when working

                    //(9) demo of recording raw data , stop() will call stopRecordRawData,
                    //or you can add a button to control it.
                    //You can change the save path by calling setRecordStreamFilePath(String filePath) before startRecordRawData
                    //tgStreamReader.startRecordRawData();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            showToast("click to compare the data", Toast.LENGTH_LONG);

                            //receivedata();

                        }

                    });

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    // Do something when getting data timeout

                    //(9) demo of recording raw data, exception handling
                    //tgStreamReader.stopRecordRawData();

                    showToast("Get data time out!", Toast.LENGTH_SHORT);

                    if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
                        tgStreamReader.stop();
                        tgStreamReader.close();
                    }

                    break;
                case ConnectionStates.STATE_STOPPED:
                    // Do something when stopped
                    // We have to call tgStreamReader.stop() and tgStreamReader.close() much more than
                    // tgStreamReader.connectAndstart(), because we have to prepare for that
                    showToast("stopped", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    // Do something when disconnected
                    Log.e("TAG", "disconnected");

                    showToast("Disconnected", Toast.LENGTH_LONG);

                    break;
                case ConnectionStates.STATE_ERROR:
                    // Do something when you get error message
                    showToast("error", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_FAILED:
                    // Do something when you get failed message
                    // It always happens when open the BluetoothSocket error or timeout
                    // Maybe the device is not working normal.
                    // Maybe you have to try again
                    Log.e("TAG", "failed");
                    break;
            }
        }


    };

    private void buttonChecked (int num) {
        firstButtonClicked = true;
        if(num == 1){
            dailyAction();
        }
        else if ( num==2){
            Sick();
        }
        else if(num==3){
            command();

        }else if(num==4){
            entertainment();
        }
    }

    public void showToast(final String msg, final int timeStyle) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), msg, timeStyle).show();
            }
        });

    }


}