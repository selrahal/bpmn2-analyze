package org.jbpm.analyze.tree;

import org.jbpm.analyze.tree.visitor.TreeVisitor;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.w3c.dom.Document;

public class Tree {
	Document document;
	Node root;
	
	public Tree(Document sourceBpmnDocument) {
		this.document = sourceBpmnDocument;
		this.root = new Node();
		this.root.id = BPMN2DocumentUtil.getStartNode(sourceBpmnDocument);
		init(root);
	}
	
	private void init(Node currentRoot) {
		for (String child : BPMN2DocumentUtil.findNextElements(document, currentRoot.id)){
			Node childNode = new Node();
			childNode.id = child;
			currentRoot.children.add(childNode);
			this.init(childNode);
		}
	}
	
	public void visit(TreeVisitor treeVisitor) {
		this.internalVisit(treeVisitor, root);
	}
	
	private void internalVisit(TreeVisitor treeVisitor, Node node) {
		TreeVisitor newVisitor = treeVisitor.visit(node);
		for (Node child : node.children) {
			this.internalVisit(newVisitor, child);
		}
	}
	
}
