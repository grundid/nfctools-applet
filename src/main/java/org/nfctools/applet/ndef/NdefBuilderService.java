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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nfctools.ndef.Record;

public class NdefBuilderService {

	private List<Builder> builders;

	public NdefBuilderService(List<Builder> builders) {
		this.builders = builders;
	}

	public List<Record> convert(List<Map<String, String>> list) {
		List<Record> records = new ArrayList<Record>();

		for (Map<String, String> data : list) {
			String type = data.get("type");
			for (Builder builder : builders) {
				if (builder.canHandle(type))
					records.add(builder.convert(data));
			}
		}
		return records;

	}

	public List<Map<String, String>> convertToListMap(Iterable<Record> records) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (Record record : records) {
			for (Builder builder : builders) {
				if (builder.canHandle(record))
					list.add(builder.convert(record));
			}
		}

		return list;
	}

}
