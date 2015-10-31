package com.kingofgolf.golfapp;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SwingActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton btnSwing1;
    private ImageButton btnSwing2;
    private ImageButton btnSwing3;
    private ImageButton btnSwing4;
    private ImageButton btnSwing5;
    private ImageButton btnSwing6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_swing);

        btnSwing1 = (ImageButton)findViewById(R.id.btnSwing1);
        btnSwing2 = (ImageButton)findViewById(R.id.btnSwing2);
        btnSwing3 = (ImageButton)findViewById(R.id.btnSwing3);
        btnSwing4 = (ImageButton)findViewById(R.id.btnSwing4);
        btnSwing5 = (ImageButton)findViewById(R.id.btnSwing5);
        btnSwing6 = (ImageButton)findViewById(R.id.btnSwing6);
        btnSwing1.setOnClickListener(this);
        btnSwing2.setOnClickListener(this);
        btnSwing3.setOnClickListener(this);
        btnSwing4.setOnClickListener(this);
        btnSwing5.setOnClickListener(this);
        btnSwing6.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch(v.getId()){
            case R.id.btnSwing1:
                intent = new Intent(SwingActivity.this, DataMapActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSwing2:

                break;
            case R.id.btnSwing3:

                break;
            case R.id.btnSwing4:

                break;
            case R.id.btnSwing5:

                break;
            case R.id.btnSwing6:

                break;
        }
    }
}
