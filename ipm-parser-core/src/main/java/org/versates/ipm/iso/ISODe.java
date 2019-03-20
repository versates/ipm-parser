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
 * Classe de representação de campos DE e PDS. Campos DE e PDS são campos que pertencem à uma mensagem ISO 8583 e,
 * obrigatorizamente, possuem um índice de identificação do campo.
 *
 * @author Ramses Vidor
 */
public class ISODe implements Serializable {

    private static final long serialVersionUID = -6595138637412625384L;
    private final int index;
    private final String value;
    private ISOTransaction transaction;

    /**
     * Constrói uma instância de campo DE ou PDS.
     *
     * @param index O índice do campo na mensagem ISO 8583
     */
    public ISODe(int index) {
        this(index, "");
    }

    /**
     * Constrói uma instância de campo DE ou PDS com valor já definido.
     *
     * @param index O índice do campo na mensagem ISO 8583
     * @param value O valor do campo DE ou PDS
     */
    public ISODe(int index, Object value) {
        this.index = index;
        this.value = (value != null) ? value.toString() : "";
    }

    /**
     * Constrói uma instância de campo DE ou PDS vinculada à uma mensagem ISO 8583 com valor já definido.
     *
     * @param transaction A mensagem de transação ISO 8583 a qual o campo está vinculado
     * @param index       O índice do campo na mensagem ISO 8583
     */
    public ISODe(ISOTransaction transaction, int index) {
        this(transaction, index, "");
    }

    /**
     * Constrói uma instância de campo DE ou PDS vinculada à uma mensagem ISO 8583.
     *
     * @param transaction A mensagem de transação ISO 8583 a qual o campo está vinculado
     * @param index       O índice do campo na mensagem ISO 8583
     * @param value       O valor do campo DE ou PDS
     */
    public ISODe(ISOTransaction transaction, int index, Object value) {
        this(index, value);
        this.transaction = transaction;
    }

    /**
     * Extrai um subcampo do campo DE ou PDS.
     *
     * @param startIndex Indica o índice inicial do subcampo
     * @param endIndex   Indica o índice final do subcampo
     * @return o subcampo do campo DE ou PDS indicado pelos índices
     * @throws ISOException Se o subcampo não é válido ou não for possível extrair as informações do subcampo, uma ISOException é
     *                      lançada
     */
    public ISODe subfield(int startIndex, int endIndex) throws ISOException {
        if (startIndex < 1) {
            throw new ISOException("ipmparser.transaction.nosuchsubfield");
        }

        return value.length() < endIndex ? new ISODe(-1) : new ISODe(-1, value.substring(--startIndex, endIndex));
    }

    /**
     * Retorna o índice do campo DE ou PDS
     *
     * @return o índice do campo
     */
    public int index() {
        return index;
    }

    /**
     * Retorna o valor do campo DE ou PDS.
     *
     * @return o valor do campo
     */
    public String value() {
        return value;
    }

    /**
     * Retorna o valor do campo DE ou PDS no tipo Character para flags de 1 byte.
     *
     * @return o valor do campo convertido para Character
     */
    public Character flag() {
        return empty() ? null : value.charAt(0);
    }

    /**
     * Retorna o valor do campo DE ou PDS convertido para Long para casos onde o valor do retorno seja numérico.
     *
     * @return o valor do campo convertido para Long
     */
    public Long number() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignore) {
        }

        return 0L;
    }

    /**
     * Retorna o valor do campo DE ou PDS convertido para BigDecimal para casos onde o valor do retorno seja monetário.
     * O número de casas decimais deve ser informado para a correta conversão em valor monetário. Para este método, deve
     * ser passado o campo DE ou PDS que contenha a informação de expoente de moeda, indicando o número de casas
     * decimais.
     *
     * @param expoent O expoente de moeda, indicando o número de casas decimais
     * @return o valor do campo convertido em BigDecimal para representação monetária
     */
    public BigDecimal currency(ISODe expoent) {
        return currency(expoent == null ? 0 : expoent.number().intValue());
    }

    /**
     * Retorna o valor do campo DE ou PDS convertido para BigDecimal para casos onde o valor do retorno seja monetário.
     * O número de casas decimais deve ser informado para a correta conversão em valor monetário. Para este método, deve
     * ser passado o valor de expoente de moeda como String, indicando o número de casas decimais.
     *
     * @param expoent O expoente de moeda, indicando o número de casas decimais
     * @return o valor do campo convertido em BigDecimal para representação monetária
     */
    public BigDecimal currency(String expoent) {
        return currency(expoent == null ? 0 : Integer.parseInt(expoent));
    }

    /**
     * Retorna o valor do campo DE ou PDS convertido para BigDecimal para casos onde o valor do retorno seja monetário.
     * O número de casas decimais deve ser informado para a correta conversão em valor monetário. Para este método, deve
     * ser passado o valor de expoente de moeda como Integer, indicando o número de casas decimais.
     *
     * @param expoent O expoente de moeda, indicando o número de casas decimais
     * @return o valor do campo convertido em BigDecimal para representação monetária
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
     * Retorna o valor do campo DE ou PDS convertido para Date para casos onde o valor do retorno seja uma data.
     *
     * @param format O formato representado no layout do campo no arquivo no formato Java. Exemplos: Para campos onde o
     *               formato do layout seja igual a AADDMM, deve ser fornecido o formato Java correspondente, nesse caso:
     *               yyMMdd.
     *               <p>
     *               Formatos ISO 8583 e seus respectivos valores em formato Java: AAMMDD: yyMMdd hhmmss: HHmmss
     *               AAMMDDhhmmss: yyMMddHHmmss
     * @return o valor do campo convertido para Date
     * @throws ISOException Se o valor do campo não é válido ou não for possível extrair as informações do campo, uma
     *                      ISOException é lançada
     */
    public Date date(String format) throws ISOException {
        try {
            return !empty() ? new SimpleDateFormat(format).parse(value) : null;
        } catch (ParseException e) {
            throw new ISOException("ipmparser.transaction.field.invaliddate");
        }
    }

    /**
     * Retorna o comprimento do valor do campo.
     *
     * @return o comprimento do valor do campo
     */
    public int length() {
        return value.length();
    }

    /**
     * Verifica se o campo contém algum valor, retornado TRUE se for vazio.
     *
     * @return TRUE se o campo for vazio, FALSE se houver qualquer valor, mesmo espaços
     */
    public boolean empty() {
        return length() <= 0;
    }

    /**
     * Retorna uma tag XML que representado as informações do campo e seu conteúdo.
     *
     * @param ident Padrão de identação da tag XML
     * @return tag XML representando o campo
     */
    public String xml(String ident) {
        if (StringUtils.isEmpty(ident) || "\t\t".equals(ident)) {
            ident = "\t";
        }

        return MessageFormat.format(getXmlTag(), ident, index, value, belongsToPds() ? "pds" : "de");
    }

    /**
     * Verifica se o campo é membro de um campo do tipo PDS.
     *
     * @return TRUE se o campo pertence a um campo PDS, caso contrário, retorna FALSE
     */
    public boolean belongsToPds() {
        return transaction != null && transaction.isPds();
    }

    /**
     * Retorna o valor do campo por padrão.
     *
     * @return valor do campo
     */
    @Override
    public String toString() {
        return value;
    }

    private String getXmlTag() {
        return (index == 0) ? "{0}<mti>{2}</mti>" : "{0}<{3} id=\"{1}\">{2}</{3}>";
    }

}
