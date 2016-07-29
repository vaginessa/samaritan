package com.samaritan.android.samaritan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.samaritan.android.samaritan.animations.AnimateAlpha;
import com.samaritan.android.samaritan.animations.AnimateLine;
import com.samaritan.android.samaritan.animations.AnimateLineListener;
import com.samaritan.android.samaritan.animations.AnimateScale;
import com.samaritan.android.samaritan.animations.AnimateText;
import com.samaritan.android.samaritan.utilities.DynamicTextView;
import com.samaritan.android.samaritan.utilities.SpeechListener;
import com.samaritan.android.samaritan.utilities.Storage;
import com.thetorine.samaritan.R;

import java.util.ArrayList;

public class Samaritan extends Activity implements Runnable {
    private ArrayList<String> allWords = new ArrayList<>();
    public int mWordIndex;
    private Handler mHandler = new Handler();
    private boolean mDestroyed;
    public boolean mRun;
    private boolean mTriangleStatus;
    public static Storage storage;
    public SpeechRecognizer sr;
    private Intent recognizerIntent;
    public boolean isListening = false;
    public boolean showText = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        
        // Set color scheme
        this.setTheme(R.style.TextBlackTheme);
        View view = findViewById(R.id.black_line);
        view.setBackgroundColor(getResources().getColor(android.R.color.white));

        DynamicTextView textView = (DynamicTextView) findViewById(R.id.displayText);
        textView.setTextColor(getResources().getColor(android.R.color.white));

        // Set font
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/magdacleanmono-regular.otf");
        ((DynamicTextView)findViewById(R.id.displayText)).setTypeface(font);

        // Set animation variables
        storage = new Storage();
        mHandler.postDelayed(this, 100);

        // Set speech listener
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new SpeechListener(this));

        // Listens to speech without popup
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Force full screen
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDestroyed = true;
        finish();
    } 

    @Override
    public void run() {
        DynamicTextView tv = (DynamicTextView) findViewById(R.id.displayText);
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        if(mRun) {
            if(mWordIndex < allWords.size()) {
                changeText(tv);
                animateLine();
            } else {
                mRun = false;
            }
            openCloseTriangle(iv, 0);
          	blinkTriangle(iv);
        } else {
        	openCloseTriangle(iv, 1);
            blinkTriangle(iv);
            if(!isListening && !showText)
                animateLine();
        }
        if(!mDestroyed) {
            mHandler.postDelayed(this, 10);
        }
    }
    
    /**
     * Runs animation according to supplied id.
     * @param animation the animation to run.
     * @param duration the duration to display
     */
    public void runAnimation(int animation, int duration) {
        switch(animation) {
            case 0: {
                DynamicTextView tv = (DynamicTextView) findViewById(R.id.displayText);
                String word = allWords.get(mWordIndex);

                AnimateText animateText = new AnimateText(tv, word);
                animateText.setDuration(750);
                tv.startAnimation(animateText);
                mWordIndex++;
                break;
            }
            case 1: {
                ImageView iv = (ImageView)findViewById(R.id.imageView);

                AnimateAlpha animateTriangle = new AnimateAlpha(iv);
                animateTriangle.setDuration(500);
                iv.startAnimation(animateTriangle);
                break;
            }
            case 2: {
                ImageView iv = (ImageView)findViewById(R.id.imageView);

                AnimateScale animateScale = new AnimateScale(iv, getResources().getDisplayMetrics().density);
                animateScale.setDuration(200);
                iv.startAnimation(animateScale);
                break;
            }
            case 3: {
            	View view = findViewById(R.id.black_line);
            	
            	AnimateLine animateLine = new AnimateLine(view, storage.mWidth);
                animateLine.setAnimationListener(new AnimateLineListener(this));
                animateLine.setInterpolator(new LinearInterpolator());
                animateLine.setDuration(duration);

                view.startAnimation(animateLine);
            }
        }
    }
    
    /**
     * Changes the word of the DynamicTextView in the current view.
     * @param tv the DynamicTextView to change the word of.
     */
    private void changeText(DynamicTextView tv) {
    	 Animation a = tv.getAnimation();
         if(a != null) {
             if(a.hasEnded()) {
                 runAnimation(0,0);
             }
         } else {
             runAnimation(0,0);
         }
    }
    
    public void animateLine() {
    	if(!(storage.mLastWidth == storage.mWidth) && !isListening) {
    		storage.mLastWidth = storage.mWidth;
    		runAnimation(3,150);
    	}
    }
    
    /**
     * Checks for any animations and either opens or closes the triangle.
     * @param iv the ImageView that corresponds to the triangle on screen.
     * @param method decides on whether to open or close the triangle. 
     */
    public void openCloseTriangle(ImageView iv, int method) {
   		if(method == 0) {
   			if(!mTriangleStatus) {
   				runAnimation(2,0);
                mTriangleStatus = true;
                iv.setAlpha(1f);
            }
        } else {
            if(mTriangleStatus) {
                runAnimation(2,0);
                mTriangleStatus = false;
                iv.setAlpha(1f);
                mWordIndex = 0;
            }
        }
    }
    
    /**
     * Blinks the triangle associated with this image.
     * @param iv the ImageView to blink.
     */
    public void blinkTriangle(ImageView iv) {
    	Animation animation = iv.getAnimation();
        if(animation != null) {
            if(animation.hasEnded()) {
                runAnimation(1,0);
            }
        } else {
            runAnimation(1,0);
        }
    }
    
    public void onClick(View view) {
        // Start listening to the user
        if(!isListening) {
            storage.mWidth = (int) (getResources().getDisplayMetrics().widthPixels - (getResources().getDisplayMetrics().density*20));
            animateLine();
            isListening = true;
            sr.startListening(recognizerIntent);
        }
    }

    // Display the animation
    public void startAnimation(){
        if(!mRun) {
            View line = findViewById(R.id.black_line);
            if(line.getAnimation() != null) {
                if(line.getAnimation().hasEnded()) {
                    mRun = true;
                    mWordIndex = 0;
                }
            } else {
                mRun = true;
                mWordIndex = 0;
            }
        }
    }
    public void parseText(String text) {
        allWords.clear();
       	for(String w : text.toUpperCase().split(" ")) {
            w = " " + w + " ";
            if (w.contains("?")) {
                allWords.add(w.replace("?", ""));
                allWords.add(" ? ");
            } else if (w.contains("!")) {
                allWords.add(w.replace("!", ""));
                allWords.add(" ! ");
            } else {
                allWords.add(w);
            }
        }
        allWords.add("   ");
    }
}