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
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * 
 * @author luoqii
 *
 * @see {@link ActivityAgent}
 */
public class EmbeddedBundleActivity extends BundleActivity
{
	
	
	private int mThemeResId = android.R.style.Theme_Black;

	@Override
	public void setTheme(int resid) {
		super.setTheme(resid);
		
//		mThemeResId = resid;
	}

	@Override
	public Theme getTheme() {
//		return super.getTheme();
		Theme theme = getResources().newTheme();
		// TODO how to get an un-installed apk's theme.
		theme.applyStyle(mThemeResId, true);
		return theme;
	}
}
