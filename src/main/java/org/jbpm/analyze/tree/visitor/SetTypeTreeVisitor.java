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
		switch (tag) {
		case "startEvent":
			node.type = Node.Type.START_EVENT;
			break;
		case "endEvent":
			node.type = Node.Type.END_EVENT;
			break;
		case "parallelGateway":
		case "exclusiveGateway":
			node.type = Node.Type.GATEWAY;
			break;
		default:
			node.type = Node.Type.WORK_ITEM;
			break;
		}
		LOGGER.debug("Node " + node.id + " is type " + node.type.name());
		return this;
	}
}
