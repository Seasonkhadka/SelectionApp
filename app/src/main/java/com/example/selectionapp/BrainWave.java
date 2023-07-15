package com.example.selectionapp;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoState;
import com.neurosky.AlgoSdk.NskAlgoType;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

public class BrainWave extends Service {
    private BluetoothAdapter bluetoothAdapter;
    private NskAlgoSdk nskAlgoSdk;
    private String connectionStatus;
    private TgStreamReader tgStreamReader;
    private short raw_data[] = {0};
    private int raw_data_index = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connectToBluetoothDevice();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectFromBluetoothDevice();
    }

    private void connectToBluetoothDevice() {
        try {
            // (1) Make sure that the device supports Bluetooth and Bluetooth is on
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Toast.makeText(
                        this,
                        "Please enable your Bluetooth and re-run this program !",
                        Toast.LENGTH_LONG).show();
            } else {
                init();
                connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "error:" + e.getMessage());

        }

    }

    private void init() {
        nskAlgoSdk = new NskAlgoSdk();
        tgStreamReader = new TgStreamReader(bluetoothAdapter, callback);

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
            //Log.e("TAG", "NskAlgoSdkStateChangeListener: state: " + stateStr + ", reason: " + reasonStr);

            final int[] finalState = {state};

            // change UI elements here
            if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_RUNNING.value || finalState[0] == NskAlgoState.NSK_ALGO_STATE_COLLECTING_BASELINE_DATA.value) {
                connectionStatus = "running";
            } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_STOP.value) {
                raw_data = null;
                raw_data_index = 0;
                connectionStatus = "Stopped";
                if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

                    // Prepare for connecting
                    tgStreamReader.stop();
                    tgStreamReader.close();
                }

                System.gc();
            } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_PAUSE.value) {
                connectionStatus = "paused";

            } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_ANALYSING_BULK_DATA.value) {
                connectionStatus = "analyzing";

            } else if (finalState[0] == NskAlgoState.NSK_ALGO_STATE_INITED.value || finalState[0] == NskAlgoState.NSK_ALGO_STATE_UNINTIED.value) {
                connectionStatus = "inited";
            }
        });
    }

    ;

    private void connect() {
        raw_data = new short[2560];
        raw_data_index = 0;


        if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

            // Prepare for connecting
            tgStreamReader.stop();
            tgStreamReader.close();
        }

        // (4) Demo of  using connect() and start() to replace connectAndStart(),
        // please call start() when the state is changed to STATE_CONNECTED
        tgStreamReader.connect();
    }

    private final TgStreamHandler callback = new TgStreamHandler() {

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
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStatesChanged(int connectionStates) {

            Log.d("TAG", "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    showToast("Connecting", Toast.LENGTH_SHORT);
                    Log.e("TAG", "ConNECTING");
                    // Do something when connecting
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    tgStreamReader.start();
                    Log.e("TAG", "Connected");
                    showToast("Connected", Toast.LENGTH_SHORT);
                    int algoTypes = NskAlgoType.NSK_ALGO_TYPE_ATT.value;

                    int ret = nskAlgoSdk.NskAlgoInit(algoTypes, getFilesDir().getAbsolutePath());
                    if (ret == 0) {
                        Log.e("TAG", "receiving data");
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

                    showToast("click to compare the data", Toast.LENGTH_LONG);

                    //receivedata();


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

        @Override
        public void onChecksumFail(byte[] bytes, int i, int i1) {

        }

        @Override
        public void onRecordFail(int i) {

        }

        public void showToast(final String msg, final int timeStyle) {
           // Toast.makeText(getApplicationContext(), msg, timeStyle).show();
        }
    };


    private void disconnectFromBluetoothDevice() {
        /*if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
                Log.d("TAG", "Disconnected from Bluetooth device");
            } catch (IOException e) {
                Log.e(TAG, "Error closing Bluetooth socket: " + e.getMessage());
            }
        }*/
    }

    /*
    private NskAlgoSdk nskAlgoSdk;
    private BluetoothAdapter bluetoothAdapter;
    //int currentIndex = 0;
    public static final String ACTION_ARRAY_UPDATED = "com.example.ACTION_ARRAY_UPDATED";
    //OptionActivity opt;
    private boolean startAction = false;
    private boolean firstButtonClicked= false;
    public String status;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      // status = connectionStatus.getText().toString();

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

        }


        init();
        connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind (Intent intent){

        return null;
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
            //Log.e("TAG", "NskAlgoSdkStateChangeListener: state: " + stateStr + ", reason: " + reasonStr);

            final int[] finalState = {state};
            mainActivity.runOnUiThread(() -> {
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




public void onDestroy(){
    super.onDestroy();
}
    private void updateArray(short[] rawData) {
        // Update the array

        // Broadcast the updated array
        Intent intent = new Intent(ACTION_ARRAY_UPDATED);
        intent.putExtra("raw_data", rawData);
        sendBroadcast(intent);
    }


*/
}

