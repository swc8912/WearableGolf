package com.kingofgolf.golfapp;

import android.util.Log;

import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by 우철 on 2015-11-01.
 */
public class SendToDataLayerThread extends Thread {
    String path;
    DataMap dataMap;

    // Constructor for sending data objects to the data layer
    SendToDataLayerThread(String p, DataMap data) {
        path = p;
        dataMap = data;
    }

    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(MainActivity.googleClient).await();
        for (Node node : nodes.getNodes()) {
            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(MainActivity.googleClient, request).await();
            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "success DataMap: " + dataMap + " sent to: " + node.getDisplayName());
            } else {
                // Log an error
                Log.v("myTag", "ERROR: failed to send DataMap");
            }
        }
    }
}