package org.jbpm.analyze.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jbpm.analyze.move.AbstractMove;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Tree;
import org.jbpm.analyze.tree.visitor.AnchorTreeVisitor;
import org.jbpm.analyze.tree.visitor.LogTreeVisitor;
import org.jbpm.analyze.tree.visitor.MoveDependencyLessNodesTreeVisitor;
import org.jbpm.analyze.tree.visitor.MoveNodeToDifferentParentTreeVisitor;
import org.jbpm.analyze.tree.visitor.PVDependencyTreeVisitor;
import org.jbpm.analyze.tree.visitor.ProcessVariableTreeVisitor;
import org.jbpm.analyze.tree.visitor.SetParentTreeVisitor;
import org.jbpm.analyze.tree.visitor.SetPriorityTreeVisitor;
import org.jbpm.analyze.tree.visitor.SetTypeTreeVisitor;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class JbpmAnalyze {
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

	public static void main(final String[] args) throws ParserConfigurationException, SAXException, IOException {
		if (args.length == 2) {
			run(args[0], args[1]);
			System.exit(0);
		} else {
			System.err.println("Usage:");
			System.err.println("  java " + JbpmAnalyze.class.getName() + " inputFile outputFile");
			System.exit(1);
		}
	}
	
	public static void run(String inputFile, String outFile) throws IOException, ParserConfigurationException, SAXException{
		File bpmnFile = new File(inputFile);
		DocumentBuilder db = FACTORY.newDocumentBuilder();
		Document bpmnDocument = db.parse(bpmnFile);
		
		
		Hints hints = analyze(bpmnDocument);
		while (!hints.getHints().isEmpty()) {
			AbstractMove move = hints.getHints().get(0);
			System.out.println("Executing " + move);
			move.command(bpmnDocument).execute();
			BPMN2DocumentUtil.writeFile(bpmnDocument, new File(outFile));
			hints = analyze(bpmnDocument);
		}
		
		BPMN2DocumentUtil.writeFile(bpmnDocument, new File(outFile));
	}
	
	public static Hints analyze(String inputDoc) throws ParserConfigurationException, SAXException, IOException {
		File bpmnFile = new File(inputDoc);
		return analyze(bpmnFile);
	}
	
	public static Hints analyze(File i) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = FACTORY.newDocumentBuilder();
		Document bpmnDocument = db.parse(i);
		return analyze(bpmnDocument);
	}
	
	public static Hints analyze(InputStream inputDoc) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = FACTORY.newDocumentBuilder();
		Document bpmnDocument = db.parse(inputDoc);
		return analyze(bpmnDocument);
	}

	public static Hints analyze(final Document bpmnDocument) throws ParserConfigurationException, SAXException, IOException {
		Hints hints = new Hints();
		
		Tree tree = new Tree(bpmnDocument);
		//Populate metadata
		tree.visit(new SetTypeTreeVisitor(bpmnDocument));
		tree.visit(new AnchorTreeVisitor());
		tree.visit(new SetPriorityTreeVisitor());
		tree.visit(new ProcessVariableTreeVisitor(bpmnDocument));
		tree.visit(new SetParentTreeVisitor());
		tree.visit(new PVDependencyTreeVisitor(bpmnDocument));
		
		//Log metadata
		tree.visit(new LogTreeVisitor());
		
		//Determine moves
		tree.visit(new MoveDependencyLessNodesTreeVisitor(hints));
		tree.visit(new MoveNodeToDifferentParentTreeVisitor(hints));
		
		return hints;
	}

}
