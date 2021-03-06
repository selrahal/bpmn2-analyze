package org.jbpm.analyze.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jbpm.analyze.move.Move;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Node;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class ComplexTest {
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
	
	@Test
	public void assertHints() throws ParserConfigurationException, SAXException, IOException {
		String testFile = "src/test/resources/complex.bpmn2";
		DocumentBuilder db = FACTORY.newDocumentBuilder();
		Document bpmnDocument = db.parse(testFile);
		Hints hints = JbpmAnalyze.analyze(bpmnDocument);

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

	}
	
	@Test
	public void testMain() throws ParserConfigurationException, SAXException, IOException {
		String testFile = "src/test/resources/complex.bpmn2";
		JbpmAnalyze.run(testFile, "out.bpmn2");
	}
}
