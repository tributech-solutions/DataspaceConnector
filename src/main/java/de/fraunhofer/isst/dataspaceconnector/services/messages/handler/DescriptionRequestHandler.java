package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.SelfLinkCreationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionResponseMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.DescriptionResponseService;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * This @{@link DescriptionRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} as part one in the multipart
 * message. This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@RequiredArgsConstructor
@SupportedMessageType(DescriptionRequestMessageImpl.class)
public class DescriptionRequestHandler implements MessageHandler<DescriptionRequestMessageImpl> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService descriptionService;

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageResponseService exceptionService;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for message processing.
     */
    private final @NonNull MessageService messageService;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The ids request message as header.
     * @param payload The request message payload.
     * @return The response message.
     */
    @Override
    public MessageResponse handleMessage(final DescriptionRequestMessageImpl message,
                                         final MessagePayload payload) {
        // Validate incoming message.
        try {
            messageService.validateIncomingRequestMessage(message);
        } catch (MessageEmptyException exception) {
            return exceptionService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return exceptionService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var requestedElement = MessageUtils.extractRequestedElement(message);
        final var issuerConnector = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        // Check if a specific resource has been requested.
        if (requestedElement == null) {
            return constructConnectorSelfDescription(issuerConnector, messageId);
        } else {
            return constructResourceDescription(requestedElement, issuerConnector, messageId);
        }
    }

    /**
     * Constructs the response message for a given resource description request message.
     *
     * @param requestedElement The requested element.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The message id of the incoming message.
     * @return The response message to the passed request.
     */
    public MessageResponse constructResourceDescription(final URI requestedElement,
                                                        final URI issuerConnector,
                                                        final URI messageId) {
        try {
            final var entity = entityResolver.getEntityById(requestedElement);

            if (entity == null) {
                throw new ResourceNotFoundException(ErrorMessages.EMTPY_ENTITY.toString());
            } else {
                // If the element has been found, build the ids response message.
                final var desc = new DescriptionResponseMessageDesc(issuerConnector, messageId);
                final var header = descriptionService.buildMessage(desc);
                final var payload = entityResolver.getEntityAsRdfString(entity);

                // Send ids response message.
                return BodyResponse.create(header, payload);
            }
        } catch (ResourceNotFoundException | InvalidResourceException exception) {
            return exceptionService.handleResourceNotFoundException(exception, requestedElement,
                    issuerConnector, messageId);
        } catch (MessageBuilderException | IllegalStateException | ConstraintViolationException e) {
            return exceptionService.handleResponseMessageBuilderException(e, issuerConnector,
                    messageId);
        } catch (SelfLinkCreationException exception) {
            return exceptionService.handleSelfLinkCreationException(exception, requestedElement);
        }
    }

    /**
     * Constructs a resource catalog description message for the connector.
     *
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The message id of the incoming message.
     * @return A response message containing the resource catalog of the connector.
     */
    public MessageResponse constructConnectorSelfDescription(final URI issuerConnector,
                                                             final URI messageId) {
        try {
            // Get self-description.
            final var selfDescription = connectorService.getConnectorWithOfferedResources();

            // Build ids response message.
            final var desc = new DescriptionResponseMessageDesc(issuerConnector, messageId);
            final var header = descriptionService.buildMessage(desc);

            // Send ids response message.
            return BodyResponse.create(header, selfDescription.toRdf());
        } catch (MessageBuilderException | IllegalStateException | ConstraintViolationException e) {
            return exceptionService.handleResponseMessageBuilderException(e, issuerConnector,
                    messageId);
        }
    }
}
