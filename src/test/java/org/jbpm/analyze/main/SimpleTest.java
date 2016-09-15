package org.jbpm.analyze.main;

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

public final class SimpleTest {
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
	
	@Test
	public void assertHints() throws ParserConfigurationException, SAXException, IOException {
		String testFile = "src/test/resources/simple.bpmn2";
		DocumentBuilder db = FACTORY.newDocumentBuilder();
		Document bpmnDocument = db.parse(testFile);
		Hints hints = JbpmAnalyze.analyze(bpmnDocument);

		Node sendEmail = new Node();
		sendEmail.id = "send_email";

		Node startEvent = new Node();
		startEvent.id = "processStartEvent";

		Move first = new Move(sendEmail, startEvent);

		Hints expectedHints = new Hints();
		expectedHints.addHint(first);

		Assert.assertEquals(expectedHints, hints);

	}
	
	@Test
	public void testMain() throws ParserConfigurationException, SAXException, IOException {
		String testFile = "src/test/resources/simple.bpmn2";
		JbpmAnalyze.run(testFile, "out.bpmn2");
	}
}
