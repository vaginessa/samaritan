package com.samaritan.android.samaritan.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import com.samaritan.android.samaritan.Samaritan;
import com.thetorine.samaritan.R;

public class AnimateLineListener implements AnimationListener {
    private Samaritan s;
    public AnimateLineListener(Samaritan s){
        this.s = s;
    }
    @Override
    public void onAnimationEnd(Animation animation) {
        View line = s.findViewById(R.id.black_line);

        if(s.isListening) {
            TextView textView = (TextView) s.findViewById(R.id.displayText);
            line.clearAnimation();

            Samaritan.storage.mWidth = (int) textView.getPaint().measureText("   ");
            s.runAnimation(3,5000);
        } else {
            s.isListening = false;
            s.sr.cancel();

            if(s.showText){
                if(line.getAnimation()!= null) {
                    if (line.getAnimation().hasEnded()) {
                        s.mRun = true;
                        s.mWordIndex = 0;
                    }
                }else{
                    s.mRun = true;
                    s.mWordIndex = 0;
                }
                s.showText = false;
            }
        }
    }
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}
}
