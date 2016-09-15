package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class SetTypeTreeVisitor implements TreeVisitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(SetTypeTreeVisitor.class);
	final Document bpmnDocument;
	
	public SetTypeTreeVisitor(Document bpmnDocument) {
		this.bpmnDocument = bpmnDocument;
	}
	
	public TreeVisitor visit(Node node) {
		//Check if the node is a gateway, if so ...... ?
		String tag = BPMN2DocumentUtil.getTag(bpmnDocument, node.id);
		if (tag.equals("startEvent")) {
			node.type = Node.Type.START_EVENT;
		} else if (tag.equals("endEvent")) {
			node.type = Node.Type.END_EVENT;
		} else if (tag.equals("parallelGateway")) {
			String direction = BPMN2DocumentUtil.getGatewayDirection(bpmnDocument, node.id);
			if ("Diverging".equals(direction)) {
				node.type = Node.Type.DIVERGING_PARALLEL_GATEWAY;
			} else if ("Converging".equals(direction)) {
				node.type = Node.Type.CONVERGING_PARALLEL_GATEWAY;
			} else {
				LOGGER.error("Node " + node.id + " has tag " + tag + " with direction " +direction);
			}
		} else if (tag.equals("exclusiveGateway")) {
			String direction = BPMN2DocumentUtil.getGatewayDirection(bpmnDocument, node.id);
			if ("Diverging".equals(direction)) {
				node.type = Node.Type.DIVERGING_EXCLUSIVE_GATEWAY;
			} else if ("Converging".equals(direction)) {
				node.type = Node.Type.CONVERGING_EXCLUSIVE_GATEWAY;
			} else {
				LOGGER.error("Node " + node.id + " has tag " + tag + " with direction " +direction);
			}
		} else {
			node.type = Node.Type.WORK_ITEM;
		}

		LOGGER.debug("Node " + node.id + " is type " + node.type.name());
		return this;
	}
}
