package com.example.selectionapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private NskAlgoSdk nskAlgoSdk;
    private BluetoothAdapter bluetoothAdapter;
    private TgStreamReader tgStreamReader;
    InputStream badAvg, goodAvg;
    List<Double> goodRange = new ArrayList<>();
    List<Double> badRange = new ArrayList<>();
    String[] goodRage;
    String[] badRage;
    Button mainImage;
    private ConstraintLayout secondaryLayout;
    private ConstraintLayout primaryLayout;
    private static ConstraintLayout secondary1;
    private ConstraintLayout secondary2;
    private ConstraintLayout secondary3;
    private ConstraintLayout secondary4;
    private boolean buttonClicked = false;
    private boolean startAction = false;
    private short raw_data[] = {0};
    private int raw_data_index = 0;
    private TextView connectionStatus;
    private boolean firstButtonClicked= false;
    private boolean SecondBtnClicked=false;
    int currentIndex = 0;
    private TextToSpeech textToSpeech;
    ImageButton dailyActivity , sick,command, entertainment,foodBtn,toiletBtn;
    double realTimeRange, goodMin, goodMax, badMin, badMax;
    List<ImageButton> imageButtons = new ArrayList<>();
    String[] foodArray,toiletArray,clothArray,temperatureArray, headacheArray,legPainArray, throatDiscomfortArray,heartArray,lightContentArray,cleanlinessContentArray,windowContentArray,
            bugContentArray, walkContentArray,gameContentArray, musicContentArray,tvContentArray;
    ArrayAdapter<String> foodAdapter,toiletAdapter, clothAdapter,temperatureAdapter,headacheAdapter,legPainAdapter,throatDiscomfortAdapter,heartAdapter,lightAdapter,cleanlinessAdapter,windowAdapter,bugAdapter
            ,walkAdapter,gameAdapter,musicAdapter,tvAdapter;
    ListView listView;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView =(ListView)findViewById(R.id.listVIew);
        mainImage = findViewById(R.id.button);
        primaryLayout=findViewById(R.id.main);
        secondaryLayout=findViewById(R.id.Secondary);
        secondary1=findViewById(R.id.Secondary1);
        secondary2=findViewById(R.id.Secondary2);
        secondary3=findViewById(R.id.Secondary3);
        secondary4=findViewById(R.id.Secondary4);
        connectionStatus = findViewById(R.id.connectionStatus);
        dailyActivity = findViewById(R.id.DailyActivity);
        sick = findViewById(R.id.Sick);
        command = findViewById(R.id.Command);
        entertainment = findViewById(R.id.Entertainment);
        foodBtn= findViewById(R.id.imgSub1);
        toiletBtn=findViewById(R.id.imgSub2);

        foodArray = ArrayString.food;
        toiletArray = ArrayString.toilet;
        clothArray = ArrayString.cloth;
        temperatureArray = ArrayString.temperature;
        headacheArray = ArrayString.headache;
        legPainArray = ArrayString.legPain;
        throatDiscomfortArray = ArrayString.throatDiscomfort;
        heartArray = ArrayString.heart;
        lightContentArray = ArrayString.lightContent;
        cleanlinessContentArray = ArrayString.cleanlinessContent;
        windowContentArray = ArrayString.windowContent;
        bugContentArray = ArrayString.bugContent;
        walkContentArray = ArrayString.walkContent;
        gameContentArray = ArrayString.gameContent;
        musicContentArray = ArrayString.musicContent;
        tvContentArray = ArrayString.tvContent;
        foodAdapter = new ArrayAdapter<>(this, R.layout.text_view_array, foodArray);
        toiletAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, toiletArray);
        clothAdapter = new ArrayAdapter<>(this, R.layout.text_view_array, clothArray);
        temperatureAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, temperatureArray);
        headacheAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, headacheArray);
        legPainAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, legPainArray);
        throatDiscomfortAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, throatDiscomfortArray);
        heartAdapter = new ArrayAdapter<>(this, R.layout.text_view_array, heartArray);
        lightAdapter = new ArrayAdapter<>(this, R.layout.text_view_array, lightContentArray);
        cleanlinessAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, cleanlinessContentArray);
        windowAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, windowContentArray);
        bugAdapter = new ArrayAdapter<>(this, R.layout.text_view_array, bugContentArray);
        walkAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, walkContentArray);
        gameAdapter = new ArrayAdapter<>(this, R.layout.text_view_array, gameContentArray);
        musicAdapter = new ArrayAdapter<>(this, R.layout.text_view_array, musicContentArray);
        tvAdapter = new ArrayAdapter<>(this,R.layout.text_view_array, tvContentArray);







        //Range of previousData
        goodAvg = getResources().openRawResource(R.raw.good);
        BufferedReader reader = new BufferedReader(new InputStreamReader(goodAvg));
        try {
            String csvline;
            while ((csvline = reader.readLine()) != null) {
                goodRage = csvline.split(",");
                try {
                    //double[] doubleArray = Arrays.stream(goodrage).mapToDouble(Double::parseDouble).toArray();
                    goodRange.add(Double.valueOf(goodRage[0] + ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (goodRange.size() > 0) {
                Collections.sort(goodRange);
                goodMin = goodRange.get(0);
                goodMax = goodRange.get(goodRange.size() - 1);
                Log.e("TAG2", "good min : " + goodMin);
                Log.e("TAG2", "good max : " + goodMax);
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
                badRage = csvline1.split(",");
                try {
                    //double[] doubleArray = Arrays.stream(goodrage).mapToDouble(Double::parseDouble).toArray();
                    badRange.add(Double.valueOf(badRage[0] + ""));
                } catch (Exception e) {
                    Log.e("TAG2", "error : " + e);
                    e.printStackTrace();
                }
            }

            if (badRange.size() > 0) {
                Collections.sort(badRange);
                badMin = badRange.get(0);
                badMax = badRange.get(badRange.size() - 1);
                Log.e("TAG2", "bad min : " + badMin);
                Log.e("TAG2", "bad max : " + badMax);
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

        mainImage.setOnClickListener(view -> {
            primaryLayout.setVisibility(View.GONE);
            secondaryLayout.setVisibility(View.VISIBLE);
            startAction=true;


        });
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

        // Set the item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(position);
                showToast(":"+selectedItem,Toast.LENGTH_SHORT); // Show the selected item text in a Toast
            }
        });


        init();
    }

    @Override
    protected void onResume() {
        connect();
        super.onResume();
    }
    private void init() {
        nskAlgoSdk = new NskAlgoSdk();
        Log.e("TAG", "test ");

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
                    if (raw_data_index == 2560) {
                        nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_EEG.value, raw_data, raw_data_index);
                        raw_data_index = 0;
                        Log.e("TAG","raw_data index ");

                        if (startAction) {

                            Arrays.sort(raw_data);
                            realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];

                            if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                                Log.e("TAG123", "realtimerange1_1=" +realTimeRange);
                                showToast("you like the image ", Toast.LENGTH_SHORT);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        startAction=false;
                                        raw_data_index=0;
                                        buttonChecked(currentIndex);

                                    }
                                });



                            } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                                Log.e("TAG123", "realtimerange2=" + realTimeRange);
                                showToast("you dont like the image ", Toast.LENGTH_SHORT);
                                Log.e("TAG","CurrentIndex "+ currentIndex);
                                currentIndex++;
                                Log.e("TAG","CurrentIndex "+ currentIndex);
                                if (currentIndex ==4 ) {
                                    currentIndex = 0; // Reset to the first button if the end is reached
                                    Log.e("TAG","CurrentIndex==4 "+ currentIndex);
                                }
                            } else {
                                Log.e("TAG123", "realtimerange3=" +realTimeRange);

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
                    // Do something when connecting
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    tgStreamReader.start();
                    showToast("Connected", Toast.LENGTH_SHORT);
                    int algoTypes = NskAlgoType.NSK_ALGO_TYPE_ATT.value;

                    int ret = nskAlgoSdk.NskAlgoInit(algoTypes, getFilesDir().getAbsolutePath());
                    if (ret == 0) {
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
        Log.e("TAG","buttonchecked");
        firstButtonClicked=true;
        raw_data_index=0;
        if(num==0){
            dailyAction();
        }
        if ( num==1){
            Sick();
        }
        else if(num==2){
            command();

        }else if(num==3){
            entertainment();
        }
    }
    private void dailyAction() {
        secondary4.setVisibility(View.GONE);
        secondary3.setVisibility(View.GONE);
        secondary2.setVisibility(View.GONE);
        secondary1.setVisibility(View.VISIBLE);
        currentIndex=0;

        Log.e("TAG","DailyActivity");


            if (firstButtonClicked) {

                Arrays.sort(raw_data);
                realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];

                if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                    Log.e("TAG123", "realtimerange2_1=" + realTimeRange);
                    showToast("you like the image ", Toast.LENGTH_SHORT);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            firstButtonClicked = false;
                            Log.e("TAG", "Secondbthclicked");
                            dailyActivityBtnChecked(currentIndex);

                        }
                    });


                } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                    Log.e("TAG123", "realtimerange2=" + realTimeRange);
                    showToast("you dont like the image ", Toast.LENGTH_SHORT);
                    Log.e("TAG", "CurrentIndex " + currentIndex);
                    currentIndex++;
                    Log.e("TAG", "CurrentIndex " + currentIndex);
                    if (currentIndex == 4) {
                        currentIndex = 0; // Reset to the first button if the end is reached
                        Log.e("TAG", "CurrentIndex==4 " + currentIndex);
                    }
                } else {
                    Log.e("TAG123", "realtimerange3=" + realTimeRange);

                    showToast("no value matched", Toast.LENGTH_SHORT);
                }

            }
        }



    public void Sick(){
        secondary4.setVisibility(View.GONE);
        secondary3.setVisibility(View.GONE);
        secondary1.setVisibility(View.GONE);
        secondary2.setVisibility(View.VISIBLE);

        currentIndex=0;


        if (firstButtonClicked ) {

            Arrays.sort(raw_data);
            realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];

            if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                Log.e("TAG123", "realtimerange1=" + realTimeRange);
                showToast("you like the image ", Toast.LENGTH_SHORT);
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        firstButtonClicked=false;
                        healthBtnChecked(currentIndex);
                    }
                });

                Log.e("TAG123", "CurrentIndex " + currentIndex);
                Log.e("TAG1", "button has been clicked");

            } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                Log.e("TAG123", "realtimerange2=" + realTimeRange);
                showToast("you dont like the image ", Toast.LENGTH_SHORT);
                Log.e("TAG", "CurrentIndex " + currentIndex);
                currentIndex++;
                Log.e("TAG", "CurrentIndex " + currentIndex);
                if (currentIndex == 4) {
                    currentIndex = 0; // Reset to the first button if the end is reached
                    Log.e("TAG", "CurrentIndex==4 " + currentIndex);
                }
            } else {
                Log.e("TAG123", "realtimerange3=" + realTimeRange);

                showToast("no value matched", Toast.LENGTH_SHORT);
            }

        }



    }
    public void command(){
        secondary2.setVisibility(View.GONE);
        secondary1.setVisibility(View.GONE);
        secondary4.setVisibility(View.GONE);
        secondary3.setVisibility(View.VISIBLE);

        currentIndex=0;


        if(firstButtonClicked ){
            Arrays.sort(raw_data);
            realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];

            if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                Log.e("TAG123", "realtimerange2_1=" +realTimeRange);
                showToast("you like the image ", Toast.LENGTH_SHORT);
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        firstButtonClicked=false;
                        helpBtnChecked(currentIndex);
                    }
                });

                Log.e("TAG1", "button has been clicked");

            } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                Log.e("TAG123", "realtimerange2=" + realTimeRange);
                showToast("you dont like the image ", Toast.LENGTH_SHORT);
                Log.e("TAG","CurrentIndex "+ currentIndex);
                currentIndex++;
                Log.e("TAG","CurrentIndex "+ currentIndex);
                if (currentIndex ==4 ) {
                    currentIndex = 0; // Reset to the first button if the end is reached

                }
            } else {
                Log.e("TAG123", "realtimerange3=" +realTimeRange);

                showToast("no value matched", Toast.LENGTH_SHORT);
            }
        }
        }

    public void  entertainment(){
        secondary1.setVisibility(View.GONE);
        secondary2.setVisibility(View.GONE);
        secondary3.setVisibility(View.GONE);
        secondary4.setVisibility(View.VISIBLE);


        currentIndex=0;

            if (firstButtonClicked ) {

                Arrays.sort(raw_data);
                realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];

                if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                    Log.e("TAG123", "realtimerange1=" + realTimeRange);
                    showToast("you like the image ", Toast.LENGTH_SHORT);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            firstButtonClicked=false;
                            entertainmentBtnChecked(currentIndex);
                        }
                    });

                    Log.e("TAG123", "CurrentIndex " + currentIndex);
                    Log.e("TAG1", "button has been clicked");

                } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                    Log.e("TAG123", "realtimerange2=" + realTimeRange);
                    showToast("you dont like the image ", Toast.LENGTH_SHORT);
                    Log.e("TAG", "CurrentIndex " + currentIndex);
                    currentIndex++;
                    Log.e("TAG", "CurrentIndex " + currentIndex);
                    if (currentIndex == 4) {
                        currentIndex = 0; // Reset to the first button if the end is reached
                        Log.e("TAG", "CurrentIndex==4 " + currentIndex);
                    }
                } else {
                    Log.e("TAG123", "realtimerange3=" + realTimeRange);

                    showToast("no value matched", Toast.LENGTH_SHORT);
                }


            }
        }


    private void dailyActivityBtnChecked(int num) {

        SecondBtnClicked=true;
        if (num == 0) {
            listView.setAdapter(foodAdapter);

        } else if (num == 1) {
            listView.setAdapter(toiletAdapter);

        } else if (num == 2) {
            listView.setAdapter(clothAdapter);


        } else if (num == 3) {
            listView.setAdapter(temperatureAdapter);

        }
        listView.setVisibility(View.VISIBLE);
        raw_data_index=0;
        FinalListView();
    }

    private void healthBtnChecked(int num) {

        SecondBtnClicked=true;

        if (num == 0) {
            listView.setAdapter(headacheAdapter);
        }
        else if (num == 1) {
            listView.setAdapter(legPainAdapter);
        } else if (num == 2) {
            listView.setAdapter(heartAdapter);
        } else if (num == 3) {

            listView.setAdapter(throatDiscomfortAdapter);
        }
        listView.setVisibility(View.VISIBLE);
        raw_data_index=0;
        FinalListView();
    }
    private void helpBtnChecked(int num) {


        if (num == 0) {
            listView.setAdapter(lightAdapter);

        } else if (num == 1) {
            listView.setAdapter(cleanlinessAdapter);

        } else if (num == 2) {
            listView.setAdapter(windowAdapter);

        } else if (num == 3) {
            listView.setAdapter(bugAdapter);

        }
        FinalListView();
    }
    private void entertainmentBtnChecked(int num) {

        SecondBtnClicked=true;
        if (num == 0) {
            listView.setAdapter(walkAdapter);

        } else if (num == 1) {
            listView.setAdapter(gameAdapter);

        } else if (num == 2) {
            listView.setAdapter(musicAdapter);

        } else if (num == 3) {
            listView.setAdapter(tvAdapter);

        }
        listView.setVisibility(View.VISIBLE);
        raw_data_index=0;
        FinalListView();
    }

    public void FinalListView(){

        currentIndex=0;

        if(SecondBtnClicked) {
            Arrays.sort(raw_data);
            realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];

            if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                Log.e("TAG123", "realtimerange3_1=" + realTimeRange);
                showToast("you like the image ", Toast.LENGTH_SHORT);
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        SecondBtnClicked=false;
                        triggerItemClick(currentIndex);
                    }
                });

                Log.e("TAG123", "CurrentIndex " + currentIndex);
                Log.e("TAG1", "button has been clicked");

            } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                Log.e("TAG123", "realtimerange2=" + realTimeRange);
                showToast("you dont like the image ", Toast.LENGTH_SHORT);
                Log.e("TAG", "CurrentIndex " + currentIndex);
                currentIndex++;
                Log.e("TAG", "CurrentIndex " + currentIndex);
                if (currentIndex == listView.getCount()) {
                    currentIndex = 0; // Reset to the first button if the end is reached
                    Log.e("TAG", "CurrentIndex==4 " + currentIndex);
                }
            } else {
                Log.e("TAG123", "realtimerange3=" + realTimeRange);

                showToast("no value matched", Toast.LENGTH_SHORT);
            }


        }
        }








    private void triggerItemClick(int currentIndex) {
        // Automatically trigger onItemClick for the item at desiredPosition
        listView.performItemClick(
                listView.getChildAt(currentIndex),
               currentIndex,
                listView.getAdapter().getItemId(currentIndex)
     );}
    public void showToast(final String msg, final int timeStyle) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), msg, timeStyle).show();
            }
        });

    }



}