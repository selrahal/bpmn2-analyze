package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;

/**
 * No matter how we re-arrange the diagram each node should always come
 * in the branch of its original anchor node (ex of anchor is start nodes and gateways)
 * 
 * @author selrahal
 *
 */
public class AnchorTreeVisitor implements TreeVisitor {
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
			node.anchor = anchor;
		}
		//Check if the node is a gateway, if so that should be the anchor for its children
		return new AnchorTreeVisitor(this.anchor);
	}
}
