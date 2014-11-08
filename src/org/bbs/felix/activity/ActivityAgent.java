package org.bbs.felix.activity;

import android.app.Activity;
import android.os.Bundle;

public class ActivityAgent{
	
	protected Activity mActivity;

	protected void onCreate(Bundle savedInstanceState) {
	}
	
	protected void onResume() {
	}

	protected void onPause() {
	}

	protected void onDestroy() {
		mActivity = null;
	}
	
}
