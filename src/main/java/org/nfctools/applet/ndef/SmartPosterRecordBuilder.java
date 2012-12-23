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
package org.nfctools.applet.ndef;

import java.util.Map;

import org.nfctools.ndef.Record;
import org.nfctools.ndef.wkt.records.Action;
import org.nfctools.ndef.wkt.records.ActionRecord;
import org.nfctools.ndef.wkt.records.SmartPosterRecord;
import org.nfctools.ndef.wkt.records.TextRecord;
import org.nfctools.ndef.wkt.records.UriRecord;

public class SmartPosterRecordBuilder implements Builder {

	@Override
	public boolean canHandle(String type) {
		return "smartPosterRecord".equals(type);
	}

	@Override
	public Record convert(Map<String, String> data) {
		TextRecord textRecord = new TextRecord(data.get("title"));
		UriRecord uriRecord = new UriRecord(data.get("uri"));
		ActionRecord actionRecord = new ActionRecord(getActionSafe(data.get("action")));

		return new SmartPosterRecord(textRecord, uriRecord, actionRecord);
	}

	private Action getActionSafe(String action) {
		try {
			return Action.valueOf(action);
		}
		catch (Exception e) {
			return Action.DEFAULT_ACTION;
		}
	}

	@Override
	public boolean canHandle(Record record) {
		return record instanceof SmartPosterRecord;
	}

	@Override
	public Map<String, String> convert(Record record) {
		SmartPosterRecord spr = (SmartPosterRecord)record;
		return MapBuilder.map("type", "smartPosterRecord", "title", spr.getTitle().getText(), "uri", spr.getUri()
				.getUri(), "action", spr.getAction().getAction().name());
	}

}
