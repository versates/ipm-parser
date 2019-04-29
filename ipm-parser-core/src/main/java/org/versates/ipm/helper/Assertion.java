package org.versates.ipm.helper;

import static java.text.MessageFormat.format;

/**
 * Pratical class for boolean expressions.
 *
 * @author Ramses Vidor
 */
public enum Assertion {
    SINGLETON;

    private static final Class<RuntimeException> DEFAULT_EXCEPTION = RuntimeException.class;

    /**
     * Verify if the object is not null.
     *
     * @param object    The object to be treated.
     * @param message   The message to be shown if the expressions returns FALSE.
     * @param arguments Bundle parameters message.
     */
    public static void notNull(final Object object, final String message, final Object... arguments) {
        notNull(DEFAULT_EXCEPTION, object, message, arguments);
    }

    /**
     * Verify if the object is not null.
     *
     * @param exceptionClass The exception class to be lunched if the expression returns FALSE.
     *
     * @param object    The object to be tested.
     * @param message   The message to be shown if the expressions returns FALSE.
     * @param arguments Bundle parameters message.
     */
    public static void notNull(final Class<? extends RuntimeException> exceptionClass, final Object object,
                               final String message, final Object... arguments) {
        isTrue(exceptionClass, object != null, message, arguments);
    }

    /**
     * Verify if {@link String} is not empty.
     *
     * @param string    The {@link String} to be tested.
     * @param message   The message to be shown if the expressions returns FALSE.
     * @param arguments Bundle parameters message.
     */
    public static void notEmptyString(final String string, final String message, final Object... arguments) {
        notEmptyString(DEFAULT_EXCEPTION, string, message, arguments);
    }

    /**
     * Verify if {@link String} is not empty.
     *
     * @param exceptionClass The exception class to be lunched if the expression returns FALSE.
     * @param string    The {@link String} to be tested.
     * @param message   The message to be shown if the expressions returns FALSE.
     * @param arguments Bundle parameters message.
     */
    public static void notEmptyString(final Class<? extends RuntimeException> exceptionClass, final String string,
                                      final String message, final Object... arguments) {
        notNull(exceptionClass, string, message, arguments);
        isTrue(exceptionClass, !string.isEmpty(), message, arguments);
    }

    /**
     *Verify if the boolean expression is TRUE.
     *
     * @param condition The boolean expression to be tested.
     * @param message   The message to be shown if the expressions returns FALSE.
     * @param arguments Bundle parameters message.
     */
    public static void isTrue(final boolean condition, final String message, final Object... arguments) {
        isTrue(DEFAULT_EXCEPTION, condition, message, arguments);
    }

    /**
     *Verify if the boolean expression is TRUE.
     *
     * @param exceptionClass The exception class to be lunched if the expression returns FALSE.
     * @param condition The boolean expression to be tested.
     * @param message   The message to be shown if the expressions returns FALSE.
     * @param arguments Bundle parameters message.
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
