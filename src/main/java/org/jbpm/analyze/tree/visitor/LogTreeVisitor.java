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
		LOGGER.info(prefix + node.id + " priority=" + node.priority);
		return new LogTreeVisitor(prefix + "-");
	}
}
