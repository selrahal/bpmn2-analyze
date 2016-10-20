package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.move.MoveFactory;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveDependencyLessNodesTreeVisitor implements TreeVisitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(MoveDependencyLessNodesTreeVisitor.class);
	final Hints hints;
	
	public MoveDependencyLessNodesTreeVisitor(Hints hints) {
		this.hints = hints;
	}
	
	public TreeVisitor visit(Node node) {
		if (Node.Type.WORK_ITEM.equals(node.type)) {

			if (node.dependencyAccordingToPV == null) {
				// This node has no PV dependencies, should be a child of its
				// anchor
				if (node.parent == null) {
					LOGGER.debug("Node " + node.id +" has no parent, no moving");
				} else if (node.anchor == null) {
					LOGGER.debug("Node " + node.id + " has no anchor, no moving");
				} else if (node.parent.type == Node.Type.DIVERGING_EXCLUSIVE_GATEWAY) {
					LOGGER.debug("Node " + node.id +" has a gateway parent "+node.parent.id+", no moving");
//				} else if (node.parent.type == Node.Type.DIVERGING_PARALLEL_GATEWAY) { //TODO this is a stopgap to prevent loops in tests :(
//					LOGGER.debug("Node " + node.id +" has a parallel gateway parent "+node.parent.id+", no moving FIX THIS");
				} else if (!node.parent.id.equals(node.anchor.id)) {
					LOGGER.debug("MOVE: Node " + node.id + " parent=" + node.parent.id + " anchor=" + node.anchor.id);
					hints.addHint(MoveFactory.createMove(node, node.anchor));
				}
			} else {
				LOGGER.debug("Node " + node.id + " has dependencies, not touching");
			}
		}
		
		return this;
	}
}
