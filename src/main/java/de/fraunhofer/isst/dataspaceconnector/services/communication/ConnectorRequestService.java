package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.Contract;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;

/**
 * <p>ConnectorRequestService interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public interface ConnectorRequestService {
    /**
     * <p>sendArtifactRequestMessage.</p>
     *
     * @param recipient a {@link java.net.URI} object.
     * @param artifact a {@link java.net.URI} object.
     * @return a {@link okhttp3.Response} object.
     * @throws java.io.IOException if any.
     */
    Response sendArtifactRequestMessage(URI recipient, URI artifact, Contract contract) throws IOException;

    /**
     * <p>sendArtifactRequestMessage.</p>
     *
     * @param recipient a {@link java.net.URI} object.
     * @param artifact a {@link java.net.URI} object.
     * @param correlationMessage a {@link java.net.URI} object.
     * @param transferContract a {@link java.net.URI} object.
     * @return a {@link okhttp3.Response} object.
     * @throws java.io.IOException if any.
     */
    Response sendArtifactRequestMessage(URI recipient, URI artifact, URI correlationMessage, URI transferContract) throws IOException;

    /**
     * <p>sendDescriptionRequestMessage.</p>
     *
     * @param recipient a {@link java.net.URI} object.
     * @param artifact a {@link java.net.URI} object.
     * @return a {@link okhttp3.Response} object.
     * @throws java.io.IOException if any.
     */
    Response sendDescriptionRequestMessage(URI recipient, URI artifact) throws IOException;
}
