package org.versates.ipm.conversor.action;

/**
 * Command class of the client.
 *
 * @author Ramses Vidor
 */
public abstract class Action {

    /**
     * Execute the command requested by the client.
     *
     * @param argument Argument to be passed for the command.
     */
    public abstract void execute(String argument);

}
