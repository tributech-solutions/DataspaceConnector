package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.isst.ids.framework.exceptions.HttpClientException;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;

/**
 * <p>MessageService interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public interface RequestService {
    /**
     * <p>sendLogMessage.</p>
     *
     * @return a {@link okhttp3.Response} object.
     * @throws de.fraunhofer.isst.ids.framework.exceptions.HttpClientException if any.
     * @throws java.io.IOException if any.
     */
    Response sendLogMessage() throws HttpClientException, IOException;

    /**
     * <p>sendNotificationMessage.</p>
     *
     * @param recipient a {@link java.lang.String} object.
     * @return a {@link okhttp3.Response} object.
     * @throws de.fraunhofer.isst.ids.framework.exceptions.HttpClientException if any.
     * @throws java.io.IOException if any.
     */
    Response sendNotificationMessage(String recipient) throws HttpClientException, IOException;

    /**
     * <p>sendContractAgreementMessage.</p>
     *
     * @param contractAgreement a {@link de.fraunhofer.iais.eis.ContractAgreement} object.
     * @param correlationMessage a {@link de.fraunhofer.iais.eis.ContractAgreement} object.
     * @throws de.fraunhofer.isst.ids.framework.exceptions.HttpClientException if any.
     * @throws java.io.IOException if any.
     */
    void sendContractAgreementMessage(ContractAgreement contractAgreement, URI correlationMessage) throws HttpClientException, IOException;
}
