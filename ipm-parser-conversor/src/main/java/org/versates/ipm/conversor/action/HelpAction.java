package org.versates.ipm.conversor.action;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * Execution class of help command.
 *
 * @author Ramses Vidor
 */
public class HelpAction extends Action {

    private static final HelpFormatter FORMATTER = new HelpFormatter();
    private final Options options;

    /**
     * Build an instance of the help command object.
     *
     * @param options The options defined to this client.
     */
    public HelpAction(Options options) {
        this.options = options;
    }

    @Override
    public void execute(String argument) {
        FORMATTER.printHelp("ipm-parser-conversor -[option] argument", options);
    }

}
