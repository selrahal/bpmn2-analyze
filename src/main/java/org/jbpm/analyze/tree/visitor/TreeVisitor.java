package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;

public interface TreeVisitor {
	
	/**
	 * Visits the node and returns a TreeVisitor that should be used to visit the children of that node
	 */
	public TreeVisitor visit(Node node);
}
