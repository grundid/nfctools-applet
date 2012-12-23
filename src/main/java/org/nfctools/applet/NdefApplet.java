/**
 * This file is part of NFC Tools Applet.
 * Copyright (c) 2012 by Adrian Stabiszewski, as@grundid.de
 *
 * Relation Analyzer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Relation Analyzer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Relation Analyzer.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nfctools.applet;

import java.applet.Applet;
import java.security.AccessController;
import java.security.PrivilegedAction;

import netscape.javascript.JSObject;

import org.nfctools.applet.json.NdefJsonMessage;

import com.google.gson.GsonBuilder;

public class NdefApplet extends Applet {

	private JSObject jsObject;
	private Thread thread;
	private NdefListenerThread ndefListenerThread;

	public void sendAppletMessage(final String jsonMessage) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				ndefListenerThread.addMessage(jsonMessage);
				return null;
			}
		});
	}

	public void sendToJavaScript(NdefJsonMessage ndefJsonMessage) {
		String json = toJson(ndefJsonMessage);
		jsObject.call("onAppletMessage", new Object[] { json });
	}

	private String toJson(Object object) {
		try {
			return new GsonBuilder().create().toJson(object);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void start() {
		jsObject = JSObject.getWindow(this);
		ndefListenerThread = new NdefListenerThread(this);
		thread = new Thread(ndefListenerThread);
		thread.start();
	}

	@Override
	public void stop() {
		thread.interrupt();
	}
}
