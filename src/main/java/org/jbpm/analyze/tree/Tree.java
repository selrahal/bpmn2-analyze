package org.jbpm.analyze.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.jbpm.analyze.tree.visitor.TreeVisitor;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class Tree {
	private static final Logger LOGGER = LoggerFactory.getLogger(Tree.class);
	Document document;
	Node root;
	Map<String, Node> nodes = new HashMap<>();
	
	public Tree(Document sourceBpmnDocument) {
		this.document = sourceBpmnDocument;
		this.root = new Node();
		this.root.id = BPMN2DocumentUtil.getStartNode(sourceBpmnDocument);
		this.root.name = BPMN2DocumentUtil.getName(sourceBpmnDocument, this.root.id);
		nodes.put(root.id, root);
		init(root);
	}
	
	private void init(Node currentRoot) {
		for (String child : BPMN2DocumentUtil.findNextElements(document, currentRoot.id)){
			Node childNode = findChild(child);
			if (childNode == null) {
				childNode = new Node();
				childNode.id = child;
				childNode.name = BPMN2DocumentUtil.getName(document, childNode.id);
				nodes.put(childNode.id,childNode);
				this.init(childNode);
			}
			currentRoot.children.add(childNode);
		}
	}
	
	private Node findChild(String id) {
		return nodes.get(id);
	}
	
	public void visit(TreeVisitor treeVisitor) {
		this.internalVisit(treeVisitor, root);
	}
	
	private void internalVisit(TreeVisitor treeVisitor, Node node) {
		LOGGER.trace("Executing visit for "+ node.id);
		TreeVisitor newVisitor = treeVisitor.visit(node);
		for (Node child : node.children) {
			LOGGER.trace("Visiting child " + child.id + " of " + node.id);
			this.internalVisit(newVisitor, child);
		}
	}
	
}
