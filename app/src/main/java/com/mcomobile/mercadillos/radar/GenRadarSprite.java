package com.mcomobile.mercadillos.radar;

/**
 * @author Yasir.Ali <ali.yasir0@gmail.com>
 *
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class GenRadarSprite extends View {
	private Paint mPaint;
    private List<GenRadarPoint> mRadarPoints;
    
    // Lint requires a Default Construct for Views, So, make it private because its useless
    private GenRadarSprite(Context context){
    	super(context);
    }
    
    public GenRadarSprite(Context context, List<GenRadarPoint> genRadarPoints) {
        super(context);
        this.mRadarPoints = new ArrayList<GenRadarPoint>(genRadarPoints);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);     
        for(int i = 0; i < mRadarPoints.size(); i++){
        	mPaint.setColor(mRadarPoints.get(i).getColor());
        	canvas.drawCircle(mRadarPoints.get(i).getX(), mRadarPoints.get(i).getY(), mRadarPoints.get(i).getRaduis(), mPaint);
        }        
    }
    
    public void updateUIWithNewRadarPoints(List<GenRadarPoint> genRadarPoints){
    	this.mRadarPoints = new ArrayList<GenRadarPoint>(genRadarPoints);
    	this.invalidate();
    }
}