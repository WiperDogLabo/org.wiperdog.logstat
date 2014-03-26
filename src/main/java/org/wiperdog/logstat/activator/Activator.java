package org.wiperdog.logstat.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.wiperdog.jrubyrunner.JrubyRunner;
import org.wiperdog.logstat.service.LogStat;
import org.wiperdog.logstat.service.impl.LogStatImpl;

public class Activator implements BundleActivator {
	BundleContext context;
	private ServiceTracker tracker = null;

	public void start(BundleContext context) throws Exception {
		this.context = context;	
		tracker = new ServiceTracker(this.context, JrubyRunner.class.getName(), new JrubyRunnerTracker());
		tracker.open();

	}

	public void stop(BundleContext context) throws Exception {

	}

	class JrubyRunnerTracker implements ServiceTrackerCustomizer {

		public Object addingService(ServiceReference reference) {
			Object service = context.getService(reference);
			if (service instanceof JrubyRunner) {
				synchronized (this) {
					context.registerService(LogStat.class.getName(), new LogStatImpl((JrubyRunner) service), null);
				}
			}
			return null;
		}

		public void modifiedService(ServiceReference reference, Object service) {
		}

		public void removedService(ServiceReference reference, Object service) {

		}
	}
}
