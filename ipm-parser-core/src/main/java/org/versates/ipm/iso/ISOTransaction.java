package org.versates.ipm.iso;

import org.versates.ipm.parser.IPMParserException;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Class representing the ISO 8583 message as a financial transaction.
 *
 * @author Ramses Vidor
 */
public class ISOTransaction implements Serializable {

    private static final long serialVersionUID = -1521612733518231608L;
    private static final int PDS_INDEX = 48;

    private final ISOMessage message;
    private ISOTransaction pds;

    /**
     * Constructs a financial transaction message instance.
     *
     * @param message The ISO 8583 message that should be represented as a financial transaction
     * @throws IPMParserException If the ISO 8583 message processing criteria are not obeyed, an IPMParserException is
       launched
     */
    public ISOTransaction(ISOMessage message) throws IPMParserException {
        if (message == null) {
            throw new IPMParserException("ipmparser.transaction.nomessage");
        }

        this.message = message;

        try {
            if (message.hasField(PDS_INDEX)) {
                pds = new ISOTransaction(new ISOMessage((ISOMsg) message.getValue(PDS_INDEX)));
            }
        } catch (ISOException e) {
            throw new IPMParserException("ipmparser.transaction.noprivatedata");
        }
    }

    /**
     * Returns the MTI of the ISO 8583 message.
     *
     * @return the MTI code of the message
     * @throws ISOException If the value of the MTI field is not valid or the field information can not be extracted, a
     * ISOException is thrown
     */
    public String mti() throws ISOException {
        return message.getMTI();
    }

    /**
     * Checks if the field is present in the message.
     *
     * @param index The identification index of the DE field
     * @return TRUE if index is present, FALSE otherwise
     */
    public boolean hasDe(int index) {
        return message.hasField(index);
    }

    /**
     * Checks whether the field is a PDS.
     *
     * @return TRUE if it is a PDS, FALSE otherwise
     */
    public boolean isPds() {
        return pds == null && !isCorrupted();
    }

    /**
     * Checks if ISO 8583 message is corrupted.
     *
     * @return TRUE if the message is corrupted, FALSE otherwise
     */
    public boolean isCorrupted() {
        return message.isCorrupted();
    }

    /**
     * Returns the Exception of a corrupted message.
     *
     * @return a Exception if the message is corrupted, or null if the message is complete
     */
    public Exception error() {
        return message.getError();
    }

    /**
     * Returns the DE field with the given index.
     *
     * @param index The field ID index
     * @return The DE field indicated by the index
     * @throws ISOException If the value of the DE field is not valid or the field information can not be extracted, a
     * ISOException is thrown
     */
    public ISODe de(int index) throws ISOException {
        return (hasDe(index)) ? new ISODe(this, index, message.getValue(index)) : new ISODe(this, index);
    }

    /**
     * Returns the PDS field with the given index.
     *
     * @param index The field ID index
     * @return The PDS field indicated by the index
     * @throws ISOException If the value of the PDS field is not valid or the field information can not be extracted, a
     * ISOException is thrown
     */
    public ISODe pds(int index) throws ISOException {
        return (pds != null) ? pds.de(index) : new ISODe(index);
    }

    /**
     * Returns the message in its original format before byte extraction [].
     *
     * @return the ISO message 8583 in byte []
     * @throws ISOException If an error occurs when converting the message to its original value, an ISOException is thrown
     */
    public byte[] pack() throws ISOException {
        return isCorrupted() ? error().toString().getBytes() : message.pack();
    }

    /**
     * Prints the output of the message content in XML format in the stream defined by the input parameter.
     *
     * @param stream Stream to output the output where the result should be printed
     */
    public void dump(PrintStream stream) {
        stream.println(xml("\t"));
    }

    /**
     * Returns the contents of the ISO 8583 message converted to XML.
     *
     * @return conversion of message content ISO 8583 to XML
     */
    public String xml(String ident) {
        if (StringUtils.isEmpty(ident)) {
            ident = "\t";
        }

        StringBuilder output = new StringBuilder(ident);
        output.append((isPds() && !isCorrupted() ? "<de id=\"" + PDS_INDEX + "\">" : "<message>"));

        for (int i = 0; i <= message.getMaxField(); i++) {
            try {
                output.append(generateXmlOutput(ident, i));
            } catch (ISOException ignore) {
            }
        }

        return output.append("\n").append(ident).append((isPds() ? "</de>" : "</message>")).toString();
    }

    private String generateXmlOutput(String ident, int i) throws ISOException {
        return (i == PDS_INDEX && pds != null) ? convertPdsToXml(ident) : convertDeToXml(ident, i);
    }

    private String convertDeToXml(String ident, int i) throws ISOException {
        return hasDe(i) ? "\n" + ident + de(i).xml(ident) : "";
    }

    private String convertPdsToXml(String ident) {
        return "\n" + pds.xml(ident + ident);
    }

}
