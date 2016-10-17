package org.jbpm.analyze.move;

import org.jbpm.analyze.tree.Node;
import org.w3c.dom.Document;

public class Move {
	public Node focus;
	public Node newAnchor;
	
	public Move(Node focus, Node newAnchor) {
		this.focus = focus;
		this.newAnchor = newAnchor;
	}
	
	public String toString() {
		return "Move " + focus.name + " to anchor " + newAnchor.name;
	}
	
	public MoveCommand command(Document bpmnDocument) {
		return new MoveCommand(bpmnDocument, this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Move) {
			Move otherMove = (Move) obj;
			if (focus.id.equals(otherMove.focus.id) && 
					newAnchor.id.equals(otherMove.newAnchor.id)) {
				return true;
			}
		} else {
			return false;
		}
		return super.equals(obj);
	}
}
