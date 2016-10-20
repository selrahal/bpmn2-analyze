package org.jbpm.analyze.validator;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;

import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.common.services.shared.validation.model.ValidationMessage.Level;
import org.jbpm.analyze.main.JbpmAnalyze;
import org.jbpm.analyze.move.AbstractMove;
import org.jbpm.analyze.tree.Hints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Stateless
public class AnalyzeProcessValidatorHelper implements BuildValidationHelper{
	private static final Logger LOG = LoggerFactory.getLogger(AnalyzeProcessValidatorHelper.class);
	
	public AnalyzeProcessValidatorHelper() {
		LOG.info("Generating new analyze process validator helper");
	}
	
    public boolean accepts( final Path path ) {
    	boolean accepts = path.getFileName().endsWith(".bpmn2") || 
    			          path.getFileName().endsWith(".bpmn");
    	return accepts;
    }

    public List<ValidationMessage> validate( final Path path ){ 
		LOG.info("Validating " + path.toString());
		List<ValidationMessage> messages = new LinkedList<>();
		try { 
			Hints hints = JbpmAnalyze.analyze(Paths.convert(path).toFile());
			for (AbstractMove hint : hints.getHints()) {
				LOG.info("Adding " + hint);
				ValidationMessage toAdd = new ValidationMessage();
				toAdd.setLevel(Level.INFO);
				toAdd.setPath(path);
				toAdd.setText("ANALYZE: " + hint.toString());
				messages.add(toAdd);
			}
		} catch (Throwable e) {
			LOG.error("Error processing " + path.getFileName(), e);
		}
		
		return messages;
	}


}
