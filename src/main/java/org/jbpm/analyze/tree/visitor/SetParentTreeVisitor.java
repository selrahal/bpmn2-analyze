package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;

public class SetParentTreeVisitor implements TreeVisitor {
	final Node parent;
	
	public SetParentTreeVisitor() {
		parent = null;
	}
	
	public SetParentTreeVisitor(Node parent) {
		this.parent = parent;
	}
	
	public TreeVisitor visit(Node node) {
		//Check if the node is a gateway, if so ...... ?
		node.parent = parent;
		return new SetParentTreeVisitor(node);
	}
}
