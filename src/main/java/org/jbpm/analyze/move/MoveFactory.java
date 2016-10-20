package org.jbpm.analyze.move;

import org.jbpm.analyze.tree.Node;

public class MoveFactory {
	public static AbstractMove createMove(Node focus, Node newAnchor) {
		if (Node.Type.DIVERGING_PARALLEL_GATEWAY.equals(newAnchor.type)) {
			return new MoveToParallelGateway(focus, newAnchor);
		}
		
		
		Node potentialAnchor = newAnchor.children.get(0);
		if (Node.Type.DIVERGING_PARALLEL_GATEWAY.equals(potentialAnchor.type)) {
			return new MoveToParallelGateway(focus, potentialAnchor);
		}
		
		return new Move(focus, newAnchor);
	}
}
