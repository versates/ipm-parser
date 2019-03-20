package org.versates.ipm.helper;

import static java.text.MessageFormat.format;

/**
 * Classe utilitária para expressões booleanas.
 *
 * @author Ramses Vidor
 */
public enum Assertion {
    SINGLETON;

    private static final Class<RuntimeException> DEFAULT_EXCEPTION = RuntimeException.class;

    /**
     * Verifica se o objeto não é nulo.
     *
     * @param object    O objeto a ser testado
     * @param message   A mensagem a ser exibida se a expressão retornar FALSE
     * @param arguments Parâmetros da mensagem de bundle
     */
    public static void notNull(final Object object, final String message, final Object... arguments) {
        notNull(DEFAULT_EXCEPTION, object, message, arguments);
    }

    /**
     * Verifica se o objeto não é nulo.
     *
     * @param exceptionClass A classe de exceção a ser lançada caso a expressão retorne FALSE
     * @param object         O objeto a ser testado
     * @param message        A mensagem a ser exibida se a expressão retornar FALSE
     * @param arguments      Parâmetros da mensagem de bundle
     */
    public static void notNull(final Class<? extends RuntimeException> exceptionClass, final Object object,
                               final String message, final Object... arguments) {
        isTrue(exceptionClass, object != null, message, arguments);
    }

    /**
     * Verifica se a {@link String} não é vazia.
     *
     * @param string    A {@link String} a ser testada
     * @param message   A mensagem a ser apresentada caso a expressão retorne FALSE
     * @param arguments Parâmetros da mensagem de bundle
     */
    public static void notEmptyString(final String string, final String message, final Object... arguments) {
        notEmptyString(DEFAULT_EXCEPTION, string, message, arguments);
    }

    /**
     * Verifica se a {@link String} não é vazia.
     *
     * @param exceptionClass A classe de exceção a ser lançada caso a expressão retorne FALSE
     * @param string         A {@link String} a ser testada
     * @param message        A mensagem a ser apresentada caso a expressão retorne FALSE
     * @param arguments      Parâmetros da mensagem de bundle
     */
    public static void notEmptyString(final Class<? extends RuntimeException> exceptionClass, final String string,
                                      final String message, final Object... arguments) {
        notNull(exceptionClass, string, message, arguments);
        isTrue(exceptionClass, !string.isEmpty(), message, arguments);
    }

    /**
     * Verifica se a expressão boolean é verdadeira.
     *
     * @param condition A expressão booleana a ser testada
     * @param message   A mensagem a ser apresentada caso a expressão retorne FALSE
     * @param arguments Parâmetros da mensagem de bundle
     */
    public static void isTrue(final boolean condition, final String message, final Object... arguments) {
        isTrue(DEFAULT_EXCEPTION, condition, message, arguments);
    }

    /**
     * Verifica se a expressão boolean é verdadeira.
     *
     * @param exceptionClass A classe de exceção a ser lançada caso a expressão retorne FALSE
     * @param condition      A expressão booleana a ser testada
     * @param message        A mensagem a ser apresentada caso a expressão retorne FALSE
     * @param arguments      Parâmetros da mensagem de bundle
     */
    public static void isTrue(final Class<? extends RuntimeException> exceptionClass, final boolean condition,
                              final String message, final Object... arguments) {
        if (!condition) {
            RuntimeException exception;

            try {
                exception = exceptionClass.getConstructor(String.class).newInstance(format(message, arguments));
            } catch (final Exception e) {
                exception = new RuntimeException(format(message, arguments), e);
            }

            throw exception;
        }
    }

}
