package org.versates.ipm.conversor.action;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * Classe de execução do comando de Ajuda.
 *
 * @author Ramses Vidor
 */
public class HelpAction extends Action {

    private static final HelpFormatter FORMATTER = new HelpFormatter();
    private final Options options;

    /**
     * Constrói uma instância do objeto de comando de ajuda.
     *
     * @param options As opções definidas para este cliente
     */
    public HelpAction(Options options) {
        this.options = options;
    }

    @Override
    public void execute(String argument) {
        FORMATTER.printHelp("ipm-parser-conversor -[option] argument", options);
    }

}
