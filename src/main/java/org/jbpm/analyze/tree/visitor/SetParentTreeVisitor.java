package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetParentTreeVisitor implements TreeVisitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(SetParentTreeVisitor.class);
	final Node parent;
	
	public SetParentTreeVisitor() {
		parent = null;
	}
	
	public SetParentTreeVisitor(Node parent) {
		this.parent = parent;
	}
	
	public TreeVisitor visit(Node node) {
		//Check if the node is a gateway, if so set to null to treat this node as the beginning of a new branch
		if (node.type != Node.Type.DIVERGING_GATEWAY) {
			node.parent = parent;
		} else {
			node.parent = null;
		}
		
		if (node.parent != null) {
			LOGGER.debug("Node " + node.id + " has parent " + node.parent.id);
		} else {
			LOGGER.debug("Node " + node.id + " has parent null");
		}
		return new SetParentTreeVisitor(node);
	}
}
