package org.bbs.bundlemgr;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private ServiceRegistration<?> mBundleList;

	@Override
	public void start(BundleContext context) throws Exception {
		mBundleList = context.registerService(BundleList.class.getName(), new BundleList(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
