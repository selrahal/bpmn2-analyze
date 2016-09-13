package org.jbpm.analyze.util;

import static org.joox.JOOX.$;
import static org.joox.JOOX.attr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.joox.Match;
import org.w3c.dom.Document;

public class BPMN2DocumentUtil {
	private static final Logger LOG = Logger.getLogger(BPMN2DocumentUtil.class);
	
	/**
	 *  This method will return the child element(s) of a BPMN2 node by following all outgoing
	 *  sequence flows from the provided node.
	 * 
	 * @param bpmnDocument to parse
	 * @param Id of the parent element
	 * @return List of children element ids
	 */
	public static List<String> findNextElements(Document bpmnDocument, String rootId) {
		Match root = $(bpmnDocument).find(attr("id", rootId)).first();
		List<Match> childOutgoingFlows = root.children("outgoing").each();
		List<String> idsOfChildren = new LinkedList<String>();
		for (Match match : childOutgoingFlows) {
			String nodeId = $(bpmnDocument).find(attr("id",match.text())).attr("targetRef");
			LOG.trace("Found child of " + root.attr("id") + " " + nodeId);
			idsOfChildren.add(nodeId);
		}
		
		return idsOfChildren;
	}
	
	public static String getStartNode(Document bpmnDocument) {
		return $(bpmnDocument).find("startEvent").first().id();
	}
	
	public static List<String> getProcessVariables(Document bpmnDocument, String elementId) {
		List<String> toReturn = new LinkedList<>();
		for (Match sourceRef : $(bpmnDocument).find(attr("id", elementId)).find("sourceRef").each()) {
			toReturn.add(sourceRef.text());
		}
		return toReturn;
	}

	public static String getTag(Document bpmnDocument, String id) {
		return $(bpmnDocument).find(attr("id", id)).tag();
	}
	

    public static File writeFile(final Document input, final File output) {
        final StreamResult result = new StreamResult(new StringWriter());

        format(new DOMSource(input), result);

        FileWriter fw = null;
        try {
                fw = new FileWriter(output);
            fw.write(result.getWriter().toString());
        } catch (final IOException ioEx) {
            System.err.println("Problem writing XML to file." + ioEx);
        } finally {
                if (fw != null ) {
                                try {
                                        fw.close();
                                } catch (IOException e) {
                                	System.err.println("Problem closing the file." + e);
                                }
                }
        }
        return output;
    }
    

        public static void format(final Source input, final Result output) {
            try {
            	final TransformerFactory transformerFactory = TransformerFactory.newInstance();
                // Use an identity transformation to write the source to the result.
                final Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                transformer.transform(input, output);
            } catch (final Exception ex) {
                System.err.println("Problem formatting DOM representation." + ex);
            }
        }
        
        


}
