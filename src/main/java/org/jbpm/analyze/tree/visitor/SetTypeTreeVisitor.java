package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.w3c.dom.Document;

public class SetTypeTreeVisitor implements TreeVisitor {
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
		default:
			node.type = Node.Type.WORK_ITEM;
			break;
		}
		return this;
	}
}
