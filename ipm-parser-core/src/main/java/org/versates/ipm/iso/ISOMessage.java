package org.versates.ipm.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

/**
 * Classe que representa uma mensagem ISO 8583.
 *
 * @author Ramses Vidor
 */
public class ISOMessage extends ISOMsg {

    private Exception error;

    /**
     * Constrói uma instância de mensagem ISO 8583.
     */
    public ISOMessage() {
        super();
    }

    /**
     * Constrói uma instaância de mensagem ISO 8583, copiando os valores dos campos de uma mensagem do tipo ISOMsg.
     *
     * @param message O objeto ISOMsg a ser copiado
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
     * No caso de mensagens corrompidas, retorna a Exception que contém o erro encontrado ao processar a mensagem ISO
     * 8583. Retorna null se a mensagem não for corrompida.
     *
     * @return A Exception contendo informações do erro no processamento da mensagem corrompida
     */
    public Exception getError() {
        return error;
    }

    /**
     * Define a Exception que contém o erro encontrado ao processar a mensagem ISO 8583 no caso de mensagens
     * corrompidas.
     *
     * @param error A Exception contendo informações do erro no processamento da mensagem corrompida
     * @return encadeamento do método, retornando a instância deste objeto
     */
    public ISOMessage setError(Exception error) {
        this.error = error;
        return this;
    }

    /**
     * Verifica se a mensagem está corrompida.
     *
     * @return TRUE se a mensagem contém qualquer campo corrompido, ou FALSE caso contrário
     */
    public boolean isCorrupted() {
        return error != null;
    }

}
