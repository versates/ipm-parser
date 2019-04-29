package org.versates.ipm.conversor.action;

import org.versates.ipm.conversor.Run;
import org.versates.ipm.helper.XMLConversor;

/**
 * Conversion command class from file IPM to XML.
 *
 * @author Ramses Vidor
 */
public class ConversorAction extends Action {

    /**
     * Build an instance of this object.
     */
    public ConversorAction() {
    }

    @Override
    public void execute(String argument) {
        XMLConversor.dump(argument, Run.getLayout());
    }

}
