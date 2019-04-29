package org.versates.ipm.parser;

import org.versates.ipm.iso.ISOMessage;
import org.versates.ipm.iso.packager.ISOLayout;
import org.versates.ipm.iso.packager.ISOLayoutNotFoundException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.GenericPackager;

import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * Main processing class of IPM files.
 *
 * @author Ramses Vidor
 */
public abstract class IPMFileParser implements Serializable {

    private static final long serialVersionUID = 5695555652184529809L;

    private final ISOLayout layout;
    private final ISOPackager packager;

    /**
     * Processes the contents of the IPM file in <code> byte [] </ code>, generating a list of ISOMessage objects.
     *
     * @param bytes The contents of the IPM file
     * @return The ISOMessage list extracted from the IPM file
     * @throws IPMParserException If there is any failure in the message extraction process of the IPM file, an exception
     * IPMParserException is thrown
     */
    abstract public List<ISOMessage> parse(byte[] bytes) throws IPMParserException;

    /**
     * Constructs an instance of this object for processing files in the defined layout.
     *
     * @param layout The layout of the file to be processed
     */
    public IPMFileParser(ISOLayout layout) {
        this.layout = layout;
        packager = createPackager();
    }

    /**
     * Returns the layout configured for IPM file processing.
     *
     * @return ISO 8583 layout used
     */
    public ISOLayout getLayout() {
        return layout;
    }

    /**
     * Returns the packager used for extracting ISO 8583 messages.
     *
     * @return the packager used for extraction
     */
    public ISOPackager getPackager() {
        return packager;
    }

    /**
     * Creates a parser based on the ISO 8583 layout defined.
     *
     * @param Layout ISO 8583 layout for parser creation
     * @return a parser instance for extracting ISO messages 8583
     */
    public static IPMFileParser createFileParser(ISOLayout layout) {
        switch (layout) {
            case MASTERCARD_ASCII:
                return createMasterCardPreEditFileParser();
            case MASTERCARD_EBCDIC:
                return createMasterCardEbcdicFileParser();
            default:
                return createGenericFileParser();
        }
    }

    /**
     * Creates a parser configured with ISO 8583 layout for IPM MasterCard files, ASCII encoding (
     * <code> ISOLayout.MASTERCARD_ASCII </ code>).
     *
     * @return an parser instance for extracting ISO 8583 MasterCard ASCII messages
     */
    public static IPMFileParser createMasterCardPreEditFileParser() {
        return new IPMMasterCardPreEditFileParser();
    }

    /**
     * Creates a parser configured with ISO 8583 layout for IPM MasterCard files, ASCII encoding (
     * <code> ISOLayout.MASTERCARD_EBCDIC </ code>).
     *
     * @return an instance of parser for extracting ISO 8583 messages MasterCard EBCDIC
     */
    public static IPMFileParser createMasterCardEbcdicFileParser() {
        return new IPMMasterCardEbcdicFileParser();
    }

    /**
     * Creates a parser configured with ISO 8583 layout for generic IPM files (<code> ISOLayout.GENERIC </ code>).
     *
     * @return an parser instance for extracting ISO 8583 MasterCard ASCII messages
     */
    public static IPMFileParser createGenericFileParser() {
        return new IPMGenericFileParser();
    }

    /**
     * Creates an ISO message for the packager in use.
     *
     * @return The ISOMessage created for the packager
     */
    protected ISOMessage createMessage() {
        final ISOMessage message = new ISOMessage();
        message.setPackager(getPackager());
        return message;
    }

    /**
     * Fills standard corrupted message information.
     *
     * @param message The corrupted message
     * @param and The thrown exception in an attempt to extract the corrupted message
     * @return An ISOMsg instance cloned from the corrupted message
     */
    protected ISOMsg feedCorruptedMessage(ISOMessage message, Exception e) {
        try {
            message.set(1, MessageFormat.format("corrupted [{0}]", e.getMessage()));
            message.setError(e);
        } catch (ISOException ignore) {
        }

        return (ISOMsg) message.clone();
    }

    private ISOPackager createPackager() {
        try {
            InputStream configuration = getClass().getClassLoader().getResourceAsStream(layout.getConfigurationPath());

            if (configuration == null) {
                configuration = getClass().getClassLoader().getResourceAsStream("/" + layout.getConfigurationPath());
            }

            return new GenericPackager(configuration);
        } catch (ISOException e) {
            throw new ISOLayoutNotFoundException(e);
        }
    }

}
