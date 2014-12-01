package org.bbs.osgi.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 *  define method consistency with {@link Activity}.
 *  <p>
 *  {@link #mHostActivity} will be inited (!= null) before {@link #onCreate(Bundle)}, after
 *  {@link #onDestroy()}, it will be un-inited ( == null).
 *  
 *  <p>
 * when add new function, keep it in section, in order.
 * @author luoqii
 *
 * @see {@link BundleActivity}
 */
public class ActivityAgent {
	
	protected FragmentActivity mHostActivity;

	// life-cycle
	protected void onCreate(Bundle savedInstanceState) {
	}
	protected void onResume() {
	}
	protected void onPause() {
	}
	protected void onDestroy() {
		mHostActivity = null;
	}
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	}
	public void onPostCreate(Bundle savedInstanceState) {
	}
	public void onRestart() {
	}
	
	// content view
	public void setContentView(int layoutResID) {
		mHostActivity.setContentView(layoutResID);
	}
	public void setContentView(View view) {
		mHostActivity.setContentView(view);
	}
	public void setContentView(View view, LayoutParams params) {
		mHostActivity.setContentView(view, params);
	}
	public void setTitle(int titleId) {
		mHostActivity.setTitle(titleId);
	}
	public void setTitle(CharSequence title) {
		mHostActivity.setTitle(title);
	}	
		
	// menu.
//	public boolean onPreparePanel(int arg0, View arg1, Menu arg2) {
//		return false;
//	}
	public boolean onCreateOptionsMenu(Menu menu){
		return false;
	}
	public boolean onOptionsItemSelected(MenuItem item){
        return false;
	}
	public boolean onPrepareOptionsMenu(Menu menu){
		return false;
	}

	// start activity
	public void startActivityFromFragment(Fragment fragment, Intent intent,
			int requestCode) {
		mHostActivity.startActivityFromFragment(fragment, intent, requestCode);
	}
	public void startActivity(Intent intent) {
		mHostActivity.startActivity(intent);
	}
	public void startActivity(Intent intent, Bundle options) {
		mHostActivity.startActivity(intent, options);
	}
	public boolean startActivityIfNeeded(Intent intent, int requestCode) {
		return mHostActivity.startActivityIfNeeded(intent, requestCode);
	}
	public boolean startActivityIfNeeded(Intent intent, int requestCode,
			Bundle options) {
		return mHostActivity.startActivityIfNeeded(intent, requestCode, options);
	}
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode) {
		mHostActivity.startActivityFromChild(child, intent, requestCode);
	}
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode, Bundle options) {
		mHostActivity.startActivityFromChild(child, intent, requestCode, options);
	}
	public void startActivityFromFragment(android.app.Fragment fragment,
			Intent intent, int requestCode) {
		mHostActivity.startActivityFromFragment(fragment, intent, requestCode);
	}
	public void startActivityFromFragment(android.app.Fragment fragment,
			Intent intent, int requestCode, Bundle options) {
		mHostActivity.startActivityFromFragment(fragment, intent, requestCode, options);
	}
	public void startActivityForResult(Intent intent, int requestCode) {
		mHostActivity.startActivityForResult(intent, requestCode);
	}
	
	// activity result.
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
	}
	
	// res
	public Resources getResources() {
		return mHostActivity.getResources();
	}	
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean dispatchGenericMotionEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean dispatchTrackballEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
	}
	public boolean onContextItemSelected(MenuItem item) {
		return false;
	}
	public void onContextMenuClosed(Menu menu) {
		
	}
	
	
	
}
