package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.exceptions.HttpClientException;
import de.fraunhofer.isst.ids.framework.messages.InfomodelMessageBuilder;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import okhttp3.MultipartBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * This class implements all methods of {@link de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestService}. It provides message handling for all outgoing
 * connector communication by passing IDS messages to the IDS framework.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class ConnectorRequestServiceImpl implements ConnectorRequestService {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ConnectorRequestServiceImpl.class);

    private Connector connector;
    private TokenProvider tokenProvider;
    private IDSHttpService idsHttpService;
    private IdsUtils idsUtils;

    @Autowired
    /**
     * <p>Constructor for ConnectorRequestServiceImpl.</p>
     *
     * @param configurationContainer a {@link de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer} object.
     * @param tokenProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @throws de.fraunhofer.isst.ids.framework.exceptions.HttpClientException if any.
     * @throws java.security.KeyManagementException if any.
     * @throws java.security.NoSuchAlgorithmException if any.
     */
    public ConnectorRequestServiceImpl(ConfigurationContainer configurationContainer, TokenProvider tokenProvider, IdsUtils idsUtils)
            throws HttpClientException, KeyManagementException, NoSuchAlgorithmException {
        this.connector = configurationContainer.getConnector();
        this.tokenProvider = tokenProvider;
        this.idsUtils = idsUtils;

        ClientProvider clientProvider = new ClientProvider(configurationContainer);
        this.idsHttpService = new IDSHttpService(clientProvider);
    }

    /**
     * {@inheritDoc}
     *
     * Builds and sends an ArtifactRequestMessage.
     */
    @Override
    public Response sendArtifactRequestMessage(URI recipient, URI artifact, ContractOffer contract) throws IOException {
        String payload = "";
        if (contract != null) {
            payload = new ContractRequestBuilder()
                    ._consumer_(connector.getMaintainer())
                    ._contractDate_(idsUtils.getGregorianOf(new Date()))
                    ._obligation_(contract.getObligation())
                    ._permission_(contract.getPermission())
                    ._prohibition_(contract.getProhibition())
                    .build().toRdf();
            LOGGER.info("Built contract request.");
        }

        ArtifactRequestMessage requestMessage = new ArtifactRequestMessageBuilder()
                ._issued_(Util.getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._requestedArtifact_(artifact)
                ._securityToken_(tokenProvider.getTokenJWS())
                ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
                .build();

        MultipartBody body = InfomodelMessageBuilder.messageWithString(requestMessage, payload);
        LOGGER.info("Sent artifact request message.");
        return idsHttpService.send(body, recipient);
    }

    /**
     * {@inheritDoc}
     *
     * Builds and sends an ArtifactRequestMessage.
     */
    @Override
    public Response sendArtifactRequestMessage(URI recipient, URI artifact, URI correlationMessage, URI transferContract) throws IOException {
        ArtifactRequestMessage requestMessage = new ArtifactRequestMessageBuilder()
                ._issued_(Util.getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._requestedArtifact_(artifact)
                ._securityToken_(tokenProvider.getTokenJWS())
                ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
                ._correlationMessage_(correlationMessage)
                ._transferContract_(transferContract)
                .build();

        MultipartBody body = InfomodelMessageBuilder.messageWithString(requestMessage, "");
        LOGGER.info("Sent artifact request message.");
        return idsHttpService.send(body, recipient);
    }

    /**
     * {@inheritDoc}
     *
     * Builds and sends an DescriptionRequestMessage.
     */
    @Override
    public Response sendDescriptionRequestMessage(URI recipient, URI artifact) throws IOException {
        DescriptionRequestMessage requestMessage;

        if (artifact == null) {
            requestMessage = new DescriptionRequestMessageBuilder()
                    ._issued_(Util.getGregorianNow())
                    ._modelVersion_(connector.getOutboundModelVersion())
                    ._issuerConnector_(connector.getId())
                    ._senderAgent_(connector.getId())
                    ._securityToken_(tokenProvider.getTokenJWS())
                    ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
                    .build();
        } else {
            requestMessage = new DescriptionRequestMessageBuilder()
                    ._issued_(Util.getGregorianNow())
                    ._modelVersion_(connector.getOutboundModelVersion())
                    ._issuerConnector_(connector.getId())
                    ._senderAgent_(connector.getId())
                    ._requestedElement_(artifact)
                    ._securityToken_(tokenProvider.getTokenJWS())
                    ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
                    .build();
        }

        MultipartBody body = InfomodelMessageBuilder.messageWithString(requestMessage, "");
        return idsHttpService.send(body, recipient);
    }
}
