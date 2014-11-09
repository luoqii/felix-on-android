package org.bbs.bundlemgr;

import org.bbs.felix.activity.BundleListActivity;
import org.bbs.felix.activity.SimpleActivityAgent;

import android.app.Activity;

public class SimpleBundleList extends 
SimpleActivityAgent
{

	private static final String TAG = SimpleBundleList.class.getSimpleName();

	public Activity getTargetActivity() {
		Activity activity = new BundleListActivity();
		return activity;
	}
	
}
