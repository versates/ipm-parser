package org.versates.ipm.conversor.action;

/**
 * Classe de comandos do cliente.
 *
 * @author Ramses Vidor
 */
public abstract class Action {

    /**
     * Executa o comando solicitado pelo cliente.
     *
     * @param argument Argumento a ser passado para o comando
     */
    public abstract void execute(String argument);

}
