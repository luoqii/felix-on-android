package org.bbs.felix.activity;

import org.bangbang.song.felixonandroid.R;
import org.bbs.felix.FelixWrapper;
import org.bbs.felix.util.OsgiUtil;
import org.osgi.framework.BundleException;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class BundleDetailActivity extends Activity {
	public static final String EXTRA_BUNDLE_ID = "extra.bundle.id";
	private org.osgi.framework.Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bundle_detail);
		
		long bId = getIntent().getLongExtra(EXTRA_BUNDLE_ID, -1);
		mBundle = FelixWrapper.getInstance(this).getFramework()
				.getBundleContext().getBundle(bId);
		
		updateUI();
	}

	private void updateUI() {
		TextView text = ((TextView)findViewById(R.id.detail));
		text.setText(toString(mBundle));
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
		getMenuInflater().inflate(R.menu.menu_bundle_detail, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case R.id.action_start:
				mBundle.start();
				break;
			case R.id.action_stop:
				mBundle.stop();
				break;
			case R.id.action_uninstall:
				mBundle.uninstall();
				break;

			default:
				break;
			}
			
			updateUI();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.onOptionsItemSelected(item);
	}

}
