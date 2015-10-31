package com.kingofgolf.golfapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.kingofgolf.golfapp.data.SensorData;

import java.lang.ref.WeakReference;


public class DataMapActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient googleClient;
    private TextView textView;
    private Button btnStart;
    private Button btnOpen;
    public static GetMassgeHandler msgHandler;
    private final String WEARABLE_DATA_PATH = "/data_from_app";
    public static boolean isConnected = false;
    public static final int WEAR_DATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_map);

        // Build a new GoogleApiClient for the the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        msgHandler = new GetMassgeHandler(this);

        textView = (TextView)findViewById(R.id.textView);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setTag("stop");
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().equals("stop") && isConnected) {
                    btnStart.setTag("start");
                    btnStart.setText("STOP");
                    textView.setText("");
                    // Create a DataMap object and send it to the data layer
                    DataMap dataMap = new DataMap();
                    dataMap.putString("sensortype", "start");
                    //Requires a new thread to avoid blocking the UI
                    new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
                } else if (v.getTag().equals("start") && isConnected) {
                    btnStart.setTag("stop");
                    btnStart.setText("START");

                    DataMap dataMap = new DataMap();
                    dataMap.putString("sensortype", "stop");
                    //Requires a new thread to avoid blocking the UI
                    new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
                }
            }
        });

        btnOpen = (Button)findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataMap dataMap = new DataMap();
                dataMap.putString("sensortype", "open");
                //Requires a new thread to avoid blocking the UI
                new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
            }
        });
    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v("myApp", "OnConnected entered");
        isConnected = true;

        Toast.makeText(DataMapActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
            isConnected = false;
        }
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                // Construct a DataRequest and send over the data layer
                PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                putDMR.getDataMap().putAll(dataMap);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient,request).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "success DataMap: " + dataMap + " sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send DataMap");
                }
            }
        }
    }

    public static class GetMassgeHandler extends Handler {
        private final WeakReference<DataMapActivity> mActivity;

        public GetMassgeHandler(DataMapActivity activity) {
            mActivity = new WeakReference<DataMapActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            DataMapActivity activity = mActivity.get();

            switch (msg.what) {
                case SensoringActivity.WEAR_DATA:
                    SensorData data = (SensorData) msg.obj;

                    if(activity != null){
                        String type = data.getSensorType();
                        double f1 = data.getArg1();
                        double f2 = data.getArg2();
                        double f3 = data.getArg3();

                        String text = "";
                        if(type.equals("accel")){
                            double sumOfSquares = (f1 * f1) + (f2 * f2) + (f3 * f3);
                            double acceleration = Math.sqrt(sumOfSquares);
                            text = activity.textView.getText().toString() + "\n" + "accel: " + acceleration;
                        }
                        else if(!type.equals("gyro") && !type.equals("magnetic")){
                            text = activity.textView.getText().toString() + "\n" + data.getSensorType() + " " + data.getArg1() + " " + data.getArg2() + " " + data.getArg3();
                        }

                        activity.textView.setText(text.toString());
                    }

                    break;
                default:
                    break;
            }
        }
    };
}
