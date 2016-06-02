package org.rul.bluetoothserial;

/**
 * Created by Rul on 25/05/2016.
 */


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

import com.google.api.services.sheets.v4.model.*;

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
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.mortbay.jetty.Main;
import org.rul.bluetoothserial.adapter.DeviceListAdapter;
import org.rul.bluetoothserial.bluetooth.Bluetooth;
import org.rul.bluetoothserial.bluetooth.BluetoothLE;
import org.rul.bluetoothserial.bluetooth.MeTimer;
import org.rul.meapi.common.MeConstants;
import org.rul.meapi.device.MeMatrixLedDevice;
import org.rul.meapi.device.MeMotorDevice;
import org.rul.meapi.model.CommandSimple;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity
        implements EasyPermissions.PermissionCallbacks {
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
    private static String address = "00:07:02:03:10:A3";  //Address mbot
//    private static String address = "00:80:5A:46:22:50";  //address pen bluetooth
    private OutputStream outStream = null;

    ImageButton buttonForward;
    ImageButton buttonLeft;
    ImageButton buttonRight;
    ImageButton buttonBackward;

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
        buttonForward = (ImageButton) findViewById(R.id.buttonForward);
        buttonLeft = (ImageButton) findViewById(R.id.buttonLeft);
        buttonRight = (ImageButton) findViewById(R.id.buttonRight);
        buttonBackward = (ImageButton) findViewById(R.id.buttonBackward);
        contentView = (FrameLayout)findViewById(R.id.content);
        contentView.getForeground().setAlpha(0);

        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = window.getDefaultDisplay().getWidth();
        screenHeight = window.getDefaultDisplay().getHeight();

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            blt = Bluetooth.sharedManager();
            blt.mHandler = mHandler;
            devAdapter = new DeviceListAdapter(this,blt.getBtDevList(),R.layout.device_list_item);
        }else{
            BluetoothLE.sharedManager().setup(this);
            BluetoothLE.sharedManager().leHandler = mLeHandler;
            devAdapter = new DeviceListAdapter(this,BluetoothLE.sharedManager().getDeviceList(),R.layout.device_list_item);
            MeTimer.startWrite();
            BluetoothLE.sharedManager().selectDevice(address);
            System.out.println(BluetoothLE.sharedManager().isConnected());

            MeMotorDevice meMotorDeviceI = new MeMotorDevice("Prueba giro inverso", MeConstants.PORT_M1, 1);
            MeMotorDevice meMotorDeviceD = new MeMotorDevice("Prueba giro directo", MeConstants.PORT_M2, 2);
            CommandSimple commandSimpleI = meMotorDeviceI.giroInverso((byte) 156);

            BluetoothLE.sharedManager().writeBuffer(commandSimpleI.getCadena());
            BluetoothLE.sharedManager().writeSingleBuffer();
        }


        /*if(blt!=null){
            if(blt.connDev==null){
                showBtSelect();
            }

        }else{
            if(BluetoothLE.sharedManager().isConnected()){

                startTimer(200);
            }else{
                showBtSelect();
            }
        }*/
        /*runBtn = (ImageButton)this.findViewById(R.id.runLayout);
        runBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(engineState==STAGE_IDLE){
                    if(blt!=null){
                        if(blt.connDev==null){
                            showBtSelect();
                        }

                    }else{
                        if(BluetoothLE.sharedManager().isConnected()){

                            startTimer(200);
                        }else{
                            showBtSelect();
                        }
                    }
                }else{
                    stopTimer();
                    engineState = STAGE_IDLE;
                    runBtn.setImageResource(R.drawable.run_button);
                }
               // MobclickAgent.onEvent(mContext, "runLayout");
            }
        });*/

        /*btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        checkBTState();
        if (btAdapter.isEnabled()) {
            for (BluetoothDevice device : devices) {
                tvLogsBluetooth.append(String.format("\n%s (%s)", device.getName(), device.getAddress()));
            }
            //Controlar si hemos conectado antes de enviar
            createConnection();
            getResultsFromApi("1jCvYSZTdo5_FheTGDo9zD6t1Rqb3yBIs3cHayob29Ns", "A1:P8", "Erik");

        }*/
