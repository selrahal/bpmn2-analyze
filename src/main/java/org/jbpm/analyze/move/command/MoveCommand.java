package org.jbpm.analyze.move.command;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.log4j.Logger;
import org.jbpm.analyze.move.AbstractMoveCommand;
import org.jbpm.analyze.move.Move;
import org.jbpm.analyze.tree.Node;
import org.jbpm.analyze.util.BPMN2DocumentUtil;
import org.joox.Match;
import org.w3c.dom.Document;


public class MoveCommand extends AbstractMoveCommand {
	private static final Logger LOGGER = Logger.getLogger(MoveCommand.class);
	private final Document bpmnDocument;
	private final Move move;
	
	public MoveCommand(Document document, Move move) {
		this.bpmnDocument = document;
		this.move = move;
	}
	
	
	public void execute() {
		LOGGER.info("Executing command for " + move);
		String anchorId = move.newAnchor.id;
		
		//create parallelconverge
		String parallelGatewayId = move.newAnchor.id + "-join";
		this.addParallelConvergingGateway(bpmnDocument, parallelGatewayId, move.focus.id);
		
		//--redirect sequence flow from "focus target" to new gateway
		String focusTargetId = $(bpmnDocument).find(attr("sourceRef",move.focus.id)).attr("targetRef");
		this.redirectSequenceFlow(bpmnDocument, focusTargetId, parallelGatewayId);
		
		this.addSequenceFlow(bpmnDocument, parallelGatewayId, focusTargetId);
		
		this.redirectSequenceFlow(bpmnDocument, move.focus.id, parallelGatewayId);

		//--add a parallel gateway
		String parallelGatewaySplitId = move.newAnchor.id + "-split";
		this.addParallelDivergingGateway(bpmnDocument, parallelGatewaySplitId, move.newAnchor.id);
		Match parallelGateway = $(bpmnDocument).find(attr("id", parallelGatewaySplitId));
				
		//--redirect sequence flow from anchors target to new gateway
		//TODO: Replace the following lines with a call to this.redirectSequenceFlow...
		Match sequenceFlowFromAnchor = $(bpmnDocument).find(attr("sourceRef",move.newAnchor.id));
		String anchorsOriginalTargetId = sequenceFlowFromAnchor.attr("targetRef");
		sequenceFlowFromAnchor.attr("targetRef", parallelGatewaySplitId);
		parallelGateway.append($("bpmn2:incoming").text(sequenceFlowFromAnchor.id()));
				
		//--add a sequence flow on gateway to anchors target
		String parallelGatewayToAnchorsOriginalTargetId = this.addSequenceFlow(bpmnDocument, parallelGatewaySplitId, anchorsOriginalTargetId);
	           
		//--anchor=gateway
        anchorId = parallelGatewaySplitId;

		//Anchor is definitely a parallel gateway
		//add sequence flow from anchor (a parallel gateway) to focus
		this.addSequenceFlow(bpmnDocument, anchorId, move.focus.id);
	}
	
}