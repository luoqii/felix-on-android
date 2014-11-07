package org.bbs.felix.activity;

import org.bangbang.song.felixonandroid.R;
import org.bbs.felix.util.OsgiUtil;
import org.osgi.framework.BundleException;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BundleDetailActivity extends FragmentActivity {
	public static final String EXTRA_BUNDLE_ID = "extra.bundle.id";
	private ViewPager mViewPager;
	private Adapter mAdapter;
	private org.osgi.framework.Bundle[] mBundles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bundle_detail);
		
		long bId = getIntent().getLongExtra(EXTRA_BUNDLE_ID, -1);
		
		mAdapter = new Adapter();
		mBundles = BundleListActivity.BundleList.getInstance().getBundles();
		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		mViewPager.setAdapter(mAdapter);
		
		int index = 0;
		for (org.osgi.framework.Bundle b : mBundles) {
			if (bId == b.getBundleId()){
				mViewPager.setCurrentItem(index);
				break;
			}
			index++;
		}
	}

	public static CharSequence toString(org.osgi.framework.Bundle b) {
		String str = ""
				 + "id: " + b.getBundleId() + "\n"
				 + "version: " + b.getVersion() + "\n"
				 + "location: " + b.getLocation() + "\n"
				 + "state: " + OsgiUtil.bundleState2Str(b.getState()) + "\n"
				 + "headers: \n" + getHeader(b) + "\n"
				;
		return str;
	}
	
	public static String getHeader(org.osgi.framework.Bundle b) {
		return OsgiUtil.getHeader(b, OsgiUtil.HEADER_OSGI);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_bundle_detail, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			int position = mViewPager.getCurrentItem();
			org.osgi.framework.Bundle b = mBundles[position];
			switch (item.getItemId()) {
			case R.id.action_start:
				b.start();
				break;
			case R.id.action_stop:
				b.stop();
				break;
			case R.id.action_uninstall:
				b.uninstall();
				break;

			default:
				break;
			}
			
			mAdapter.notifyDataChanged(position);
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.onOptionsItemSelected(item);
	}
	
	class Adapter extends PagerAdapter {
		private int mPendingUpdatePositon;

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			org.osgi.framework.Bundle b = mBundles[position];
			TextView text = new TextView(BundleDetailActivity.this);
			text.setText(BundleDetailActivity.toString(b));
			text.setTag(position);
			text.setMovementMethod(new ScrollingMovementMethod());
			
			container.addView(text);
			return text;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position,
				Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return "id: " + mBundles[position].getBundleId();
		}
		
		@Override
		public int getCount() {
			return mBundles.length;
		}
		
		// viewpager bug.
		public int getItemPosition(Object object) {
			int position = (Integer) ((View)object).getTag();
			if (position == mPendingUpdatePositon) {
				mPendingUpdatePositon = -1;
				return POSITION_NONE;
			}
			return super.getItemPosition(object);
		}
		public void notifyDataChanged(int position){
			mPendingUpdatePositon = position;
			notifyDataSetChanged();
		}
	}

}
