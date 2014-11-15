package org.bbs.felix.activity.bundlemanager;

import org.bbs.felixonandroid.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BundleDetailActivity extends FragmentActivity {
	public static final String EXTRA_BUNDLE_ID = "extra.bundle.id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bundle_detail);
	}



}
