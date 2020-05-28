package org.r.framework.thrift.client.core.exception;

/**
 * date 20-5-27 上午10:27
 *
 * @author casper
 **/
public class TransportFailException extends Exception {


    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public TransportFailException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public TransportFailException(String message) {
        super(message);
    }
}
