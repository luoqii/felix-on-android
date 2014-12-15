package org.bbs.osgi.activity;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 *  define method consistency with {@link Application}.
 *  <p>
 *  
 *  <p>
 * when add new function, keep it in section, in order.
 * @author luoqii
 *
 * @see {@link BundleActivity}
 */
public class ApplicationAgent 
extends Application
{

	Application mHostApplicion;
	
	public void onTerminate() {
	}

	public void onConfigurationChanged(Configuration newConfig) {
	}

	public void onLowMemory() {
	}

	public void onTrimMemory(int level) {
	}
	
	

	// life-cycle
	public void onCreate() {
	}

	@Override
	public Resources getResources() {
		return mHostApplicion.getResources();
	}
		
}
