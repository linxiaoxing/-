package com.example.speakapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1.*;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.ByteString;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// https://code.tutsplus.com/tutorials/android-sdk-build-a-speak-and-repeat-app--mobile-11197
public class SpeechFromWavActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener {

    //voice recognition and general variables

    //variable for checking Voice Recognition support on user device
    private static final int VR_REQUEST = 999;

    //ListView for displaying suggested words
    private TextView wordList;

    //Log tag for output information
    private final String LOG_TAG = "SpeechRepeatActivity";//***enter your own tag here***

//TTS variables

    //variable for checking TTS engine data on user device
    private int MY_DATA_CHECK_CODE = 0;

    //Text To Speech instance
    private TextToSpeech repeatTTS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        //set content view
        setContentView(R.layout.main_speak );
        init();
    }

    private void init() {
        //gain reference to speak button
        Button speechBtn = (Button) findViewById(R.id.speech_btn);
        //gain reference to word list
        wordList = (TextView) findViewById(R.id.word);
        //find out whether speech recognition is supported
        PackageManager packManager = getPackageManager();
        List<ResolveInfo> intActivities = packManager.queryIntentActivities(new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0) {
            //speech recognition is supported - detect user button clicks
            speechBtn.setOnClickListener(this);

            //prepare the TTS to repeat chosen words
            Intent checkTTSIntent = new Intent();
            //check TTS data
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            //start the checking Intent - will retrieve result in onActivityResult
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        }
        else
        {
            //speech recognition not supported, disable button and output message
            speechBtn.setEnabled(false);
            Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
        }

        //detect user clicks of suggested words
//        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            //click listener for items within list
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//                //cast the view
//                TextView wordView = (TextView)view;
//                //retrieve the chosen word
//                String wordChosen = (String) wordView.getText();
//                //output for debugging
//                Log.v(LOG_TAG, "chosen: "+wordChosen);
//                //output Toast message
//                Toast.makeText( SpeechFromWavActivity.this, "You said: "+wordChosen, Toast.LENGTH_SHORT).show();//**alter for your Activity name***
//
//                //speak the word using the TTS
//                repeatTTS.speak("You said: "+wordChosen, TextToSpeech.QUEUE_FLUSH, null);
//
//            }
//        });
    }

    /**
     * Called when the user presses the speak button
     */
    public void onClick(View v) {
        if (v.getId() == R.id.speech_btn) {
            //listen for results
            listenToSpeech();
        }
    }

    /**
     * Instruct the app to listen for user speech input
     */
    private void listenToSpeech() {

        //start the speech recognition intent passing required data
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //message to display while listening
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
        //set speech model
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify number of results to retrieve
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

        //start listening
        startActivityForResult(listenIntent, VR_REQUEST);
    }

    /**
     * onActivityResults handles:
     *  - retrieving results of speech recognition listening
     *  - retrieving result of TTS data check
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check speech recognition result
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK)
        {
            //store the returned word list as an ArrayList
            ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //set the retrieved list to display in the ListView using an ArrayAdapter
//            wordList.setAdapter(new ArrayAdapter<String>(this, R.layout.word, suggestedWords));
            wordList.setText( suggestedWords.toString() );
        }

        //returned from TTS data check
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            //we have the data - create a TTS instance
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                repeatTTS = new TextToSpeech(this, this);
                //data not installed, prompt the user to install it
            else
            {
                //intent will take user to TTS download page in Google Play
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        //tss code here

        //call superclass method
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * onInit fires when TTS initializes
     */
    public void onInit(int initStatus) {
        //if successful, set locale
        if (initStatus == TextToSpeech.SUCCESS)
            repeatTTS.setLanguage( Locale.UK);//***choose your own locale here***
    }

    public static void call(){
        // Instantiates a client
        try (SpeechClient speechClient = SpeechClient.create()) {

            // The path to the audio file to transcribe
            String fileName = "data/wav/cn/早饭吃西红柿炒鸡蛋.wav";

            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("cmn-Hans-CN") //中文
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
