package org.rul.bluetoothserial;

/**
 * Created by Rul on 25/05/2016.
 */


import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.rul.bluetoothserial.adapter.DeviceListAdapter;
import org.rul.bluetoothserial.bluetooth.Bluetooth;
import org.rul.bluetoothserial.bluetooth.BluetoothLE;
import org.rul.bluetoothserial.bluetooth.MeTimer;
import org.rul.meapi.common.MeConstants;
import org.rul.meapi.device.MeMotorDevice;
import org.rul.meapi.model.CommandSimple;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class BluetoothLESimpleActivity extends Activity {
    static final String dbg = "LayoutView";
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

    private static final String TAG = "ConnectTest";
    TextView tvLogsBluetooth;
    // Well known SPP UUID
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your bluetooth devices MAC address
//    private static String address = "00:07:02:03:10:A3";  //Address mbot
    private static String address = "00:80:5A:46:22:50";  //address pen bluetooth
    private OutputStream outStream = null;


    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    private ByteBuffer cadenaExcel = null;


    static float firmVersion = 0f;

    // bluetooth related
    MenuItem blticon;
    Bluetooth blt;
    PopupWindow popupBtSelect;
    DeviceListAdapter devAdapter;
    ArrayAdapter<String> pairAdapter;
    ProgressDialog uploadPb;

    // Mscript related
    static final int STAGE_IDLE = 0; // the layout is editable in this state
    static final int STAGE_PROBING = 1;
    static final int STAGE_DONWLOAD_PROCESS = 2;
    static final int STAGE_DONWLOAD_FIN = 2;
    static final int STAGE_RUN = 4;
    int engineState = STAGE_IDLE;
    Timer mTimer;
    TimerTask mTimerTask;

    private Button btnRefresh;
    ImageButton runBtn;

    private int screenWidth,screenHeight;
    FrameLayout contentView;
    UpgradeFirm firmup;

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "In onCreate()");

        setContentView(R.layout.activity_main);
        tvLogsBluetooth = (TextView) findViewById(R.id.out);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            BluetoothLE.sharedManager().setup(this);
            BluetoothLE.sharedManager().leHandler = mLeHandler;
            //BluetoothLE.sharedManager().start();
            devAdapter = new DeviceListAdapter(this,BluetoothLE.sharedManager().getDeviceList(),R.layout.device_list_item);
            MeTimer.startWrite();

            if(engineState==STAGE_IDLE){
                if(BluetoothLE.sharedManager().isConnected()){
                    if(firmVersion==0){
                        stopTimer(); // stop previous version probing timer
                        Log.d("firmversion", "UNKNOW");
                        engineState = STAGE_IDLE;
                    }else{
                        engineState = STAGE_RUN;
                    }
                    startTimer(200);
                }else{
                    showBtSelect();
                }
            }else{
                stopTimer();
                engineState = STAGE_IDLE;
                runBtn.setImageResource(R.drawable.run_button);
            }
