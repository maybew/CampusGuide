package com.campusguide.fragments;

import java.util.List;

import com.campusguide.R;
import com.campusguide.objects.BaseBuilding;
import com.campusguide.views.ARFrameView;
import com.campusguide.views.Preview;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ARFragment extends Fragment {

	Camera mCamera;
	int numberOfCameras;
	int cameraCurrentlyLocked;
	// The first rear facing camera
	int defaultCameraId;

	private Preview mPreview;
	private FrameLayout mContent;

	private String[] names;
	private double[] lats;
	private double[] lngs;

	public static ARFragment newInstance(List<BaseBuilding> buidlings) {
		ARFragment f = new ARFragment();
		Bundle args = new Bundle();
		int size = buidlings.size();
		String[] names = new String[size];
		double[] lats = new double[size];
		double[] lngs = new double[size];
		BaseBuilding temp;
		for (int i = 0; i < size; i++) {
			temp = buidlings.get(i);
			names[i] = temp.getName();
			lats[i] = temp.getLat();
			lngs[i] = temp.getLng();
		}
		args.putDoubleArray("lats", lats);
		args.putDoubleArray("lngs", lngs);
		args.putStringArray("names", names);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		names = getArguments() != null ? getArguments().getStringArray("names")
				: new String[0];
		lats = getArguments() != null ? getArguments().getDoubleArray("lats")
				: new double[0];
		lngs = getArguments() != null ? getArguments().getDoubleArray("lngs")
				: new double[0];
		
		// Find the total number of cameras available
		numberOfCameras = Camera.getNumberOfCameras();

		// Find the ID of the default camera
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				defaultCameraId = i;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		View view = inflater.inflate(R.layout.fragment_ar, container, false);
		mPreview = (Preview) view.findViewById(R.id.preview);
		mContent = (FrameLayout) view.findViewById(R.id.ar_content);
		for(int i=0;i<names.length;i++)
			mContent.addView(new ARFrameView(getActivity(), names[i], lats[i], lngs[i]));
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCamera = Camera.open();
		cameraCurrentlyLocked = defaultCameraId;
		mPreview.setCamera(mCamera);
		ARFrameView.setCameraConfig(mCamera.getParameters()
				.getHorizontalViewAngle(), mCamera.getParameters()
				.getVerticalViewAngle());
	}

}
