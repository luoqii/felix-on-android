package org.bbs.felix.activity.bundlemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.bangbang.song.felixonandroid.R;
import org.bbs.felix.FelixWrapper;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class BundleListActivity extends FragmentActivity {
	private static final String TAG = BundleListActivity.class.getSimpleName();
	private static final int REAUEST_CODE_PICK_JAR = Activity.RESULT_FIRST_USER + 1;

	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_bundle_list);
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_bundle_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_install) {
			//			Intent intent = new Intent(Intent.ACTION_PICK);
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			//			intent.setDataAndType(data, type);
			intent.setType("*/*");
			startActivityForResult(intent, REAUEST_CODE_PICK_JAR);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult. requestCode: " + requestCode + " resultCode: " + resultCode + " data: " + data);
		String path = data.getData().toString();
		if (!TextUtils.isEmpty(path)) {
			try {
				if (path.startsWith("file:///")) {
					path = path.substring("file://".length());
				}
				Log.d(TAG, "path: " + path);
				
				FelixWrapper.getInstance(this).getFramework().getBundleContext().installBundle(path, new FileInputStream(new File(path)));
				updateUI();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateUI() {
		
	}

	public static class BundleList {
		private static BundleList sInstance;
		
		private org.osgi.framework.Bundle[] mBundles;
		
		public static BundleList getInstance(){
			if (sInstance == null){
				sInstance = new BundleList();
			}
			
			sInstance.syncWithOsgi();
			return sInstance;
		}
		
		private BundleList(){
		}
		
		public org.osgi.framework.Bundle[] getBundles(){
			return mBundles;
		}

		private void syncWithOsgi() {
			Framework f = FelixWrapper.getInstance(null).getFramework();
			mBundles = f.getBundleContext().getBundles();
		}
	}
}
