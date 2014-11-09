package org.bbs.felix.activity;

import org.bangbang.song.felixonandroid.R;
import org.bbs.felix.util.OsgiUtil;
import org.osgi.framework.BundleException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BundleDetailFragment extends Fragment {
	public static final String EXTRA_BUNDLE_ID = "extra.bundle.id";
	private ViewPager mViewPager;
	private Adapter mAdapter;
	private org.osgi.framework.Bundle[] mBundles;
	
	public BundleDetailFragment(){
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_bundle_detail, null);
		long bId = getActivity().getIntent().getLongExtra(EXTRA_BUNDLE_ID, -1);
		
		mAdapter = new Adapter();
		mBundles = BundleListActivity.BundleList.getInstance().getBundles();
		mViewPager = (ViewPager)v.findViewById(R.id.viewpager);
		mViewPager.setAdapter(mAdapter);
		
		int index = 0;
		for (org.osgi.framework.Bundle b : mBundles) {
			if (bId == b.getBundleId()){
				mViewPager.setCurrentItem(index);
				break;
			}
			index++;
		}
		return v;
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
	public void onCreateOptionsMenu(Menu menu,  MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.menu_bundle_detail, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			int position = mViewPager.getCurrentItem();
			org.osgi.framework.Bundle b = mBundles[position];
			int itemId = item.getItemId();
			if (itemId == R.id.action_start) {
				b.start();
			} else if (itemId == R.id.action_stop) {
				b.stop();
			} else if (itemId == R.id.action_uninstall) {
				b.uninstall();
			} else {
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
			TextView text = new TextView(getActivity());
			text.setText(BundleDetailFragment.toString(b));
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
