package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that that a problem with the resource composition occurred.
 */
public class InvalidResourceException extends ResourceException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public InvalidResourceException(final String msg) {
        super(msg);
    }

    /**
     * Construct an InvalidResourceException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public InvalidResourceException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
