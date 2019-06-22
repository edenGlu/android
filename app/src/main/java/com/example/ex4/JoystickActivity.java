package com.example.ex4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class JoystickActivity extends AppCompatActivity implements JoystickView.JoystickListener{

    private JoystickView joystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        setContentView(R.layout.activity_joystick);
        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        int port = Integer.parseInt(intent.getStringExtra("port"));

        joystick = new JoystickView(this, ip, port);
        setContentView(joystick);
    }


    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        Log.d("Main Method", "x percent" + xPercent + " Y percent" + yPercent);
    }
}
