package org.jbpm.analyze.move;

import org.jbpm.analyze.move.command.MoveCommandDivergingParallelGatewayAnchor;
import org.jbpm.analyze.tree.Node;
import org.w3c.dom.Document;

public class MoveToParallelGateway extends AbstractMove {
	public MoveToParallelGateway(Node focus, Node newAnchor) {
		super(focus, newAnchor);
	}
	
	public String toString() {
		return "Move '" + focus.name + "' to parallel gateway '" + newAnchor.name + "'";
	}
	
	public AbstractMoveCommand command(Document bpmnDocument) {
		return new MoveCommandDivergingParallelGatewayAnchor(bpmnDocument, this);
	}
}