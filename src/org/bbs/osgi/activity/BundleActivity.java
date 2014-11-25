package org.bbs.osgi.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.bbs.felix.FelixWrapper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * if android call us we call through to {@link #mActivityAgent};
 * otherwise call super or do ourself.
 * 
 * @author luoqii
 *
 * @see {@link ActivityAgent}
 */
public class BundleActivity extends 
//Activity 
FragmentActivity
{
	private static final String RES_PATH_APK_RES = "res.apk";

	private static final String TAG = BundleActivity.class.getSimpleName();
	
	/**
	 * type {@link String}
	 */
	public static final String EXTRA_SERVICE_NAME = ".extra_service_name";
	 /**
	  *  type {@link String}
	  */
	public static final String EXTRA_SERVICE_FILTER = ".extra_service_filter_name";
	public static final String DEFAULT_LAUNCHER_SERVICE_NAME = 
//			"org.bbs.bundlemgr.BundleList" 
//			"org.bbs.bundlemgr.SimpleBundleList"
			"com.example.android.apis.ApiDemos"
			;
	public static final String DEFAULT_LAUNCHER_SERVICE_FILTER = "";
	
	public static final String EXTRA_EMBEDED_ACTIVITY_CLASS_NAME = ".extra_embed_activity_class_name";
	
	ActivityAgent mActivityAgent;
	private String mServiceName;
	private String mServiceFilter;
	private Resources mSourceMerger;

	private LazyContext mLazyContext;
	
    @Override 
    protected void attachBaseContext(Context newBase) {
    	mLazyContext = new LazyContext(newBase);
        super.attachBaseContext(mLazyContext);
    }
	
	@Override
	public void setTheme(int resid) {
		super.setTheme(resid);
	}

	public Resources getResources() {
		// this will call before onCreate().
//		initActivityAgent();
//		return super.getResources();
		return mLazyContext.getResources();
//		return mSourceMerger == null ? super.getResources() : mSourceMerger;
	}

	// life-cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// call this as early as possible.
		initActivityAgent();
		if (null != mActivityAgent) {
			mActivityAgent.mHostActivity = this;
			mActivityAgent.onCreate(savedInstanceState);
		} else {
			TextView t = new TextView(this);
			t.setText("no service avaiable: \n" + "serviceName: " + mServiceName
						+ " serviceFilter: " + mServiceFilter);
			throw new IllegalArgumentException("no ActivityAgent avaiable.");
		}
	}

	private void initActivityAgent() {
		if (null == mActivityAgent) {
			mActivityAgent = getActivityAgent();
		}
	}
		@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mActivityAgent.onPostCreate(savedInstanceState);
	}
	@Override
	protected void onResume() {
		super.onResume();
		mActivityAgent.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		mActivityAgent.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mActivityAgent.onDestroy();
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mActivityAgent.onRestoreInstanceState(savedInstanceState);
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		mActivityAgent.onRestart();
	}

	// menu	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return mActivityAgent.onCreateOptionsMenu(menu);
	}
//	@Override
//	public boolean onPreparePanel(int arg0, View arg1, Menu arg2) {
//		return mActivityAgent.onPreparePanel(arg0, arg1, arg2);
//	}
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		return mActivityAgent.onPrepareOptionsMenu(menu);
//	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mActivityAgent.onOptionsItemSelected(item);
	}	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		mActivityAgent.onCreateContextMenu(menu, v, menuInfo);
//	}
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		return mActivityAgent.onContextItemSelected(item);
//	}
//	@Override
//	public void onContextMenuClosed(Menu menu) {
//		mActivityAgent.onContextMenuClosed(menu);
//	}

	// keyevent
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		if (mActivityAgent.dispatchKeyEvent(event)) {
//			return true;
//		}
//		return super.dispatchKeyEvent(event);
//	}
//	@Override
//	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
//		if (mActivityAgent.dispatchKeyEvent(event)) {
//			return true;
//		}
//		return super.dispatchKeyShortcutEvent(event);
//	}
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		if (mActivityAgent.dispatchTouchEvent(ev)) {
//			return true;
//		}
//		return super.dispatchTouchEvent(ev);
//	}
//	@Override
//	public boolean dispatchTrackballEvent(MotionEvent ev) {
//		if (mActivityAgent.dispatchTrackballEvent(ev)) {
//			return true;
//		}
//		return super.dispatchTrackballEvent(ev);
//	}
//	@Override
//	public boolean dispatchGenericMotionEvent(MotionEvent ev) {
//		if (mActivityAgent.dispatchGenericMotionEvent(ev)) {
//			return true;
//		}
//		return super.dispatchGenericMotionEvent(ev);
//	}

	// private method.
	private ActivityAgent getActivityAgent() {
		ActivityAgent agent = null;
		Intent intent = getIntent();
		mServiceName =  intent.getStringExtra(EXTRA_SERVICE_NAME);
		if (TextUtils.isEmpty(mServiceName)) {
			mServiceName = DEFAULT_LAUNCHER_SERVICE_NAME;
		}
		mServiceFilter =  intent.getStringExtra(EXTRA_SERVICE_FILTER);
		BundleContext bundleContext = FelixWrapper.getInstance(null).getFramework().getBundleContext();
		ServiceReference<?> s = null;
		if (TextUtils.isEmpty(mServiceFilter)) {
			s = bundleContext.getServiceReference(mServiceName);
		} else {
			try {
				s = bundleContext.getServiceReferences(mServiceName, mServiceFilter)[0];
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
		
		if (null != s) {
			// FIXME update theme.
			Resources bundleRes = getBundleResources(s.getBundle());
			if (bundleRes != null) {
				mSourceMerger = new ResourcesMerger(bundleRes, super.getResources());
				mLazyContext.bundleResReady(mSourceMerger);
			}
			
			agent = (ActivityAgent) bundleContext.getService(s);
		}
		
		return agent;
	}

	private Resources getBundleResources(org.osgi.framework.Bundle bundle) {
			File resApk = getFileStreamPath("id" + bundle.getBundleId() + "_v" + bundle.getVersion());
			
			//debug
			resApk.delete();
			
			if (!resApk.exists()) {
				URL url = bundle.getResource(RES_PATH_APK_RES);
				try {
					InputStream ins = url.openStream();
					OutputStream ous = new FileOutputStream(resApk);
					final int LEN = 8 * 1024;
					byte[] buff = new byte[LEN];
					int read = -1;
					while ((read = ins.read(buff)) != -1){
						ous.write(buff, 0, read);
	//					Log.d(TAG, "" + new String(buff, 0, read));
					}
					ins.close();
					ous.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return loadApkResource(resApk.getAbsolutePath());
		}

	private Resources loadApkResource(String apkFilePath) {
		AssetManager assets = null;
		try {
			assets = AssetManager.class.getConstructor(null).newInstance(null);
			Method method = assets.getClass().getMethod("addAssetPath", new Class[]{String.class});
			Object r = method.invoke(assets, apkFilePath);
			Log.d(TAG, "result: " + r);
			DisplayMetrics metrics = null;
			Configuration config = null;
			Resources res = new Resources(assets, metrics, config);
			return res;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
