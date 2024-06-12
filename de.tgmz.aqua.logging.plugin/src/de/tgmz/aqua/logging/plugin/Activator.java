package de.tgmz.aqua.logging.plugin;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {
	// The plug-in ID
	public static final String PLUGIN_ID = "de.tgmz.aqua.logging";

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private static void setContext(BundleContext context) {
		Activator.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) {
		setContext(bundleContext);

		try {
			configureLogbackInBundle(bundleContext.getBundle());
		} catch (JoranException | IOException e) {
			Platform.getLog(context.getBundle()).error("Exception occurred while configuring logback", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) {
		setContext(null);
	}

	private void configureLogbackInBundle(Bundle bundle) throws JoranException, IOException {
		ILoggerFactory lf = LoggerFactory.getILoggerFactory();

		if (lf instanceof LoggerContext lc) {
			JoranConfigurator jc = new JoranConfigurator();
			
			jc.setContext(lc);
			lc.reset();

			URL logbackConfigFileUrl = FileLocator.find(bundle, new Path("logback.xml"), null);

			jc.doConfigure(logbackConfigFileUrl.openStream());

			Platform.getLog(bundle).info(String.format("SLF4J configuered with %s. ", lf.getClass().getName()));
		} else {
			Platform.getLog(bundle).info(String.format("SLF4J already configuered with %s. ", lf.getClass().getName()));
		}
	}
}
