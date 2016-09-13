package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.move.Move;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Node;
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
			if (pvDep != null 
					&& parent != null
//					&& parent.type != Node.Type.GATEWAY
					&& pvDep != parent 
					&& pvDep.priority < parent.priority) {
				
				hints.addHint(new Move(node, pvDep));
			}
		}

		return this;
	}
}
