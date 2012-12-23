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
import org.nfctools.ndef.mime.MimeRecord;
import org.nfctools.ndef.mime.TextMimeRecord;

public class MimeRecordBuilder implements Builder {

	@Override
	public boolean canHandle(String type) {
		return "mimeRecord".equals(type);
	}

	@Override
	public boolean canHandle(Record record) {
		return record instanceof MimeRecord;
	}

	@Override
	public Record convert(Map<String, String> data) {
		TextMimeRecord record = new TextMimeRecord(data.get("contentType"), data.get("content"));
		return record;
	}

	@Override
	public Map<String, String> convert(Record record) {
		TextMimeRecord mimeRecord = (TextMimeRecord)record;
		return MapBuilder.map("type", "mimeRecord", "contentType", mimeRecord.getContentType(), "content",
				mimeRecord.getContent());
	}

}
