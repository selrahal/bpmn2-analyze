package org.jbpm.analyze.move.command;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import org.apache.log4j.Logger;
import org.jbpm.analyze.move.AbstractMoveCommand;
import org.jbpm.analyze.move.MoveToParallelGateway;
import org.w3c.dom.Document;


public class MoveCommandDivergingParallelGatewayAnchor extends AbstractMoveCommand {
	private static final Logger LOGGER = Logger.getLogger(MoveCommandDivergingParallelGatewayAnchor.class);
	private final Document bpmnDocument;
	private final MoveToParallelGateway move;
	
	public MoveCommandDivergingParallelGatewayAnchor(Document document, MoveToParallelGateway move) {
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
		
		//Anchor is definitely a parallel gateway
		//add sequence flow from anchor (a parallel gateway) to focus
		this.addSequenceFlow(bpmnDocument, anchorId, move.focus.id);
	}
	
}