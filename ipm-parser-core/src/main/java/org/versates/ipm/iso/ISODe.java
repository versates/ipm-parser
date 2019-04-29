package org.versates.ipm.iso;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representation class of the DE and PDS fields. DE and PDS are fields which belong to a message ISO 8583 and,
 * mandatorily, have an identification index field.
 * @author Ramses Vidor
 */
public class ISODe implements Serializable {

    private static final long serialVersionUID = -6595138637412625384L;
    private final int index;
    private final String value;
    private ISOTransaction transaction;

    /**
     * Build an instance of the DE and PDS field.
     * @param index The field index into the ISO 8583 message
     */
    public ISODe(int index) {
        this(index, "");
    }

    /**
     * Build an instance of the DE and PDS field with already defined value.
     * @param index The field index into the ISO 8583 message.
     * @param value The field value of DE or PDS.
     */
    public ISODe(int index, Object value) {
        this.index = index;
        this.value = (value != null) ? value.toString() : "";
    }

    /**
     * Build an instance of the DE and PDS field linked to an ISO 8583 message with already defined value.
     * @param transaction The ISO 8583 transaction message which the field is linked.
     * @param index       The field index into ISO 8583 message.
     */
    public ISODe(ISOTransaction transaction, int index) {
        this(transaction, index, "");
    }

    /**
     * Build an instance of the DE and PDS field linked to an ISO 8583 message.
     * @param transaction The ISO 8583 transaction message which the field is linked.
     * @param index       The field index into ISO 8583 message.
     * @param value       The DE or PDS field valeu.
     */
    public ISODe(ISOTransaction transaction, int index, Object value) {
        this(index, value);
        this.transaction = transaction;
    }

    /**
     * Extracts subfield from the DE or PDS field.
     *
     * @param startIndex Indicates the initial subfield index.
     * @param endIndex Indicates the final index of the subfield
     * @return the subfield of the DE or PDS field indicated by the indexes.
     * @throws ISOException If the subfield is not valid or the subfield information can not be extracted, an ISOException is
     *       launched.
     */
    public ISODe subfield(int startIndex, int endIndex) throws ISOException {
        if (startIndex < 1) {
            throw new ISOException("ipmparser.transaction.nosuchsubfield");
        }

        return value.length() < endIndex ? new ISODe(-1) : new ISODe(-1, value.substring(--startIndex, endIndex));
    }

    /** Returns the index of the DE or PDS field
     *
     *
     * @return the field index
     */

    public int index() {
        return index;
    }

    /**
     * Returns the value of the DE or PDS field.
     *      
     * @return the value of the field
     */

    public String value() {
        return value;
    }

    /**
     * Returns the value of the DE or PDS field in the Character type for 1-byte flags.
     *
     * @return the value of the field converted to Character
     */

    public Character flag() {
        return empty() ? null : value.charAt(0);
    }

   /**
    * Returns the value of the DE or PDS field converted to Long for cases where the return value is numeric.
    *
    * @return the converted field value to Long
    */

    public Long number() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignore) {
        }

        return 0L;
    }

   /**
    * Returns the value of the DE or PDS field converted to BigDecimal for cases where the return value is monetary.
    * The number of decimal places must be informed for the correct conversion into monetary value. For this method,
    * be passed the DE or PDS field that contains the currency exponent information, indicating the number of houses
    * decimals.
    *
    * @param expoent The currency exponent, indicating the number of decimal places
    * @return the value of the field converted into BigDecimal for monetary representation
    */
    public BigDecimal currency(ISODe expoent) {
        return currency(expoent == null ? 0 : expoent.number().intValue());
    }

    /**
     * Returns the value of the DE or PDS field converted to BigDecimal for cases where the return value is monetary.
     * The number of decimal places must be informed for the correct conversion into monetary value. For this method,
     * be passed the currency exponent value as String, indicating the number of decimal places.
     *
     * @param expoent The currency exponent, indicating the number of decimal places
     * @return the value of the field converted into BigDecimal for monetary representation
     */
    public BigDecimal currency(String expoent) {
        return currency(expoent == null ? 0 : Integer.parseInt(expoent));
    }

    /**
     * Returns the value of the DE or PDS field converted to BigDecimal for cases where the return value is monetary.
     * The number of decimal places must be informed for the correct conversion into monetary value. For this method,
     * be passed the currency exponent value as Integer, indicating the number of decimal places.
     *
     * @param expoent The currency exponent, indicating the number of decimal places
     * @return the value of the field converted into BigDecimal for monetary representation
     */
    public BigDecimal currency(Integer expoent) {
        int scale = expoent == null ? 0 : expoent;

        if (empty()) {
            return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_EVEN);
        }

        double currency = !empty() ? Double.parseDouble(value) : 0d;
        StringBuilder decimal = new StringBuilder("1");

        for (int i = 0; i < scale; i++) {
            decimal.append("0");
        }

        return new BigDecimal((currency / Integer.parseInt(decimal.toString()))).setScale(scale, RoundingMode.HALF_EVEN);
    }

    /**
     * Returns the value of the DE or PDS field converted to Date for cases where the return value is a date.
     *
     * @param format The format represented in the layout of the field in the file in Java format. Examples: For fields where the
     * layout format is equal to AADDMM, the corresponding Java format must be provided, in this case:
     * yyMMdd.
     * <p>
     * ISO 8583 formats and their respective values in Java format: AAMMDD: yyMMdd hhmmss: HHmmss
     * AAMMDDhhmmss: yyMMddHHmmss
     * @return the value of the field converted to Date
     * @throws ISOException If the field value is not valid or the field information can not be extracted, a
     * ISOException is thrown
     */
    public Date date(String format) throws ISOException {
        try {
            return !empty() ? new SimpleDateFormat(format).parse(value) : null;
        } catch (ParseException e) {
            throw new ISOException("ipmparser.transaction.field.invaliddate");
        }
    }

   /**
    * Returns the length of the field value.
    *
    * @return the length of the field value
    */
    public int length() {
        return value.length();
    }

   /**
    * Checks whether the field contains any value, returned TRUE if it is empty.
    *
    * @return TRUE if the field is empty, FALSE if there is any value, even spaces
    */
    public boolean empty() {
        return length() <= 0;
    }

  /**
   * Returns an XML tag that represents the field information and its contents.
   *
   * @param ident XML tag indentation pattern
   * @return XML tag representing the field
   */
    public String xml(String ident) {
        if (StringUtils.isEmpty(ident) || "\t\t".equals(ident)) {
            ident = "\t";
        }

        return MessageFormat.format(getXmlTag(), ident, index, value, belongsToPds() ? "pds" : "de");
    }

  /**
   * Checks whether the field is a member of a field of type PDS.
   *
   * @return TRUE if the field belongs to a PDS field, otherwise returns FALSE
   */
    public boolean belongsToPds() {
        return transaction != null && transaction.isPds();
    }

    /**
     * Returns the value of the field by default.
     *
     * @return field value
     */
    @Override
    public String toString() {
        return value;
    }

    private String getXmlTag() {
        return (index == 0) ? "{0}<mti>{2}</mti>" : "{0}<{3} id=\"{1}\">{2}</{3}>";
    }

}
