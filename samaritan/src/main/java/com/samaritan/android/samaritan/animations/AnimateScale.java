package com.samaritan.android.samaritan.animations;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import java.lang.reflect.Field;

public class AnimateScale extends Animation {

    private ImageView imageView;
    private int sMeasurement;
    private boolean sPoint;
    private int density;
    private int textSP;

    public AnimateScale(ImageView view, float d) {
        this.imageView = view;
        this.density = (int)d;
        this.textSP = (int) (getMaxWidth(imageView) / d);
        if(view.getWidth() <= 0) {
            sPoint = false;
        } else if(view.getWidth() >= 0) {
            sPoint = true;
            sMeasurement = textSP*density;
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        int mApply = (int) (sMeasurement + (sPoint ? -1 : 1) * (textSP*density*interpolatedTime));

        imageView.getLayoutParams().width = mApply;
        imageView.getLayoutParams().height = mApply;
        imageView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
    
    private int getMaxWidth(ImageView view) {
    	try {
    		Field maxWidthField = ImageView.class.getDeclaredField("mMaxWidth");
    		maxWidthField.setAccessible(true);
            return (int) (Integer) maxWidthField.get(view);
    	} catch(Exception ignored) {}
    	return 0;
    }
}
