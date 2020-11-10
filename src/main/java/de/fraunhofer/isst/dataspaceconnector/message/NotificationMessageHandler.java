package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This @{@link de.fraunhofer.isst.dataspaceconnector.message.NotificationMessageHandler} handles all incoming messages
 * that have a {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} as part one in the multipart message. This
 * header must have the correct '@type' reference as defined in the {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl}
 * JsonTypeName annotation. In this example, the received payload is not defined and will be returned immediately.
 * Usually, the payload would be well defined as well, such that it can be deserialized into a proper Java-Object.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Component
@SupportedMessageType(NotificationMessageImpl.class)
public class NotificationMessageHandler implements MessageHandler<NotificationMessageImpl> {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageHandler.class);

    private final TokenProvider tokenProvider;
    private final Connector connector;

    @Autowired
    public NotificationMessageHandler(ConfigurationContainer configurationContainer, TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
        this.connector = configurationContainer.getConnector();
    }

    /**
     * {@inheritDoc}
     *
     * This message implements the logic that is needed to handle the message. As it just returns the input as string
     * the messagePayload-InputStream is converted to a String.
     */
    @Override
    public MessageResponse handleMessage(NotificationMessageImpl message, MessagePayload messagePayload) {
        LOGGER.info("Message received: " + messagePayload);
        return BodyResponse.create(new MessageProcessedNotificationMessageBuilder()
                ._securityToken_(tokenProvider.getTokenJWS())
                ._correlationMessage_(message.getId())
                ._issued_(de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(message.getIssuerConnector()))
                .build(), "Message received.");
    }
}
