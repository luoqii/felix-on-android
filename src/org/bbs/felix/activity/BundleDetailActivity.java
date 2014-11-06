package org.bbs.felix.activity;

import org.bangbang.song.felixonandroid.R;
import org.bbs.felix.FelixWrapper;
import org.bbs.felix.util.OsgiUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class BundleDetailActivity extends Activity {
	public static final String EXTRA_BUNDLE_ID = "extra.bundle.id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bundle_detail);
		
		long bId = getIntent().getLongExtra(EXTRA_BUNDLE_ID, -1);
		org.osgi.framework.Bundle b = FelixWrapper.getInstance(this).getFramework()
				.getBundleContext().getBundle(bId);
		
		TextView text = ((TextView)findViewById(R.id.detail));
		text.setText(toString(b));
		text.setMovementMethod(new  ScrollingMovementMethod());
	}

	private CharSequence toString(org.osgi.framework.Bundle b) {
		String str = ""
				 + "id: " + b.getBundleId() + "\n"
				 + "version: " + b.getVersion() + "\n"
				 + "location: " + b.getLocation() + "\n"
				 + "state: " + OsgiUtil.bundleState2Str(b.getState()) + "\n"
				 + "headers: \n" + getHeader(b) + "\n"
				;
		return str;
	}
	
	String getHeader(org.osgi.framework.Bundle b) {
		return OsgiUtil.getHeader(b, OsgiUtil.HEADER_OSGI);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
