package org.nfctools.applet.ndef;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nfctools.ndef.Record;
import org.nfctools.ndef.ext.AndroidApplicationRecord;
import org.nfctools.ndef.wkt.records.Action;
import org.nfctools.ndef.wkt.records.SmartPosterRecord;
import org.nfctools.ndef.wkt.records.TextRecord;
import org.nfctools.ndef.wkt.records.UriRecord;

import com.google.gson.GsonBuilder;

public class NdefBuilderServiceTest {

	private NdefBuilderService ndefBuilderService = BuilderConfig.getBuilderServiceInstance();

	@Test
	public void testBuild() throws Exception {

		String json = "[{\"type\":\"textRecord\",\"text\":\"nfctools\"},"
				+ "{\"type\":\"smartPosterRecord\",\"uri\":\"http://www.nfctools.org\",\"title\":\"nfctools\",\"action\":\"DEFAULT_ACTION\"},"
				+ "{\"type\":\"androidApplicationRecord\",\"packageName\":\"de.grundid.qrtimer\"},"
				+ "{\"type\":\"uriRecord\",\"uri\":\"http://www.nfctools.org\"}]";

		List<Map<String, String>> list = new GsonBuilder().create().fromJson(json, ArrayList.class);

		List<Record> records = ndefBuilderService.convert(list);

		assertEquals(4, records.size());

		assertEquals(TextRecord.class, records.get(0).getClass());
		assertEquals(SmartPosterRecord.class, records.get(1).getClass());
		assertEquals(AndroidApplicationRecord.class, records.get(2).getClass());
		assertEquals(UriRecord.class, records.get(3).getClass());

		assertEquals("nfctools", ((TextRecord)records.get(0)).getText());

		assertEquals("nfctools", ((SmartPosterRecord)records.get(1)).getTitle().getText());
		assertEquals("http://www.nfctools.org", ((SmartPosterRecord)records.get(1)).getUri().getUri());
		assertEquals(Action.DEFAULT_ACTION, ((SmartPosterRecord)records.get(1)).getAction().getAction());

		assertEquals("de.grundid.qrtimer", ((AndroidApplicationRecord)records.get(2)).getPackageName());

		assertEquals("http://www.nfctools.org", ((UriRecord)records.get(3)).getUri());
	}
}
