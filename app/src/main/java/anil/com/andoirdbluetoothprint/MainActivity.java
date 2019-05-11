package anil.com.andoirdbluetoothprint;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.UUID;
import java.util.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class MainActivity extends Activity implements Runnable {
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    EditText e1;
ImageView im;
    @Override
    public void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        setContentView(R.layout.activity_main);
        e1=(EditText)findViewById(R.id.ttt);

        mScan = (Button) findViewById(R.id.Scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(MainActivity.this, "Message1", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(MainActivity.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });










        mPrint = (Button) findViewById(R.id.mPrint);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                         Getlogin();
                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket
                                    .getOutputStream();
                            OutputStream os1 = mBluetoothSocket
                                    .getOutputStream();

                            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                                    R.drawable.bio);

                            byte[] command = Utils.decodeBitmap(bmp);

                 os.write(command);

                            String BILL = "";
                            String time =getCurrentTimeStamp();

                   // BILL=""+e1.getText().toString()+"\n";

                            BILL =  "          Hospital - 7 Hills  \n   "+

                                       "        Code-Hills123   \n" +"";

                            BILL = BILL +"Time- "+time+"\n";
                            BILL=BILL+"Transaction.ID-123456789"+"\n";

                            BILL = BILL
                                    + "--------------------------------\n";


                          BILL = BILL + String.format("%1$-8s %2$12s %3$10s", "Barcode", "BagColor", "Weight");
                            BILL = BILL
                                    + "--------------------------------";
                            BILL = BILL + "\n " + String.format("%1$-10s %2$5s %3$8s ", "BARKCODE-40", "R", "10");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$5s %3$8s ", "BARKCODE-41", "Y", "5");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$5s %3$8s ", "BARKCODE-42", "B", "15");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$5s %3$8s ", "BARKCODE-43", "R", "20");

                            BILL = BILL
                                    + "\n-------------------------------";
                            BILL = BILL + "\n ";

                            BILL = BILL + "      Total weight:" + "  -  " + "50" + "\n";
                            BILL = BILL + "       Total Bags:" + "  -  " + "4" + "\n";

                            BILL = BILL
                                    + "-------------------------------\n\n";
                            BILL=BILL+""+String.format("%1$-16s %2$10s  ", "","Ram");
                            BILL = BILL + " \n" + String.format("%1$-18s %2$10s  ", "Authorized By","Driver");
                            BILL = BILL + "\n\n\n ";





                     os1.write(BILL.getBytes());
                            //This is printer specific code you can comment ==== > Start

                            // Setting height
                            int gs = 29;
                            os.write(intToByteArray(gs));
                            int h = 104;
                            os.write(intToByteArray(h));
                            int n = 162;
                            os.write(intToByteArray(n));

                            // Setting Width
                            int gs_width = 29;
                            os.write(intToByteArray(gs_width));
                            int w = 119;
                            os.write(intToByteArray(w));
                            int n_width = 2;
                            os.write(intToByteArray(n_width));


                        } catch (Exception e) {
                            Log.e("MainActivity", "Exe ", e);
                        }
                    }
                };
                t.start();
            }
        });

        mDisc = (Button) findViewById(R.id.dis);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
            }
        });

    }// onCreate

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(MainActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(MainActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }


    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }



    public void Getlogin()    {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();



        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .url("http://www.mocky.io/v2/5cd529c22e000015485274e5")
                .get()
                .build();


        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //login.setVisibility(View.GONE);
                Log.d("result dadi", e.getMessage().toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        Toast.makeText(getBaseContext(), "Unable to fetch the data", Toast.LENGTH_SHORT).show();
                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                // pd.dismiss();
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            Toast.makeText(getBaseContext(), "No Response", Toast.LENGTH_SHORT).show();

                        }
                    });

                    Log.d("result dadi", response.toString());
                    throw new IOException("Unexpected code " + response);
                } else {
//                    Toast.makeText(MainActivity.this, "Response", Toast.LENGTH_SHORT).show();
                    //  pd.dismiss();
                    Log.d("result", response.toString());
                    String responseBody = response.body().string();
                    final JSONObject obj;

                    try {

                        obj = new JSONObject(responseBody);
                        if (obj.getString("status").equals("true")) {


                            Toast.makeText(MainActivity.this, "Response", Toast.LENGTH_SHORT).show();

//                            JSONObject user = obj.getJSONObject("");
//                            JSONObject routes_masters_driver = user.getJSONObject("routes_masters_driver");
//                            String driverid = routes_masters_driver.getString("id");
//
//                            String truck_id = routes_masters_driver.getString("truck_id");
//
//                            SharedPreferences.Editor ss = getSharedPreferences("Login", MODE_PRIVATE).edit();
//                            ss.putString("type",user.getString("employee_type"));
//                            ss.putString("type",user.getString("employee_type"));
//                            ss.putString("user_id",user.getString("user_id"));
//                            ss.putString("access_token", obj.getString("access_token"));
//                            ss.putString("driverid", driverid);
//                            ss.putString("truck_id", truck_id);
//                            ss.putString("routeid",routes_masters_driver.getString("route_number"));
//                            ss.putString("data", obj.toString());
//                            ss.commit();
//
//                            if (user.getString("employee_type").equals("Driver")){
//                                Intent dashboard = new Intent(Login.this, Dashboard_Agent.class);
//                                startActivity(dashboard);
//                            }else {
//                                Intent dashboard = new Intent(Login.this, Dashboard.class);
//                                startActivity(dashboard);
//                            }
//
//                        } else {
//                            System.out.println("JONDDDd " + obj.toString());
//                            System.out.println("JONDDDd " + obj.getString("token"));
//
//                            Login.this.login_btn.post(new Runnable() {
//                                public void run() {
//                                    showDialog(Login.this, "Unauthorized user", "true");
//                                }
//                            });


                        }
                } catch (JSONException e) {
                       final String s= e.toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                Toast.makeText(getBaseContext(), "EXception"+s, Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
            }
        });

    }






}
