package org.bbs.felix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.bbs.felix.util.OsgiUtil;
import org.knopflerfish.framework.FrameworkFactoryImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
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
	private static final String ASSERT_AUTO_EXTRACT_DIR = "autoExtract";
	
	private static FelixWrapper sInstance;
	private Framework mFramework;
	private String mCacheDir;
	private String mBundleDir;

	private FelixWrapper(Context context){
		mCacheDir = context.getDir(OSGI_BUNDLE_CACHE_DIR, Context.MODE_WORLD_WRITEABLE).toString();
		mBundleDir = context.getDir(OSGI_BUNDLE_DIR, Context.MODE_WORLD_WRITEABLE).toString();;
		extractPreloadBundle(context);
		extractAssets(context);
		
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(Constants.FRAMEWORK_STORAGE, mCacheDir);
		configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, ANDROID_PACKAGES_FOR_EXPORT_EXTRA);
		Log.d(TAG, "ANDROID_PACKAGE : " + ANDROID_PACKAGE);
		Log.d(TAG, "ANDROID_PACKAGE_V4 : " + ANDROID_PACKAGE_V4);
		Log.d(TAG, "APP_PACKAGE : " + APP_PACKAGE);
		Log.d(TAG, "THIRD_PACKAGE : " + THIRD_PACKAGE);
		Log.d(TAG, Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA + ": " + ANDROID_PACKAGES_FOR_EXPORT_EXTRA);
		
