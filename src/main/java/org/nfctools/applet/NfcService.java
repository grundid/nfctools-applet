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

import java.util.Collection;

import org.nfctools.NfcAdapter;
import org.nfctools.api.UnknownTagListener;
import org.nfctools.llcp.LlcpConnectionManager;
import org.nfctools.llcp.LlcpConstants;
import org.nfctools.llcp.LlcpOverNfcip;
import org.nfctools.mf.classic.MfClassicNfcTagListener;
import org.nfctools.mf.ul.Type2NfcTagListener;
import org.nfctools.ndef.NdefListener;
import org.nfctools.ndef.NdefOperationsListener;
import org.nfctools.ndef.Record;
import org.nfctools.ndefpush.NdefPushFinishListener;
import org.nfctools.ndefpush.NdefPushLlcpService;
import org.nfctools.scio.Terminal;
import org.nfctools.scio.TerminalHandler;
import org.nfctools.scio.TerminalMode;
import org.nfctools.scio.TerminalStatusListener;
import org.nfctools.spi.acs.AcsTerminal;

public class NfcService {

	private NdefPushLlcpService ndefPushLlcpService;
	private boolean initiatorMode = true;

	private LlcpOverNfcip llcpOverNfcip;
	private Terminal terminal;

	public NfcService(TerminalStatusListener statusListener) {
		TerminalHandler terminalHandler = new TerminalHandler();
		terminalHandler.addTerminal(new AcsTerminal());
		terminalHandler.addTerminal(new SclTerminal());	// otherwise doesn't work with that class of devvices

		terminal = terminalHandler.getAvailableTerminal();
		terminal.setStatusListener(statusListener);
	}

	public String getTerminalName() {
		return terminal.getTerminalName();
	}

	public void addMessages(Collection<Record> ndefRecords, NdefPushFinishListener finishListener) {
		ndefPushLlcpService.addMessages(ndefRecords, finishListener);
	}

	public void startListening(NdefOperationsListener ndefOperationsListener, NdefListener ndefListener,
			UnknownTagListener unknownTagListener) {
		ndefPushLlcpService = new NdefPushLlcpService(ndefListener);
		llcpOverNfcip = new LlcpOverNfcip();
		LlcpConnectionManager connectionManager = llcpOverNfcip.getConnectionManager();
		connectionManager.registerWellKnownServiceAccessPoint(LlcpConstants.COM_ANDROID_NPP, ndefPushLlcpService);

		TerminalMode terminalMode = initiatorMode ? TerminalMode.INITIATOR : TerminalMode.TARGET;
		NfcAdapter nfcAdapter = new NfcAdapter(terminal, terminalMode);
		nfcAdapter.registerTagListener(new Type2NfcTagListener(ndefOperationsListener));
		nfcAdapter.registerTagListener(new MfClassicNfcTagListener(ndefOperationsListener));
		nfcAdapter.registerUnknownTagListerner(unknownTagListener);
		nfcAdapter.setNfcipConnectionListener(llcpOverNfcip);
		nfcAdapter.startListening();
	}
}
