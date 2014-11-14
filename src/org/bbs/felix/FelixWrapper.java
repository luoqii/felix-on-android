package org.bbs.felix;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.bbs.felix.util.OsgiUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * 
 * @see <a href="http://felix.apache.org/site/apache-felix-framework-and-google-android.html">felist-on-android</a>
 * @see {felix source}/main/src/main/java/org/apache/felix/main/Main.java
 * @author bangbang.song@gmail.com
 *
 */
public class FelixWrapper{
	private static final String TAG = FelixWrapper.class.getSimpleName();
	
	private static final String OSGI_BUNDLE_DIR = "osgi_bundle";
	private static final String OSGI_BUNDLE_CACHE_DIR = "osgi_bundlecache";
	
	private static final String ASSERT_PRELOAD_BUNDLE_DIR = "felix/preloadbundle";
	
	private static FelixWrapper sInstance;
	private Framework mFramework;
	private String mCacheDir;
	private String mBundleDir;

	private FelixWrapper(Context context){
		mCacheDir = context.getDir(OSGI_BUNDLE_CACHE_DIR, Context.MODE_WORLD_WRITEABLE).toString();
		mBundleDir = context.getDir(OSGI_BUNDLE_DIR, Context.MODE_WORLD_WRITEABLE).toString();;
		extractPreloadBundle(context);
		
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(Constants.FRAMEWORK_STORAGE, mCacheDir);
		configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, ANDROID_PACKAGES_FOR_EXPORT_EXTRA);
		
		configMap.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY, mBundleDir);
		configMap.put(AutoProcessor.AUTO_DEPLOY_ACTION_PROPERY, 
				AutoProcessor.AUTO_DEPLOY_UNINSTALL_VALUE + "," +
				AutoProcessor.AUTO_DEPLOY_INSTALL_VALUE + "," + AutoProcessor.AUTO_DEPLOY_START_VALUE);
		
		configMap.put(FelixConstants.LOG_LEVEL_PROP, 4 + "");
		
		mFramework = new FrameworkFactory().newFramework(configMap);
		
		Log.d(TAG, "init & start osgi." );
		try {
			mFramework.init();
			AutoProcessor.process(configMap, mFramework.getBundleContext());
			
			mFramework.start();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d(TAG, "OSGi framework running, state: " + OsgiUtil.bundleState2Str(mFramework.getState()));
		
		Bundle[] bundles = mFramework.getBundleContext().getBundles();
		for (Bundle b : bundles) {
			Log.d(TAG, "b: " + b.getBundleId() + " " + OsgiUtil.bundleState2Str(b.getState()));
		}
	}
	
	public Framework getFramework(){
		return mFramework;
	}
	
	private void extractPreloadBundle(Context context) {
		try {
			AssetManager assetsM = context.getResources().getAssets();
			String[] files = assetsM.list(ASSERT_PRELOAD_BUNDLE_DIR);
			for (String aFile: files) {
				String assertFile = ASSERT_PRELOAD_BUNDLE_DIR + "/" + aFile;
				Log.d(TAG, "prepare bundle: " + aFile);
				InputStream in = assetsM.open(assertFile);
				String bFile = mBundleDir + "/" + aFile;
				OutputStream out = 
//						context.openFileOutput(mBundleDir + "/" + aFile, 0);
						
						new FileOutputStream(bFile);
				
				int byteCount = 8096;
				byte[] buffer = new byte[byteCount];
				int count = 0;
				while ((count = in.read(buffer, 0, byteCount)) != -1){
					out.write(buffer, 0, count);
				}
				
				in.close();
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public static FelixWrapper getInstance(Context context) {
		if (null == sInstance) {
			sInstance = new FelixWrapper(context);
		}
		return sInstance;
	}
	
	// copied from http://code.google.com/p/felix-on-android/
	private static final String ANDROID_PACKAGES_FOR_EXPORT_EXTRA = 
			"org.bbs.felix.activity," +
			"org.bbs.felix.activity.ActivityAgent," +
			"org.bbs.felix.activity.bundlemanager," + 
			"org.bbs.felixonandroid," + 
			//android
	        "android, " + 
	        "android.app," + 
	        "android.content," + 
	        "android.content.res," + 
	        "android.database," + 
	        "android.database.sqlite," + 
	        "android.graphics, " + 
	        "android.graphics.drawable, " + 
	        "android.graphics.glutils, " + 
	        "android.hardware, " + 
	        "android.location, " + 
	        "android.media, " + 
	        "android.net, " + 
	        "android.opengl, " + 
	        "android.os, " + 
	        "android.os.bundle, " + 
	        "android.provider, " + 
	        "android.sax, " + 
	        "android.speech.recognition, " + 
	        "android.telephony, " + 
	        "android.telephony.gsm, " + 
	        "android.text, " + 
	        "android.text.method, " + 
	        "android.text.style, " + 
	        "android.text.util, " + 
	        "android.util, " + 
	        "android.view, " + 
	        "android.view.animation, " + 
	        "android.webkit, " + 
	        "android.widget, " + 
	        // JAVAx
	        "javax.crypto; " + 
	        "javax.crypto.interfaces; " + 
	        "javax.crypto.spec; " + 
	        "javax.microedition.khronos.opengles; " + 
	        "javax.net; " + 
	        "javax.net.ssl; " + 
	        "javax.security.auth; " + 
	        "javax.security.auth.callback; " + 
	        "javax.security.auth.login; " + 
	        "javax.security.auth.x500; " + 
	        "javax.security.cert; " + 
	        "javax.sound.midi; " + 
	        "javax.sound.midi.spi; " + 
	        "javax.sound.sampled; " + 
	        "javax.sound.sampled.spi; " + 
	        "javax.sql; " + 
	        "javax.xml.parsers; " + 
	        //JUNIT
	        "junit.extensions; " + 
	        "junit.framework; " + 
	        //APACHE
	        "org.apache.commons.codec; " + 
	        "org.apache.commons.codec.binary; " + 
	        "org.apache.commons.codec.language; " + 
	        "org.apache.commons.codec.net; " + 
	        "org.apache.commons.httpclient; " + 
	        "org.apache.commons.httpclient.auth; " + 
	        "org.apache.commons.httpclient.cookie; " + 
	        "org.apache.commons.httpclient.methods; " + 
	        "org.apache.commons.httpclient.methods.multipart; " + 
	        "org.apache.commons.httpclient.params; " + 
	        "org.apache.commons.httpclient.protocol; " + 
	        "org.apache.commons.httpclient.util; " + 
	        
	        //OTHERS
	        "org.bluez; " + 
	        "org.json; " + 
	        "org.w3c.dom; " + 
	        "org.xml.sax; " + 
	        "org.xml.sax.ext; " + 
	        "org.xml.sax.helpers; " +
	        
	        // this no ';' or ',' , shit.
	        "net.neosum.android.view";
}