//		configMap.put(FelixConstants.LOG_LEVEL_PROP, 4 + "");
		
		mFramework = new FrameworkFactoryImpl().newFramework(configMap);
		
		Log.d(TAG, "init & start osgi." );
		try {
			mFramework.init();
			Bundle[] bundles = mFramework.getBundleContext().getBundles();
			
			// for re-deploy bundle.
			for (Bundle b : bundles) {
				if (0 != b.getBundleId()) {
					b.uninstall();
				}
			}
			
			mFramework.start();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "OSGi framework running, state: " + OsgiUtil.bundleState2Str(mFramework.getState()));

		registerListener(mFramework);
		
		Bundle[] bundles = mFramework.getBundleContext().getBundles();
		for (Bundle b : bundles) {
			Log.d(TAG, "b: " + b.getBundleId() + " " + OsgiUtil.bundleState2Str(b.getState()));
		}
	}
	
	public Framework getFramework(){
		return mFramework;
	}
	
	public static void registerListener(Framework f) {
		f.getBundleContext().addBundleListener(new BundleListener(){

			@Override
			public void bundleChanged(BundleEvent e) {
				Log.d(TAG, "bundleChanged. e:" + e);
				
			}});
	}
	
	private void extractAssets(Context context) {
		try {
			AssetManager assetsM = context.getResources().getAssets();
			String[] files = assetsM.list(ASSERT_AUTO_EXTRACT_DIR);
			for (String aFile: files) {
				String assertFile = ASSERT_AUTO_EXTRACT_DIR + "/" + aFile;
				Log.d(TAG, "prepare bundle: " + aFile);
				InputStream in = assetsM.open(assertFile);
				String bFile = "/sdcard/autoextract/" + aFile;
				File f = new File(bFile);
				f.getParentFile().mkdirs();
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
	
	
	private void extractPreloadBundle(Context context) {
		try {
			AssetManager assetsM = context.getResources().getAssets();
			String[] files = assetsM.list(ASSERT_PRELOAD_BUNDLE_DIR);
			for (String aFile: files) {
				String assertFile = ASSERT_PRELOAD_BUNDLE_DIR + "/" + aFile;
				Log.d(TAG, "prepare bundle: " + aFile);
				InputStream in = assetsM.open(assertFile);
				String bFile = mBundleDir + "/" + aFile;
				if (aFile.endsWith("apk")) {
					bFile = bFile + ".jar";
				}
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
	private static String ANDROID_PACKAGES_FOR_EXPORT_EXTRA = null;
	private static String THIRD_PACKAGE  =
	        // JAVAx
	        "javax.crypto, " + 
	        "javax.crypto.interfaces, " + 
	        "javax.crypto.spec, " + 
	        "javax.microedition.khronos.opengles, " + 
	        "javax.net, " + 
	        "javax.net.ssl, " + 
	        "javax.security.auth, " + 
	        "javax.security.auth.callback, " + 
	        "javax.security.auth.login, " + 
	        "javax.security.auth.x500, " + 
	        "javax.security.cert, " + 
	        "javax.sound.midi, " + 
	        "javax.sound.midi.spi, " + 
	        "javax.sound.sampled, " + 
	        "javax.sound.sampled.spi, " + 
	        "javax.sql, " + 
	        "javax.xml.parsers, " + 
	        //JUNIT
	        "junit.extensions, " + 
	        //APACHE
	        "org.apache.commons.codec, " + 
	        "org.apache.commons.codec.binary, " + 
	        "org.apache.commons.codec.language, " + 
	        "org.apache.commons.codec.net, " + 
	        "org.apache.commons.httpclient, " + 
	        "org.apache.commons.httpclient.auth, " + 
	        "org.apache.commons.httpclient.cookie, " + 
	        "org.apache.commons.httpclient.methods, " + 
	        "org.apache.commons.httpclient.methods.multipart, " + 
	        "org.apache.commons.httpclient.params, " + 
	        "org.apache.commons.httpclient.protocol, " + 
	        "org.apache.commons.httpclient.util, " + 
	        
	        //OTHERS
	        "org.bluez, " + 
	        "org.json, " + 
	        "org.w3c.dom, " + 
	        "org.xml.sax, " + 
	        "org.xml.sax.ext, " + 
	        "org.xml.sax.helpers, " +
	        
	        // this no ';' or ',' , shit.
	        "net.neosum.android.view,";
	
	private static String APP_PACKAGE = 
			"org.bbs.osgi.activity," +
			"org.bbs.osgi.activity.embed," +
			"org.bbs.felix.activity.ActivityAgent," +
			"org.bbs.felix.activity.bundlemanager," + 
			"org.bbs.felixonandroid,"
			;

	private static String ANDROID_PACKAGE_V4 = 
	        ""
			;
	
	//see http://developer.android.com/reference/android/package-summary.html
	// api level 21
	private static String ANDROID_PACKAGE = 
        "android, " + 
        "android.app," + 
        "android.animation," + 
        "android.content," + 
        "android.content.res," + 
        "android.content.pm," + 
        "android.database," + 
        "android.database.sqlite," + 
        "android.graphics, " + 
        "android.graphics.drawable, " + 
        "android.graphics.glutils, " + 
        "android.hardware, " + 
        "android.location, " + 
        "android.media, " + 
        "android.net, " + 
        "android.net.wifi, " + 
        "android.opengl, " + 
        "android.os, " + 
        "android.os.bundle, " + 
        "android.preference, " +
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
        
        "junit.framework, " + 
        "junit.runner, " + 
	    "org.apache.http, " + 
	    "org.apache.http.auth, " + 
	    "org.apache.http.auth.params, " + 
	    "org.apache.http.client, " + 
	    "org.apache.http.client.entity, " + 
	    "org.apache.http.client.methods, " + 
	    "org.apache.http.client.params, " + 
	    "org.apache.http.client.protocol, " + 
	    "org.apache.http.client.utils, " + 
	    "org.apache.http.conn, " + 
	    "org.apache.http.conn.params, " + 
	    "org.apache.http.conn.routing, " + 
	    "org.apache.http.conn.scheme, " + 
	    "org.apache.http.conn.ssl, " + 
	    "org.apache.http.conn.util, " + 
	    "org.apache.http.cookie, " + 
	    "org.apache.http.cookie.params, " + 
	    "org.apache.http.entity, " + 
	    "org.apache.http.impl, " + 
	    "org.apache.http.impl.auth, " + 
	    "org.apache.http.impl.client, " + 
	    "org.apache.http.impl.conn, " + 
	    "org.apache.http.impl.conn.tsccm, " + 
	    "org.apache.http.impl.cookie, " + 
	    "org.apache.http.impl.entity, " + 
	    "org.apache.http.impl.io, " + 
	    "org.apache.http.io, " + 
	    "org.apache.http.message, " + 
	    "org.apache.http.params, " + 
	    "org.apache.http.impl, " + 
	    "org.apache.http.protocol, " + 
	    "org.apache.http.util, " + 	    
	    "org.json, " + 
	    "org.w3c.dom, " + 
	    "org.w3c.dom.ls, " + 
	    "org.xml.sax, " + 
	    "org.xml.sax.extv, " + 
	    "org.xml.sax.helpers, " + 
	    "org.xmlpull.v1, " + 
	    "org.xmlpull.v1.sax2, " + 
	    
	    ""
	;
	
	static {
		ANDROID_PACKAGES_FOR_EXPORT_EXTRA += "," + ANDROID_PACKAGE;
		ANDROID_PACKAGES_FOR_EXPORT_EXTRA += ANDROID_PACKAGE_V4;
		ANDROID_PACKAGES_FOR_EXPORT_EXTRA += APP_PACKAGE;
		ANDROID_PACKAGES_FOR_EXPORT_EXTRA += THIRD_PACKAGE;

		ANDROID_PACKAGES_FOR_EXPORT_EXTRA = ANDROID_PACKAGES_FOR_EXPORT_EXTRA.replace("\\s*,\\s*,\\s*", "");
		ANDROID_PACKAGES_FOR_EXPORT_EXTRA = ANDROID_PACKAGES_FOR_EXPORT_EXTRA.replace("\\s*,\\s*$", "");
	}
}
