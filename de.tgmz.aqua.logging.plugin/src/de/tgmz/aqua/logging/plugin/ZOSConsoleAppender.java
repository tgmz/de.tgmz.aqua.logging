/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.aqua.logging.plugin;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Logs to a dedicated console. 
 */
public final class ZOSConsoleAppender extends AppenderBase<ILoggingEvent> {
	private static final String CONSOLE_ID = "SLF4J";

	@Override
	protected void append(ILoggingEvent event) {
		MessageConsole mc = findZDevConsole();

		try (MessageConsoleStream ms = mc.newMessageStream()) {
			ms.print(event.getFormattedMessage());
		} catch (@SuppressWarnings("unused") IOException e) {
			// Ignore this silently
		}
	}

	private MessageConsole findZDevConsole() {
		IConsoleManager conMan = ConsolePlugin.getDefault().getConsoleManager();

		for (IConsole ic : conMan.getConsoles()) {
			if (CONSOLE_ID.equals(ic.getName())) {
				return (MessageConsole) ic;
			}
		}

		// no console found, so create a new one
		URL resource = this.getClass().getClassLoader().getResource("icons/logo-16-16.gif");

		ImageDescriptor id = resource != null ? ImageDescriptor.createFromURL(resource) : null;

		MessageConsole myConsole = new MessageConsole(CONSOLE_ID, id);
		conMan.addConsoles(new IConsole[] { myConsole });

		return myConsole;
	}

}