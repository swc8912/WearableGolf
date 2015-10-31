package com.kingofgolf.golfapp;

import android.util.Log;
import android.widget.Toast;

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
//                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//                    Log.v("myTag", "DataMap send from app: " + dataMap);
                }
                else if(path.equals(WEARABLE_WATCH_PATH)){
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received from watch: " + dataMap);
                    Toast.makeText(getApplicationContext(), dataMap.getString("front"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
