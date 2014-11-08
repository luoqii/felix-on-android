package org.bbs.felix.activity;

import org.bbs.felix.FelixWrapper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class BundleActivity extends Activity {
	/**
	 * type {@link String}
	 */
	public static final String EXTRA_SERVICE_NAME = ".extra_service_name";	/**
	 /**
	  *  type {@link String}
	  */
	public static final String EXTRA_SERVICE_FILTER = ".extra_service_filter_name";
	public static final String DEFAULT_LAUNCHER_SERVICE_NAME = "org.bbs.bundlemgr.BundleList";
	public static final String DEFAULT_LAUNCHER_SERVICE_FILTER = "";
	
	ActivityAgent mActivateAgent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// call this as early as possible.
		mActivateAgent = getActivator();
		mActivateAgent.mActivity = this;
		mActivateAgent.onCreate(savedInstanceState);
	}
	
	ActivityAgent getActivator(){
		ActivityAgent activator = null;
		Intent intent = getIntent();
		String serviceName =  intent.getStringExtra(EXTRA_SERVICE_NAME);
		if (TextUtils.isEmpty(serviceName)) {
			serviceName = DEFAULT_LAUNCHER_SERVICE_NAME;
		}
		String serviceFilter =  intent.getStringExtra(EXTRA_SERVICE_FILTER);
		BundleContext bundleContext = FelixWrapper.getInstance(null).getFramework().getBundleContext();
		if (TextUtils.isEmpty(serviceFilter)) {
			ServiceReference<?> s = bundleContext.getServiceReference(serviceName);
			activator = (ActivityAgent) bundleContext.getService(s);
/*		} else {
			activator = (ActivityActivator) bundleContext.getServiceReferences(serviceName, serviceFilter);*/
		}
		
		return activator;
	}
	
		
	@Override
	protected void onResume() {
		super.onResume();
		mActivateAgent.onResume();
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mActivateAgent.onPause();
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mActivateAgent.onDestroy();
	}


}
