package org.versates.ipm.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

/**
 * Class representing an ISO 8583 message.
 *
 * @author Ramses Vidor
 */
public class ISOMessage extends ISOMsg {

    private Exception error;

    /**
     * Constructs an ISO 8583 message instance.
     */
    public ISOMessage() {
        super();
    }

   /**
    * Constructs an ISO 8583 message instance by copying field values from a message of type ISOMsg.
    *
    * @param message The ISOMsg object to be copied
    */
    public ISOMessage(ISOMsg message) {
        try {
            for (int i = -1; i <= message.getMaxField(); i++) {
                if (message.hasField(i)) {
                    set(message.getComponent(i));
                    setPackager(message.getPackager());
                }
            }
        } catch (ISOException ignore) {
        }
    }

    /**
     * In the case of corrupted messages, returns the Exception that contains the error encountered while processing the ISO message
     * 8583. Returns null if the message is not corrupted.
     *
     * @return A Exception containing error information in corrupted message processing
     */
    public Exception getError() {
        return error;
    }

    /**
     * Sets the Exception that contains the error encountered while processing the message ISO 8583 in case of messages
     * corrupted.
     *
     * @param error The Exception containing error information in the processing of the corrupted message
     * @return chaining method, returning the instance of this object
     */
    public ISOMessage setError(Exception error) {
        this.error = error;
        return this;
    }

    /**
     * Checks if the message is corrupted.
     *
     * @return TRUE if the message contains any corrupted field, or FALSE otherwise
     */
    public boolean isCorrupted() {
        return error != null;
    }

}
