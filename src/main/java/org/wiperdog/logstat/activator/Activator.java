package org.wiperdog.logstat.activator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wiperdog.logstat.service.LogStat;
import org.wiperdog.logstat.service.impl.LogStatImpl;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		// Bundle bundle =
		// FrameworkUtil.getBundle(Activator.class).getBundleContext().getBundle();
		Bundle bundle = context.getBundle();
		context.registerService(LogStat.class.getName(),
				new LogStatImpl(bundle), null);
		System.out.println("LogStat Service registered !");

	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
