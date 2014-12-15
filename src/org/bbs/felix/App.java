package org.bbs.felix;

import org.bbs.osgi.activity.BundleApplication;

public class App extends BundleApplication {
	@Override
	public void onCreate() {
		super.onCreate();
		initFelix();
	}

	private void initFelix() {
		FelixWrapper.getInstance(this);
		
		// simulate init osgi is time-consuming
		try {
			Thread.sleep(0 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
