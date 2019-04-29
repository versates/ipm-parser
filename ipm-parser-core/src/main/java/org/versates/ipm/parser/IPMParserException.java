package org.versates.ipm.parser;

import org.jpos.iso.ISOException;

/**
 * Expected exception class for problems encountered when trying to process IPM files.
 *
 * @author Ramses Vidor
 */
public final class IPMParserException extends Exception {

    private static final long serialVersionUID = -2959592272962443876L;

    /**
     * Constructs an instance of this exception with a defined message.
     *
     * @param message The message to be set for this exception
     */
    public IPMParserException(String message) {
        super(message);
    }

    /**
     * Constructs an instance of this exception with the root cause defined.
     *
     * @param cause The root cause of this exception
     */
    public IPMParserException(ISOException cause) {
        super(cause);
    }

    /**
     * Constructs an instance of this exception with a message and root cause defined.
     *
     * @param message The message to be set for this exception
     * @param cause The root cause of this exception
     */
    IPMParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
