package com.mcomobile.mercadillos.radar;

/*
 * Copyright (C) 2013 Yasir Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Yasir.Ali <ali.yasir0@gmail.com>
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class GenRadarManager implements SensorEventListener {

	private static final String TAG = "GenRadarManager";
	private SensorManager mSensorManager;
	private Sensor mOrientationSensor;
	private Context mContext;

	private GenRadarPoint mCenterRadarPoint;
	public List<GenRadarPoint> mRadarPoints;

	// CHANGE THIS: minimum padding in pixel
	private static int sMminimumImagePaddingInPx = 63;

	// formula for quarter PI
	private static final double sQuarterpi = Math.PI / 4.0;

	double minXY [] = new double[]{-1,-1};

	double maxXY [] = new double[] {-1,-1};

	public static int MAP_WIDTH;
	public static int MAP_HEIGHT;
	public static int X_TRANSFORMATION;
	public static int Y_TRANSFORMATION;
	public static float POINT_RADIUS = 1.5f;

	// record the compass picture angle turned
	private float currentDegree = 0f;

	private GenRadarSprite mRadarSprite;
	private LinearLayout mRadarContainer;

	public GenRadarManager(Context context, LinearLayout radarContainer, int radarWidth, int radarHeight){
		mContext = context;
		mRadarContainer = radarContainer;
		mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		MAP_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radarHeight, context.getResources().getDisplayMetrics());
		MAP_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radarWidth, context.getResources().getDisplayMetrics());
	}

	public void setCenterRadarPoint(GenRadarPoint center){
		Log.d(TAG, "Setting CenterRadarPoint");
		mCenterRadarPoint = center;
	}

	public void initRadarlayout() {
		Log.d(TAG, "Initiating Radar Layout");
		mRadarPoints = new ArrayList<GenRadarPoint>();
		mRadarSprite = new GenRadarSprite(mContext, mRadarPoints);
		LayoutParams params = new LayoutParams(MAP_WIDTH, MAP_HEIGHT);
		mRadarSprite.setLayoutParams(params);
		mRadarContainer.addView(mRadarSprite);

	}

	public void registerListeners(){
		Log.d(TAG, "Registering Event Listeners");
		mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public synchronized void updateRadarWithPoints(List<GenRadarPoint> genRadarPoints){
		Log.d(TAG, "Updating Radar With New Points");
		resetTransformationValues();
		mRadarPoints =  new ArrayList<GenRadarPoint>(genRadarPoints);
		mRadarPoints.add(0, mCenterRadarPoint);
		applyMercatorProjection();
		adjustForNegativeValues();
		List<GenRadarPoint> finalPointsToDraw = applyCenterTransformation();
		mRadarSprite.updateUIWithNewRadarPoints(finalPointsToDraw);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d(TAG, "on Accuracy Changed");
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		//Log.d(TAG, "on Sensor Changed");
		if(mRadarContainer != null){
			
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
				float degree = LowPassFilter.filter3D( event.values[0], ((float)-currentDegree) );	
				
		        // create a rotation animation (reverse turn degree degrees)
		        RotateAnimation ra = new RotateAnimation(
		                currentDegree, 
		                -degree,
		                Animation.RELATIVE_TO_SELF, 0.5f, 
		                Animation.RELATIVE_TO_SELF,
		                0.5f);

		        // how long the animation will take place
		        ra.setDuration(210);

		        // set the animation after the end of the reservation status
		        ra.setFillAfter(true);

		        // Start the animation
		        mRadarContainer.startAnimation(ra);
		        currentDegree = -degree;
			}
		}
	}

	private void applyMercatorProjection() {

		Log.d(TAG, "Applying Mercator Projection on Points");
		// for every Point, convert the longitude/latitude to X/Y using Mercator projection formula
		for(int i = 0; i < mRadarPoints.size(); i++){
			// convert to radian
			double longitude = mRadarPoints.get(i).getLng() * Math.PI / 180;
			double latitude = mRadarPoints.get(i).getLat() * Math.PI / 180;

			double x = longitude;
			double y = Math.log(Math.tan(sQuarterpi + 0.5 * latitude));

			// The reason we need to determine the min X and Y values is because in order to draw the map,
			// we need to offset the position so that there will be no negative X and Y values
			minXY[0] = (minXY[0] == -1) ? x : Math.min(minXY[0], x);
			minXY[1] = (minXY[1] == -1) ? y : Math.min(minXY[1], y);

			mRadarPoints.get(i).setX((float) x);
			mRadarPoints.get(i).setY((float) y);
		}
	}

	private void adjustForNegativeValues() {
		
		Log.d(TAG, "Adjusting Radar Points for Negative x/y Values");
		
		// re-adjust coordinate to ensure there are no negative values
		int size = mRadarPoints.size();

		for(int i = 0; i < size; i++){
			mRadarPoints.get(i).setX( (float) (mRadarPoints.get(i).getX() - minXY[0]) );
			mRadarPoints.get(i).setY( (float) (mRadarPoints.get(i).getY() - minXY[1]) );

			// now, we need to keep track the max X and Y values
			maxXY[0] = (maxXY[0] == -1) ? mRadarPoints.get(i).getX() : Math.max(maxXY[0], mRadarPoints.get(i).getX());
			maxXY[1] = (maxXY[1] == -1) ? mRadarPoints.get(i).getY() : Math.max(maxXY[1], mRadarPoints.get(i).getY());
		}
	}

	private List<GenRadarPoint> applyCenterTransformation() {
		
		Log.d(TAG, "Applying Center Transformation to Points");
		
		int paddingBothSides = sMminimumImagePaddingInPx * 2;

		// the actual drawing space for the map on the image
		int mapWidth = (int) (MAP_WIDTH - paddingBothSides);
		int mapHeight = (int) (MAP_HEIGHT - paddingBothSides);

		// determine the width and height ratio because we need to magnify the map to fit into the given image dimension
		double mapWidthRatio = mapWidth / maxXY[0];
		double mapHeightRatio = mapHeight / maxXY[1];

		// using different ratios for width and height will cause the map to be stretched. So, we have to determine
		// the global ratio that will perfectly fit into the given image dimension
		double globalRatio = Math.min(mapWidthRatio, mapHeightRatio);

		// now we need to readjust the padding to ensure the map is always drawn on the center of the given image dimension
		double heightPadding = (MAP_HEIGHT - (globalRatio * maxXY[1])) / 2;
		double widthPadding = (MAP_WIDTH - (globalRatio * maxXY[0])) / 2;

		// for each point, draw on UI
		int size = mRadarPoints.size();

		for(int i = 0; i < size; i++){

			//if(mRadarPoints.get(i).isVisibleOnRadar()){
			int adjustedX = (int) (widthPadding + (mRadarPoints.get(i).getX() * globalRatio));

			// need to invert the Y since 0,0 starts at top left
			int adjustedY = (int) (MAP_HEIGHT - heightPadding - (mRadarPoints.get(i).getY() * globalRatio));

			mRadarPoints.get(i).setX(adjustedX);
			mRadarPoints.get(i).setY(adjustedY);

			//}
		}


		// Update X Coordinate
		X_TRANSFORMATION = (int) ((MAP_WIDTH / 2) - mCenterRadarPoint.getX());
		mCenterRadarPoint.setX( X_TRANSFORMATION + mCenterRadarPoint.getX() );

		Log.d("X_TRANSFORMATION",""+X_TRANSFORMATION);

		// Update Y Coordinate
		Y_TRANSFORMATION = (int) ((MAP_HEIGHT / 2) - mCenterRadarPoint.getY());
		mCenterRadarPoint.setY( Y_TRANSFORMATION + mCenterRadarPoint.getY() );

		Log.d("Y_TRANSFORMATION",""+Y_TRANSFORMATION);

		for(int i = 1; i < mRadarPoints.size(); i++){
			mRadarPoints.get(i).setX(mRadarPoints.get(i).getX() + X_TRANSFORMATION);
			mRadarPoints.get(i).setY(mRadarPoints.get(i).getY() + Y_TRANSFORMATION);
		}


		List<GenRadarPoint> finalPointsToDraw = new ArrayList<GenRadarPoint>();
		// Remove the locations which are now out of map

		for(int i = 0; i < mRadarPoints.size(); i++){
			Log.d("circle", mRadarPoints.get(i).toString());
			if(mRadarPoints.get(i).getX() > MAP_WIDTH || mRadarPoints.get(i).getY() > MAP_HEIGHT){
				Log.d("oop", "removing this circle...");
			}else{
				finalPointsToDraw.add(mRadarPoints.get(i));
			}
		}
		return Collections.unmodifiableList(finalPointsToDraw);
	}

	public void initAndUpdateRadarWithPoints(GenRadarPoint center, List<GenRadarPoint> genRadarPoints){
		Log.d(TAG, "Initiating New Radar With Points");
		this.setCenterRadarPoint(center);		
		this.initRadarlayout();
		this.registerListeners();
		this.updateRadarWithPoints(genRadarPoints);
	}



	public void unregisterListeners(){
		Log.d(TAG, "Unregistering the event listeners");
		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);
	}
	
	private void resetTransformationValues(){
		minXY = new double[]{-1,-1};
		maxXY = new double[] {-1,-1};		
		X_TRANSFORMATION = 0;
		Y_TRANSFORMATION = 0;
	}

}
