package org.bbs.osgi.activity.embed;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 *
 * @author luoqii
 *
 */
public class SimpleActivityAgent extends EmbeddedActivityAgent {
	private static final String TAG = SimpleActivityAgent.class.getSimpleName();
	private String mName;
	
	public SimpleActivityAgent(String targetActivityClassName){
		mName = targetActivityClassName;
	}
	
	public Activity getTargetActivity(){
		try {
			return (Activity) Class.forName(mName).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	};
	
}
