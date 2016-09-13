package org.jbpm.analyze.move;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.log4j.Logger;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.joox.Match;
import org.w3c.dom.Document;


public class MoveCommand {
	private static final Logger LOGGER = Logger.getLogger(MoveCommand.class);
	private final Document bpmnDocument;
	private final Move move;
	
	public MoveCommand(Document document, Move move) {
		this.bpmnDocument = document;
		this.move = move;
	}
	
	
	public void execute() {
		LOGGER.info("Executing command for " + move);
		
		Match process = $(bpmnDocument).find("process").first();
		Match bpmnPlane = $(bpmnDocument).find("BPMNPlane").first();
		
		String anchorTag = BPMN2DocumentUtil.getTag(bpmnDocument, move.newAnchor.id);
		String anchorId = move.newAnchor.id;
		LOGGER.info("-new anchor tag: " + anchorTag);
		
		//create parallelconverge
		String parallelGatewayId = move.newAnchor.id + "-join";
		this.addParallelGatewayBelow(bpmnDocument, parallelGatewayId, move.focus.id);
		
		//--redirect sequence flow from "focus target" to new gateway
		String focusTargetId = $(bpmnDocument).find(attr("sourceRef",move.focus.id)).attr("targetRef");
		this.redirectSequenceFlow(bpmnDocument, focusTargetId, parallelGatewayId);
		
		this.addSequenceFlow(bpmnDocument, parallelGatewayId, focusTargetId);
		
		this.redirectSequenceFlow(bpmnDocument, move.focus.id, parallelGatewayId);
		
		
		if (!"parallelGateway".equals(anchorTag)) {
			LOGGER.info("-new anchor " + move.newAnchor.id + " is not a parallel gateway, need to add");

			//--add a parallel gateway
			String parallelGatewaySplitId = move.newAnchor.id + "-split";
			this.addParallelGatewayAbove(bpmnDocument, parallelGatewaySplitId, move.newAnchor.id);
			Match parallelGateway = $(bpmnDocument).find(attr("id", parallelGatewaySplitId));
			
			//--redirect sequence flow from anchors target to new gateway
			//TODO: Replace the following lines with a call to this.redirectSequenceFlow...
			Match sequenceFlowFromAnchor = $(bpmnDocument).find(attr("sourceRef",move.newAnchor.id));
			String anchorsOriginalTargetId = sequenceFlowFromAnchor.attr("targetRef");
			sequenceFlowFromAnchor.attr("targetRef", parallelGatewaySplitId);
			parallelGateway.append($("bpmn2:incoming").text(sequenceFlowFromAnchor.id()));
			
			//--add a sequence flow on gateway to anchors target
			String parallelGatewayToAnchorsOriginalTargetId = this.addSequenceFlow(bpmnDocument, parallelGatewaySplitId, anchorsOriginalTargetId);
			parallelGateway.append($("bpmn2:outgoing").text(parallelGatewayToAnchorsOriginalTargetId));
			$(bpmnDocument).find(attr("id", anchorsOriginalTargetId)).child("incoming").text(parallelGatewayToAnchorsOriginalTargetId);            
            
			//--anchor=gateway
            anchorId = parallelGatewaySplitId;
		}

		//Anchor is definitely a parallel gateway
		//add sequence flow from anchor (a parallel gateway) to focus
		this.addSequenceFlow(bpmnDocument, anchorId, move.focus.id);
	}
	
	
	
	private void redirectSequenceFlow(Document bpmnDocument, String oldTargetId, String newTargetId) {
		Match oldTarget = $(bpmnDocument).find(attr("id", oldTargetId));
		Match newTarget = $(bpmnDocument).find(attr("id", newTargetId));
		Match sequenceFlow = $(bpmnDocument).find(attr("targetRef",oldTarget.id()));
		LOGGER.info("Redirecting flow "+sequenceFlow.id()+" from " + oldTarget.id() + " to " + newTarget.id());
		sequenceFlow.attr("targetRef", newTarget.id());
		oldTarget.children().remove("incoming");
		newTarget.append($("bpmn2:incoming").text(sequenceFlow.id()));
	}
	
	private void addParallelGatewayAbove(Document bpmnDocument, String gatewayId, String targetNodeId) {
		Match process = $(bpmnDocument).find("process").first();
		Match bpmnPlane = $(bpmnDocument).find("BPMNPlane").first();
		
		Match parallelGateway =$("bpmn2:parallelGateway").attr("id", gatewayId)
                .attr("name", "parallel split")
                .attr("gatewayDirection", "Diverging");
		
		Match target = $(bpmnDocument).find(attr("bpmnElement", targetNodeId)).child("Bounds");
		LOGGER.info("-NODE" + targetNodeId + " found " + target);
		String newY = new Double(Double.parseDouble(target.attr("y")) -100).toString();
		LOGGER.info(target.attr("y") + "->" + newY);
		String newX = new Double(Double.parseDouble(target.attr("x")) + 30).toString();;
		
		Match parallelGatewayShape = $("bpmndi:BPMNShape").attr("id", gatewayId + "-shape")
				.attr("bpmnElement", gatewayId)
				.append($("dc:Bounds").attr("height","50.0").attr("width","50.0").attr("x",newX).attr("y",newY));
		
        process.append(parallelGateway);
        bpmnPlane.append(parallelGatewayShape);
	}
	
	private void addParallelGatewayBelow(Document bpmnDocument, String gatewayId, String targetNodeId) {
		Match process = $(bpmnDocument).find("process").first();
		Match bpmnPlane = $(bpmnDocument).find("BPMNPlane").first();
		
		Match parallelGateway =$("bpmn2:parallelGateway").attr("id", gatewayId)
                .attr("name", "parallel split")
                .attr("gatewayDirection", "Diverging");
		
		Match target = $(bpmnDocument).find(attr("bpmnElement", targetNodeId)).child("Bounds");
		LOGGER.info("+NODE" + targetNodeId + " found " + target);
		
		String newY = new Double(Double.parseDouble(target.attr("y")) + 100).toString();
		String newX = target.attr("x");
		LOGGER.info(target.attr("y") + "->" + newY);
		Match parallelGatewayShape = $("bpmndi:BPMNShape").attr("id", gatewayId + "-shape")
				.attr("bpmnElement", gatewayId)
				.append($("dc:Bounds").attr("height","50.0").attr("width","50.0").attr("x",newX).attr("y",newY));
		
        process.append(parallelGateway);
        bpmnPlane.append(parallelGatewayShape);
	}
	
	private String addSequenceFlow(Document bpmnDocument, String sourceId, String targetId) {
		Match process = $(bpmnDocument).find("process").first();
		Match bpmnPlane = $(bpmnDocument).find("BPMNPlane").first();
		
		String sequenceFlowId = sourceId + "-" + targetId;
		
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
