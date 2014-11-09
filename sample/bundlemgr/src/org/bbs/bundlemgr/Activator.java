package org.bbs.bundlemgr;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(BundleList.class.getName(), new BundleList(), null);
		context.registerService(SimpleBundleList.class.getName(), new SimpleBundleList(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