//        LinearLayout activityLayout = new LinearLayout(this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        activityLayout.setLayoutParams(lp);
//        activityLayout.setOrientation(LinearLayout.VERTICAL);
//        activityLayout.setPadding(16, 16, 16, 16);
//
//        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        mCallApiButton = new Button(this);
//        mCallApiButton.setText(BUTTON_TEXT);
//        mCallApiButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallApiButton.setEnabled(false);
//                mOutputText.setText("");

//                mCallApiButton.setEnabled(true);
//            }
//        });
//        activityLayout.addView(mCallApiButton);

//        mOutputText = new TextView(this);
//        mOutputText.setLayoutParams(tlp);
//        mOutputText.setPadding(16, 16, 16, 16);
//        mOutputText.setVerticalScrollBarEnabled(true);
//        mOutputText.setMovementMethod(new ScrollingMovementMethod());
//        mOutputText.setText(
//                "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
//        activityLayout.addView(mOutputText);




    }



    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi(String sheetId, String range, String sheet) {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount(sheetId, range, sheet);
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential, sheetId, range, sheet).execute();
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
    private void chooseAccount(String sheetId, String range, String sheet) {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi(sheetId, range, sheet);
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
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    //getResultsFromApi();
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
                        //getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    //getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

       /* if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }*/
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
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, ByteBuffer> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        String spreadsheetId = "1jCvYSZTdo5_FheTGDo9zD6t1Rqb3yBIs3cHayob29Ns";
        String range;

        public MakeRequestTask(GoogleAccountCredential credential, String sheetId, String range, String sheet) {
            if(sheetId != null){
                this.spreadsheetId = sheetId;
            }
            this.range = String.format("%s!%s", sheet, range);
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google sheets Android")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected ByteBuffer doInBackground(Void... params) {
            try {
                return getDataFromApi();
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
        private ByteBuffer getDataFromApi() throws IOException {
            ByteBuffer cadenaFace = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(this.spreadsheetId, this.range)
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
                            cadenaFace.put(i, (byte) valorCelda);
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
            mProgress.show();
        }

        @Override
        protected void onPostExecute(ByteBuffer output) {
            mProgress.hide();
            if (output == null || output.limit() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                mOutputText.setText(output.toString());

                //TODO: aqui podemos recuperar el resultado de las matriz de los excel
              /*  MeMatrixLedDevice meMatrixLedDevice = new MeMatrixLedDevice("Matrix", 1);
                CommandSimple commandSimple = meMatrixLedDevice.pintarFace(output, 0, 0);

                sendData(commandSimple);*/
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
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
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

/*    private void createConnection(){
        Log.d(TAG, "...In onResume - Attempting client connect...");
        tvLogsBluetooth.append("\n...In onResume - Attempting client connect...");
        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting to Remote...");
        tvLogsBluetooth.append("\n...Connecting to Remote...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection established and data link opened...");
            tvLogsBluetooth.append("\n...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Creating Socket...");
        tvLogsBluetooth.append("\n...Creating Socket...");
        try {
            outStream = btSocket.getOutputStream();

        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth is enabled...");
                tvLogsBluetooth.append("\n...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }*/
/*
    private void errorExit(String title, String message) {
        Toast msg = Toast.makeText(getBaseContext(),
                title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        tvLogsBluetooth.append(String.format("ERROR: %s", message));
        finish();
    }

    private void sendData(CommandSimple commandSimple) {
        //byte[] msgBuffer = new byte[] { (byte)0xff, (byte)0x55 , (byte)0x06 , (byte)0x60 , (byte)0x02 , (byte)0x0a , (byte)0x09 , (byte)0x00 , (byte)0x00}; //message.getBytes();

        Log.d(TAG, "...Sending data: " + commandSimple.toString() + "...");
        tvLogsBluetooth.append("\n....Sending data: " + commandSimple.toString() + "...");
        try {
            outStream.write(commandSimple.getCadena());
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
            msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }*/

    // --------------- BULETOOTH BELOW ---------------------
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case Bluetooth.MSG_CONNECTED:
                {
                    devListChanged();
                    blticon.setIcon(R.drawable.bluetooth_on);
                    Intent intent = new Intent(MainActivity.this,DialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg", getString(R.string.connected));
                    startActivity(intent);
                    firmVersion = 0.0f;
                    startTimer(1000);
                }
                break;
                case Bluetooth.MSG_DISCONNECTED:
                    stopTimer();
                    devListChanged();
                    blticon.setIcon(R.drawable.bluetooth_off);
                    break;
                case Bluetooth.MSG_CONNECT_FAIL:
                {
                    Intent intent = new Intent(MainActivity.this,DialogActivity.class);
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
                    if(btnRefresh!=null){
                        AnimationDrawable d=(AnimationDrawable)btnRefresh.getCompoundDrawables()[0];
                        d.stop();
                    }
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
    final Handler mLeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case BluetoothLE.MSG_CONNECTED:
                {
                    devLEListChanged();
                    blticon.setIcon(R.drawable.bluetooth_on);
                    Intent intent = new Intent(MainActivity.this,DialogActivity.class);
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
                    Intent intent = new Intent(MainActivity.this,DialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("msg", getString(R.string.connectfail));
                    startActivity(intent);
                    Log.d(dbg,"connect fail");
                }
                break;
                case BluetoothLE.MSG_SCAN_START:{
                    if(btnRefresh!=null){
                        AnimationDrawable d=(AnimationDrawable)btnRefresh.getCompoundDrawables()[0];
                        d.start();
                    }
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
                    parseMsg(rx);
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
                    Intent intent =new Intent(MainActivity.this,DialogActivity.class);
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
                    /*if(engineState==STAGE_PROBING){
                        byte[] queryStr = firmup.getProbeCmd();
                        if(queryStr!=null){
                            if(blt!=null){
                                blt.bluetoothWrite(queryStr);
                            }else{
                                BluetoothLE.sharedManager().resetIO(queryStr);
                            }
                        }
                    }else */
                    if(engineState==STAGE_RUN){
                        byte[] queryStr = getQueryString();
                        if(queryStr!=null){
                            if(blt!=null){
                                blt.bluetoothWrite(queryStr);
                            }else{
                                BluetoothLE.sharedManager().writeBuffer(queryStr);
                            }
                        }
                    }else if(engineState==STAGE_IDLE){
                        queryVersion();
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

    byte[] getQueryString()
    {
        //TODO: Aquí hay que poner el tratamiento para procesar las cadenas de comandos
//        if(queryListIndex>=layout.moduleList.size()){
//            return null;
//        }
//        MeModule mod = layout.moduleList.get(queryListIndex);
//        byte[] query = mod.getQuery(queryListIndex);
//        if(query==null){
//            queryListIndex++;
//            if(queryListIndex==layout.moduleList.size()){
//                queryListIndex = 0;
//                return null;
//            }
//            return getQueryString();
//        }
//        queryListIndex++;
//        if(queryListIndex==layout.moduleList.size()){
//            queryListIndex = 0;
//        }
//        return query;

        return null;
    }

    void queryVersion(){
        //Log.d("mb", "queryVersion");
        byte[] cmd = new byte[7];
        //a[8]={0xff,0x55,len,VERSION_INDEX,action,device,'\n'};
        cmd[0]=(byte) 0xff;
        cmd[1]=(byte) 0x55;
        cmd[2]=(byte) 3;
        cmd[3]=(byte) MeConstants.VERSION_INDEX;
        cmd[4]=(byte) MeConstants.READMODULE;
        cmd[5]=(byte) 0;
        cmd[6]=(byte) '\n';
        if(blt!=null){
            blt.bluetoothWrite(cmd);
        }else{
            BluetoothLE.sharedManager().writeBuffer(cmd);
        }
    }

    void parseMsg(int[] msg){

			Log.d("mb", "parseMSG:"+msg.length);
        /*if(engineState==STAGE_PROBING){
            engineState = STAGE_DONWLOAD_PROCESS;
            stopTimer();
            int ret = firmup.parseCmd(msg);
            //if(ret>0) stopTimer();
            //firmup.loadFirm();
            firmup.startDownload();
            uploadPb.setMessage("Found Orion, now downloading");
            byte[] tosend = firmup.getHexPage();
            if(tosend!=null){
                if(blt!=null){
                    blt.bluetoothWrite(tosend);
                }else{
                    MeTimer.delayWrite(tosend,10);
                }
            }
        }else if(engineState == STAGE_DONWLOAD_PROCESS){
            byte[] tosend = firmup.getHexPage();
            if(tosend!=null){
                int percent = firmup.getDowningProcess();
                uploadPb.setMessage(getString(R.string.Upgrading)+" "+percent+"%");
                if(blt!=null){
                    blt.bluetoothWrite(tosend);
                }else{
                    MeTimer.delayWrite(tosend, 100);
                }
                if(percent==100){
                    firmVersion = 1.1f;
                    uploadPb.dismiss();
                }
            }
        }else */
        if(engineState != STAGE_DONWLOAD_PROCESS &&
                engineState != STAGE_PROBING && msg.length>2){
            if((msg[2]&0xff)==MeConstants.VERSION_INDEX){
                int len = msg[4];
                String hexStr="";
                for(int i=0;i<len;i++){
                    hexStr+=String.format("%c", msg[5+i]);
                }
                Log.d("mb","version:"+hexStr);
                firmVersion = 1.1f;
                if(engineState==STAGE_IDLE){
                    stopTimer();
                }
            }else{
                int moduleIndex = msg[2];
                if(msg.length<7) return;
                float f = 0.0f;
                if(msg[3]==2){
                    if(msg.length>7){
                        int tint = (msg[4]&0xff)+((msg[5]&0xff)<<8)+((msg[6]&0xff)<<16)+((msg[7]&0xff)<<24);
                        f = Float.intBitsToFloat(tint);
                    }
                }else if(msg[3]==1){
                    f = (msg[4]&0xff);
                }else if(msg[3]==3){
                    f = (msg[4]&0xff)+((msg[5]&0xff)<<8);
                }
                /*if(moduleIndex<0 || moduleIndex>layout.moduleList.size()){
                    return;
                }
                //rx:FF 55 04 04 07 31 2E 31 2E 31 30 32 0D 0A

                if(moduleIndex<layout.moduleList.size()){
                    MeModule mod = layout.moduleList.get(moduleIndex);
                    mod.setEchoValue(""+f);
                }*/

            }
        }else if(msg.length<3){
            return;
        }
    }
/*
    void showBtSelect(){
        if(blt==null){
            BluetoothLE.sharedManager().start();
        }
        new Handler().postDelayed(new Runnable(){

            public void run() {
                LinearLayout popupBtDevLayout;
                popupBtDevLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.popup_btselect, null);
                popupBtSelect = new PopupWindow(MainActivity.this);
                popupBtSelect.setWidth(screenWidth/2);
                popupBtSelect.setHeight((int) (screenHeight*0.8));
                popupBtSelect.setOutsideTouchable(true);
                popupBtSelect.setFocusable(true);
                popupBtSelect.setContentView(popupBtDevLayout);
                popupBtSelect.showAtLocation(contentView, Gravity.LEFT|Gravity.TOP, screenWidth/4, screenHeight/10+25);
                ListView devlist = (ListView) popupBtDevLayout.findViewById(R.id.btdevList);
                //ListView pairlist = (ListView) popupBtDevLayout.findViewById(R.id.btpairList);
                devlist.setAdapter(devAdapter);
                //pairlist.setAdapter(pairAdapter);

                popupBtSelect.setOnDismissListener(new PopupWindow.OnDismissListener(){
                    @Override
                    public void onDismiss() {
                        contentView.getForeground().setAlpha(0);
                    }
                });

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
 //                                   BluetoothLE.sharedManager().selectDevice(position);
                                }
                            }
                        }catch(Exception e){
                            Log.e(dbg, e.toString());
                        }
                    }
                });
                contentView.getForeground().setAlpha(150);
                btnRefresh = (Button) popupBtDevLayout.findViewById(R.id.popupRefreshBtn);
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
                        }else{
                            BluetoothLE.sharedManager().stop();
                            BluetoothLE.sharedManager().clear();
                            devLEListChanged();
                            BluetoothLE.sharedManager().start();
                        }
                        AnimationDrawable d=(AnimationDrawable)btnRefresh.getCompoundDrawables()[0];
                        d.start();
                    }
                });

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
            }

        }, 100L);

    }*/
}