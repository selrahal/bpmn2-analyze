package org.jbpm.analyze.tree.visitor;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.analyze.tree.Node;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.w3c.dom.Document;

public class PVDependencyTreeVisitor implements TreeVisitor {
	final Document document;
	final Map<String, Node> currentDependenciesForPV;
	
	public PVDependencyTreeVisitor(Document bpmnDocument) {
		document = bpmnDocument;
		currentDependenciesForPV = new HashMap<>();
	}
	
	public PVDependencyTreeVisitor(Document bpmnDocument,Map<String, Node> currentContext) {
		this.document = bpmnDocument;
		currentDependenciesForPV = currentContext;
	}
	
	public TreeVisitor visit(Node node) {
		//Check the pv on the node to see if we already have a node in this context depending on the PV
		Node pvProvider = null;
		for (String pv : BPMN2DocumentUtil.getProcessVariables(document, node.id)) {
			if (!currentDependenciesForPV.containsKey(pv)) {
				// for this PV the node is the first we have seen in this context, so no dependency from the map
			} else {
				// we have a candidate for a parent dependency 
				Node newProvider = currentDependenciesForPV.get(pv);
				if (pvProvider == null) {
					//this is the first parent dependency
					pvProvider = newProvider;
				} else {
					//We have two competing parents (pvProvider and currentDependenciesForPV) check the priorities
					if (newProvider.priority > pvProvider.priority) {
						pvProvider = newProvider;
					}
					
				}
			}				
			
			//Update the map with the current node as the latest for this PV
			currentDependenciesForPV.put(pv, node);
		}
		node.dependencyAccordingToPV = pvProvider;
		
		//Check if the node is a gateway, if so ...... ?
		return new PVDependencyTreeVisitor(this.document, this.currentDependenciesForPV);
	}
}
