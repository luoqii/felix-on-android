package org.bbs.felix.activity.bundlemanager;

import org.bbs.felix.FelixWrapper;
import org.bbs.felixonandroid.R;
import org.osgi.framework.ServiceReference;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DictionaryActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary);
		
		findViewById(R.id.check).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CharSequence word = ((TextView)findViewById(R.id.word)).getText();
				
				ServiceReference<?> s = FelixWrapper.getInstance(null).getFramework().getBundleContext().getServiceReference("tutorial.example2.service.DictionaryService");
				Object p = s.getProperty("dd");
			}
		});
	}

}
