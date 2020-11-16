package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messages.InfomodelMessageBuilder;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import okhttp3.MultipartBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

/**
 * <p>MessageServiceImpl class.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class RequestServiceImpl implements RequestService {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(RequestServiceImpl.class);

    private Connector connector;
    private TokenProvider tokenProvider;
    private IDSHttpService idsHttpService;

    @Autowired
    /**
     * <p>Constructor for MessageServiceImpl.</p>
     *
     * @param connector a {@link de.fraunhofer.iais.eis.Connector} object.
     * @param tokenProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @param idsHttpService a {@link de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService} object.
     */
    public RequestServiceImpl(ConfigurationContainer configurationContainer, TokenProvider tokenProvider, IDSHttpService idsHttpService) {
        this.connector = configurationContainer.getConnector();
        this.tokenProvider = tokenProvider;
        this.idsHttpService = idsHttpService;
    }

    /** {@inheritDoc} */
    @Override
    public Response sendLogMessage(String payload, String pid) throws IOException {
        String clearingHouse = "https://ch-ids.aisec.fraunhofer.de/logs/messages/";

        LogMessage message = new LogMessageBuilder()
                ._issued_(Util.getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getTokenJWS())
                ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(URI.create(clearingHouse + pid)))
                .build();

        MultipartBody body = InfomodelMessageBuilder.messageWithString(message, payload);
        return idsHttpService.send(body, URI.create(clearingHouse));
    }

    /** {@inheritDoc} */
    @Override
    public Response sendNotificationMessage(String recipient) throws IOException {
        NotificationMessage message = new NotificationMessageBuilder()
                ._issued_(Util.getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getTokenJWS())
                ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(URI.create(recipient)))
                .build();

        MultipartBody body = InfomodelMessageBuilder.messageWithString(message, "");
        return idsHttpService.send(body, URI.create(recipient));
    }
}
