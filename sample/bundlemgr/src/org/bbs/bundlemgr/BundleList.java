package org.bbs.bundlemgr;

import org.bbs.bundlemgr.res.R;
import org.bbs.osgi.activity.ActivityAgent;

import android.os.Bundle;
import android.widget.TextView;

public class BundleList extends ActivityAgent {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		TextView t = new TextView(mHostActivity.getApplicationContext());
		String hellworld = getResources().getString(R.string.hello_world);
		t.setText(hellworld);
		setContentView(t);
		
		setTitle("imp by osgi bundle");
	}
}
