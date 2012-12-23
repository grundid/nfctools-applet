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

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {

	public static Map<String, String> map(String... strings) {
		Map<String, String> map = new HashMap<String, String>();

		for (int x = 0; x < strings.length; x += 2) {
			map.put(strings[x], strings[x + 1]);
		}

		return map;

	}
}
