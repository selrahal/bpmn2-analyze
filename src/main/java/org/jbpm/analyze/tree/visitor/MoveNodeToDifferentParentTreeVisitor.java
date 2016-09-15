package org.jbpm.analyze.tree.visitor;

import java.util.Stack;

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
				return this;
			} else if (parent == null) {
				LOGGER.debug("Node " + node.id +" has no parent, no moving to " + pvDep);
				return this;
			}
			
			LOGGER.debug("Node " + node.id + " has parent=" + parent.id + "," + parent.priority + " pvDep=" + pvDep.id + "," + pvDep.priority);
			
			//if pvDep.priority < parent.priority  (aka pvDep comes first in the diagram) then
			if (pvDep.priority >= parent.priority) {
				LOGGER.debug("Node " + node.id +" has no pvDep before parent, no moving to " + pvDep.id);
				return this;
			}
			
			if (parent.type == Type.DIVERGING_EXCLUSIVE_GATEWAY) { //Why does this matter if we have a correct anchor?
				LOGGER.debug("Node " + node.id +" has diverging gateway parent, not moving ");
				return this;
			} else if (parent.type == Type.CONVERGING_EXCLUSIVE_GATEWAY) {
				LOGGER.debug("Node " + node.id +" has converging gateway parent, no moving ");
				return this;
			} 
			
			//Need to walk the tree between pvDep and node, all nodes are possible candidates and should be visited checking the following conditions
			// not an exclusive gateway
			// not equal to the parent (that would be a no-op)
			// not in a different anchor than node
			
			
			Stack<Node> toVisit = new Stack<>();
			toVisit.push(pvDep);
			while (!toVisit.isEmpty()) {
				Node currentPvDep = toVisit.pop();
				if (!currentPvDep.id.equals(node.id)) {
					if (currentPvDep.priority > node.priority) {
						LOGGER.debug("CurrentPvDep " + currentPvDep.id + " is after Node " + node.id);
					} else if (currentPvDep.id.equals(parent.id)) {
						LOGGER.debug("Node " + node.id + " parent==currentPvDep parent, no moving to " + currentPvDep.id);
					} else if (!currentPvDep.anchor.id.equals(node.anchor.id)) { 
						LOGGER.debug("Node " + node.id + ",a=" + node.anchor.id + " has different anchor than currentPvDep " + currentPvDep.id + ",a=" + currentPvDep.anchor.id);
					} else {
						LOGGER.debug("MOVE: Node " + node.id + " parent=" + parent.id + " currentPvDep=" + currentPvDep.id);
						hints.addHint(new Move(node, pvDep));
						return this;
					}

					// Can't use currentPvDep and currentPvDep != node, continue
					// with the children of currentPvDep
					for (Node child : currentPvDep.children) {
						toVisit.push(child);
					}

				}
				
			}
			
			
			
		}

		return this;
	}
}
