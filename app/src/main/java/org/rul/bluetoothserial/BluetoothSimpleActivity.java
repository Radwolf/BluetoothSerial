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
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
import org.rul.meapi.common.*;
import org.rul.meapi.device.MeMatrixLedDevice;
import org.rul.meapi.device.MeMotorDevice;
import org.rul.meapi.device.MeUltrasonicDevice;
import org.rul.meapi.model.CommandSimple;
import org.rul.meapi.service.GoogleSheetsService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class BluetoothSimpleActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    static final String dbg = "BluetoothSimpleActivity";

    MeMatrixLedDevice matrixLedDevice;

    TextView tvLogsBluetooth;
    ImageButton buttonForward;
    ImageButton buttonLeft;
    ImageButton buttonRight;
    ImageButton buttonBackward;
    Button bRed;

    //API Google Sheets
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "raul.gomo";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };
    //Fin Api

    // Well known SPP UUID
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your bluetooth devices MAC address
//    private static String address = "00:07:02:03:10:A3";  //Address mbot
    //private static String address = "00:80:5A:46:22:50";  //address pen bluetooth concentronic
    private static String address = "00:14:7D:DA:71:09";  //address pen bluetooth trust

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

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        final MeMotorDevice motorDevice = new MeMotorDevice("Motor1", MeConstants.PORT_M1, 1);
        matrixLedDevice = new MeMatrixLedDevice("Facets", 2);
        bRed = (Button) findViewById(R.id.bRed);
        bRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResultsFromApi();
            }
        });
        /*buttonForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();

//                        CommandSimple commandGiroDirecto = motorDevice.giroDirecto(100);
//                        blt.bluetoothWrite(commandGiroDirecto.getCadena());
//                        tvLogsBluetooth.append(String.format("\n%s", commandGiroDirecto.toString()));

                        //String cadena = "Prueba\n\r";
                        //blt.bluetoothWrite(cadena.getBytes());

                        CommandSimple commandUSGetDistancia = ultrasonicDevice.getDistancia();
                        blt.bluetoothWrite(commandUSGetDistancia.getCadena());
                        tvLogsBluetooth.append(String.format("\n%s", commandUSGetDistancia.toString()));
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
        });*/
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
                    parseMsg(rx);
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

    void parseMsg(int[] msg) {

//			Log.d("mb", "parseMSG:"+msg.length);
        if (msg.length > 2) {
            if ((msg[2] & 0xff) == MeConstants.VERSION_INDEX) {
                int len = msg[4];
                String hexStr = "";
                for (int i = 0; i < len; i++) {
                    hexStr += String.format("%c", msg[5 + i]);
                }
                Log.d("mb", "version:" + hexStr);
                if (engineState == STAGE_IDLE) {
                    stopTimer();
                }
            } else {
                int moduleIndex = msg[2];
                if (msg.length < 7) return;
                float f = 0.0f;
                if (msg[3] == 2) {
                    if (msg.length > 7) {
                        int tint = (msg[4] & 0xff) + ((msg[5] & 0xff) << 8) + ((msg[6] & 0xff) << 16) + ((msg[7] & 0xff) << 24);
                        f = Float.intBitsToFloat(tint);
                    }
                } else if (msg[3] == 1) {
                    f = (msg[4] & 0xff);
                } else if (msg[3] == 3) {
                    f = (msg[4] & 0xff) + ((msg[5] & 0xff) << 8);
                }
                //rx:FF 55 04 04 07 31 2E 31 2E 31 30 32 0D 0A

                tvLogsBluetooth.append("\nLa respuesta: " + f);

            }
        } else if (msg.length < 3) {
            return;
        }
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


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            tvLogsBluetooth.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute("Cara :)");
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    tvLogsBluetooth.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                BluetoothSimpleActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<String, Void, ByteBuffer> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected ByteBuffer doInBackground(String... params) {
            try {
                return getDataFromApi(params[0]);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        private ByteBuffer getDataFromApi(String face) throws IOException {
            ByteBuffer cadenaFace = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
            String spreadsheetId = "1jCvYSZTdo5_FheTGDo9zD6t1Rqb3yBIs3cHayob29Ns";
            String range = String.format("%s!A1:P8", face);
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.size() == 0) {
                System.out.println("No data found.");
            } else {
                double exponente = 0;
                for (List row : values) {
                    double valorFila = 128 / (Math.pow(2, exponente));
                    for(int i = 0; i < cadenaFace.limit(); i++){
                        if("1".equals(row.get(i))) {
                            int valorCelda = cadenaFace.get(i);
                            valorCelda += valorFila;
                            cadenaFace.put(i, org.rul.meapi.common.Utils.intToByte(valorCelda));
                        }
                    }
                    //System.out.println(Utils.bytesToHexString(cadenaFace.array()));
                    exponente++;
                }
                //System.out.println(Utils.bytesToHexString(cadenaFace.array()));
            }

            return cadenaFace;
        }



        @Override
        protected void onPreExecute() {
            tvLogsBluetooth.setText("Empezamos a procesar");
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(ByteBuffer cadena) {
//            mProgress.hide();
            if (cadena == null) {
                tvLogsBluetooth.setText("Face no encontrada");
            } else {
                CommandSimple commandFace = matrixLedDevice.pintarFace(cadena, 0, 0);
                blt.bluetoothWrite(commandFace.getCadena());
            }
        }

        @Override
        protected void onCancelled() {
//            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    tvLogsBluetooth.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                tvLogsBluetooth.setText("Request cancelled.");
            }
        }
    }
}