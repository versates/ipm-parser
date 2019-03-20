package org.versates.ipm.conversor.action;

import org.versates.ipm.iso.packager.ISOLayout;
import org.apache.commons.lang.StringUtils;

/**
 * Classe de comando de definição de encoding.
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
     * Retorna o layout ISO 8583 configurado para a conversão do arquivo IPM.
     *
     * @return layout ISO 8583 a ser utilizado para conversão
     */
    public ISOLayout layout() {
        return layout == null ? ISOLayout.MASTERCARD_EBCDIC : layout;
    }

}