/*            BluetoothLE.sharedManager().selectDevice(address);
            System.out.println(BluetoothLE.sharedManager().isConnected());

            MeMotorDevice meMotorDeviceI = new MeMotorDevice("Prueba giro inverso", MeConstants.PORT_M1, 1);
            MeMotorDevice meMotorDeviceD = new MeMotorDevice("Prueba giro directo", MeConstants.PORT_M2, 2);
            CommandSimple commandSimpleI = meMotorDeviceI.giroInverso((byte) 156);

            BluetoothLE.sharedManager().writeBuffer(commandSimpleI.getCadena());
            BluetoothLE.sharedManager().writeSingleBuffer();*/
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("mb", "start");
    }

    @Override
    protected void onStop(){
        stopTimer();
        super.onStop();
        Log.d("mb", "stop");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("mb", "resume");
    }

    final Handler mLeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case BluetoothLE.MSG_CONNECTED:
                {
                    devLEListChanged();
                    blticon.setIcon(R.drawable.bluetooth_on);
                    Intent intent = new Intent(BluetoothLESimpleActivity.this,DialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg", getString(R.string.connected));
                    startActivity(intent);
                    firmVersion = 0.0f;
                    startTimer(1000);
                }
                break;
                case BluetoothLE.MSG_DISCONNECTED:
                    stopTimer();
                    devLEListChanged();
                    blticon.setIcon(R.drawable.bluetooth_off);
                    break;
                case BluetoothLE.MSG_CONNECT_FAIL:
                {
                    Intent intent = new Intent(BluetoothLESimpleActivity.this,DialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg", getString(R.string.connectfail));
                    startActivity(intent);
                    Log.d(dbg,"connect fail");
                }
                break;
                case BluetoothLE.MSG_SCAN_START:{
                    /*if(btnRefresh!=null){
                        AnimationDrawable d=(AnimationDrawable)btnRefresh.getCompoundDrawables()[0];
                        d.start();
                    }*/
                }
                break;
                case BluetoothLE.MSG_SCAN_END:{
                    /*if(btnRefresh!=null){
                        AnimationDrawable d=(AnimationDrawable)btnRefresh.getCompoundDrawables()[0];
                        d.stop();
                    }*/
                }
                break;
                case BluetoothLE.MSG_RX:
                    int[] rx = (int[]) msg.obj;
                    //parseMsg(rx);
                    break;
                case BluetoothLE.MSG_FOUNDDEVICE:
                    devLEListChanged();
                    break;
                case BluetoothLE.MSG_DISCOVERY_FINISHED:{
                    if(btnRefresh!=null){
                        AnimationDrawable d=(AnimationDrawable)btnRefresh.getCompoundDrawables()[0];
                        d.stop();
                    }
                }
                break;
                case BluetoothLE.MSG_CONNECTING:{
                    Intent intent =new Intent(BluetoothLESimpleActivity.this,DialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg", getString(R.string.connecting));
                    startActivity(intent);
                }
                break;
                case MeConstants.MSG_VALUECHANGED:
                    byte[] cmd = (byte[]) msg.obj;
                    BluetoothLE.sharedManager().writeBuffer(cmd);
                    break;
            }
        }
    };

    public void devListChanged(){
        if(blt!=null){
            devAdapter.updateData (blt.getBtDevList());
            devAdapter.notifyDataSetChanged();
        }
    }
    public void devLEListChanged(){
        List<String> list = BluetoothLE.sharedManager().getDeviceList();
        if(devAdapter!=null){
            devAdapter.updateData (list);
            devAdapter.notifyDataSetChanged();
        }
    }

    void startTimer(int interval){
        if(mTimerTask == null){
            mTimerTask = new TimerTask(){
                @Override
                public void run() {
                    if(engineState==STAGE_PROBING){
                        byte[] queryStr = firmup.getProbeCmd();
                        BluetoothLE.sharedManager().resetIO(queryStr);
                    }
                }
            };
            if(mTimerTask !=null){
                mTimer = new Timer(true);
                mTimer.schedule(mTimerTask,600,interval);
            }
        }
    }

    void stopTimer(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }

        if(mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    void showBtSelect(){

        if(blt==null){
            BluetoothLE.sharedManager().start();
        }

        ListView devlist = (ListView) findViewById(R.id.list_v);
        //ListView pairlist = (ListView) popupBtDevLayout.findViewById(R.id.btpairList);
        devlist.setAdapter(devAdapter);
        //pairlist.setAdapter(pairAdapter);

        devlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{ // the bluetooth list may vary
                    if(blt!=null){
                        BluetoothDevice dev = blt.btDevices.get(position);
                        if(blt.connDev!=null && blt.connDev.equals(dev)){
                            // disconnect device
                            blt.bluetoothDisconnect(blt.connDev);
                            return;
                        }
                        blt.bluetoothConnect(dev);
                    }else{
                        if(BluetoothLE.sharedManager().isConnected()){
                            BluetoothLE.sharedManager().close();
                            devLEListChanged();
                        }else{
                            BluetoothLE.sharedManager().selectDevice(position);
                            BluetoothLE.sharedManager().isConnected();
                        }
                    }
                }catch(Exception e){
                    Log.e(dbg, e.toString());
                }
            }
        });
        btnRefresh = (Button) findViewById(R.id.buttonRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                BluetoothLE.sharedManager().stop();
                BluetoothLE.sharedManager().clear();
                devLEListChanged();
                BluetoothLE.sharedManager().start();
            }
        });
/*
        Button btnOk = (Button)popupBtDevLayout.findViewById(R.id.popupOkBtn);
        btnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupBtSelect.dismiss();
                if(blt!=null){
                    if(blt.connDev!=null){
                        engineState = STAGE_RUN;
              *//*  if(firmVersion==0){
                    stopTimer(); // stop previous version probing timer
                    Log.d("firmversion", "UNKNOW");
                    showUpgradeDialog();
                }
                stopTimer();
                startTimer(200);
                enableAllModule();
                runBtn.setImageResource(R.drawable.pause_button);*//*
                    }
                }else{
                    if(BluetoothLE.sharedManager().isConnected()){
                        engineState = STAGE_RUN;
                *//*if(firmVersion==0){
                    stopTimer(); // stop previous version probing timer
                    Log.d("firmversion", "UNKNOW");
                    showUpgradeDialog();
                }
                stopTimer();
                startTimer(200);
                enableAllModule();*//*
                        runBtn.setImageResource(R.drawable.pause_button);
                    }
                }
            }
        });
*/
    }
}