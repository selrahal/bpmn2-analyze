package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.move.Move;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Node;

public class MoveNodeToDifferentParentTreeVisitor implements TreeVisitor {

	final Hints hints;

	public MoveNodeToDifferentParentTreeVisitor(Hints hints) {
		this.hints = hints;
	}

	public TreeVisitor visit(Node node) {
		if (Node.Type.WORK_ITEM.equals(node.type)) {
			Node pvDep = node.dependencyAccordingToPV;
			Node parent = node.parent;
			if (pvDep != null && parent != null && pvDep != parent && pvDep.priority < parent.priority) {
				hints.addHint(new Move(node, pvDep));
			}
		}

		return this;
	}
}
