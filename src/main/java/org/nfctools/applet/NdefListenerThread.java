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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nfctools.api.Tag;
import org.nfctools.api.UnknownTagListener;
import org.nfctools.applet.json.NdefJsonMessage;
import org.nfctools.applet.json.NdefOperationsMessage;
import org.nfctools.applet.ndef.BuilderConfig;
import org.nfctools.applet.ndef.MapBuilder;
import org.nfctools.ndef.NdefListener;
import org.nfctools.ndef.NdefOperations;
import org.nfctools.ndef.NdefOperationsListener;
import org.nfctools.ndef.Record;
import org.nfctools.scio.TerminalStatus;
import org.nfctools.scio.TerminalStatusListener;
import org.nfctools.utils.NfcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;

public class NdefListenerThread implements Runnable, NdefListener, TerminalStatusListener, NdefOperationsListener,
		UnknownTagListener {

	private Logger log = LoggerFactory.getLogger(getClass());

	private NfcService service;
	private NdefApplet ndefApplet;

	private NdefOperations lastNdefOperations;

	public NdefListenerThread(NdefApplet ndefApplet) {
		this.ndefApplet = ndefApplet;
	}

	private void onNdefStarted(boolean status, String message) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("status", Boolean.toString(status));
		map.put("message", message);

		ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefStarted", map));
	}

	@Override
	public void onNdefOperations(NdefOperations ndefOperations) {
		lastNdefOperations = ndefOperations;
		NdefOperationsMessage message = new NdefOperationsMessage();
		message.setFormatted(ndefOperations.isFormatted());
		message.setWriteable(ndefOperations.isWritable());

		if (ndefOperations.isFormatted()) {
			message.setEmpty(!ndefOperations.hasNdefMessage());
			if (ndefOperations.hasNdefMessage()) {
				List<Record> ndefMessage = ndefOperations.readNdefMessage();
				List<Map<String, String>> listMap = BuilderConfig.getBuilderServiceInstance().convertToListMap(
						ndefMessage);
				message.setNdefRecords(listMap);
			}
		}
		else
			message.setEmpty(true);

		ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefOperations", message));
	}

	@Override
	public void onNdefMessages(Collection<Record> records) {

		NdefOperationsMessage message = new NdefOperationsMessage();
		message.setEmpty(false);

		List<Map<String, String>> listMap = BuilderConfig.getBuilderServiceInstance().convertToListMap(records);
		message.setNdefRecords(listMap);

		ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefPush", message));

		//		byte[] rawData = NdefContext.getNdefMessageEncoder().encode(records);
		//		String rawDataAsString = NfcUtils.convertBinToASCII(rawData);
		//		List<NdefRecordData> dataList = new ArrayList<NdefRecordData>();
		//		for (Record record : records) {
		//			dataList.add(new NdefRecordData(record, rawDataAsString));
		//		}
		//
		//		Gson gson = new GsonBuilder().create();
		//		String json = gson.toJson(dataList);
		//		ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefMessages", json));
	}

	public void ndefPushSuccess(int id) {
		ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefPushSuccess", MapBuilder.map("id", id + "")));
	}

	public void ndefPushFailed(int id) {
		ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefPushFailed", MapBuilder.map("id", id + "")));
	}

	public void addMessage(String jsonMessage) {
		try {
			log.info(jsonMessage);
			NdefOperationsMessage ndefOperationsMessage = new GsonBuilder().create().fromJson(jsonMessage,
					NdefOperationsMessage.class);

			if (lastNdefOperations != null) {
				if (lastNdefOperations.isWritable()) {
					List<Record> records = BuilderConfig.getBuilderServiceInstance().convert(
							ndefOperationsMessage.getNdefRecords());
					Record[] recordArray = records.toArray(new Record[records.size()]);

					if (lastNdefOperations.isFormatted())
						lastNdefOperations.writeNdefMessage(recordArray);
					else
						lastNdefOperations.format(recordArray);

					if (!ndefOperationsMessage.isWriteable())
						lastNdefOperations.makeReadOnly();

					ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefOperationsFinished", "ok"));
				}
				else
					ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefOperationsFinished", "Tag not writeable"));
			}
			else
				ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefOperationsFinished", "No tag in range"));
		}
		catch (Exception e) {
			e.printStackTrace();
			ndefApplet.sendToJavaScript(new NdefJsonMessage("onNdefOperationsFinished", e.getMessage()));
		}
	}

	@Override
	public void onStatusChanged(TerminalStatus status) {
		ndefApplet.sendToJavaScript(new NdefJsonMessage("onStatus", MapBuilder.map("status", status.toString()
				.toLowerCase())));
	}

	@Override
	public void unsupportedTag(Tag tag) {
		log.info("Unsupported tag: " + tag.getTagType() + ", generalBytes: "
				+ NfcUtils.convertBinToASCII(tag.getGeneralBytes()));

	}

	@Override
	public void run() {
		service = null;
		try {
			service = new NfcService(this);
			onNdefStarted(true, "Connected to " + service.getTerminalName());
		}
		catch (Exception e) {
			onNdefStarted(false, e.getLocalizedMessage());
		}
		if (service != null)
			service.startListening(this, this, this);
	}

}
