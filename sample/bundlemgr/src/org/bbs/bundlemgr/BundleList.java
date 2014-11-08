package org.bbs.bundlemgr;

import org.bbs.felix.activity.ActivityAgent;

import android.os.Bundle;
import android.widget.TextView;

public class BundleList extends ActivityAgent {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView t = new TextView(mActivity);
		t.setText("hello, bundle ");
		mActivity.setContentView(t);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
