package org.jbpm.analyze.tree.visitor;

import java.util.List;
import java.util.Stack;

import org.jbpm.analyze.tree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No matter how we re-arrange the diagram each node should always come
 * in the branch of its original anchor node (ex of anchor is start nodes and gateways)
 * 
 * Its the earliest node in the diagram that, when hit, ensures we hit the focus. We can
 * branch immediatley after the anchor and go to our node.
 * 
 * @author selrahal
 *
 */
public class AnchorTreeVisitor implements TreeVisitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnchorTreeVisitor.class);
	private Stack<Node> anchors = new Stack<>();
	
	public AnchorTreeVisitor() {
	}
	
	public AnchorTreeVisitor(Stack<Node> anchors) {
		this.anchors = anchors;
	}
	
	public TreeVisitor visit(Node node) {
		Stack<Node> inputAnchors = (Stack<Node>) anchors.clone();
		LOGGER.debug("Start node " + node.id + "with " + ids(inputAnchors) + inputAnchors.hashCode() + " from " + anchors.hashCode());
		if (inputAnchors.isEmpty()) {
			LOGGER.debug("Empty anchor context at " + node.id + ", populating context with this node");
			inputAnchors.add(node);
		} else if (node.type == Node.Type.CONVERGING_EXCLUSIVE_GATEWAY || node.type == Node.Type.CONVERGING_PARALLEL_GATEWAY) {
			if (inputAnchors.peek().id.contains("-join")) {
				LOGGER.debug("Popping " + inputAnchors.peek().id + " from to anchor context");
				inputAnchors.pop();
			} else {
				LOGGER.debug("Not Popping " + inputAnchors.peek().id + " from to anchor context, even though we are at a conv gateway");
			}
		}
		
			LOGGER.debug("Setting anchor of " + node.id + " to the first in the list "+inputAnchors.peek().id+", whole list is: " + ids(inputAnchors) + " ref=" + inputAnchors.hashCode());
			node.anchor = inputAnchors.peek();
		
		if (node.type == Node.Type.DIVERGING_EXCLUSIVE_GATEWAY || node.type == Node.Type.DIVERGING_PARALLEL_GATEWAY) {
			//Update the anchor for the children of this node. the safest new anchor is this 
			//gateway. but really should this include the previous anchor plus the branch taken?
			LOGGER.debug("Pushing " + node.id + " on to anchor context");
			inputAnchors.push(node);
		}
		
		return new AnchorTreeVisitor(inputAnchors);
	}
	
	private String ids(Stack<Node> list) {
		Stack<Node> toExhaust = (Stack<Node>) list.clone();
		StringBuilder sb = new StringBuilder("[");
		while (!toExhaust.isEmpty()) {
			sb.append(toExhaust.pop().id + ",");
		}
		sb.append("]");
		return sb.toString();
	}
	
}
