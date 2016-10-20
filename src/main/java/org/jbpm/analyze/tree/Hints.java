package org.jbpm.analyze.tree;

import java.util.LinkedList;
import java.util.List;

import org.jbpm.analyze.move.AbstractMove;

public class Hints {
	private List<AbstractMove> hints = new LinkedList<>();

	public void addHint(AbstractMove hint) {
		if (!hints.contains(hint)) {
			hints.add(hint);
		}
	}
	
	public List<AbstractMove> getHints() {
		return hints;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Hints) {
			Hints otherHints = (Hints) obj;
			if (hints.size() == otherHints.hints.size()) {
				int index = 0;
				while (index < hints.size()) {
					if (!hints.get(index).equals(otherHints.hints.get(index))) { 
						return false;
					}
					index++;
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
