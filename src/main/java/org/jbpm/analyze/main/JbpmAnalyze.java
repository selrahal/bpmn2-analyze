package org.jbpm.analyze.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jbpm.analyze.move.Move;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Tree;
import org.jbpm.analyze.tree.visitor.AnchorTreeVisitor;
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
		if (args.length == 1) {
			Hints hints = analyze(new File(args[0]));
			// Print hints
			for (Move hint : hints.getHints()) {
				System.out.println(hint);
			}
			System.exit(0);
		} else {
			System.err.println("Usage:");
			System.err.println("  java " + SimpleTest.class.getName() + " bpmnFile");
			System.exit(1);
		}
	}

	public static Hints analyze(final File bpmnFile) throws ParserConfigurationException, SAXException, IOException {
		//Build the Document
		DocumentBuilder db = FACTORY.newDocumentBuilder();
		Document bpmnDocument = db.parse(bpmnFile);
		
		Hints hints = new Hints();
		
		Tree tree = new Tree(bpmnDocument);
		tree.visit(new AnchorTreeVisitor());
		tree.visit(new SetTypeTreeVisitor(bpmnDocument));
		tree.visit(new SetPriorityTreeVisitor());
		tree.visit(new ProcessVariableTreeVisitor(bpmnDocument));
		tree.visit(new SetParentTreeVisitor());
		tree.visit(new PVDependencyTreeVisitor(bpmnDocument));
		tree.visit(new MoveDependencyLessNodesTreeVisitor(hints));
		tree.visit(new MoveNodeToDifferentParentTreeVisitor(hints));
		
		return hints;
	}
	
	public static void execute(final File bpmnFile, final Hints hints) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = FACTORY.newDocumentBuilder();
		Document bpmnDocument = db.parse(bpmnFile);
		
		for (Move move : hints.getHints()) {
			move.command(bpmnDocument).execute();
		}
		
		BPMN2DocumentUtil.writeFile(bpmnDocument, new File("out2.bpmn2"));
	}

}
