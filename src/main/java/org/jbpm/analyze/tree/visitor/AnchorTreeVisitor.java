package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No matter how we re-arrange the diagram each node should always come
 * in the branch of its original anchor node (ex of anchor is start nodes and gateways)
 * 
 * Its the earliest node in the diagram that, when hit, ensures we hit the focus
 * 
 * @author selrahal
 *
 */
public class AnchorTreeVisitor implements TreeVisitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnchorTreeVisitor.class);
	Node anchor;
	
	public AnchorTreeVisitor() {
	}
	
	public AnchorTreeVisitor(Node node) {
		this.anchor = node;
	}
	
	public TreeVisitor visit(Node node) {
		if (anchor == null) {
			anchor = node;
		} else {
			LOGGER.debug("Setting anchor of " + node.id + " to " + anchor.id);
			node.anchor = anchor;
		}
		
		if (node.type == Node.Type.DIVERGING_GATEWAY || node.type == Node.Type.CONVERGING_GATEWAY) {
			return new AnchorTreeVisitor(node);
		}
		return new AnchorTreeVisitor(this.anchor);
	}
}
