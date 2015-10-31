package com.kingofgolf.golfapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by 우철 on 2015-10-31.
 */
public class MainActivity extends Activity{
    private ImageButton btnGoodSwing;
    private ImageButton btnGoSwing;
    private ImageButton btnSwingList;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

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
    }
}
