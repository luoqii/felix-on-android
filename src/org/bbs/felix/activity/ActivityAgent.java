package org.bbs.felix.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 *  keep method consistency with {@link Activity}
 * @author luoqii
 *
 */
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
