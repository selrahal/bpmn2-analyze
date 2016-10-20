package org.jbpm.analyze.move;

import org.jbpm.analyze.tree.Node;
import org.w3c.dom.Document;

public abstract class AbstractMove {
	public Node focus;
	public Node newAnchor;
	
	public AbstractMove(Node focus, Node newAnchor) {
		this.focus = focus;
		this.newAnchor = newAnchor;
	}
	
	public abstract String toString();
	
	public abstract AbstractMoveCommand command(Document bpmnDocument);
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractMove) {
			AbstractMove otherMove = (AbstractMove) obj;
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
