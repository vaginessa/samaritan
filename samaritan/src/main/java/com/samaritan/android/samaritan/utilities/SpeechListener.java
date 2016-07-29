package com.samaritan.android.samaritan.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.View;

import com.samaritan.android.samaritan.Samaritan;
import com.thetorine.samaritan.R;

import java.util.ArrayList;

public class SpeechListener implements RecognitionListener {
    private Samaritan s;
    public SpeechListener(Samaritan s){
        this.s = s;
    }

    @Override
    public void onResults(Bundle results) {
        // Get results
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches == null) return;

        try {
            // Request response from database
            if(isNetworkAvailable()) {
                new Connection(s).execute(matches.get(0));
            }
            else{
                s.parseText("no internet connection");
                s.startAnimation();

                // Quickly finish animating "listen" animation so the app can start the "display" animation
                View v = s.findViewById(R.id.black_line);
                if (v.getAnimation() != null) {
                    v.getAnimation().setDuration(200);
                    v.requestLayout();
                }
            }
        }catch(Exception e){
            s.parseText("calculating response");
            s.startAnimation();

            // Quickly finish animating "listen" animation so the app can start the "display" animation
            View v = s.findViewById(R.id.black_line);
            if (v.getAnimation() != null) {
                v.getAnimation().setDuration(200);
                v.requestLayout();
            }
        }

        // Reset variables
        s.isListening = false;
    }

    // Check if it's connected to the internet
    public boolean isNetworkAvailable(){
        NetworkInfo info = ((ConnectivityManager) s.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override public void onError(int errorCode) { s.isListening = false; }
    @Override public void onBeginningOfSpeech() {}
    @Override public void onBufferReceived(byte[] buffer) {}
    @Override public void onEndOfSpeech() {}
    @Override public void onEvent(int arg0, Bundle arg1) {}
    @Override public void onPartialResults(Bundle arg0) {}
    @Override public void onReadyForSpeech(Bundle arg0) {}
    @Override public void onRmsChanged(float arg0) {}
}