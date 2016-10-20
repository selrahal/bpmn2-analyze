package org.jbpm.analyze.move;

import org.jbpm.analyze.move.command.MoveCommand;
import org.jbpm.analyze.tree.Node;
import org.w3c.dom.Document;

public class Move extends AbstractMove{

	public Move(Node focus, Node newAnchor) {
		super(focus, newAnchor);
	}
	
	public String toString() {
		return "Add parallel gateway after '" + newAnchor.name + "' to put '" + focus.name + "' alongside '" + newAnchor.children.get(0).name + "'";
	}
	
	public AbstractMoveCommand command(Document bpmnDocument) {
		return new MoveCommand(bpmnDocument, this);
	}
}