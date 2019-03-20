package org.versates.ipm.iso.packager;

import org.jpos.iso.ISOException;

/**
 * Classe de exceção não verificada para configuração não localizada.
 *
 * @author Ramses Vidor
 */
public class ISOLayoutNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -812742284044553303L;

    /**
     * Constrói uma instância dessa exceção, definindo sua causa raiz.
     *
     * @param cause A causa raiz dessa exceção
     */
    public ISOLayoutNotFoundException(ISOException cause) {
        super(cause);
    }

}
