package org.bangbang.song.felix.activity;

import java.util.Dictionary;
import java.util.Enumeration;

import org.bangbang.song.felix.FelixWrapper;
import org.bangbang.song.felix.util.OsgiUtil;
import org.bangbang.song.felixonandroid.R;

import android.app.Activity;
import android.os.Bundle;
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
		
		((TextView)findViewById(R.id.detail)).setText(toString(b));
	}

	private CharSequence toString(org.osgi.framework.Bundle b) {
		String str = ""
				 + "id: " + b.getBundleId() + "\n"
				 + "version: " + b.getVersion() + "\n"
				 + "location: " + b.getLocation() + "\n"
				 + "state: " + OsgiUtil.bundleState2Str(b.getState()) + "\n"
				 + "headers: " + (b.getHeaders()) + "\n"
				;
		return str;
	}
	
	String toString(Dictionary<String, String> d) {
		String str = "";
		Enumeration<String> e = d.keys();
		while (e != null && e.hasMoreElements()) {
			str += e + ": " + d.get(e) + "\n";
			e.nextElement();
		}
		
		return str;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
