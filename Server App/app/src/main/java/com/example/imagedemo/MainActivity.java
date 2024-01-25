package com.example.imagedemo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static java.sql.Types.NULL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Time;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
public class MainActivity extends AppCompatActivity {

    Button listen, send, listDevices;
    ListView listView;
    TextView text, status;
    EditText pos, speedE;
    Handler handler2;
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;
    private final static int REQUEST_ENABLE_BT = 1;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private byte[] mmBuffer;
    int ind1 = 0, myCode, i = 0, k = 0, j = 0, tar, speed, curPos=1;
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    BluetoothDevice[] btArray;
    String APP_NAME = "Demo1", TAG = "nandu", targetS = "", speedS = "";
    BluetoothSocket pSocket = null;
    SendReceive sendReceive;
    UUID MY_UUID = UUID.fromString("c3039a82-7feb-4326-8068-cbfcbd966c86");
    BluetoothDevice device;
    Button button;
    liveMap LiveMap=new liveMap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewByIdes();
        askPermission();
        init();
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(receiver, filter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, liveMap.class);
                startActivity(intent);

            }
        });
        handler2=LiveMap.mHandler;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    text.setText(tempMsg);
                    handler2.obtainMessage(1,tempMsg);
                    break;
            }
            return true;
        }
    });

    public void onClickListDevices(View view) {
        Toast.makeText(MainActivity.this, "listDevices clicked", Toast.LENGTH_SHORT).show();
        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
        String[] strings = new String[bt.size()];
        btArray = new BluetoothDevice[bt.size()];
        int index = 0;

        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                btArray[index] = device;
                strings[index] = device.getName();
                index++;
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
            listView.setAdapter(arrayAdapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                ClientClass clientClass = new ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });
    }


    public void onClickListen(View view) {
        Toast.makeText(MainActivity.this, "listen clicked", Toast.LENGTH_SHORT).show();
        ServerClass serverClass = new ServerClass();
        serverClass.start();
    }
    /*
    public void onClickSend(View view) {
        Log.d(TAG, "on Send clicked");
        String position = String.valueOf(pos.getText());
        String speedSt = String.valueOf(speedE.getText());
        String string = position + "$" + speedSt;
        if (mmSocket != null)
            sendReceive.write(string.getBytes());
    }
    */

    public void findViewByIdes() {
        listen = (Button) findViewById(R.id.listen);
        button=findViewById(R.id.button2);
        listView = (ListView) findViewById(R.id.listview);
        text = (TextView) findViewById(R.id.msg);
        status = (TextView) findViewById(R.id.status);
        listDevices = (Button) findViewById(R.id.listDevices);
    }

    public void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE}, 2);//Manifest.permission.ACCESS_FINE_LOCATION
    }

    public void init() {
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        enableBT();
//        makeDiscoverable();
//        makeServer();
//        startDiscovery();
    }

    public void enableBT() {
        if (bluetoothAdapter == null) {
            text.setText("Bluetooth not available : 404!");
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
                Log.d(TAG, "Server Socket Created.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Server Socket creation failed.", e);
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                    Log.d(TAG, "Socket created in Server.");
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                    Log.e(TAG, "Socket creation in server failed", e);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;
//        private BluetoothSocket mmSocket;

        public ClientClass(BluetoothDevice device1) {
            device = device1;

            try {
                mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                Log.d(TAG, "Socket created in client.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Socket creation in client failed", e);
            }
        }

        public void run() {
            try {
                mmSocket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
                Log.d(TAG, "Socket connected");

                sendReceive = new SendReceive(mmSocket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
                Log.e(TAG, "Socket Connection failed", e);
            }
        }
    }

    private class SendReceive extends Thread {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        BluetoothSocket bluetoothSocket;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
                Log.d(TAG, "I/O stream created");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "I/O stream creation failed", e);
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                    Log.d(TAG, "Reading input stream");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Reading input stream failed", e);
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
                Log.d(TAG, "Writing output stream");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Writing output stream failed", e);
            }
        }
    }
}

