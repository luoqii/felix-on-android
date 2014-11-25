package com.example.android.apis;

import org.bbs.osgi.activity.EmbeddedActivityAgent;
import org.bbs.osgi.activity.SimpleActivityAgent;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import android.app.Activity;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
//		context.registerService(ApiDemos.class.getName(), new Api(), null);
//		context.registerService(SimpleBundleList.class.getName(), new SimpleBundleList(), null);
//		registerActivity(context, ApiDemos.class.getName());
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
	}
	
	public static void registerActivity(BundleContext context, String ActivityClassName){
		context.registerService(ActivityClassName, new SimpleActivityAgent(ActivityClassName), null);
	}
	
	public static class Api extends EmbeddedActivityAgent {

		
		
		@Override
		public Activity getTargetActivity() {
			// TODO Auto-generated method stub
			return new ApiDemos();
		}
		
	}

}
