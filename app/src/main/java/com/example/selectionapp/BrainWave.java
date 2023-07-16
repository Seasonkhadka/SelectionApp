package com.example.selectionapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
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
    private BrainWaveListener brainWaveListener;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setBrainWaveListener(BrainWaveListener listener) {
        brainWaveListener = listener;
    }

    public interface BrainWaveListener {
        void getData(short data);

        void resetData();
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

    private void connectToBluetoothDevice() {
        try {
            // (1) Make sure that the device supports Bluetooth and Bluetooth is on
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Toast.makeText(
                        this,
                        "Please enable your Bluetooth and re-run this program!",
                        Toast.LENGTH_LONG).show();
            } else {
                init();
                connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error: " + e.getMessage());
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
            Log.e("TAG", "NskAlgoSdkStateChangeListener: state: " + stateStr + ", reason: " + reasonStr);

            final int finalState = state;
            // change UI elements here
            if (finalState == NskAlgoState.NSK_ALGO_STATE_RUNNING.value || finalState == NskAlgoState.NSK_ALGO_STATE_COLLECTING_BASELINE_DATA.value) {
                connectionStatus = "running";
            } else if (finalState == NskAlgoState.NSK_ALGO_STATE_STOP.value) {
                if (brainWaveListener != null) {
                    brainWaveListener.resetData();
                }
                connectionStatus = "stopped";
                if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
                    // Prepare for connecting
                    tgStreamReader.stop();
                    tgStreamReader.close();
                }
                System.gc();
            } else if (finalState == NskAlgoState.NSK_ALGO_STATE_PAUSE.value) {
                connectionStatus = "paused";
            } else if (finalState == NskAlgoState.NSK_ALGO_STATE_ANALYSING_BULK_DATA.value) {
                connectionStatus = "analyzing";
            } else if (finalState == NskAlgoState.NSK_ALGO_STATE_INITED.value || finalState == NskAlgoState.NSK_ALGO_STATE_UNINTIED.value) {
                connectionStatus = "inited";
            }
        });
    }

    private void connect() {
        if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
            // Prepare for connecting
            tgStreamReader.stop();
            tgStreamReader.close();
        }

        // Demo of using connect() and start() to replace connectAndStart()
        // Please call start() when the state is changed to STATE_CONNECTED
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
                        brainWaveListener.getData((short) data);

                    Log.e("TAG","errror : "+(short) data);
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
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    tgStreamReader.start();
                    showToast("Connected", Toast.LENGTH_SHORT);
                    int algoTypes = NskAlgoType.NSK_ALGO_TYPE_ATT.value;

                    int ret = nskAlgoSdk.NskAlgoInit(algoTypes, getFilesDir().getAbsolutePath());
                    if (ret == 0) {
                        showToast("Receiving data", Toast.LENGTH_LONG);
                        nskAlgoSdk.NskAlgoStart(false);
                    }
                    break;
                case ConnectionStates.STATE_WORKING:
                    showToast("Click to compare the data", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    showToast("Get data time out!", Toast.LENGTH_SHORT);

                    if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
                        tgStreamReader.stop();
                        tgStreamReader.close();
                    }
                    break;
                case ConnectionStates.STATE_STOPPED:
                    showToast("Stopped", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    showToast("Disconnected", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_ERROR:
                    showToast("Error", Toast.LENGTH_LONG);
                    break;
                case ConnectionStates.STATE_FAILED:
                    showToast("Failed", Toast.LENGTH_LONG);
                    break;
            }
        }

        @Override
        public void onChecksumFail(byte[] bytes, int i, int i1) {
            // Handle checksum failure
        }

        @Override
        public void onRecordFail(int i) {
            // Handle record failure
        }

        public void showToast(final String msg, final int timeStyle) {
            Log.e("TAG","msg : "+msg);
            //Toast.makeText(getApplicationContext(), msg, timeStyle).show();
        }
    };

    public class LocalBinder extends Binder {
        BrainWave getBrainWave() {
            return BrainWave.this;
        }
    }

}
