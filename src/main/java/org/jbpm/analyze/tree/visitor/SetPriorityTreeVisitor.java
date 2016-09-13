package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;

public class SetPriorityTreeVisitor implements TreeVisitor {
	final Integer priority;
	
	public SetPriorityTreeVisitor() {
		priority = 0;
	}
	
	public SetPriorityTreeVisitor(Integer priority) {
		this.priority = priority;
	}
	
	public TreeVisitor visit(Node node) {
		node.priority = priority;
		return new SetPriorityTreeVisitor(priority + 1);
	}
}
