package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.move.Move;
import org.jbpm.analyze.tree.Hints;
import org.jbpm.analyze.tree.Node;

public class MoveDependencyLessNodesTreeVisitor implements TreeVisitor {
	
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
					// Probably a start node, ignore it
				} else if (node.anchor == null) {
					// Probably a start node, ignoreit
				} else if (!node.parent.id.equals(node.anchor.id)) {
					hints.addHint(new Move(node, node.anchor));
				}
			}
		}
		
		return this;
	}
}
