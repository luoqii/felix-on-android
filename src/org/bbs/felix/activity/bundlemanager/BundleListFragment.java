package org.bbs.felix.activity.bundlemanager;

import org.bbs.felix.activity.bundlemanager.BundleListActivity.BundleList;
import org.bbs.felix.util.OsgiUtil;
import org.bbs.felixonandroid.R;
import org.bbs.osgi.activity.BundleActivity;
import org.osgi.framework.BundleException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BundleListFragment extends Fragment {
	private static final String TAG = BundleListFragment.class.getSimpleName();
	private ListView mBundles;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_bundle_list, null);
		mBundles = ((ListView)v.findViewById(R.id.listbundle));
		
		registerForContextMenu(mBundles);
		return v;
	}



	@Override
	public void onResume() {
		super.onResume();
		
		updateUI();
	}

	private void updateUI() {
		org.osgi.framework.Bundle[] bundles = BundleList.getInstance().getBundles();
		
		ArrayAdapter<org.osgi.framework.Bundle> adapter 
		= new ArrayAdapter<org.osgi.framework.Bundle>(getActivity(), android.R.layout.simple_list_item_1, bundles){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView v = new TextView(getActivity());
				org.osgi.framework.Bundle b = (org.osgi.framework.Bundle) mBundles.getAdapter().getItem(position);
				CharSequence text = OsgiUtil.getName(b) + " " 
						+ b.getBundleId() + " "
						+ OsgiUtil.bundleState2Str(b.getState());
				v.setText(text);
				return v;
			}
		};
		mBundles.setAdapter(adapter);
		mBundles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				org.osgi.framework.Bundle b = (org.osgi.framework.Bundle) parent.getAdapter().getItem(position);
				Intent detail = new Intent(getActivity(), BundleDetailActivity.class);
				detail.putExtra(BundleDetailActivity.EXTRA_BUNDLE_ID, b.getBundleId());
				startActivity(detail);
				
				Intent bundleIntent = new Intent(getActivity(), BundleActivity.class);
				bundleIntent.putExtra(BundleActivity.EXTRA_SERVICE_NAME, "org.bbs.bundlemgr.BundleList");
				bundleIntent.putExtra(BundleActivity.EXTRA_SERVICE_NAME, "org.bbs.bundlemgr.SimpleBundleList");
//				startActivity(bundleIntent);
			}
		});

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

	    MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.menu_bundle_detail, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		org.osgi.framework.Bundle b = (org.osgi.framework.Bundle) mBundles.getAdapter().getItem(info.position);
		try {
			int itemId = item.getItemId();
			if (itemId == R.id.action_start) {
				b.start();
			} else if (itemId == R.id.action_stop) {
				b.stop();
			} else if (itemId == R.id.action_uninstall) {
				b.uninstall();
			} else {
			}
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateUI();
		
		return super.onContextItemSelected(item);
	}


	
}
