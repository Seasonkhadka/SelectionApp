package com.example.selectionapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
public class MainActivity extends AppCompatActivity {
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
    private static ConstraintLayout healthLayout;
    private ConstraintLayout dailyActivityLayout;
    private ConstraintLayout helpLayout;
    private ConstraintLayout entertainmentLayout;
    private boolean startAction = false;
    private short[] raw_data = {0};
    private int raw_data_index = 0;
    private TextView connectionStatus;
    private boolean dailyButtonClicked= false;
    private boolean SickBtnClicked=false;
    private boolean helpBtnClicked=false;
    private boolean listViewButtonClicked=false;
    private boolean entBtnClicked=false;
    int currentIndex = 0;
    int backgroundColor;
    ImageButton dailyActivity , sick,command, entertainment,bathroomBtn,foodBtn,clothBtn,tempBtn,headacheBtn,legPainBtn,throatBtn,
                heartBtn,lightBtn,cleaninessBtn,windowBtn,bugBtn,walkBtn,gameBtn,musicBtn,tvBtn;
    double realTimeRange, goodMin, goodMax, badMin, badMax;
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
        listView = findViewById(R.id.listVIew);
        mainImage = findViewById(R.id.button);
        primaryLayout=findViewById(R.id.main);
        backgroundColor = ContextCompat.getColor(this, androidx.cardview.R.color.cardview_light_background);
        secondaryLayout=findViewById(R.id.Secondary);
        dailyActivityLayout=findViewById(R.id.DailyActivity1);
        helpLayout=findViewById(R.id.help1);
        healthLayout=findViewById(R.id.health1);
        entertainmentLayout=findViewById(R.id.Entertainment1);
        connectionStatus = findViewById(R.id.connectionStatus);
        dailyActivity = findViewById(R.id.DailyActivity);
        sick = findViewById(R.id.Sick);
        command = findViewById(R.id.Command);
        entertainment = findViewById(R.id.Entertainment);
        //buttons
        foodBtn=findViewById(R.id.food);
        bathroomBtn=findViewById(R.id.Bathroom);
        clothBtn=findViewById(R.id.cloth);
        tempBtn=findViewById(R.id.temperature);
        headacheBtn=findViewById(R.id.headache);
        legPainBtn=findViewById(R.id.legPain);
        throatBtn=findViewById(R.id.throatPain);
        heartBtn=findViewById(R.id.heartPain);
        lightBtn=findViewById(R.id.light);
        cleaninessBtn=findViewById(R.id.cleaniness);
        windowBtn=findViewById(R.id.window);
        bugBtn=findViewById(R.id.bug);
        walkBtn=findViewById(R.id.walk);
        gameBtn=findViewById(R.id.game);
        musicBtn=findViewById(R.id.music);
        tvBtn=findViewById(R.id.tv);
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
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                goodRage = csvLine.split(",");
                try {
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
            String csvLine1;
            while ((csvLine1 = reader1.readLine()) != null) {
                badRage = csvLine1.split(",");
                try {
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
        //Bluetooth
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
        init();
    }
    @Override
    protected void onResume() {
        connect();
        super.onResume();
    }
    @SuppressLint("SetTextI18n")
    private void init() {
        NskAlgoSdk nskAlgoSdk = new NskAlgoSdk();
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
                        tgStreamReader.stop();
                        tgStreamReader.close();
                    }
                    System.gc();
                } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_PAUSE.value) {
                    connectionStatus.setText("paused");

                } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_ANALYSING_BULK_DATA.value) {
                    connectionStatus.setText("analyzing");
                } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_INITED.value || finalState[0] == NskAlgoState.NSK_ALGO_STATE_UNINTIED.value) {
                    connectionStatus.setText("init ed");
                }
            });
        });
    }
    private void connect() {
        raw_data = new short[2560];
        raw_data_index = 0;
        tgStreamReader = new TgStreamReader(bluetoothAdapter, callback);
        if (tgStreamReader.isBTConnected()) {
            // Prepare for connecting
            tgStreamReader.stop();
            tgStreamReader.close();
        }
        tgStreamReader.connect();
    }
    private final TgStreamHandler callback = new TgStreamHandler() {
        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            switch (datatype) {
                case MindDataType.CODE_ATTENTION:
                    short[] attValue = {(short) data};
                    NskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ATT.value, attValue, 1);
                    break;
                case MindDataType.CODE_MEDITATION:
                    short[] medValue = {(short) data};
                    NskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_MED.value, medValue, 1);
                    break;
                case MindDataType.CODE_POOR_SIGNAL:
                    short[] pqValue = {(short) data};
                    NskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_PQ.value, pqValue, 1);
                    break;
                case MindDataType.CODE_RAW:
                    raw_data[raw_data_index++] = (short) data;
                    if (raw_data_index == 2560) {
                        NskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_EEG.value, raw_data, raw_data_index);
                        raw_data_index = 0;
                        Log.e("TAG","raw_data index "+raw_data);
                        if (startAction) {
                            Arrays.sort(raw_data);
                            realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];
                            if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                                Log.e("TAG123", "realTimeRange1_1=" +realTimeRange);
                                showToast("you like the image ", Toast.LENGTH_SHORT);
                                MainActivity.this.runOnUiThread(() -> {
                                    startAction=false;
                                    buttonChecked(currentIndex);
                                });
                            } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) { //check .....
                                showToast("you don't like the image ", Toast.LENGTH_SHORT);
                                currentIndex++;
                                Log.e("TAG","currentIndex"+currentIndex);
                                if (currentIndex ==4 ) {
                                    currentIndex = 0; // Reset to the first button if the end is reached
                                }
                            } else {
                                Log.e("TAG123", "realTimeRange3=" +realTimeRange);
                                showToast("no value matched", Toast.LENGTH_SHORT);
                            }break;
                        }
                        if(dailyButtonClicked) {
                            Arrays.sort(raw_data);
                            realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];
                            if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                                Log.e("TAG123", "realTimeRange2_1=" + realTimeRange);
                                showToast("you like the image ", Toast.LENGTH_SHORT);
                                MainActivity.this.runOnUiThread(() -> {
                                       Log.e("TAG", "SecondBthClicked");
                                       setAdapterByCategory(0,currentIndex);
                               });
                             } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                                Log.e("TAG123", "realTimeRange2=" + realTimeRange);
                                showToast("you don't like the image ", Toast.LENGTH_SHORT);
                                currentIndex++;
                                if (currentIndex == 4) {
                                    currentIndex = 0; // Reset to the first button if the end is reached
                                }
                        } else {
                            Log.e("TAG123", "realTimeRange3_3=" + realTimeRange);
                            showToast("no value matched", Toast.LENGTH_SHORT);
                        }
                    }
                        if(SickBtnClicked){
                        Arrays.sort(raw_data);
                        realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];
                        if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                            Log.e("TAG123", "realTimeRange1=" + realTimeRange);
                            showToast("you like the image ", Toast.LENGTH_SHORT);
                            MainActivity.this.runOnUiThread(() -> {
                                SickBtnClicked=false;
                                setAdapterByCategory(1,currentIndex);
                            });
                        } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                            Log.e("TAG123", "realTimeRange2=" + realTimeRange);
                            showToast("you don't like the image ", Toast.LENGTH_SHORT);
                            currentIndex++;
                            if (currentIndex == 4) {
                                currentIndex = 0; // Reset to the first button if the end is reached
                            }
                        } else {
                            Log.e("TAG123", "realTimeRange3=" + realTimeRange);
                            showToast("no value matched", Toast.LENGTH_SHORT);
                        }


                    }
                        if(helpBtnClicked){
                        Arrays.sort(raw_data);
                        realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];
                        if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                            Log.e("TAG123", "realTimeRange2_1=" +realTimeRange);
                            showToast("you like the image ", Toast.LENGTH_SHORT);
                            MainActivity.this.runOnUiThread(() -> {
                                helpBtnClicked=false;
                                setAdapterByCategory(2,currentIndex);
                            });
                        } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                            Log.e("TAG123", "realTimeRange2=" + realTimeRange);
                            showToast("you don't like the image ", Toast.LENGTH_SHORT);
                            currentIndex++;
                            if (currentIndex ==4 ) {
                                currentIndex = 0; // Reset to the first button if the end is reached
                            }
                        } else {
                            Log.e("TAG123", "realTimeRange3=" +realTimeRange);
                            showToast("no value matched", Toast.LENGTH_SHORT);
                        }
                    }
                        if(entBtnClicked){
                        Arrays.sort(raw_data);
                        realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];
                        if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                            Log.e("TAG123", "realTimeRange1=" + realTimeRange);
                            showToast("you like the image ", Toast.LENGTH_SHORT);
                            MainActivity.this.runOnUiThread(() -> {
                               entBtnClicked=false;
                                setAdapterByCategory(3,currentIndex);
                            });
                        } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                            Log.e("TAG123", "realTimeRange2=" + realTimeRange);
                            showToast("you don't like the image ", Toast.LENGTH_SHORT);
                            currentIndex++;
                            if (currentIndex == 4) {
                                currentIndex = 0; // Reset to the first button if the end is reached
                            }
                        } else {
                            Log.e("TAG123", "realTimeRange3=" + realTimeRange);
                            showToast("no value matched", Toast.LENGTH_SHORT);
                        }
                    }
                        if(listViewButtonClicked){
                            Arrays.sort(raw_data);
                            realTimeRange = raw_data[raw_data.length - 1] - raw_data[0];
                            if ((realTimeRange <= goodMax) && ((goodMin) < realTimeRange)) {
                                Log.e("TAG123", "realTimeRange3_1=" + realTimeRange);
                                MainActivity.this.runOnUiThread(() -> {
                                    listViewButtonClicked=false;
                                    triggerItemClick(currentIndex);
                                });
                            } else if ((realTimeRange <= badMax) && ((badMin) < realTimeRange)) {
                                Log.e("TAG123", "realTimeRange3_2=" + realTimeRange);
                                showToast("you don't like the image ", Toast.LENGTH_SHORT);
                                currentIndex++;
                                if (currentIndex == listView.getCount()) {
                                    currentIndex = 0; // Reset to the first button if the end is reached
                                }
                            } else {
                                Log.e("TAG123", "realTimeRange3_3=" + realTimeRange);
                                showToast("no value matched", Toast.LENGTH_SHORT);
                            }
                        }
                    } break;
                default:
                    break;
            }
        }

        @Override
        public void onChecksumFail(byte[] bytes, int i, int i1) {}
        @Override
        public void onRecordFail(int flag) {Log.e("TAG", "onRecordFail: " + flag);}
        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
            Log.d("TAG", "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    showToast("Connecting", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    tgStreamReader.start();
                    showToast("Connected", Toast.LENGTH_SHORT);
                    int algoTypes = NskAlgoType.NSK_ALGO_TYPE_ATT.value;

                    int ret = NskAlgoSdk.NskAlgoInit(algoTypes, getFilesDir().getAbsolutePath());
                    if (ret == 0) {
                        showToast("Receiving data ", Toast.LENGTH_LONG);
                        NskAlgoSdk.NskAlgoStart(false);
                    }
                    break;
                case ConnectionStates.STATE_WORKING:
                    MainActivity.this.runOnUiThread(() -> showToast("click to compare the data", Toast.LENGTH_LONG));
                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    showToast("Get data time out!", Toast.LENGTH_SHORT);

                    if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
                        tgStreamReader.stop();
                        tgStreamReader.close();
                    }
                    break;
                case ConnectionStates.STATE_STOPPED:
                    showToast("stopped", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    // Do something when disconnected
                    Log.e("TAG", "disconnected");
                    showToast("Disconnected", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_ERROR:
                    showToast("error", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_FAILED:
                    Log.e("TAG", "failed");
                    break;
            }
        }
    };

    private void buttonChecked (int num) {
        if(num==0){
            dailyAction();
        }
        else if ( num==1){
            Sick();
        }
        else if(num==2){
            command();

        }else if(num==3){
            entertainment();
        }
        currentIndex=0;
    }
    private void dailyAction() {
        dailyButtonClicked=true;
        dailyActivity.setBackgroundColor(R.drawable.btn_clicked);
        entertainmentLayout.setVisibility(View.GONE);
        healthLayout.setVisibility(View.GONE);
        helpLayout.setVisibility(View.GONE);
        dailyActivityLayout.setVisibility(View.VISIBLE);

        if (dailyButtonClicked){
            return;
        }else {dailyButtonClicked=true;}
    }
    public void Sick(){
        SickBtnClicked=true;
        sick.setBackgroundColor(R.drawable.btn_clicked);
        entertainmentLayout.setVisibility(View.GONE);
        healthLayout.setVisibility(View.VISIBLE);
        dailyActivityLayout.setVisibility(View.GONE);
        helpLayout.setVisibility(View.GONE);
        if(SickBtnClicked){
            return;
        }
    }
    public void command(){
        helpBtnClicked=true;
        command.setBackgroundColor(R.drawable.btn_clicked);
        helpLayout.setVisibility(View.VISIBLE);
        dailyActivityLayout.setVisibility(View.GONE);
        entertainmentLayout.setVisibility(View.GONE);
        healthLayout.setVisibility(View.GONE);
        if(helpBtnClicked){
            return;
        }

    }
    public void  entertainment(){
        entBtnClicked=true;
        entertainment.setBackgroundColor(R.drawable.btn_clicked);
        dailyActivityLayout.setVisibility(View.GONE);
        helpLayout.setVisibility(View.GONE);
        healthLayout.setVisibility(View.GONE);
        entertainmentLayout.setVisibility(View.VISIBLE);
        if (entBtnClicked) {
            return;
        }
    }
     private void setAdapterByCategory(int category, int num) {
        listViewButtonClicked=true;
        ArrayAdapter<?> adapter;
        switch (category) {
            case 0: // Daily Activity
                switch (num) {
                    case 0:
                        foodBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = foodAdapter;
                        break;
                    case 1:
                        bathroomBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = toiletAdapter;
                        break;
                    case 2:
                        clothBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = clothAdapter;
                        break;
                    case 3:
                        tempBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = temperatureAdapter;
                        break;
                    default:
                        return;
                }
                break;
            case 1: // Health
                switch (num) {
                    case 0:
                        heartBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = headacheAdapter;
                        break;
                    case 1:
                        legPainBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = legPainAdapter;
                        break;
                    case 2:
                        heartBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = heartAdapter;
                        break;
                    case 3:
                        throatBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = throatDiscomfortAdapter;
                        break;
                    default:
                        return;
                }
                break;
            case 2: // Help
                switch (num) {
                    case 0:
                        lightBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = lightAdapter;
                        break;
                    case 1:
                        cleaninessBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = cleanlinessAdapter;
                        break;
                    case 2:
                        windowBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = windowAdapter;
                        break;
                    case 3:
                        bugBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = bugAdapter;
                        break;
                    default:
                        return;
                }
                break;
            case 3: // Entertainment
                switch (num) {
                    case 0:
                        walkBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = walkAdapter;
                        break;
                    case 1:
                        gameBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = gameAdapter;
                        break;
                    case 2:
                        musicBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = musicAdapter;
                        break;
                    case 3:
                        tvBtn.setBackgroundColor(R.drawable.btn_clicked);
                        adapter = tvAdapter;
                        break;
                    default:
                        return;
                }
                break;
            default:
                return;
        }
        currentIndex=0;
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        if(listViewButtonClicked){
            return;
        }
    }

    private void triggerItemClick(int currentIndex) {
        String selectedListViewItem;
        listView.performItemClick(
                listView.getChildAt(currentIndex),
               currentIndex,
                listView.getAdapter().getItemId(currentIndex));
                selectedListViewItem= (String) listView.getAdapter().getItem(currentIndex);
                showToast( "" + selectedListViewItem, Toast.LENGTH_SHORT);
               }
    public void showToast(final String msg, final int timeStyle) {
        MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, timeStyle).show());
    }
}