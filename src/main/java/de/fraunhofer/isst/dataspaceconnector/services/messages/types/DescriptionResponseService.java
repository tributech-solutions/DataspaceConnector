package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.DescriptionResponseMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionResponseMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.AbstractMessageService;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Service;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids description response messages.
 */
@Service
public final class DescriptionResponseService
        extends AbstractMessageService<DescriptionResponseMessageDesc> {

    /**
     * @throws IllegalArgumentException If desc is null.
     */
    @Override
    public Message buildMessage(final DescriptionResponseMessageDesc desc)
            throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var recipient = desc.getRecipient();
        final var correlationMessage = desc.getCorrelationMessage();

        return new DescriptionResponseMessageBuilder()
                ._securityToken_(token)
                ._correlationMessage_(correlationMessage)
                ._issued_(getGregorianNow())
                ._issuerConnector_(connectorId)
                ._modelVersion_(modelVersion)
                ._senderAgent_(connectorId)
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return null;
    }
}
