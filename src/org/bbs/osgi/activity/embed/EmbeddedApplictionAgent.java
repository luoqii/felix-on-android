package org.bbs.osgi.activity.embed;

import org.bbs.osgi.activity.ApplicationAgent;

import android.app.Application;

public class EmbeddedApplictionAgent extends ApplicationAgent {
	
	// TODO make it private
	public Application mBundelApp;

	public EmbeddedApplictionAgent(Application app){
		mBundelApp = app;
	}

	@Override
	public void onCreate() {
		mBundelApp.onCreate();
	}	
	
}
