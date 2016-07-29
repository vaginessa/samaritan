package com.samaritan.android.samaritan.animations;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

public class AnimateText extends Animation {
    private TextView textView;
    private String animatingWord;

    public AnimateText(TextView view, String word) {
        this.textView = view;
        this.animatingWord = word;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(!textView.getText().toString().equals(animatingWord)) {
            textView.setAlpha(0);

			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					textView.setText(animatingWord);
				}
			});
        }
        
       	int currentTime = (int) (getDuration() * interpolatedTime);

        textView.setAlpha((currentTime < 50 ? currentTime / 50f : 1));
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

