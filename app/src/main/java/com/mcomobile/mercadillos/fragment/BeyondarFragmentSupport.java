package com.mcomobile.mercadillos.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mcomobile.mercadillos.opengl.renderer.ARRenderer;
import com.mcomobile.mercadillos.opengl.util.LowPassFilter;
import com.mcomobile.mercadillos.screenshot.OnScreenshotListener;
import com.mcomobile.mercadillos.screenshot.ScreenshotHelper;
import com.mcomobile.mercadillos.util.math.geom.Ray;
import com.mcomobile.mercadillos.view.BeyondarGLSurfaceView;
import com.mcomobile.mercadillos.view.BeyondarViewAdapter;
import com.mcomobile.mercadillos.view.CameraView;
import com.mcomobile.mercadillos.view.OnClickBeyondarObjectListener;
import com.mcomobile.mercadillos.view.OnTouchBeyondarViewListener;
import com.mcomobile.mercadillos.world.BeyondarObject;
import com.mcomobile.mercadillos.world.World;

public class BeyondarFragmentSupport extends Fragment implements ARRenderer.FpsUpdatable,
		OnClickListener, OnTouchListener {

	private static final int CORE_POOL_SIZE = 1;
	private static final int MAXIMUM_POOL_SIZE = 1;
	private static final long KEEP_ALIVE_TIME = 1000; // 1000 ms

	private CameraView mBeyondarCameraView;
	private BeyondarGLSurfaceView mBeyondarGLSurface;
	private TextView mFpsTextView;
	private RelativeLayout mMailLayout;

	private World mWorld;

	private OnTouchBeyondarViewListener mTouchListener;
	private OnClickBeyondarObjectListener mClickListener;

	private float mLastScreenTouchX, mLastScreenTouchY;

	private ThreadPoolExecutor mThreadPool;
	private BlockingQueue<Runnable> mBlockingQueue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBlockingQueue = new LinkedBlockingQueue<Runnable>();
		mThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
				KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, mBlockingQueue);
	}

	private void init() {

		ViewGroup.LayoutParams params = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		mMailLayout = new RelativeLayout(getActivity());
		mBeyondarGLSurface = createBeyondarGLSurfaceView();
		mBeyondarGLSurface.setOnTouchListener(this);

		mBeyondarCameraView = createCameraView();

		mMailLayout.addView(mBeyondarCameraView, params);
		mMailLayout.addView(mBeyondarGLSurface, params);
	}

	private void checkIfSensorsAvailable() {
		PackageManager PM = getActivity().getPackageManager();
		boolean compass = PM
				.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
		boolean accelerometer = PM
				.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
		if (!compass && !accelerometer) {
			throw new IllegalStateException(
					getClass().getName()
							+ " can not run without the compass and the acelerometer sensors.");
		} else if (!compass) {
			throw new IllegalStateException(getClass().getName()
					+ " can not run without the compass sensor.");
		} else if (!accelerometer) {
			throw new IllegalStateException(getClass().getName()
					+ " can not run without the acelerometer sensor.");
		}

	}

	/**
	 * Override this method to personalize the {@link BeyondarGLSurfaceView}
	 * that will be instantiated
	 * 
	 * @return
	 */
	protected BeyondarGLSurfaceView createBeyondarGLSurfaceView() {
		return new BeyondarGLSurfaceView(getActivity());
	}

	/**
	 * Override this method to personalize the {@link CameraView} that will be
	 * instantiated
	 * 
	 * @return
	 */
	protected CameraView createCameraView() {
		return new CameraView(getActivity());
	}

	/**
	 * 
	 * Returns the CameraView for this class instance
	 * 
	 * @return
	 */
	public CameraView getCameraView() {
		return mBeyondarCameraView;
	}

	/**
	 * Returns the SurfaceView for this class instance
	 * 
	 * @return
	 */
	public BeyondarGLSurfaceView getGLSurfaceView() {
		return mBeyondarGLSurface;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		init();
		startRenderingAR();
		return mMailLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		mBeyondarCameraView.startPreviewCamera();
		mBeyondarGLSurface.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mBeyondarCameraView.releaseCamera();
		mBeyondarGLSurface.onPause();
	}

	/**
	 * Set the listener to get notified when the user touch the AR view
	 * 
	 * @param listener
	 */
	public void setOnTouchBeyondarViewListener(
			OnTouchBeyondarViewListener listener) {
		mTouchListener = listener;
	}

	public void setOnClickBeyondarObjectListener(
			OnClickBeyondarObjectListener listener) {
		mClickListener = listener;
		mMailLayout.setClickable(listener != null);
		mMailLayout.setOnClickListener(this);
	}

	@Override
	public boolean onTouch(View v, final MotionEvent event) {
		mLastScreenTouchX = event.getX();
		mLastScreenTouchY = event.getY();

		if (mWorld == null || mTouchListener == null || event == null) {
			return false;
		}
		mTouchListener.onTouchBeyondarView(event, mBeyondarGLSurface);
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == mMailLayout) {
			if (mClickListener == null) {
				return;
			}
			final float lastX = mLastScreenTouchX;
			final float lastY = mLastScreenTouchY;

			mThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					final ArrayList<BeyondarObject> beyondarObjects = new ArrayList<BeyondarObject>();
					mBeyondarGLSurface.getBeyondarObjectsOnScreenCoordinates(
							lastX, lastY, beyondarObjects);
					if (beyondarObjects.size() == 0)
						return;
					mBeyondarGLSurface.post(new Runnable() {
						@Override
						public void run() {
							OnClickBeyondarObjectListener listener = mClickListener;
							if (listener != null) {
								listener.onClickBeyondarObject(beyondarObjects);
							}
						}
					});
				}
			});
		}
	}

	/**
	 * Get the world in use by the fragment
	 * 
	 * @return
	 */
	public World getWorld() {
		return mWorld;
	}

	/**
	 * Set the world to be shown
	 * 
	 * @param world
	 * 
	 * @throws IllegalStateException
	 *             If the device do not have the required sensors available
	 */
	public void setWorld(World world) {
		try {
			checkIfSensorsAvailable();
		} catch (IllegalStateException e) {
			throw e;
		}
		mWorld = world;
		mBeyondarGLSurface.setWorld(world);
	}

	/**
	 * Specify the delay to apply to the accelerometer and the magnetic field
	 * sensor. If you don't know what is the best value, don't touch it. The
	 * following values are applicable:<br>
	 * <br>
	 * SensorManager.SENSOR_DELAY_UI<br>
	 * SensorManager.SENSOR_DELAY_NORMAL <br>
	 * SensorManager.SENSOR_DELAY_GAME <br>
	 * SensorManager.SENSOR_DELAY_GAME <br>
	 * SensorManager.SENSOR_DELAY_FASTEST <br>
	 * <br>
	 * You can find more information in the
	 * {@link android.hardware.SensorManager} class
	 * 
	 * 
	 * @param delay
	 */
	public void setSensorDelay(int delay) {
		mBeyondarGLSurface.setSensorDelay(delay);
	}

	/**
	 * Get the current sensor delay. See {@link android.hardware.SensorManager}
	 * for more information
	 * 
	 * @return sensor delay
	 */
	public int getSensorDelay() {
		return mBeyondarGLSurface.getSensorDelay();
	}

	public void setFpsUpdatable(ARRenderer.FpsUpdatable fpsUpdatable) {
		mBeyondarGLSurface.setFpsUpdatable(fpsUpdatable);
	}

	/**
	 * Force the GLSurface to stop rendering the AR world
	 */
	public void stopRenderingAR() {
		mBeyondarGLSurface.setVisibility(View.INVISIBLE);
	}

	/**
	 * Force the GLSurface to start rendering the AR world
	 */
	public void startRenderingAR() {
		mBeyondarGLSurface.setVisibility(View.VISIBLE);
	}

	/**
	 * Get the GeoObject that intersect with the coordinates x, y on the screen.<br>
	 * NOTE: When this method is called a new {@link List} is created.
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return A new list with the {@link BeyondarObject} that collide with the
	 *         screen cord
	 */
	public List<BeyondarObject> getBeyondarObjectsOnScreenCoordinates(float x,
			float y) {
		ArrayList<BeyondarObject> beyondarObjects = new ArrayList<BeyondarObject>();
		mBeyondarGLSurface.getBeyondarObjectsOnScreenCoordinates(x, y,
				beyondarObjects);
		return beyondarObjects;
	}

	/**
	 * Get the GeoObject that intersect with the coordinates x, y on the screen
	 * 
	 * @param x
	 * @param y
	 * @param beyondarObjects
	 *            The output list to place all the {@link BeyondarObject} that
	 *            collide with the screen cord
	 * @return
	 */
	public void getBeyondarObjectsOnScreenCoordinates(float x, float y,
			ArrayList<BeyondarObject> beyondarObjects) {
		mBeyondarGLSurface.getBeyondarObjectsOnScreenCoordinates(x, y,
				beyondarObjects);
	}

	/**
	 * Get the GeoObject that intersect with the coordinates x, y on the screen
	 * 
	 * @param x
	 * @param y
	 * @param beyondarObjects
	 *            The output list to place all the {@link BeyondarObject} that
	 *            collide with the screen cord
	 * @param ray
	 *            The ray that will hold the direction of the screen coordinate
	 * @return
	 */
	public void getBeyondarObjectsOnScreenCoordinates(float x, float y,
			ArrayList<BeyondarObject> beyondarObjects, Ray ray) {
		mBeyondarGLSurface.getBeyondarObjectsOnScreenCoordinates(x, y,
				beyondarObjects, ray);

	}

	/**
	 * When a {@link } is rendered according to its position it could
	 * look very small if it is far away. Use this method to render far objects
	 * as if there were closer.<br>
	 * For instance if there is an object at 100 meters and we want to have
	 * everything at least at 25 meters, we could use this method for that
	 * purpose. <br>
	 * To set it to the default behavior just set it to 0
	 * 
	 * @param maxDistanceSize
	 *            The top far distance (in meters) which we want to draw a
	 *            {@link } , 0 to set again the default behavior
	 */
	public void setMaxFarDistance(float maxDistanceSize) {
		mBeyondarGLSurface.setMaxDistanceSize(maxDistanceSize);
	}

	/**
	 * Get the max distance which a {@link } will be rendered.
	 * 
	 * @return The current max distance. 0 is the default behavior
	 */
	public float getMaxDistanceSize() {
		return mBeyondarGLSurface.getMaxDistanceSize();
	}

	/**
	 * When a {@link } is rendered according to its position it could
	 * look very big if it is too close. Use this method to render near objects
	 * as if there were farther.<br>
	 * For instance if there is an object at 1 meters and we want to have
	 * everything at least at 10 meters, we could use this method for that
	 * purpose. <br>
	 * To set it to the default behavior just set it to 0
	 * 
	 * @param minDistanceSize
	 *            The top near distance (in meters) which we want to draw a
	 *            {@link } , 0 to set again the default behavior
	 */
	public void setMinFarDistanceSize(float minDistanceSize) {
		mBeyondarGLSurface.setMinDistanceSize(minDistanceSize);
	}

	/**
	 * Get the minimum distance which a {@link } will be rendered.
	 * 
	 * @return The current minimum distance. 0 is the default behavior
	 */
	public float getMinDistanceSize() {
		return mBeyondarGLSurface.getMinDistanceSize();
	}

	/**
	 * Take a screenshot of the beyondar fragment. The screenshot will contain
	 * the camera + the AR world
	 * 
	 * @param listener
	 */
	public void takeScreenshot(OnScreenshotListener listener) {
		ScreenshotHelper.takeScreenshot(getCameraView(), getGLSurfaceView(),
				listener);
	}

	/**
	 * Show the number of frames per second. False by default
	 * 
	 * @param show
	 *            True to show the FPS, false otherwise
	 */
	public void showFPS(boolean show) {
		if (show) {
			if (mFpsTextView == null) {
				mFpsTextView = new TextView(getActivity());
				mFpsTextView.setBackgroundResource(android.R.color.black);
				mFpsTextView.setTextColor(getResources().getColor(
						android.R.color.white));
				LayoutParams params = new LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				mMailLayout.addView(mFpsTextView, params);
			}
			mFpsTextView.setVisibility(View.VISIBLE);
			setFpsUpdatable(this);
		} else if (mFpsTextView != null) {
			mFpsTextView.setVisibility(View.GONE);
			setFpsUpdatable(null);
		}
	}

	@Override
	public void onFpsUpdate(final float fps) {
		if (mFpsTextView != null) {
			mFpsTextView.post(new Runnable() {
				@Override
				public void run() {
					mFpsTextView.setText("fps: " + fps);
				}
			});
		}
	}

	/**
	 * Set the adapter to draw the views on top of the AR View.
	 * 
	 * @param adapter
	 */
	public void setBeyondarViewAdapter(BeyondarViewAdapter adapter) {
		mBeyondarGLSurface.setBeyondarViewAdapter(adapter, mMailLayout);
	}

	public void forceFillBeyondarObjectPositionsOnRendering(boolean fill) {
		mBeyondarGLSurface.forceFillBeyondarObjectPositionsOnRendering(fill);
	}

	/**
	 * Use this method to fill all the screen positions of the
	 * {@link BeyondarObject}. After this method is called you can use the
	 * following:<br>
	 * {@link BeyondarObject#getScreenPositionBottomLeft()}<br>
	 * {@link BeyondarObject#getScreenPositionBottomRight()}<br>
	 * {@link BeyondarObject#getScreenPositionTopLeft()}<br>
	 * {@link BeyondarObject#getScreenPositionTopRight()}
	 * 
	 * @param beyondarObject
	 *            The {@link BeyondarObject} to compute
	 */
	public void fillBeyondarObjectPositions(BeyondarObject beyondarObject) {
		mBeyondarGLSurface.fillBeyondarObjectPositions(beyondarObject);
	}

	/**
	 * Set the alpha value of the sensors low pass filter.
	 * 
	 * @param alpha
	 *            A number between 0 and 1
	 */
	public void setSensorFilterAlpha(float alpha) {
		if (alpha < 0 || alpha > 1) {
			throw new IllegalArgumentException("Alpha must be between 0 and 1");
		}
		LowPassFilter.ALPHA = alpha;
	}
}