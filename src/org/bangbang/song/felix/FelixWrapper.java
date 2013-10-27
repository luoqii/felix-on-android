package org.bangbang.song.felix;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

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
		mCacheDir = context.getDir(OSGI_BUNDLE_CACHE_DIR, 0).toString();
		mBundleDir = context.getDir(OSGI_BUNDLE_DIR, 0).toString();;
		extractPreloadBundle(context);
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(Constants.FRAMEWORK_STORAGE, mCacheDir);
		parameters.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY, mBundleDir);
		mFramework = new FrameworkFactory().newFramework(parameters);
		
		Log.d(TAG, "init & start osgi." );
		try {
			mFramework.init();
			mFramework.start();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d(TAG, "osgi state:" + mFramework.getState());
		
		Bundle[] bundles = mFramework.getBundleContext().getBundles();
		for (Bundle b : bundles) {
			Log.d(TAG, "b: " + b.getBundleId());
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
}
