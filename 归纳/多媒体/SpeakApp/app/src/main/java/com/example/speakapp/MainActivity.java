package com.example.speakapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main);
    }

    public void speak(View view) {
        Intent intent = new Intent(this, SpeechRepeatActivity.class);
        startActivity(intent);
    }

    public void text(View view) {
        Intent intent = new Intent(this, SpeakingAndroid.class);
        startActivity(intent);
    }
}
