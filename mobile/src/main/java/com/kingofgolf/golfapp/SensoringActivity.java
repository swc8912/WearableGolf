package com.kingofgolf.golfapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class SensoringActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleClient;
    private TextView textView;
    private Button btnStart;
    private Button btnOpen;
    public static GetMassgeHandler msgHandler;
    private static final String WEARABLE_DATA_PATH = "/data_from_app";
    public static boolean isPaired = false;
    public static final int WEAR_DATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sensoring);

        msgHandler = new GetMassgeHandler(this);

        // Build a new GoogleApiClient for the the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

//        textView = (TextView) findViewById(R.id.textView);
//
//        btnStart = (Button) findViewById(R.id.btnStart);
//        btnStart.setTag("stop");
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getTag().equals("stop") && isConnected) {
//                    btnStart.setTag("start");
//                    btnStart.setText("STOP");
//                    textView.setText("");
//                    // Create a DataMap object and send it to the data layer
//                    DataMap dataMap = new DataMap();
//                    dataMap.putString("sensortype", "start");
//                    //Requires a new thread to avoid blocking the UI
//                    new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
//                } else if (v.getTag().equals("start") && isConnected) {
//                    btnStart.setTag("stop");
//                    btnStart.setText("START");
//
//                    DataMap dataMap = new DataMap();
//                    dataMap.putString("sensortype", "stop");
//                    //Requires a new thread to avoid blocking the UI
//                    new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
//                }
//            }
//        });
//
//        btnOpen = (Button) findViewById(R.id.btnOpen);
//        btnOpen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DataMap dataMap = new DataMap();
//                dataMap.putString("sensortype", "open");
//                //Requires a new thread to avoid blocking the UI
//                new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
//            }
//        });
    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        if(googleClient != null)
            googleClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ImageView iv = (ImageView) findViewById(R.id.gifView);
        iv.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation = (AnimationDrawable) iv
                        .getBackground();
                frameAnimation.start();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        final ImageView iv = (ImageView) findViewById(R.id.gifView);
        iv.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation = (AnimationDrawable) iv
                        .getBackground();
                frameAnimation.stop();
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v("myApp", "OnConnected entered");
        StartSensoring("open");
        //Toast.makeText(SensoringActivity.this, "Connected!", Toast.LENGTH_SHORT).show();

//        NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.icon)
//                .setContentTitle("GolfWang")
//                .setContentText("골프왕!");
//
//        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
//        manager.notify(1, noti.build());
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            StartSensoring("stop");
            googleClient.disconnect();
        }
        isPaired = false;
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


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

    class SendToDataLayerThread2 extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread2(String p, DataMap data) {
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
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "success DataMap: " + dataMap + " sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send DataMap");
                }
            }
        }
    }

    public void StartSensoring(String type){
        DataMap dataMap = new DataMap();
        dataMap.putString("sensortype", type);
        dataMap.putDouble("x", 0);
        dataMap.putDouble("y", 0);
        dataMap.putDouble("z", 0);
        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread2(WEARABLE_DATA_PATH, dataMap).start();
    }

    private int accelCnt = 0;
    private int gyroCnt = 0;
    private double accelSum = 0;
    private double gyroSum = 0;

    public static class GetMassgeHandler extends Handler {
        private final WeakReference<SensoringActivity> mActivity;

        public GetMassgeHandler(SensoringActivity activity) {
            mActivity = new WeakReference<SensoringActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SensoringActivity activity = mActivity.get();

            switch (msg.what) {
                case WEAR_DATA:
                    SensorData data = (SensorData) msg.obj;
                    Log.d("myApp", "in handler");
                    if (activity != null) {
                        String type = data.getSensorType();
                        Log.d("myApp","type: " + type);
                        double f1 = data.getArg1();
                        double f2 = data.getArg2();
                        double f3 = data.getArg3();

                        String text = "";
                        if (type.equals("accel")) {
                            activity.accelCnt++;
                            activity.accelSum += Math.sqrt((f1 * f1) + (f2 * f2) + (f3 * f3));
                            //double sumOfSquares = (f1 * f1) + (f2 * f2) + (f3 * f3);
                            //double acceleration = Math.sqrt(sumOfSquares);
//                            text = activity.textView.getText().toString() + "\n" + "accel: " + acceleration;
                        } else if(type.equals("gyro")){
                            activity.gyroCnt++;
                            activity.gyroSum += (f1 * f1) + (f2 * f2) + (f3 * f3);
                        } else if(type.equals("watchconnected")){
                            SensoringActivity.isPaired = true;
                            Log.d("myApp", "watchconnected");
                            activity.StartSensoring("start");
                        } else if (type.equals("swing stop")) {
                            Toast.makeText(activity, "Swing Ended!", Toast.LENGTH_SHORT).show();
                            // 데이터 저장 db 저장 프로세싱
                            double accelAvg = activity.accelSum / activity.accelCnt;
                            double gyroAvg = activity.gyroSum / activity.gyroCnt;
                            // accelAvg: 11.044862406399062 gryoAvg: NaN  accelAvg: 14.848149193253327 gryoAvg: NaN
                            Log.d("myApp", "accelAvg: " + accelAvg + " gryoAvg: " + gyroAvg);
                            activity.accelCnt = 0;
                            activity.accelSum = 0;
                            activity.gyroCnt = 0;
                            activity.gyroSum = 0;

                            activity.finish();
                        }
//                        else if (!type.equals("gyro") && !type.equals("magnetic")) {
////                            text = activity.textView.getText().toString() + "\n" + data.getSensorType() + " " + data.getArg1() + " " + data.getArg2() + " " + data.getArg3();
//                        }
//                        activity.textView.setText(text.toString());
                    }

                    break;
                default:
                    break;
            }
        }
    }

    ;
}
