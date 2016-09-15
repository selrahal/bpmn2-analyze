package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.move.Move;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Node;
import org.jbpm.analyze.tree.Node.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveNodeToDifferentParentTreeVisitor implements TreeVisitor {
	public static final Logger LOGGER = LoggerFactory.getLogger(MoveNodeToDifferentParentTreeVisitor.class);

	final Hints hints;

	public MoveNodeToDifferentParentTreeVisitor(Hints hints) {
		this.hints = hints;
	}

	public TreeVisitor visit(Node node) {
		if (Node.Type.WORK_ITEM.equals(node.type)) {
			Node pvDep = node.dependencyAccordingToPV;
			Node parent = node.parent;
			
			if (pvDep == null) {
				LOGGER.debug("Node " + node.id + " has no dependencies, not touching");
			} else if (parent == null) {
				LOGGER.debug("Node " + node.id +" has no parent, no moving to " + pvDep);
			} else if (parent.type == Type.DIVERGING_EXCLUSIVE_GATEWAY) { //Why does this matter if we have a correct anchor?
				LOGGER.debug("Node " + node.id +" has diverging gateway parent, not moving to " + pvDep.id);
			} else if (parent.type == Type.CONVERGING_EXCLUSIVE_GATEWAY) {
				LOGGER.debug("Node " + node.id +" has converging gateway parent, no moving to " + pvDep.id);
			} else if (pvDep.id.equals(parent.id)) {
				LOGGER.debug("Node " + node.id +" parent==pvDep parent, no moving to " + pvDep.id);
			} else if (pvDep.priority >= parent.priority) {
				LOGGER.debug("Node " + node.id +" has no pvDep before parent, no moving to " + pvDep.id);
			} else if (!pvDep.anchor.id.equals(node.anchor.id)) { //Be wary of moving a node to a branch with a different anchor!
				LOGGER.debug("Node " + node.id +" has no pvDep before parent, no moving to " + pvDep.id);
			} else {
				LOGGER.debug("MOVE: Node " + node.id + " parent=" + parent.id + " pvDep=" + pvDep.id);
				hints.addHint(new Move(node, pvDep));
			}
		}

		return this;
	}
}
