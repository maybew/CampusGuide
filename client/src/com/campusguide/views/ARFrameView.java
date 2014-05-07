package com.campusguide.views;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

public class ARFrameView extends View implements SensorEventListener {

	private String title;
	
	private Location dLoc;

	private static Location sLoc;

	SensorManager mSensorManager;
	Sensor mSensor;

	private double mAngle;
	private double mDeclination;
	private static double mHCameraAngle;
	private static double mVCameraAngle;

	private float[] mRotationMatrix = new float[9];
	private float[] mValues = new float[3];

	private Path mPath = new Path();
	Paint textPaint = new Paint();
	Paint strokePaint = new Paint();

	public static void setCameraConfig(double hAngle, double vAngle) {
		mHCameraAngle = Math.toRadians(hAngle);
		mVCameraAngle = Math.toRadians(vAngle);
	}
	
	public static void setStartLocation(Location s) {
		sLoc = s;
	}

	public ARFrameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		// Construct a wedge-shaped path
		//init();
	}

	public ARFrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//init();
	}

	public ARFrameView(Context context, String name, double lat, double lng) {
		super(context);
		init(name, lat, lng);
	}

	private void init(String name, double lat, double lng) {
		title = name;
		
		dLoc = new Location(name);
		dLoc.setLatitude(lat);
		dLoc.setLongitude(lng);
		
		mPath.moveTo(0, -50);
		mPath.lineTo(-20, 60);
		mPath.lineTo(0, 50);
		mPath.lineTo(20, 60);
		mPath.close();

		mDeclination = Math.toRadians(new GeomagneticField((float) sLoc
				.getLatitude(), (float) sLoc.getLongitude(), (float) sLoc
				.getAltitude(), System.currentTimeMillis()).getDeclination());
		double a = SphericalUtil.computeDistanceBetween(
				new LatLng(dLoc.getLatitude(), sLoc.getLongitude()),
				new LatLng(dLoc.getLatitude(), dLoc.getLongitude()));
		double b = SphericalUtil.computeDistanceBetween(
				new LatLng(sLoc.getLatitude(), dLoc.getLongitude()),
				new LatLng(dLoc.getLatitude(), dLoc.getLongitude()));
		mAngle = Math.atan(a / b);
		if (dLoc.getLatitude() > sLoc.getLatitude()) {
			if (dLoc.getLongitude() > sLoc.getLongitude()) {

			} else {
				mAngle = -mAngle;
			}
		} else {
			if (dLoc.getLongitude() > sLoc.getLongitude()) {
				mAngle = Math.PI - mAngle;
			} else {
				mAngle = mAngle - Math.PI;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

	    textPaint.setColor(Color.BLACK);
	    textPaint.setTextAlign(Paint.Align.CENTER);
	    textPaint.setTextSize(25);
	    textPaint.setTypeface(Typeface.DEFAULT_BOLD);

	    float length = textPaint.measureText(title);
	    strokePaint.setColor(Color.GRAY);
		//Paint paint = mPaint;

		// canvas.drawColor(Color.WHITE);

		//paint.setAntiAlias(true);
		//paint.setColor(Color.GREEN);
		//paint.setStyle(Paint.Style.FILL);

		int w = canvas.getWidth();
		int h = canvas.getHeight();

		double startX = mValues[0] - (mHCameraAngle / 2);
		if (startX < -Math.PI)
			startX += 2 * Math.PI;
		double endX = mValues[0] + (mHCameraAngle / 2);
		if (endX > Math.PI)
			endX -= 2 * Math.PI;
		double startY = (-Math.PI - mVCameraAngle) / 2;
		double endY = (-Math.PI + mVCameraAngle) / 2;

		boolean isXInView = false, isYInView = false;
		double diffX = -1, diffY = -1;
		if (startX > 0 && endX < 0) {
			if (startX < mAngle) {
				diffX = mAngle - startX;
				isXInView = true;
			} else if (mAngle < endX) {
				diffX = mHCameraAngle - endX + mAngle;
				isXInView = true;
			}
		} else if (mAngle > startX && mAngle < endX) {

			diffX = mAngle - startX;
			isXInView = true;

		}
		if(mValues[2] > startY && endY > mValues[2]) {
			diffY = endY - mValues[2];
			isYInView = true;
		}
		if (isXInView && isYInView) {
			double cx = diffX * w / mHCameraAngle;
			double cy = diffY * h / mVCameraAngle;
			//canvas.translate((float) cx, (float) cy);
			if (mValues != null) {
				if (mValues[2] < 0)
					canvas.rotate((float) Math.toDegrees(mValues[1]));
				else
					canvas.rotate((float) Math.toDegrees(Math.PI - mValues[1]));
			}
			//canvas.drawPath(mPath, mPaint);
			//canvas.drawText(title, (float) cx, (float) cy, mPaint);
			//canvas.drawRect(0, 0, 100, 100, strokePaint);

		    canvas.drawRect((float) cx, (float) cy - 100, length, 200, strokePaint);
		    canvas.drawText(title, (float) cx, (float) cy, textPaint);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		mSensorManager = (SensorManager) getContext().getSystemService(
				Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		mSensorManager.unregisterListener(this, mSensor);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			// convert the rotation-vector to a 4x4 matrix. the matrix
			// is interpreted by Open GL as the inverse of the
			// rotation-vector, which is what we want
			SensorManager.getRotationMatrixFromVector(mRotationMatrix,
					event.values);
			SensorManager.getOrientation(mRotationMatrix, mValues);

			mValues[0] += mDeclination + (Math.PI / 2);
			if (mValues[0] > Math.PI)
				mValues[0] -= 2 * Math.PI;
			else if (mValues[0] < -Math.PI)
				mValues[0] += 2 * Math.PI;
			invalidate();
		}
	}

}
