package org.bangbang.song.felix;

import android.app.Application;

public class App extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		initFelix();
	}

	private void initFelix() {
		FelixWrapper.getInstance(this);
	}
}
