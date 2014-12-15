package org.bbs.osgi.activity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bbs.osgi.activity.embed.EmbeddedApplictionAgent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.view.LayoutInflater;

/**
 * if android call us we call through to {@link #mAgents};
 * otherwise call super or do ourself.
 * 
 * <p>
 * when add new function, keep it in section, in order.
 * 
 * @author luoqii
 *
 * @see {@link ApplicationAgent}
 */
public class BundleApplication extends Application
{

	private static final String TAG = BundleApplication.class.getSimpleName();
	
	List<ApplicationAgent> mAgents;
	
	private LazyContext mLazyContext;
	
	public void attachBundleAplication(ApplicationAgent agent, Resources res, Context baseContext){
		agent.mHostApplicion = this;
		if (null != res) {
			if (mLazyContext == null) {
				mLazyContext = new LazyContext(baseContext);
			}
			mLazyContext.bundleResReady(res);
		}
		if (agent instanceof EmbeddedApplictionAgent) {
			Application app = ((EmbeddedApplictionAgent)agent).mBundelApp;
//			ApplicationUtil.copyFields(agent.mHostApplicion, app);
			ApplicationUtil.callAttach(app, baseContext);
		}
		
		agent.onCreate();
		
		mAgents.add(agent);
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
	public void onCreate() {
		super.onCreate();
		mAgents = new ArrayList<ApplicationAgent>();
	}
	
	static class ApplicationUtil {
		public static void callAttach(Application app, Context baseContext){
			try {
				Method m = Class.forName("android.app.Application").getDeclaredMethod("attach", new Class[]{Context.class});
				m.setAccessible(true);
				m.invoke(app, new Object[]{baseContext});
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
		
		public static void copyFields(Application host, Application target) {
		    String[] fields = new String[] {
		            "mMainThread",
		            "mInstrumentation",
		            "mToken",
		            "mIdent",
		            "mApplication",
		            "mIntent",
		            "mActivityInfo",
		            "mTitle",
		            "mParent",
		            "mEmbeddedID",
		            "mLastNonConfigurationInstances",
		            "mFragments",// java.lang.IllegalStateException
		                         // FragmentManagerImpl.moveToState
		            "mWindow",
		            "mWindowManager",
		            "mCurrentConfig"
		    };
		    copyFields(Application.class, fields, host, target);
		}

		public static void copyFields(Class clazz, String[] fields, Application host, Application target) {

		    try {
		        for (String f : fields) {
		            Field declaredField = clazz.getDeclaredField(f);
		            setField(target, declaredField, getFiledValue(host, f));
		        }
		    } catch (NoSuchFieldException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		}
		
		public static  Object getFiledValue(Object object, String fieldName) {
	        Object f = null;
	        try {
	            Class<?> clazz = Class.forName("android.app.Application");
	            Field declaredField = clazz.getDeclaredField(fieldName);
	            declaredField.setAccessible(true);
	            f = declaredField.get(object);
	        } catch (ClassNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (NoSuchFieldException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IllegalArgumentException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	
	//        Log.d(TAG, "getFiledValue(). fieldName: " + fieldName + " field: " + f);
	        return f;
	    }
		
		public static  void setField(Object object, Field field, Object value) {
		    field.setAccessible(true);
		    try {
		        field.set(object, value);
		    } catch (IllegalArgumentException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    } catch (IllegalAccessException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		}
	}
	
}
