package org.versates.ipm;

import org.versates.ipm.helper.XMLConversor;
import org.versates.ipm.iso.ISOMessage;
import org.versates.ipm.iso.ISOTransaction;
import org.versates.ipm.parser.IPMFileParser;
import org.versates.ipm.parser.IPMParserException;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * File representation class of Integrated Products Messages (IPM). Compensation by means of the
 * Integrated Product Messages (IPM) is the movement of information pertaining to the transaction sent by the member to the
 * Compensation system. Each IPM file contains an ISO 8583 message group, which presents this information
 * of transaction.
 *
 * @author Ramses Vidor
 */
public class IPMFile implements Serializable {

    private static final long serialVersionUID = -3141984967806646322L;
    private static final int FUNCTION_CODE_DE = 24;
    private static final int CICLE_ID_DE = 63;
    private static final String HEADER_MTI = "1644";
    private static final String FUNCTION_CODE_HEADER = "697";
    private static final String FUNCTION_CODE_FOOTER = "695";

    private ISOTransaction header;
    private ISOTransaction footer;
    private final String name;
    private final List<ISOTransaction> transactions = new ArrayList<ISOTransaction>();

    /**
     * Constructs an IPM file instance.
     *
     * @param name The name of the IPM file
     * @param bytes IPM file contents in bytes
     * @param parser The parser to be used to interpret the IPM file. It must be compatible with coding and
     * IPM file layout to be processed
     * @throws IPMParserException Exception thrown if the IPM file does not match the processing criteria.
     */
    public IPMFile(String name, byte[] bytes, IPMFileParser parser) throws IPMParserException {
        if (StringUtils.isEmpty(name)) {
            throw new IPMParserException("ipmparser.file.invalidfilename");
        }

        this.name = name;
        extractTransactions(bytes, parser);
    }

    /**
     * Returns the name of the physical IPM file.
     *
     * @return the IPM file name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the file header message (the first ISO message 8583 displayed in the file), which
     * file information. The file header is identified by MTI 1644, function code (DE 24)
     * 697.
     *
     * @return IPM file header
     */
    public ISOTransaction header() {
        return header;
    }

    /**
     * Returns the file footer message (last message ISO 8583 presented in the file), which presents
     * file information. The file footer is identified by the MTI 1644, function code (DE 24)
     * 695.
     *
     * @return IPM file footer
     */
    public ISOTransaction footer() {
        return footer;
    }

    /**
     * Returns the total of ISO 8583 messages contained in the IPM file, not counting the header messages and
     * footer.
     *
     * @return total of ISO 8583 messages contained in the IPM file
     */
    public int countTransactions() {
        return transactions.size();
    }

    /**
     * Returns ISO 8583 messages contained in the IPM file, with the exception of header and footer messages.
     *
     * @return ISO 8583 messages contained in the IPM file
     */
    public List<ISOTransaction> transactions() {
        return transactions;
    }

    /**
     * Prints the output of the file contents in XML format in the stream defined by the input parameter.
     *
     * @param printStream Stream to output the output where the result should be printed
     */
    public void dump(PrintStream printStream) {
        printStream.println(xml());
    }

    /**
     * Returns the contents of the converted IPM file for XML.
     *
     * @return conversion of IPM file contents to XML
     */
    public String xml() {
        StringBuilder output = new StringBuilder(openXmlTag() + infoAsXml(header, "header"));

        for (ISOTransaction transaction : transactions) {
            output.append("\n").append(transaction.xml("\t"));
        }

        return output.append("\n").append(infoAsXml(footer, "footer")).append(closeXmlTag()).toString();
    }

    private void extractTransactions(byte[] bytes, IPMFileParser parser) throws IPMParserException {
        for (ISOMessage message : parser.parse(bytes)) {
            ISOTransaction tx = new ISOTransaction(message);
            if (isHeader(tx)) {
                header = tx;
            } else if (isFooter(tx)) {
                footer = tx;
            } else if (isValidTransaction(tx) || tx.isCorrupted()) {
                transactions.add(tx);
            }
        }

        if (header == null) {
            throw new IPMParserException("ipmparser.file.noheader");
        }
    }

    private boolean isValidTransaction(ISOTransaction tx) {
        return tx.hasDe(CICLE_ID_DE);
    }

    private boolean isHeader(ISOTransaction tx) throws IPMParserException {
        return isHeaderMessage(tx, FUNCTION_CODE_HEADER);
    }

    private boolean isFooter(ISOTransaction tx) throws IPMParserException {
        return isHeaderMessage(tx, FUNCTION_CODE_FOOTER);
    }

    private boolean isHeaderMessage(ISOTransaction tx, String functionCode) throws IPMParserException {
        try {
            return HEADER_MTI.equals(tx.mti()) && functionCode.equals(tx.de(FUNCTION_CODE_DE).value());
        } catch (ISOException e) {
            throw new IPMParserException(e);
        }
    }

    private String infoAsXml(ISOTransaction info, String type) {
        return info.xml("\t").replaceAll("<(/)?message>", "<$1" + type + ">");
    }

    private String openXmlTag() {
        return format(XMLConversor.HEADER_TAG, name(), countTransactions());
    }

    private String closeXmlTag() {
        return XMLConversor.FOOTER_TAG;
    }

}
