package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;

public class LogTreeVisitor implements TreeVisitor {
	final String prefix;
	
	public LogTreeVisitor() {
		prefix = "";
	}
	
	public LogTreeVisitor(String initialPrefix) {
		this.prefix = initialPrefix;
	}
	
	public TreeVisitor visit(Node node) {
		System.out.println(prefix + node);
		return new LogTreeVisitor(prefix + "-");
	}
}
