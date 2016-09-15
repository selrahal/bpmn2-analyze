package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTreeVisitor implements TreeVisitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogTreeVisitor.class);
	final String prefix;
	
	public LogTreeVisitor() {
		prefix = "";
	}
	
	public LogTreeVisitor(String initialPrefix) {
		this.prefix = initialPrefix;
	}
	
	public TreeVisitor visit(Node node) {
		LOGGER.info(prefix + node.id 
				+ " priority=" + node.priority 
				+ " parent=" + id(node.parent) 
				+ " anchor=" + id(node.anchor)
				+ " pvDep=" + id(node.dependencyAccordingToPV)
				+ " type=" + node.type.name()
				+ " hc=" + node.hashCode());
		return new LogTreeVisitor(prefix + "-");
	}
	
	private String id(Node node) {
		if (node == null) 
			return "null";
		return node.id;
		
	}
}
