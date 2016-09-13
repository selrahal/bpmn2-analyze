package org.jbpm.analyze.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jbpm.analyze.move.Move;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Node;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public final class ComplexTest {
	@Test
	public void testMain() throws ParserConfigurationException, SAXException, IOException {
		String testFile = "src/test/resources/complex.bpmn2";
		Hints hints = JbpmAnalyze.analyze(new File(testFile));

		Node sendEmail = new Node();
		sendEmail.id = "send_email";

		Node startEvent = new Node();
		startEvent.id = "processStartEvent";

		Node updateDb = new Node();
		updateDb.id = "update_db_id";

		Node writeDb = new Node();
		writeDb.id = "write_to_db_id";

		Move first = new Move(sendEmail, startEvent);
		Move second = new Move(updateDb, writeDb);

		Hints expectedHints = new Hints();
		expectedHints.addHint(first);
		expectedHints.addHint(second);

		Assert.assertEquals(expectedHints, hints);

		JbpmAnalyze.execute(new File(testFile), expectedHints);

	}
}
