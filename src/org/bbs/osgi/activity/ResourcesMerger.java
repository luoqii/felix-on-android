package org.bbs.osgi.activity;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * <pre>
 * we assume that: 
 *  1) if first resource search failed, so second must be success, so 
 *  we only catch exception for first resource.
 *  
 * @author luoqii
 *
 */
public class ResourcesMerger 
extends Resources 
{
	private static final boolean DEBUG = true;
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
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		
		return mSecond.getText(id);
	}


	public CharSequence getQuantityText(int id, int quantity)
			throws NotFoundException {
		try {
			return mFirst.getQuantityText(id, quantity);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getQuantityText(id, quantity);
	}


	public String getString(int id) throws NotFoundException {
		try {
			return mFirst.getString(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getString(id);
	}


	public String getString(int id, Object... formatArgs)
			throws NotFoundException {
		try {
			return mFirst.getString(id, formatArgs);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getString(id, formatArgs);
	}


	public String getQuantityString(int id, int quantity,
			Object... formatArgs) throws NotFoundException {
		try {
			return mFirst.getQuantityString(id, quantity, formatArgs);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getQuantityString(id, quantity, formatArgs);
	}


	public String getQuantityString(int id, int quantity)
			throws NotFoundException {
		
		try {
			return mFirst.getQuantityString(id, quantity);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getQuantityString(id, quantity);
	}


	public CharSequence getText(int id, CharSequence def) {
		
		try {
			return mFirst.getText(id, def);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getText(id, def);
	}


	public CharSequence[] getTextArray(int id) throws NotFoundException {
		
		try {
			return mFirst.getTextArray(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getTextArray(id);
	}


	public String[] getStringArray(int id) throws NotFoundException {
		
		try {
			return mFirst.getStringArray(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getStringArray(id);
	}


	public int[] getIntArray(int id) throws NotFoundException {
		
		try {
			return mFirst.getIntArray(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getIntArray(id);
	}


	public TypedArray obtainTypedArray(int id) throws NotFoundException {
		
		try {
			return mFirst.obtainTypedArray(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.obtainTypedArray(id);
	}


	public float getDimension(int id) throws NotFoundException {
		
		try {
			return mFirst.getDimension(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getDimension(id);
	}


	public int getDimensionPixelOffset(int id) throws NotFoundException {
		
		try {
			return mFirst.getDimensionPixelOffset(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getDimensionPixelOffset(id);
	}


	public int getDimensionPixelSize(int id) throws NotFoundException {
		
		try {
			return mFirst.getDimensionPixelSize(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getDimensionPixelSize(id);
	}


	public float getFraction(int id, int base, int pbase) {
		
		try {
			return mFirst.getFraction(id, base, pbase);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getFraction(id, base, pbase);
	}


	public Drawable getDrawable(int id) throws NotFoundException {
		
		try {
			return mFirst.getDrawable(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getDrawable(id);
	}


	public Drawable getDrawableForDensity(int id, int density)
			throws NotFoundException {
		
		try {
			return mFirst.getDrawableForDensity(id, density);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getDrawableForDensity(id, density);
	}


	public Movie getMovie(int id) throws NotFoundException {
		
		try {
			return mFirst.getMovie(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getMovie(id);
	}


	public int getColor(int id) throws NotFoundException {
		
		try {
			return mFirst.getColor(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getColor(id);
	}


	public ColorStateList getColorStateList(int id)
			throws NotFoundException {
		
		try {
			return mFirst.getColorStateList(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getColorStateList(id);
	}


	public boolean getBoolean(int id) throws NotFoundException {
		
		try {
			return mFirst.getBoolean(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getBoolean(id);
	}


	public int getInteger(int id) throws NotFoundException {
		
		try {
			return mFirst.getInteger(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getInteger(id);
	}


	public XmlResourceParser getLayout(int id) throws NotFoundException {
		
		try {
			return mFirst.getLayout(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getLayout(id);
	}


	public XmlResourceParser getAnimation(int id) throws NotFoundException {
		
		try {
			return mFirst.getAnimation(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getAnimation(id);
	}


	public XmlResourceParser getXml(int id) throws NotFoundException {
		
		try {
			return mFirst.getXml(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getXml(id);
	}


	public InputStream openRawResource(int id) throws NotFoundException {
		
		try {
			return mFirst.openRawResource(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.openRawResource(id);
	}


	public InputStream openRawResource(int id, TypedValue value)
			throws NotFoundException {
		
		try {
			return mFirst.openRawResource(id, value);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.openRawResource(id, value);
	}


	public AssetFileDescriptor openRawResourceFd(int id)
			throws NotFoundException {
		
		try {
			return mFirst.openRawResourceFd(id);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.openRawResourceFd(id);
	}


	public void getValue(int id, TypedValue outValue, boolean resolveRefs)
			throws NotFoundException {
		
		try {
			 mFirst.getValue(id, outValue, resolveRefs);
			 return;
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
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
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		mSecond.getValueForDensity(id, density, outValue, resolveRefs);
	}


	public void getValue(String name, TypedValue outValue,
			boolean resolveRefs) throws NotFoundException {
		
		try {
			 mFirst.getValue(name, outValue, resolveRefs);
			 return;
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		mSecond.getValue(name, outValue, resolveRefs);
	}


	public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
		
		try {
			return mFirst.obtainAttributes(set, attrs);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
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
			if (DEBUG) {
				e.printStackTrace();
			}
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
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getDisplayMetrics();
	}


	public Configuration getConfiguration() {
		
		try {
			return mFirst.getConfiguration();
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getConfiguration();
	}


	public int getIdentifier(String name, String defType, String defPackage) {
		
		try {
			return mFirst.getIdentifier(name, defType, defPackage);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getIdentifier(name, defType, defPackage);
	}


	public String getResourceName(int resid) throws NotFoundException {
		
		try {
			return mFirst.getResourceName(resid);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getResourceName(resid);
	}


	public String getResourcePackageName(int resid)
			throws NotFoundException {
		
		try {
			return mFirst.getResourcePackageName(resid);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getResourcePackageName(resid);
	}


	public String getResourceTypeName(int resid) throws NotFoundException {
		
		try {
			return mFirst.getResourceTypeName(resid);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getResourceTypeName(resid);
	}


	public String getResourceEntryName(int resid) throws NotFoundException {
		
		try {
			return mFirst.getResourceEntryName(resid);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getResourceEntryName(resid);
	}


	public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle)
			throws XmlPullParserException, IOException {
		
		try {
			mFirst.parseBundleExtras(parser, outBundle);
			return;
		} catch (XmlPullParserException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		mSecond.parseBundleExtras(parser, outBundle);
	}


	public void parseBundleExtra(String tagName, AttributeSet attrs,
			Bundle outBundle) throws XmlPullParserException {
		
		try {
			mFirst.parseBundleExtra(tagName, attrs, outBundle);
			return;
		} catch (XmlPullParserException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		mSecond.parseBundleExtra(tagName, attrs, outBundle);
	}

	@Override
	public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
		
		try {
			return mFirst.getDrawable(id, theme);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getDrawable(id, theme);
	}

	@Override
	public Drawable getDrawableForDensity(int id, int density, Theme theme) {
		
		try {
			return mFirst.getDrawableForDensity(id, density, theme);
		} catch (NotFoundException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
		}
		return mSecond.getDrawableForDensity(id, density, theme);
	}
	
	
}