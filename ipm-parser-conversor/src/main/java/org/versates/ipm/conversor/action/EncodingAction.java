package org.versates.ipm.conversor.action;

import org.versates.ipm.iso.packager.ISOLayout;
import org.apache.commons.lang.StringUtils;

/**
 * Command class of encoding definition.
 *
 * @author Ramses Vidor
 */
public class EncodingAction extends Action {

    private static final String EBCDIC = "ebcdic";
    private static final String ASCII = "ascii";

    private ISOLayout layout;

    @Override
    public void execute(String argument) {
        if (StringUtils.isNotEmpty(argument)) {
            if (EBCDIC.equals(argument)) {
                layout = ISOLayout.MASTERCARD_EBCDIC;
            } else if (ASCII.equals(argument)) {
                layout = ISOLayout.MASTERCARD_ASCII;
            }
        }
    }

    /**
     * Return layout ISO 8583 configurated to convertion of IPM file.
     *
     * @return layout ISO 8583 to be utilized for convertion.
     */
    public ISOLayout layout() {
        return layout == null ? ISOLayout.MASTERCARD_EBCDIC : layout;
    }

}
