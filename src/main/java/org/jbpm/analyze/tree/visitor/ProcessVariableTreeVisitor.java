package org.jbpm.analyze.tree.visitor;

import org.jbpm.analyze.tree.Node;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.w3c.dom.Document;

public class ProcessVariableTreeVisitor implements TreeVisitor {
	final Document document;
	
	public ProcessVariableTreeVisitor(Document bpmnDocument) {
		this.document = bpmnDocument;
	}
	
	public TreeVisitor visit(Node node) {
		for (String pvName : BPMN2DocumentUtil.getProcessVariables(document, node.id)) {
			node.processVariables.add(pvName);
		}
		
		return this;
	}
}
