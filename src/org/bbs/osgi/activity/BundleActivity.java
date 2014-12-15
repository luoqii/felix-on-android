package org.bbs.osgi.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bbs.felix.App;
import org.bbs.felix.FelixWrapper;
import org.bbs.osgi.activity.embed.EmbeddedActivityAgent;
import org.bbs.osgi.activity.embed.EmbeddedApplictionAgent;
import org.bbs.osgi.activity.embed.EmbeddedBundleActivity;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * if android call us we call through to {@link #mActivityAgent};
 * otherwise call super or do ourself.
 * 
 * <p>
 * when add new function, keep it in section, in order.
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
//			"com.example.android.apis.ApiDemos"
//			"com.example.android.apis.Activator$EmbeddedApiDemos"
			"com.youku.tv.osgi.Activator$EmbeddedApiDemos"
			
			;
	public static final String DEFAULT_LAUNCHER_SERVICE_FILTER = "";
	
	public static final String EXTRA_EMBEDED_ACTIVITY_CLASS_NAME = ".extra_embed_activity_class_name";

	private static final boolean FORCE_CLOSE = true;

	private static final boolean DEBUG_CREATE_VIEW = false;
	
	ActivityAgent mActivityAgent;
	private String mServiceName;
	private String mServiceFilter;
	private Resources mSourceMerger;

	private LazyContext mLazyContext;
	
	private List<String> mExtendWidgetReg;

	private org.osgi.framework.Bundle mBundle;
	private static Map<WeakReference<org.osgi.framework.Bundle>, WeakReference<Resources>> sBundle2ResMap;
	private static Map<WeakReference<org.osgi.framework.Bundle>, WeakReference<Application>> sBundle2AppMap;
	
	static {
		sBundle2ResMap = new HashMap<WeakReference<org.osgi.framework.Bundle>, WeakReference<Resources>>();
		sBundle2AppMap = new HashMap<WeakReference<org.osgi.framework.Bundle>, WeakReference<Application>>();
	}

	// XXX why we need this???
	protected List<String> getExtendWidgetPackageReg() {
		List<String> w = new ArrayList<String>();
		w.add("com.youku.lib.widget.*");
		w.add("com.youku.tv.widget.*");
		w.add("com.youku.tv.ui.*");
		w.add("com.baseproject.volley.toolbox.*");
		w.add("com.youku.lib.focuslayer.*");
		return w;
	}
	
    @Override 
    protected void attachBaseContext(Context newBase) {
    	mLazyContext = new LazyContext(newBase);
        super.attachBaseContext(mLazyContext);
    }
	
	@Override
	public void setTheme(int resid) {
		super.setTheme(resid);
	}
	
	@Override
	public Theme getTheme() {
		return super.getTheme();
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
		injectInstrumentation();
		initActivityAgent();
		
		if (null != mActivityAgent) {
			mActivityAgent.mHostActivity = this;
			mActivityAgent.onCreate(savedInstanceState);
		} else {
			TextView t = new TextView(this);
			t.setText("no service avaiable: \n" + "serviceName: " + mServiceName
						+ " serviceFilter: " + mServiceFilter);
			setContentView(t);
			if (FORCE_CLOSE) {
				throw new IllegalArgumentException("no ActivityAgent avaiable.");
			}
		}
	}
	private void injectInstrumentation() {
		Instrumentation i = (Instrumentation) EmbeddedActivityAgent.ActivityReflectUtil.getFiledValue(this, "mInstrumentation");
		Field f = EmbeddedActivityAgent.ActivityReflectUtil.getFiled(this, "mInstrumentation");
		EmbeddedActivityAgent.ActivityReflectUtil.setField(this, f, new InstrumentationWrapper(i));
	}

	private void initActivityAgent() {
		if (null == mActivityAgent) {
			mActivityAgent = prepareActivityAgent();
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
	
	// start activity
    @Override
	public void startActivity(Intent intent) {
//    	ComponentName com = intent.getComponent();
//		String c = com.getClassName();
//		if (!TextUtils.isEmpty(c)) {
//			intent.setComponent(new ComponentName(com.getPackageName(), BundleActivity.class.getCanonicalName()));
//			intent.putExtra(EXTRA_SERVICE_NAME, c);
//		}
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent,
			int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityFromFragment(fragment, intent, requestCode);
	}

	@SuppressLint("NewApi")
	@Override
	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode, options);
	}

	@SuppressLint("NewApi")
	@Override
	public void startActivity(Intent intent, Bundle options) {
		// TODO Auto-generated method stub
		super.startActivity(intent, options);
	}

	@SuppressLint("NewApi")
	@Override
	public void startActivities(Intent[] intents) {
		// TODO Auto-generated method stub
		super.startActivities(intents);
	}

	@SuppressLint("NewApi")
	@Override
	public void startActivities(Intent[] intents, Bundle options) {
		// TODO Auto-generated method stub
		super.startActivities(intents, options);
	}

	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		return super.startActivityIfNeeded(intent, requestCode);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode,
			Bundle options) {
		// TODO Auto-generated method stub
		return super.startActivityIfNeeded(intent, requestCode, options);
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityFromChild(child, intent, requestCode);
	}

	@SuppressLint("NewApi")
	@Override
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode, Bundle options) {
		// TODO Auto-generated method stub
		super.startActivityFromChild(child, intent, requestCode, options);
	}

	@SuppressLint("NewApi")
	@Override
	public void startActivityFromFragment(android.app.Fragment fragment,
			Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityFromFragment(fragment, intent, requestCode);
	}

	@SuppressLint("NewApi")
	@Override
	public void startActivityFromFragment(android.app.Fragment fragment,
			Intent intent, int requestCode, Bundle options) {
		// TODO Auto-generated method stub
		super.startActivityFromFragment(fragment, intent, requestCode, options);
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
	private ActivityAgent prepareActivityAgent() {
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
			mBundle = s.getBundle();
			
			// FIXME update theme.
			WeakReference<Resources> r = sBundle2ResMap.get(mBundle);
			if (null != r) {
				mSourceMerger = r.get();
				mLazyContext.bundleResReady(mSourceMerger);
			} 
			if (null == mSourceMerger) {
				Resources bundleRes = getBundleResources(s.getBundle());
				if (bundleRes != null) {
					mSourceMerger = new ResourcesMerger(bundleRes, super.getResources());
					mLazyContext.bundleResReady(mSourceMerger);
					
					sBundle2ResMap.put(new WeakReference(mBundle), new WeakReference(mSourceMerger));
				}
			}
			
			agent = (ActivityAgent) bundleContext.getService(s);
		}
		
		if (null != agent) {
			WeakReference<Application> r = sBundle2AppMap.get(mBundle);
			if (null == r) {
				Application app = agent.getBundleApplication();
				if (null != app) {
					((App)getApplication()).attachBundleAplication(new EmbeddedApplictionAgent(app), mSourceMerger, mLazyContext);
					sBundle2AppMap.put(new WeakReference(mBundle), new WeakReference(app));
				}
			}
		}
		
		return agent;
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		
		if (DEBUG_CREATE_VIEW) {
			////////////1234567890123456789
			Log.d(TAG, "onCreateView. name: " + name);
		}
		return super.onCreateView(name, context, attrs);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(View parent, String name, Context context,
			AttributeSet attrs) {
		if (mExtendWidgetReg == null){
			mExtendWidgetReg = getExtendWidgetPackageReg();
		}
		
		if (DEBUG_CREATE_VIEW) {
			////////////1234567890123456789
			Log.d(TAG, "onCreateView. name: " + name + " parent: " + parent);
		}
		
		if (null != mExtendWidgetReg) {
			for (String r : mExtendWidgetReg) {
				if (name.matches(r)){
					try {
						if (DEBUG_CREATE_VIEW) {
							////////////1234567890123456789012345
							Log.d(TAG, "try to load  class. name: " + name);
						}
						
						Class clazz = mBundle.loadClass(name);
						Constructor c = clazz.getDeclaredConstructor(new Class[]{Context.class, AttributeSet.class});
						
						if (DEBUG_CREATE_VIEW) {
							////////////1234567890123456789012345
							Log.d(TAG, "load class success. name: " + name);
						}
						
						return (View) c.newInstance(new Object[]{context, attrs});
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return super.onCreateView(parent, name, context, attrs);
	}

	private Resources getBundleResources(org.osgi.framework.Bundle bundle) {
			File resApk = getFileStreamPath("id" + bundle.getBundleId() + "_v" + bundle.getVersion());

			//debug
			resApk.delete();
			
			if (!resApk.exists()) {
				URL url = bundle.getResource(".");
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
	
	public static class InstrumentationWrapper extends Instrumentation {
		private Instrumentation mBase;

		public InstrumentationWrapper(Instrumentation base){
			mBase = base;
		}
		
		public ActivityResult execStartActivity(
	            Context who, IBinder contextThread, IBinder token, Activity target,
	            Intent intent, int requestCode, Bundle options) {
	    	processIntent(intent);
	    	
			return ReflectUtil.execStartActivity(mBase, who, contextThread, token, target,
					intent, requestCode, options);
		}
//	    public void execStartActivities(Context who, IBinder contextThread,
//	            IBinder token, Activity target, Intent[] intents, Bundle options) {
//	        execStartActivitiesAsUser(who, contextThread, token, target, intents, options,
//	                UserHandle.myUserId());
//	    }
//	    public void execStartActivitiesAsUser(Context who, IBinder contextThread,
//	            IBinder token, Activity target, Intent[] intents, Bundle options,
//	            int userId) {
//	    }
//	    public ActivityResult execStartActivity(
//	            Context who, IBinder contextThread, IBinder token, Fragment target,
//	            Intent intent, int requestCode, Bundle options) {
//	    	
//	    }

		private void processIntent(Intent intent) {
			ComponentName com = intent.getComponent();
			String c = com.getClassName();
			c = "com.youku.tv.osgi.Activator$Home";
			if (!TextUtils.isEmpty(c)) {
				intent.setComponent(new ComponentName(com.getPackageName(), EmbeddedBundleActivity.class.getCanonicalName()));
				intent.putExtra(EXTRA_SERVICE_NAME, c);
			}
		}

		
		// call base method instead.
		@Override
		public void onCreate(Bundle arguments) {
			// TODO Auto-generated method stub
			mBase.onCreate(arguments);
		}

		@Override
		public void start() {
			// TODO Auto-generated method stub
			mBase.start();
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			mBase.onStart();
		}

		@Override
		public boolean onException(Object obj, Throwable e) {
			// TODO Auto-generated method stub
			return mBase.onException(obj, e);
		}

		@Override
		public void sendStatus(int resultCode, Bundle results) {
			// TODO Auto-generated method stub
			mBase.sendStatus(resultCode, results);
		}

		@Override
		public void finish(int resultCode, Bundle results) {
			// TODO Auto-generated method stub
			mBase.finish(resultCode, results);
		}

		@Override
		public void setAutomaticPerformanceSnapshots() {
			// TODO Auto-generated method stub
			mBase.setAutomaticPerformanceSnapshots();
		}

		@Override
		public void startPerformanceSnapshot() {
			// TODO Auto-generated method stub
			mBase.startPerformanceSnapshot();
		}

		@Override
		public void endPerformanceSnapshot() {
			// TODO Auto-generated method stub
			mBase.endPerformanceSnapshot();
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			mBase.onDestroy();
		}

		@Override
		public Context getContext() {
			// TODO Auto-generated method stub
			return mBase.getContext();
		}

		@Override
		public ComponentName getComponentName() {
			// TODO Auto-generated method stub
			return mBase.getComponentName();
		}

		@Override
		public Context getTargetContext() {
			// TODO Auto-generated method stub
			return mBase.getTargetContext();
		}

		@Override
		public boolean isProfiling() {
			// TODO Auto-generated method stub
			return mBase.isProfiling();
		}

		@Override
		public void startProfiling() {
			// TODO Auto-generated method stub
			mBase.startProfiling();
		}

		@Override
		public void stopProfiling() {
			// TODO Auto-generated method stub
			mBase.stopProfiling();
		}

		@Override
		public void setInTouchMode(boolean inTouch) {
			// TODO Auto-generated method stub
			mBase.setInTouchMode(inTouch);
		}

		@Override
		public void waitForIdle(Runnable recipient) {
			// TODO Auto-generated method stub
			mBase.waitForIdle(recipient);
		}

		@Override
		public void waitForIdleSync() {
			// TODO Auto-generated method stub
			mBase.waitForIdleSync();
		}

		@Override
		public void runOnMainSync(Runnable runner) {
			// TODO Auto-generated method stub
			mBase.runOnMainSync(runner);
		}

		@Override
		public Activity startActivitySync(Intent intent) {
			// TODO Auto-generated method stub
			return mBase.startActivitySync(intent);
		}

		@Override
		public void addMonitor(ActivityMonitor monitor) {
			// TODO Auto-generated method stub
			mBase.addMonitor(monitor);
		}

		@Override
		public ActivityMonitor addMonitor(IntentFilter filter,
				ActivityResult result, boolean block) {
			// TODO Auto-generated method stub
			return mBase.addMonitor(filter, result, block);
		}

		@Override
		public ActivityMonitor addMonitor(String cls, ActivityResult result,
				boolean block) {
			// TODO Auto-generated method stub
			return mBase.addMonitor(cls, result, block);
		}

		@Override
		public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
			// TODO Auto-generated method stub
			return mBase.checkMonitorHit(monitor, minHits);
		}

		@Override
		public Activity waitForMonitor(ActivityMonitor monitor) {
			// TODO Auto-generated method stub
			return mBase.waitForMonitor(monitor);
		}

		@Override
		public Activity waitForMonitorWithTimeout(ActivityMonitor monitor,
				long timeOut) {
			// TODO Auto-generated method stub
			return mBase.waitForMonitorWithTimeout(monitor, timeOut);
		}

		@Override
		public void removeMonitor(ActivityMonitor monitor) {
			// TODO Auto-generated method stub
			mBase.removeMonitor(monitor);
		}

		@Override
		public boolean invokeMenuActionSync(Activity targetActivity, int id,
				int flag) {
			// TODO Auto-generated method stub
			return mBase.invokeMenuActionSync(targetActivity, id, flag);
		}

		@Override
		public boolean invokeContextMenuAction(Activity targetActivity, int id,
				int flag) {
			// TODO Auto-generated method stub
			return mBase.invokeContextMenuAction(targetActivity, id, flag);
		}

		@Override
		public void sendStringSync(String text) {
			// TODO Auto-generated method stub
			mBase.sendStringSync(text);
		}

		@Override
		public void sendKeySync(KeyEvent event) {
			// TODO Auto-generated method stub
			mBase.sendKeySync(event);
		}

		@Override
		public void sendKeyDownUpSync(int key) {
			// TODO Auto-generated method stub
			mBase.sendKeyDownUpSync(key);
		}

		@Override
		public void sendCharacterSync(int keyCode) {
			// TODO Auto-generated method stub
			mBase.sendCharacterSync(keyCode);
		}

		@Override
		public void sendPointerSync(MotionEvent event) {
			// TODO Auto-generated method stub
			mBase.sendPointerSync(event);
		}

		@Override
		public void sendTrackballEventSync(MotionEvent event) {
			// TODO Auto-generated method stub
			mBase.sendTrackballEventSync(event);
		}

		@Override
		public Application newApplication(ClassLoader cl, String className,
				Context context) throws InstantiationException,
				IllegalAccessException, ClassNotFoundException {
			// TODO Auto-generated method stub
			return mBase.newApplication(cl, className, context);
		}

		@Override
		public void callApplicationOnCreate(Application app) {
			// TODO Auto-generated method stub
			mBase.callApplicationOnCreate(app);
		}

		@Override
		public Activity newActivity(Class<?> clazz, Context context, IBinder token,
				Application application, Intent intent, ActivityInfo info,
				CharSequence title, Activity parent, String id,
				Object lastNonConfigurationInstance) throws InstantiationException,
				IllegalAccessException {
			// TODO Auto-generated method stub
			return mBase.newActivity(clazz, context, token, application, intent, info,
					title, parent, id, lastNonConfigurationInstance);
		}

		@Override
		public Activity newActivity(ClassLoader cl, String className, Intent intent)
				throws InstantiationException, IllegalAccessException,
				ClassNotFoundException {
			// TODO Auto-generated method stub
			return mBase.newActivity(cl, className, intent);
		}

		@Override
		public void callActivityOnCreate(Activity activity, Bundle icicle) {
			// TODO Auto-generated method stub
			mBase.callActivityOnCreate(activity, icicle);
		}

		@Override
		public void callActivityOnCreate(Activity activity, Bundle icicle,
				PersistableBundle persistentState) {
			// TODO Auto-generated method stub
			mBase.callActivityOnCreate(activity, icicle, persistentState);
		}

		@Override
		public void callActivityOnDestroy(Activity activity) {
			// TODO Auto-generated method stub
			mBase.callActivityOnDestroy(activity);
		}

		@Override
		public void callActivityOnRestoreInstanceState(Activity activity,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState);
		}

		@Override
		public void callActivityOnRestoreInstanceState(Activity activity,
				Bundle savedInstanceState, PersistableBundle persistentState) {
			// TODO Auto-generated method stub
			mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState,
					persistentState);
		}

		@Override
		public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
			// TODO Auto-generated method stub
			mBase.callActivityOnPostCreate(activity, icicle);
		}

		@Override
		public void callActivityOnPostCreate(Activity activity, Bundle icicle,
				PersistableBundle persistentState) {
			// TODO Auto-generated method stub
			mBase.callActivityOnPostCreate(activity, icicle, persistentState);
		}

		@Override
		public void callActivityOnNewIntent(Activity activity, Intent intent) {
			// TODO Auto-generated method stub
			mBase.callActivityOnNewIntent(activity, intent);
		}

		@Override
		public void callActivityOnStart(Activity activity) {
			// TODO Auto-generated method stub
			mBase.callActivityOnStart(activity);
		}

		@Override
		public void callActivityOnRestart(Activity activity) {
			// TODO Auto-generated method stub
			mBase.callActivityOnRestart(activity);
		}

		@Override
		public void callActivityOnResume(Activity activity) {
			// TODO Auto-generated method stub
			mBase.callActivityOnResume(activity);
		}

		@Override
		public void callActivityOnStop(Activity activity) {
			// TODO Auto-generated method stub
			mBase.callActivityOnStop(activity);
		}

		@Override
		public void callActivityOnSaveInstanceState(Activity activity,
				Bundle outState) {
			// TODO Auto-generated method stub
			mBase.callActivityOnSaveInstanceState(activity, outState);
		}

		@Override
		public void callActivityOnSaveInstanceState(Activity activity,
				Bundle outState, PersistableBundle outPersistentState) {
			// TODO Auto-generated method stub
			mBase.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
		}

		@Override
		public void callActivityOnPause(Activity activity) {
			// TODO Auto-generated method stub
			mBase.callActivityOnPause(activity);
		}

		@Override
		public void callActivityOnUserLeaving(Activity activity) {
			// TODO Auto-generated method stub
			mBase.callActivityOnUserLeaving(activity);
		}

		@Override
		public void startAllocCounting() {
			// TODO Auto-generated method stub
			mBase.startAllocCounting();
		}

		@Override
		public void stopAllocCounting() {
			// TODO Auto-generated method stub
			mBase.stopAllocCounting();
		}

		@Override
		public Bundle getAllocCounts() {
			// TODO Auto-generated method stub
			return mBase.getAllocCounts();
		}

		@Override
		public Bundle getBinderCounts() {
			// TODO Auto-generated method stub
			return mBase.getBinderCounts();
		}

		@Override
		public UiAutomation getUiAutomation() {
			// TODO Auto-generated method stub
			return mBase.getUiAutomation();
		}
		
		
		static class ReflectUtil {

			public static ActivityResult execStartActivity(Object receiver, Context who,
					IBinder contextThread, IBinder token, Activity target,
					Intent intent, int requestCode, Bundle options) {
				try {
//					EmbeddedActivityAgent.ActivityReflectUtil.dumpMethod(Instrumentation.class, "execStartActivity");
					Method m = Instrumentation.class.getDeclaredMethod("execStartActivity", 
							new Class[]{Context.class,
										IBinder.class, 
										IBinder.class, 
										Activity.class,
										Intent.class, 
										// int.class VS Integer.class
										int.class, 
										Bundle.class});
					m.setAccessible(true);
					m.invoke(receiver, new Object[]{who,
										contextThread, 
										token, 
										target,
										intent,
										requestCode,
										options});
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		}
		
	}
}
