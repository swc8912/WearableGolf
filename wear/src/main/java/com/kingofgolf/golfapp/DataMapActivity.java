package com.kingofgolf.golfapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;

public class DataMapActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    GoogleApiClient googleClient;

    private SensorManager mSm;
    private TextView mTextView;

    public static GetMassgeHandler msgHandler;

    public static final int STATE_SWING_NOT_START = 1;
    public static final int STATE_SWING_READY = 2;
    public static final int STATE_SWING_START = 3;
    public static final int STATE_SWING_END = 4;
    public static final int STATE_SWING_RESTART = 5;
    public static final int STATE_SWING_UP = 6;
    public static final int STATE_SWING_SECOND_START = 7;

    private int STATE = STATE_SWING_NOT_START;

    private RelativeLayout layout;

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

        mSm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        msgHandler = new GetMassgeHandler(this);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText("SWING NOT START");
                layout = (RelativeLayout)stub.findViewById(R.id.background);
            }
        });

        stub.setKeepScreenOn(true);
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

        //startSensoring();

//        String WEARABLE_DATA_PATH = "/data_from_watch";
//
//        // Create a DataMap object and send it to the data layer
//        DataMap dataMap = new DataMap();
//        dataMap.putString("sensortype", "accel");
//        dataMap.putDouble("x", 123.123);
//        dataMap.putDouble("y", 456.456);
//        dataMap.putDouble("z", 789.789);
//        //Requires a new thread to avoid blocking the UI
//        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();

        // 영상 촬영용 세팅
        //Message msg = new Message();
        //msg.what = APP_DATA;
        //msg.obj = new SensorData("start", 0, 0, 0);
        //msgHandler.sendMessage(msg);
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }

        stopSensoring();

        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

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

    public static final int WEAR_DATA = 1;
    public static final int APP_DATA = 2;

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
                case APP_DATA:
                    SensorData data = (SensorData) msg.obj;

                    if(activity != null){
                        if(data.getSensorType() == null)
                            return;

                        if(data.getSensorType().equals("start")){
                            activity.STATE = activity.STATE_SWING_READY;
                            activity.mTextView.setText("SWING READY");
                            activity.startSensoring();

                            activity.layout.setBackgroundResource(R.drawable.watchface1);
                        }
                        else if(data.getSensorType().equals("stop")){
                            activity.STATE = activity.STATE_SWING_END;
                            activity.mTextView.setText("SWING STOPPED");
                            activity.stopSensoring();

                            activity.layout.setBackgroundResource(R.drawable.watchface2);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private void startSensoring(){
        int delay = SensorManager.SENSOR_DELAY_UI;

//        mSm.registerListener(mSensorListener,
//                mSm.getDefaultSensor(Sensor.TYPE_LIGHT), delay);
//        mSm.registerListener(mSensorListener,
//                mSm.getDefaultSensor(Sensor.TYPE_PROXIMITY), delay);
//        mSm.registerListener(mSensorListener,
//                mSm.getDefaultSensor(Sensor.TYPE_PRESSURE), delay);
//        mSm.registerListener(mSensorListener,
//                mSm.getDefaultSensor(Sensor.TYPE_ORIENTATION), delay);
        mSm.registerListener(mSensorListener,
                mSm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), delay);
        mSm.registerListener(mSensorListener,
                mSm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), delay);
//        mSm.registerListener(mSensorListener,
//                mSm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), delay);
        mSm.registerListener(mSensorListener,
                mSm.getDefaultSensor(Sensor.TYPE_GYROSCOPE), delay);

//        btnStart.setText("정지");
//        btnStart.setTag("start");

//        accList.clear();
//        mAccelCount = mMagneticCount = mOrientCount = 0;
    }

    private void stopSensoring(){
//        if(btnStart.getTag().equals("start")){
            mSm.unregisterListener(mSensorListener);
//            btnStart.setText("시작");
//            btnStart.setTag("stop");

//            Log.d("1", "lenth: " + accList.size());
//        }
    }

    private SensorEventListener mSensorListener = new SensorEventListener() {
        private int cnt = 0;
        private String WEARABLE_DATA_PATH = "/data_from_watch";
        private DataMap dataMap = null;
        private float[] gravity = new float[3];
        private static final float ALPHA = 0.8f;
        private static final int THRESHHOLD = 11;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // 특별히 처리할 필요없음
        }

        @SuppressWarnings("deprecation")
        public void onSensorChanged(SensorEvent event) {
            // 신뢰성없는 값은 무시
            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                //return;
            }

            float[] v = event.values.clone();
            switch (event.sensor.getType()) {
//                case Sensor.TYPE_LIGHT:
//                    mTxtLight.setText("조도 = " + ++mLightCount + "회 : " + v[0]);
//                    break;
//                case Sensor.TYPE_PROXIMITY:
//                    mTxtProxi.setText("근접 = " + ++mProxiCount + "회 : " + v[0]);
//                    break;
//                case Sensor.TYPE_PRESSURE:
//                    mTxtPress.setText("압력 = " + ++mPressCount + "회 : " + v[0]);
//                    break;
                case Sensor.TYPE_ORIENTATION:
//                    mTxtOrient.setText("방향 = " + ++mOrientCount + "회 : \n  azimuth:" +
//                            v[0] + "\n  pitch:" + v[1] + "\n  roll:" + v[2]);
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    //SensorData data = new SensorData("accel", v[0], v[1], v[2]);
                    //accList.add(data);
//                    mTxtAccel.setText("가속 = " + ++mAccelCount + "회 : \n  X:" +
//                            v[0] + "\n  Y:" + v[1] + "\n  Z:" + v[2]);

                    double sumOfSquares = (v[0] * v[0]) + (v[1] * v[1]) + (v[2] * v[2]);
                    double acceleration = Math.sqrt(sumOfSquares);
                    Log.d("myTag", "acceleration; " + acceleration);

                    if(STATE == STATE_SWING_READY && acceleration >= THRESHHOLD){
                        STATE = STATE_SWING_START;
                        mTextView.setText("SWING START");
                        SetAndSendData("swing start", 0, 0, 0);
                    }
                    else if(STATE == STATE_SWING_START && acceleration < THRESHHOLD){
                        STATE = STATE_SWING_UP;
                        mTextView.setText("SWING UP");
                        SetAndSendData("swing up", 0, 0, 0);
                    }
                    else if(STATE == STATE_SWING_UP && acceleration > THRESHHOLD){
                        STATE = STATE_SWING_SECOND_START;
                        mTextView.setText("SWING SECOND START");
                        SetAndSendData("swing seconde start", 0, 0, 0);
                    }
                    else if(STATE == STATE_SWING_SECOND_START && acceleration <= THRESHHOLD){
                        STATE = STATE_SWING_END;
                        mTextView.setText("SWING END");
                        SetAndSendData("swing stop", 0, 0, 0);
                        stopSensoring();

                        layout.setBackgroundResource(R.drawable.watchface2);
                        return;
                    }

                    if(STATE == STATE_SWING_START) {
                        SetAndSendData("accel", v[0], v[1], v[2]);
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    if(STATE == STATE_SWING_START) {
                        SetAndSendData("magnetic", v[0], v[1], v[2]);
                    }
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    if(STATE == STATE_SWING_START){
                        SetAndSendData("gyro", v[0], v[1], v[2]);
                    }
                    break;
            }
        }

        private void SetAndSendData(String type, double f1, double f2, double f3){
            dataMap = new DataMap();
            dataMap.putString("sensortype", type);
            dataMap.putDouble("x", f1);
            dataMap.putDouble("y", f2);
            dataMap.putDouble("z", f3);
            //Requires a new thread to avoid blocking the UI
            new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
        }

        private float[] highPass(float x, float y, float z)
        {
            float[] filteredValues = new float[3];

            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

            filteredValues[0] = x - gravity[0];
            filteredValues[1] = y - gravity[1];
            filteredValues[2] = z - gravity[2];

            return filteredValues;
        }
    };
}
