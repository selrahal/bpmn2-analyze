package org.jbpm.analyze.tree;

import java.util.LinkedList;
import java.util.List;

public class Node {
	public String id;
	public Integer priority;
	public Node parent;
	public Node dependencyAccordingToPV;
	public List<Node> children = new LinkedList<Node>();
	public Node anchor;
	
	//TODO: separate in/out PVs
	public List<String> processVariables = new LinkedList<String>();
	
	public Type type;
	
	public enum Type {
		//TODO: Add more
		WORK_ITEM, START_EVENT, END_EVENT, GATEWAY;
	}

}
