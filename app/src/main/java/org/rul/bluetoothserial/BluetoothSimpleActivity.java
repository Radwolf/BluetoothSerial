package org.rul.bluetoothserial;

/**
 * Created by Rul on 25/05/2016.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.SheetsScopes;

import org.rul.bluetoothserial.adapter.DeviceListAdapter;
import org.rul.bluetoothserial.bluetooth.Bluetooth;
import org.rul.bluetoothserial.bluetooth.BluetoothLE;
import org.rul.bluetoothserial.bluetooth.MeTimer;
import org.rul.meapi.common.MeConstants;
import org.rul.meapi.device.MeMotorDevice;
import org.rul.meapi.model.CommandSimple;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothSimpleActivity extends Activity {
    static final String dbg = "BluetoothSimpleActivity";
    TextView tvLogsBluetooth;
    ImageButton buttonForward;
    ImageButton buttonLeft;
    ImageButton buttonRight;
    ImageButton buttonBackward;

    // Well known SPP UUID
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your bluetooth devices MAC address
//    private static String address = "00:07:02:03:10:A3";  //Address mbot
    private static String address = "00:80:5A:46:22:50";  //address pen bluetooth


    private Intent serviceIntent = new Intent("org.rul.bluetoothserial");
    private boolean isexit = false;
    private boolean hastask = false;
    private boolean ispause = false;
    private TimerTask task;
    private Timer texit = new Timer();

    Bluetooth blt;
    // Mscript related
    static final int STAGE_IDLE = 0; // the layout is editable in this state
    static final int STAGE_PROBING = 1;
    static final int STAGE_DONWLOAD_PROCESS = 2;
    static final int STAGE_DONWLOAD_FIN = 2;
    static final int STAGE_RUN = 4;
    int engineState = STAGE_IDLE;

    DeviceListAdapter devAdapter;

    Timer mTimer;
    TimerTask mTimerTask;

    UpgradeFirm firmup;

    private Button btnRefresh;
    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(dbg, "In onCreate()");

        setContentView(R.layout.activity_main);
        tvLogsBluetooth = (TextView) findViewById(R.id.out);
        buttonForward = (ImageButton) findViewById(R.id.buttonForward);

        serviceIntent.setPackage(BluetoothSimpleActivity.class.getPackage().getName());

        startService(serviceIntent);
        task = new TimerTask() {
            public void run() {
                isexit = false;
                hastask = true;
            }
        };

        blt = Bluetooth.sharedManager();
        blt.mHandler = mHandler;
        devAdapter = new DeviceListAdapter(this,blt.getBtDevList(),R.layout.device_list_item);

        if(blt!=null){
            if(blt.connDev==null){
                showBtSelect();
            }else{
                engineState = STAGE_RUN;
                startTimer(200);
            }
        }else{
            stopTimer();
            engineState = STAGE_IDLE;
        }

        final MeMotorDevice motorDevice = new MeMotorDevice("Motor1", MeConstants.PORT_M1, 1);
        buttonForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        CommandSimple commandGiroDirecto = motorDevice.giroDirecto(100);
                        //blt.bluetoothWrite(commandGiroDirecto.getCadena());
                        String cadena = "Prueba\n\r";
                        blt.bluetoothWrite(cadena.getBytes());
                        //tvLogsBluetooth.append(String.format("\n%s", commandGiroDirecto.toString()));
                        tvLogsBluetooth.append("\nPulso el botón avanzar");
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        CommandSimple commandStop = motorDevice.stop();
                        //blt.bluetoothWrite(commandStop.getCadena());
                        tvLogsBluetooth.append(String.format("\n%s", commandStop.toString()));
                        tvLogsBluetooth.append("\nSuelto el botón avanzar");
                        // Your action here on button click
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(dbg, "...In onPause()...");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(dbg, "start");
    }

    @Override
    protected void onStop(){
        stopTimer();
        super.onStop();
        Log.d(dbg, "stop");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(dbg, "resume");
    }

    // --------------- BULETOOTH BELOW ---------------------
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case Bluetooth.MSG_CONNECTED:
                {
                    Intent intent = new Intent(BluetoothSimpleActivity.this,DialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg", getString(R.string.connected));
                    startActivity(intent);
                    startTimer(1000);
                }
                break;
                case Bluetooth.MSG_DISCONNECTED:
                    stopTimer();
                    devListChanged();
                    //blticon.setIcon(R.drawable.bluetooth_off);
                    break;
                case Bluetooth.MSG_CONNECT_FAIL:
                {
                    Intent intent = new Intent(BluetoothSimpleActivity.this,DialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg", getString(R.string.connectfail));
                    startActivity(intent);
                    Log.d(dbg,"connect fail");
                }
                break;
                case Bluetooth.MSG_RX:
                    int[] rx = (int[]) msg.obj;
                    Log.d(dbg, rx.toString());
                    break;
                case Bluetooth.MSG_FOUNDDEVICE:
                    devListChanged();
                    break;
                case Bluetooth.MSG_DISCOVERY_FINISHED:{
                   /* if(btnRefresh!=null){
                        AnimationDrawable d=(AnimationDrawable)btnRefresh.getCompoundDrawables()[0];
                        d.stop();
                    }*/
                }
                break;
                case MeConstants.MSG_VALUECHANGED:
                    byte[] cmd = (byte[]) msg.obj;
                    if(blt!=null){
                        blt.bluetoothWrite(cmd);
                    }else{
                        BluetoothLE.sharedManager().writeBuffer(cmd);
                    }
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
                        Log.d(dbg, "El bluetoot no encontrado porque está a null");
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
                if(blt!=null){
                    if(!blt.isDiscovery()){
                        blt.devListClear();
                        devListChanged();
                        Log.i(dbg,"startDiscovery");
                        blt.startDiscovery();
                    }
                }
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            if(isexit == false){
                isexit = true;
                Toast.makeText(getApplicationContext(), getString(R.string.pressbackagain), Toast.LENGTH_SHORT).show();
                if(!hastask) {
                    texit.schedule(task, 2000);
                }
            }else{
                //BluetoothAdapter.getDefaultAdapter().disable();
                stopService(serviceIntent);
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}