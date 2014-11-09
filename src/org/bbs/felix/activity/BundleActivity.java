package org.bbs.felix.activity;

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
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * if android call us call through to {@link #mActivateAgent};
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
	private static final String RES_PATH_APK_RES = "META-INF/MANIFEST.MF";

	private static final String TAG = BundleActivity.class.getSimpleName();
	
	/**
	 * type {@link String}
	 */
	public static final String EXTRA_SERVICE_NAME = ".extra_service_name";	/**
	 /**
	  *  type {@link String}
	  */
	public static final String EXTRA_SERVICE_FILTER = ".extra_service_filter_name";
	public static final String DEFAULT_LAUNCHER_SERVICE_NAME = 
//			"org.bbs.bundlemgr.BundleList" 
			"org.bbs.bundlemgr.SimpleBundleList"
			;
	public static final String DEFAULT_LAUNCHER_SERVICE_FILTER = "";
	
	ActivityAgent mActivateAgent;
	private String mServiceName;
	private String mServiceFilter;
	private Resources mSourceMerger;
	
	public Resources getResources() {
		// this will call before onCreate().
		return mSourceMerger == null ? super.getResources() : mSourceMerger;
	}

	// life-cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// call this as early as possible.
		mActivateAgent = getActivator();
		if (null != mActivateAgent) {
			mActivateAgent.mHostActivity = this;
			mActivateAgent.onCreate(savedInstanceState);
		} else {
			TextView t = new TextView(this);
			t.setText("no service avaiable: \n" + "serviceName: " + mServiceName
						+ " serviceFilter: " + mServiceFilter);
		}
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
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mActivateAgent.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		mActivateAgent.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		mActivateAgent.onRestart();
	}

	// menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return mActivateAgent.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return mActivateAgent.onOptionsItemSelected(item);
	}
	
	// keyevent
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mActivateAgent.dispatchKeyEvent(event)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
		if (mActivateAgent.dispatchKeyEvent(event)) {
			return true;
		}
		return super.dispatchKeyShortcutEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mActivateAgent.dispatchTouchEvent(ev)) {
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean dispatchTrackballEvent(MotionEvent ev) {
		if (mActivateAgent.dispatchTrackballEvent(ev)) {
			return true;
		}
		return super.dispatchTrackballEvent(ev);
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent ev) {
		if (mActivateAgent.dispatchGenericMotionEvent(ev)) {
			return true;
		}
		return super.dispatchGenericMotionEvent(ev);
	}

	// private method.
	private ActivityAgent getActivator() {
		ActivityAgent activator = null;
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
			
			mSourceMerger = new ResourcesMerger(super.getResources(), bundleRes);
			activator = (ActivityAgent) bundleContext.getService(s);
		}
		
		return activator;
	}

	private Resources getBundleResources(org.osgi.framework.Bundle bundle) {
			File resApk = getFileStreamPath("id" + bundle.getBundleId() + "_v" + bundle.getVersion());
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




	public static class ResourcesMerger 
	extends Resources 
	{
		private Resources mFirst;
		private Resources mSecond;

		public ResourcesMerger(Resources first, Resources second) {
			// we do NOT need implements a new Resourcs, just need it's interface
			super(first.getAssets(), first.getDisplayMetrics(), first.getConfiguration());
			
			mFirst = first;
			mSecond = second;
		}

		public CharSequence getText(int id) throws NotFoundException {
			try {
				return mFirst.getText(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			
			return mSecond.getText(id);
		}


		public CharSequence getQuantityText(int id, int quantity)
				throws NotFoundException {
			try {
				return mFirst.getQuantityText(id, quantity);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getQuantityText(id, quantity);
		}


		public String getString(int id) throws NotFoundException {
			try {
				return mFirst.getString(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getString(id);
		}


		public String getString(int id, Object... formatArgs)
				throws NotFoundException {
			try {
				return mFirst.getString(id, formatArgs);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getString(id, formatArgs);
		}


		public String getQuantityString(int id, int quantity,
				Object... formatArgs) throws NotFoundException {
			try {
				return mFirst.getQuantityString(id, quantity, formatArgs);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getQuantityString(id, quantity, formatArgs);
		}


		public String getQuantityString(int id, int quantity)
				throws NotFoundException {
			
			try {
				return mFirst.getQuantityString(id, quantity);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getQuantityString(id, quantity);
		}


		public CharSequence getText(int id, CharSequence def) {
			
			try {
				return mFirst.getText(id, def);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getText(id, def);
		}


		public CharSequence[] getTextArray(int id) throws NotFoundException {
			
			try {
				return mFirst.getTextArray(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getTextArray(id);
		}


		public String[] getStringArray(int id) throws NotFoundException {
			
			try {
				return mFirst.getStringArray(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getStringArray(id);
		}


		public int[] getIntArray(int id) throws NotFoundException {
			
			try {
				return mFirst.getIntArray(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getIntArray(id);
		}


		public TypedArray obtainTypedArray(int id) throws NotFoundException {
			
			try {
				return mFirst.obtainTypedArray(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.obtainTypedArray(id);
		}


		public float getDimension(int id) throws NotFoundException {
			
			try {
				return mFirst.getDimension(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getDimension(id);
		}


		public int getDimensionPixelOffset(int id) throws NotFoundException {
			
			try {
				return mFirst.getDimensionPixelOffset(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getDimensionPixelOffset(id);
		}


		public int getDimensionPixelSize(int id) throws NotFoundException {
			
			try {
				return mFirst.getDimensionPixelSize(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getDimensionPixelSize(id);
		}


		public float getFraction(int id, int base, int pbase) {
			
			try {
				return mFirst.getFraction(id, base, pbase);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getFraction(id, base, pbase);
		}


		public Drawable getDrawable(int id) throws NotFoundException {
			
			try {
				return mFirst.getDrawable(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getDrawable(id);
		}


		public Drawable getDrawableForDensity(int id, int density)
				throws NotFoundException {
			
			try {
				return mFirst.getDrawableForDensity(id, density);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getDrawableForDensity(id, density);
		}


		public Movie getMovie(int id) throws NotFoundException {
			
			try {
				return mFirst.getMovie(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getMovie(id);
		}


		public int getColor(int id) throws NotFoundException {
			
			try {
				return mFirst.getColor(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getColor(id);
		}


		public ColorStateList getColorStateList(int id)
				throws NotFoundException {
			
			try {
				return mFirst.getColorStateList(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getColorStateList(id);
		}


		public boolean getBoolean(int id) throws NotFoundException {
			
			try {
				return mFirst.getBoolean(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getBoolean(id);
		}


		public int getInteger(int id) throws NotFoundException {
			
			try {
				return mFirst.getInteger(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getInteger(id);
		}


		public XmlResourceParser getLayout(int id) throws NotFoundException {
			
			try {
				return mFirst.getLayout(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getLayout(id);
		}


		public XmlResourceParser getAnimation(int id) throws NotFoundException {
			
			try {
				return mFirst.getAnimation(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getAnimation(id);
		}


		public XmlResourceParser getXml(int id) throws NotFoundException {
			
			try {
				return mFirst.getXml(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getXml(id);
		}


		public InputStream openRawResource(int id) throws NotFoundException {
			
			try {
				return mFirst.openRawResource(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.openRawResource(id);
		}


		public InputStream openRawResource(int id, TypedValue value)
				throws NotFoundException {
			
			try {
				return mFirst.openRawResource(id, value);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.openRawResource(id, value);
		}


		public AssetFileDescriptor openRawResourceFd(int id)
				throws NotFoundException {
			
			try {
				return mFirst.openRawResourceFd(id);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.openRawResourceFd(id);
		}


		public void getValue(int id, TypedValue outValue, boolean resolveRefs)
				throws NotFoundException {
			
			try {
				 mFirst.getValue(id, outValue, resolveRefs);
				 return;
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			mSecond.getValue(id, outValue, resolveRefs);
		}


		public void getValueForDensity(int id, int density,
				TypedValue outValue, boolean resolveRefs)
				throws NotFoundException {
			
			try {
				 mFirst.getValueForDensity(id, density, outValue, resolveRefs);
				 return;
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			mSecond.getValueForDensity(id, density, outValue, resolveRefs);
		}


		public void getValue(String name, TypedValue outValue,
				boolean resolveRefs) throws NotFoundException {
			
			try {
				 mFirst.getValue(name, outValue, resolveRefs);
				 return;
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			mSecond.getValue(name, outValue, resolveRefs);
		}


		public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
			
			try {
				return mFirst.obtainAttributes(set, attrs);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.obtainAttributes(set, attrs);
		}


		public void updateConfiguration(Configuration config,
				DisplayMetrics metrics) {
			
			try {
				// this will be called in Constructor,so .
				 if (null != mFirst) {
					 mFirst.updateConfiguration(config, metrics);
				 }
				 return;
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			// this will be called in Constructor,so .
			if (null != mSecond) {
				mSecond.updateConfiguration(config, metrics);
			}
		}


		public DisplayMetrics getDisplayMetrics() {
			
			try {
				return mFirst.getDisplayMetrics();
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getDisplayMetrics();
		}


		public Configuration getConfiguration() {
			
			try {
				return mFirst.getConfiguration();
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getConfiguration();
		}


		public int getIdentifier(String name, String defType, String defPackage) {
			
			try {
				return mFirst.getIdentifier(name, defType, defPackage);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getIdentifier(name, defType, defPackage);
		}


		public String getResourceName(int resid) throws NotFoundException {
			
			try {
				return mFirst.getResourceName(resid);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getResourceName(resid);
		}


		public String getResourcePackageName(int resid)
				throws NotFoundException {
			
			try {
				return mFirst.getResourcePackageName(resid);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getResourcePackageName(resid);
		}


		public String getResourceTypeName(int resid) throws NotFoundException {
			
			try {
				return mFirst.getResourceTypeName(resid);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getResourceTypeName(resid);
		}


		public String getResourceEntryName(int resid) throws NotFoundException {
			
			try {
				return mFirst.getResourceEntryName(resid);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			return mSecond.getResourceEntryName(resid);
		}


		public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle)
				throws XmlPullParserException, IOException {
			
			try {
				mFirst.parseBundleExtras(parser, outBundle);
				return;
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSecond.parseBundleExtras(parser, outBundle);
		}


		public void parseBundleExtra(String tagName, AttributeSet attrs,
				Bundle outBundle) throws XmlPullParserException {
			
			try {
				mFirst.parseBundleExtra(tagName, attrs, outBundle);
				return;
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			mSecond.parseBundleExtra(tagName, attrs, outBundle);
		}
		
		
	}
}
