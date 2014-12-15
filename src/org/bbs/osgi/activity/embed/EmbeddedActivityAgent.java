package org.bbs.osgi.activity.embed;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bbs.osgi.activity.ActivityAgent;
import org.bbs.osgi.activity.BundleActivity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 *  all method will call through {@link #mTargetActivity}, so, we can
 *  "embed' an exist activity to {@link BundleActivity}.
 * @author luoqii
 * 
 * @see {@link BundleActivity}
 *
 */
public abstract class EmbeddedActivityAgent extends ActivityAgent {
	private static final String TAG = EmbeddedActivityAgent.class.getSimpleName();
	
	protected Activity mTargetActivity;
	
	public abstract Activity getTargetActivity();
	
	protected void onCreate(Bundle savedInstanceState) {
		mTargetActivity = getTargetActivity();
		
		if (null == mTargetActivity) {
			throw new IllegalStateException("target activity is null");
		}
		
		// prepare new activity.
//		ActivityUtil.attach(mHostActivity, mTargetActivity);
		ActivityReflectUtil.attachBaseContext(mTargetActivity, mHostActivity.getApplication());
		ActivityReflectUtil.copyFields(mHostActivity, mTargetActivity);
		
		ActivityReflectUtil.onCreate(mTargetActivity, savedInstanceState);
	}
	
	protected void onResume() {
		ActivityReflectUtil.onResume(mTargetActivity);
	}

	protected void onPause() {
		ActivityReflectUtil.onPause(mTargetActivity);
	}
	
	protected void onDestroy() {
		ActivityReflectUtil.onDestroy(mTargetActivity);
	}
	
	
	public boolean onPreparePanel(int arg0, View arg1, Menu arg2) {
		return mTargetActivity.onPreparePanel(arg0, arg1, arg2);
	}
	public boolean onPrepareOptionsMenu(Menu menu) {
		return mTargetActivity.onPrepareOptionsMenu(menu);
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		return mTargetActivity.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mTargetActivity.onOptionsItemSelected(item);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		mTargetActivity.onCreateContextMenu(menu, v, menuInfo);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return mTargetActivity.onContextItemSelected(item);
	}
	@Override
	public void onContextMenuClosed(Menu menu) {
		mTargetActivity.onContextMenuClosed(menu);
	}

	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		ActivityReflectUtil.onActivityResult(mTargetActivity, arg0, arg1, arg2);
	}
	
	/**
	 *  keep function name consistency with {@link Activity}
	 *  
	 * @author bysong
	 *
	 */
	public static class ActivityReflectUtil {
		public static void onCreate(Activity activity, Bundle savedInstanceState){
			try {
				Method m = Activity.class.getDeclaredMethod("onCreate", new Class[]{Bundle.class});
				m.setAccessible(true);
				m.invoke(activity, new Object[]{savedInstanceState});
			} catch (Exception e) {
				throw new RuntimeException("error in onCreate", e);
			}
		}
		
		public static void onResume(Activity activity){
			try {
				Method m = Activity.class.getDeclaredMethod("onResume", (Class[]) null);
				m.setAccessible(true);
				m.invoke(activity, (Object[]) null);
			} catch (Exception e) {
				throw new RuntimeException("error in onResume", e);
			}
		}
		
		public static void onPause(Activity activity){
			try {
				Method m = Activity.class.getDeclaredMethod("onPause", (Class[]) null);
				m.setAccessible(true);
				m.invoke(activity, (Object[]) null);
			} catch (Exception e) {
				throw new RuntimeException("error in onPause", e);
			}
		}
		
		public static void onDestroy(Activity activity){
			try {
				Method m = Activity.class.getDeclaredMethod("onDestroy", (Class[])null);
				m.setAccessible(true);
				m.invoke(activity, (Object[])null);
			} catch (Exception e) {
				throw new RuntimeException("error in onDestroy", e);
			}
		}		

		public static void onContextMenuClosed(Activity activity,
				Menu menu) {
			try {
				Method m = Activity.class.getDeclaredMethod("onContextMenuClosed", new Class[]{Menu.class});
				m.setAccessible(true);
				m.invoke(activity, new Object[]{menu});
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
			
		}


		public static boolean onContextItemSelected(Activity activity,
				MenuItem item) {
			try {
				Method m = Activity.class.getDeclaredMethod("onContextItemSelected", new Class[]{MenuItem.class});
				m.setAccessible(true);
				return (Boolean) m.invoke(activity, new Object[]{item});
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
			return false;
		}


		public static void onCreateContextMenu(Activity activity,
				ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			try {
				Method m = Activity.class.getDeclaredMethod("onCreateContextMenu", new Class[]{ContextMenu.class, View.class, ContextMenuInfo.class});
				m.setAccessible(true);
				m.invoke(activity, new Object[]{menu, v, menuInfo});
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
			
		}


		public static void onActivityResult(Activity activity, int arg0,
				int arg1, Intent arg2) {
			try {
				Method m = Activity.class.getDeclaredMethod("onActivityResult", new Class[]{int.class, int.class, Intent.class});
				m.setAccessible(true);
				m.invoke(activity, new Object[]{arg0, arg1, arg2});
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
		}


		public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {
			try {
				Method m = Activity.class.getDeclaredMethod("onOptionsItemSelected", new Class[]{MenuItem.class});
				m.setAccessible(true);
				return (Boolean) m.invoke(activity, new Object[]{item});
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
			return false;
		}

		public static boolean onCreateOptionsMenu(Activity activity, Menu menu) {
			try {
				Method m = Activity.class.getDeclaredMethod("onCreateOptionsMenu", new Class[]{Menu.class});
				return (Boolean) m.invoke(activity, new Object[]{menu});
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
			return false;
		}
		
		public static void dumpMethod(Class clazz, String methodName){
			Method[] methods = clazz.getDeclaredMethods();
			for (Method m: methods) {
				if (m.getName().startsWith(methodName)) {
					Log.d(TAG, "method name: " + m.getName());
					Log.d(TAG, "paramter: [");
					Class<?>[] parameterTypes = m.getParameterTypes();
					for (Class p : parameterTypes) {
						Log.d(TAG, "" + p.getCanonicalName());
					}
					Log.d(TAG, "]");
				}
			}
		}

		public static void attach(Activity hostActivity,
				Activity embeddedActivity) {
			try {
//				dumpMethod(Activity.class,"attach");
				Class<Object>[] parameters = new Class[]{Context.class, Class.forName("android.app.ActivityThread"),
						Class.forName("android.app.Instrumentation"), 
						Class.forName("android.os.IBinder"), 
						// not Integer.class
						int.class, 
						Application.class,
						Intent.class, 
						Class.forName("android.content.pm.ActivityInfo"),
//						Class.forName("android.os.IBinder"),
						CharSequence.class, 
						Activity.class, 
						String.class,
						Class.forName("android.app.Activity$NonConfigurationInstances"),
						Configuration.class,
						Class.forName("com.android.internal.app.IVoiceInteractor")
						};
				Method m = Activity.class.getDeclaredMethod("attach", parameters);
				m.setAccessible(true);
				Object[] args = new Object[]{
						hostActivity.getBaseContext(),
						getFiledValue(hostActivity, "mMainThread"),
						getFiledValue(hostActivity, "mInstrumentation"),
						getFiledValue(hostActivity, "mToken"),
						getFiledValue(hostActivity, "mIdent"),	
						getFiledValue(hostActivity, "mApplication"),
						getFiledValue(hostActivity, "mIntent"),
						getFiledValue(hostActivity, "mActivityInfo"),
						getFiledValue(hostActivity, "mTitle"),
						getFiledValue(hostActivity, "mParent"),
						getFiledValue(hostActivity, "mEmbeddedID"),
						getFiledValue(hostActivity, "mLastNonConfigurationInstances"),
						getFiledValue(hostActivity, "mCurrentConfig"),
						null};
				m.invoke(embeddedActivity, args );
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
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public static void attachBaseContext(Activity activity,
				Application application) {
			try {
				Method m = ContextWrapper.class.getDeclaredMethod("attachBaseContext", new Class[]{Context.class});
				m.setAccessible(true);
				m.invoke(activity, new Object[]{application});
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
			
		}


		public static void copyFields(Activity host, Activity target) {
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
		    copyFields(Activity.class, fields, host, target);
		    
		    fields = new String[] {
//		    		"mBase", // android.content.ContextWrapper
		    };
		    copyFields(ContextWrapper.class, fields, host, target);
		    
		    try {
			    LayoutInflater in = LayoutInflater.from(host);
			    
				Field inflator = Class.forName("com.android.internal.policy.impl.PhoneWindow").getDeclaredField("mLayoutInflater");
				inflator.setAccessible(true);
				Object winF = getFiledValue(host, "mWindow");
				inflator.set(winF, in);
			} catch (NoSuchFieldException e) {
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
			}
		}


		public static void copyFields(Class clazz, String[] fields, Activity host, Activity target) {

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
		
		public static  Field getFiled(Object object, String fieldName) {
			Class<?> ACTIVITY;
			try {
				ACTIVITY = Class.forName("android.app.Activity");
	            Field declaredField = ACTIVITY.getDeclaredField(fieldName);
	            
	            return declaredField;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		public static  Object getFiledValue(Object object, String fieldName) {
		        Object f = null;
		        try {
		            Field declaredField = getFiled(object, fieldName);
		            declaredField.setAccessible(true);
		            f = declaredField.get(object);
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
	}
	
	
}
