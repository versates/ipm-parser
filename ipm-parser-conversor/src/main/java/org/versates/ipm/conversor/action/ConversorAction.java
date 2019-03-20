package org.versates.ipm.conversor.action;

import org.versates.ipm.conversor.Run;
import org.versates.ipm.helper.XMLConversor;

/**
 * Classe do comando de conversão de arquivo IPM para XML.
 *
 * @author Ramses Vidor
 */
public class ConversorAction extends Action {

    /**
     * Constrói uma instância desse objeto.
     */
    public ConversorAction() {
    }

    @Override
    public void execute(String argument) {
        XMLConversor.dump(argument, Run.getLayout());
    }

}
