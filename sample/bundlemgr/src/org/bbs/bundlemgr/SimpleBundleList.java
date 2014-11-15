package org.bbs.bundlemgr;

import org.bbs.bundlemgr.res.R;
import org.bbs.felix.activity.bundlemanager.BundleListActivity;
import org.bbs.osgi.activity.SimpleActivityAgent;

import android.app.Activity;
import android.util.Log;

public class SimpleBundleList extends 
SimpleActivityAgent
{

	private static final String TAG = SimpleBundleList.class.getSimpleName();

	public Activity getTargetActivity() {
		Activity activity = new BundleListActivity();
		
		String hellworld = getResources().getString(R.string.hello_world);
		Log.d(TAG, hellworld);
		return activity;
	}
	
}
