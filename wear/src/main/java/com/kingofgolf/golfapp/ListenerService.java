package com.kingofgolf.golfapp;

import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by michaelHahn on 1/16/15.
 * Listener service or data events on the data layer
 */
public class ListenerService extends WearableListenerService {
    private static final String WEARABLE_WATCH_PATH = "/data_from_watch";
    private static final String WEARABLE_APP_PATH = "/data_from_app";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;
        for (DataEvent event : dataEvents) {

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_APP_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received from app: " + dataMap);

                    String cmd = dataMap.getString("sensortype");

                    if(cmd.equals("open")){
                        Intent intent = new Intent(getApplicationContext(), DataMapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return;
                    }

                    SensorData data = new SensorData(dataMap.getString("sensortype"), dataMap.getDouble("x"), dataMap.getDouble("y"), dataMap.getDouble("z"));
                    Message msg = new Message();
                    msg.what = DataMapActivity.APP_DATA;
                    msg.obj = data;
                    DataMapActivity.msgHandler.sendMessage(msg);
                }
                else if(path.equals(WEARABLE_WATCH_PATH)){
                    //dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    //Log.v("myTag", "DataMap send from watch: " + dataMap);
                }
            }
        }
    }
}
