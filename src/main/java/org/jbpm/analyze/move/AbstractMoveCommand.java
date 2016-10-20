package org.jbpm.analyze.move;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.log4j.Logger;
import org.joox.Match;
import org.w3c.dom.Document;


public abstract class AbstractMoveCommand {
	private static final Logger LOGGER = Logger.getLogger(AbstractMoveCommand.class);
	
	protected void redirectSequenceFlow(Document bpmnDocument, String oldTargetId, String newTargetId) {
		Match oldTarget = $(bpmnDocument).find(attr("id", oldTargetId));
		Match newTarget = $(bpmnDocument).find(attr("id", newTargetId));
		Match sequenceFlow = $(bpmnDocument).find(attr("targetRef",oldTarget.id()));
		LOGGER.info("Redirecting flow "+sequenceFlow.id()+" from " + oldTarget.id() + " to " + newTarget.id());
		sequenceFlow.attr("targetRef", newTarget.id());
		oldTarget.children().remove("incoming");
		newTarget.append($("bpmn2:incoming").text(sequenceFlow.id()));
	}
	
	protected void addParallelDivergingGateway(Document bpmnDocument, String gatewayId, String targetNodeId) {
		LOGGER.info("Adding Diverging Gateway for " + targetNodeId);
		Match process = $(bpmnDocument).find("process").first();
		Match bpmnPlane = $(bpmnDocument).find("BPMNPlane").first();
		
		Match parallelGateway =$("bpmn2:parallelGateway").attr("id", gatewayId)
                .attr("name", "parallel split")
                .attr("gatewayDirection", "Diverging");
		
		Match target = $(bpmnDocument).find(attr("bpmnElement", targetNodeId)).child("Bounds");
		String newY = new Double(Double.parseDouble(target.attr("y")) -100).toString();
		String newX = new Double(Double.parseDouble(target.attr("x")) + 30).toString();;
		
		Match parallelGatewayShape = $("bpmndi:BPMNShape").attr("id", gatewayId + "-shape")
				.attr("bpmnElement", gatewayId)
				.append($("dc:Bounds").attr("height","50.0").attr("width","50.0").attr("x",newX).attr("y",newY));
		
        process.append(parallelGateway);
        bpmnPlane.append(parallelGatewayShape);
	}
	
	protected void addParallelConvergingGateway(Document bpmnDocument, String gatewayId, String targetNodeId) {
		LOGGER.info("Adding Converging Gateway for " + targetNodeId);
		Match process = $(bpmnDocument).find("process").first();
		Match bpmnPlane = $(bpmnDocument).find("BPMNPlane").first();
		
		Match parallelGateway =$("bpmn2:parallelGateway").attr("id", gatewayId)
                .attr("name", "parallel split")
                .attr("gatewayDirection", "Converging");
		
		Match target = $(bpmnDocument).find(attr("bpmnElement", targetNodeId)).child("Bounds");
		
		String newY = new Double(Double.parseDouble(target.attr("y")) + 100).toString();
		String newX = target.attr("x");
		Match parallelGatewayShape = $("bpmndi:BPMNShape").attr("id", gatewayId + "-shape")
				.attr("bpmnElement", gatewayId)
				.append($("dc:Bounds").attr("height","50.0").attr("width","50.0").attr("x",newX).attr("y",newY));
		
        process.append(parallelGateway);
        bpmnPlane.append(parallelGatewayShape);
	}
	
	protected String addSequenceFlow(Document bpmnDocument, String sourceId, String targetId) {
		LOGGER.info("Adding sequence flow from " + sourceId + " to " + targetId);
		Match process = $(bpmnDocument).find("process").first();
		Match bpmnPlane = $(bpmnDocument).find("BPMNPlane").first();
		
		String sequenceFlowId = sourceId + "-" + targetId;
		
		$(bpmnDocument).find(attr("id", sourceId)).append(
				$("bpmn2:outgoing").text(sequenceFlowId)
				);
		
		$(bpmnDocument).find(attr("id", targetId)).append(
				$("bpmn2:incoming").text(sequenceFlowId)
				);
		
		Match parallelGatewayToFocus = $("bpmn2:sequenceFlow").attr("id", sequenceFlowId)
				.attr("sourceRef", sourceId)
				.attr("targetRef", targetId)
				;
		Match parallelGatewayToFocusShape = $("bpmndi:BPMNEdge").attr("id", sequenceFlowId + "-shape")
				.attr("bpmnElement", sequenceFlowId)
				.append($("di:waypoint").attr("x","100.0").attr("y","100.0").attr("xsi:type","dc:Point"));
		
		process.append(parallelGatewayToFocus);
		bpmnPlane.append(parallelGatewayToFocusShape);
		
		return sequenceFlowId;
	}
}
