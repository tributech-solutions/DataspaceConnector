package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the data access has not been successfully reported.
 */
public class DataAccessNotificationFailedException extends PolicyRestrictionException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor without params.
     */
    public DataAccessNotificationFailedException() { }

    /**
     * Construct a DataAccessNotificationFailedException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public DataAccessNotificationFailedException(final String msg) {
        super(msg);
    }

    /**
     * Construct a DataAccessNotificationFailedException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public DataAccessNotificationFailedException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}