package org.bbs.felix.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.bangbang.song.felixonandroid.R;
import org.bbs.felix.FelixWrapper;
import org.bbs.felix.util.OsgiUtil;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BundleListActivity extends Activity {
	private static final String TAG = BundleListActivity.class.getSimpleName();

	private static final int REAUEST_CODE_PICK_JAR = RESULT_FIRST_USER + 1;
	private ListView mBundles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bundle_list);
		
		mBundles = ((ListView)findViewById(R.id.listbundle));
		
		registerForContextMenu(mBundles);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		updateUI();
	}

	private void updateUI() {
		Framework f = FelixWrapper.getInstance(this).getFramework();
		org.osgi.framework.Bundle[] bundles = f.getBundleContext().getBundles();
		
		ArrayAdapter<org.osgi.framework.Bundle> adapter 
		= new ArrayAdapter<org.osgi.framework.Bundle>(this, android.R.layout.simple_list_item_1, bundles){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView v = new TextView(BundleListActivity.this);
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
				Intent detail = new Intent(BundleListActivity.this, BundleDetailActivity.class);
				detail.putExtra(BundleDetailActivity.EXTRA_BUNDLE_ID, b.getBundleId());
				startActivity(detail);
			}
		});

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_bundle_detail, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		org.osgi.framework.Bundle b = (org.osgi.framework.Bundle) mBundles.getAdapter().getItem(info.position);
		try {
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
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateUI();
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_bundle_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_install:
//			Intent intent = new Intent(Intent.ACTION_PICK);
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setDataAndType(data, type);
			intent.setType("*/*");
			startActivityForResult(intent, REAUEST_CODE_PICK_JAR);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult. requestCode: " + requestCode + " resultCode: " + resultCode + " data: " + data);
		String path = data.getData().toString();
		if (!TextUtils.isEmpty(path)) {
			try {
				if (path.startsWith("file:///")) {
					path = path.substring("file://".length());
				}
				Log.d(TAG, "path: " + path);
				
				FelixWrapper.getInstance(this).getFramework().getBundleContext().installBundle(path, new FileInputStream(new File(path)));
				updateUI();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
