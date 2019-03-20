package org.versates.ipm.parser;

import org.jpos.iso.ISOException;

/**
 * Classe de exceção verificada para problemas encontrados na tentativa de processamento de arquivos IPM.
 *
 * @author Ramses Vidor
 */
public final class IPMParserException extends Exception {

    private static final long serialVersionUID = -2959592272962443876L;

    /**
     * Constrói uma instância dessa exceção com uma mensagem definida.
     *
     * @param message A mensagem a ser definida para esta exceção
     */
    public IPMParserException(String message) {
        super(message);
    }

    /**
     * Constrói uma instância dessa exceção com a causa raiz definida.
     *
     * @param cause A causa raiz dessa exceção
     */
    public IPMParserException(ISOException cause) {
        super(cause);
    }

    /**
     * Constrói uma instância dessa exceção com uma mensagem e a causa raiz definidas.
     *
     * @param message A mensagem a ser definida para esta exceção
     * @param cause   A causa raiz dessa exceção
     */
    IPMParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
