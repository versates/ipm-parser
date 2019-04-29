package org.versates.ipm.iso.packager;

import org.jpos.iso.ISOException;

/**
 * Unchecked exception class for configuration not found.
 *
 * @author Ramses Vidor
 */
public class ISOLayoutNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -812742284044553303L;

    /**
     * Constructs an instance of this exception, defining its root cause.
     *
     * @param cause The root cause of this exception
     */
    public ISOLayoutNotFoundException(ISOException cause) {
        super(cause);
    }

}
