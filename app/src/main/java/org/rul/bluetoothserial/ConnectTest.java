package org.rul.bluetoothserial;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.rul.meapi.MeConstants;
import org.rul.meapi.MeMotorDevice;
import org.rul.meapi.model.CommandSimple;

public class ConnectTest extends Activity {
    private static final String TAG = "ConnectTest";

    TextView tvLogsBluetooth;
    ImageButton buttonForward;
    ImageButton buttonLeft;
    ImageButton buttonRight;
    ImageButton buttonBackward;

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private CommandSimple commandSimple;

    // Well known SPP UUID
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your bluetooth devices MAC address
    private static String address = "00:07:02:03:10:A3";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "In onCreate()");

        setContentView(R.layout.activity_main);

        tvLogsBluetooth = (TextView) findViewById(R.id.out);
        buttonForward = (ImageButton) findViewById(R.id.buttonForward);
        buttonLeft = (ImageButton) findViewById(R.id.buttonLeft);
        buttonRight = (ImageButton) findViewById(R.id.buttonRight);
        buttonBackward = (ImageButton) findViewById(R.id.buttonBackward);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        checkBTState();
        if (btAdapter.isEnabled()) {
            for (BluetoothDevice device : devices) {
                tvLogsBluetooth.append(String.format("\n%s (%s)", device.getName(), device.getAddress()));
            }
            createConnection();
            //sendData("a");
            MeMotorDevice meMotorDeviceI = new MeMotorDevice("Prueba giro inverso", MeConstants.PORT_M1);
            MeMotorDevice meMotorDeviceD = new MeMotorDevice("Prueba giro directo", MeConstants.PORT_M2);
            CommandSimple commandSimpleI = meMotorDeviceI.giroInverso((byte) 156);
            tvLogsBluetooth.append("\n" + commandSimpleI.maskToString());
            sendData(commandSimpleI);
            CommandSimple commandSimpleD = meMotorDeviceD.giroDirecto((byte) 100);
            tvLogsBluetooth.append("\n" + commandSimpleD.maskToString());
            sendData(commandSimpleD);
//            MeMotorCommunication motorD = new MeMotorCommunication("MotorDerecho", MeModuleCommunication.PORT_M1);
//            motorD.writeCommand(1, MeModuleCommunication.WRITEMODULE, 0, 100);
//
//            byte[] commandBytes = motorD.commandToByteArray();
//
//            tvLogsBluetooth.append(String.format("\n%s", Arrays.toString(commandBytes)));
//            tvLogsBluetooth.append(String.format("\n%s", motorD.readCommand(commandBytes)));
        }

//        buttonForward.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View button) {
//                //Set the button's appearance
//                button.setSelected(!button.isSelected());
//
//                if (button.isSelected()) {
//                    tvLogsBluetooth.append("\nPulso el botón avanzar");
//                } else {
//                    tvLogsBluetooth.append("\nSuelto el botón avanzar");
//                }
//
//            }
//
//        });

 /*       final MeMotorAPI motorAPI = new MeMotorAPI(MeModuleCommunication.PORT_M1, MeModuleCommunication.PORT_M2, 100, -100);
        buttonForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        motorAPI.runForward();
                        sendData(motorAPI.getMsgLeft());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgLeft())));
                        sendData(motorAPI.getMsgRight());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgRight())));
                        tvLogsBluetooth.append("\nPulso el botón avanzar");
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        motorAPI.stop();
                        sendData(motorAPI.getMsgLeft());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgLeft())));
                        sendData(motorAPI.getMsgRight());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgRight())));
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

        buttonBackward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        motorAPI.runBackward();
                        sendData(motorAPI.getMsgLeft());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgLeft())));
                        sendData(motorAPI.getMsgRight());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgRight())));
                        tvLogsBluetooth.append("\nPulso el botón avanzar");
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        motorAPI.stop();
                        sendData(motorAPI.getMsgLeft());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgLeft())));
                        sendData(motorAPI.getMsgRight());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgRight())));
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

        buttonLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        motorAPI.turnLeft();
                        sendData(motorAPI.getMsgLeft());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgLeft())));
                        sendData(motorAPI.getMsgRight());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgRight())));
                        tvLogsBluetooth.append("\nPulso el botón avanzar");
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        motorAPI.stop();
                        sendData(motorAPI.getMsgLeft());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgLeft())));
                        sendData(motorAPI.getMsgRight());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgRight())));
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

        buttonRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        motorAPI.turnRight();
                        sendData(motorAPI.getMsgLeft());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgLeft())));
                        sendData(motorAPI.getMsgRight());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgRight())));
                        tvLogsBluetooth.append("\nPulso el botón avanzar");
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        motorAPI.stop();
                        sendData(motorAPI.getMsgLeft());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgLeft())));
                        sendData(motorAPI.getMsgRight());
                        tvLogsBluetooth.append(String.format("\n%s", Utils.bytesToHexString(motorAPI.getMsgRight())));
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

    public void calibrateRunOneCell(int speed){
        Calendar calendarIni = Calendar.getInstance();
        long milisIni = calendarIni.getTimeInMillis();
        //runForward(speed);

    }


    private void createConnection(){
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

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
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
    }

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
    }

}