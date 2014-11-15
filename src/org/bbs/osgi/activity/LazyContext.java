package org.bbs.osgi.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

class LazyContext extends ContextWrapper {

	private Resources mResource;

	public LazyContext(Context base) {
		super(base);
	}
	
	public void bundleResReady(Resources res){
		mResource = res;
	}

	@Override
	public Resources getResources() {
		if (null == mResource) {
			return super.getResources();
		} else {
			return mResource;
		}
	}
}