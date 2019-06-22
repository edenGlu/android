package com.example.ex4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // Called when user taps the Send button
    public void connectToServer(View view) {
        EditText ipText =
                (EditText)findViewById(R.id.ipText);
        Intent intent = new Intent(this,
                JoystickActivity.class);
        String ip = ipText.getText().toString();
        EditText portText =
                (EditText)findViewById(R.id.portText);
        String port = portText.getText().toString();

        intent.putExtra("ip", ip);
        intent.putExtra("port", port);
        startActivity(intent);
    }
}
