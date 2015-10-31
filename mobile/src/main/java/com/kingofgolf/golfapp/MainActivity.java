package com.kingofgolf.golfapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by 우철 on 2015-10-31.
 */
public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static GoogleApiClient googleClient;
    public static final String WEARABLE_DATA_PATH = "/data_from_app";
    private ImageButton btnGoodSwing;
    private ImageButton btnGoSwing;
    private ImageButton btnSwingList;
    public static boolean isPaired = false;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();

        btnGoodSwing = (ImageButton)findViewById(R.id.btnGoodSwing);
        btnGoodSwing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SwingActivity.class);
                intent.putExtra("isgood", true);
                startActivity(intent);
            }
        });

        btnGoSwing = (ImageButton)findViewById(R.id.btnGoSwing);
        btnGoSwing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SwingActivity.class);
                intent.putExtra("isgood", false);
                startActivity(intent);
            }
        });

        btnSwingList = (ImageButton)findViewById(R.id.btnSwingList);
        btnSwingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SwingListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        isPaired = false;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v("myApp", "OnConnected entered");
        Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();

    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
