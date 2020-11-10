package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

import de.fraunhofer.isst.dataspaceconnector.model.SentMessage;

import java.util.List;

/**
 * <p>MessageService interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public interface MessageService {
    /**
     * <p>addMessage.</p>
     *
     * @param message a {@link SentMessage} object as string.
     */
    void addMessage(SentMessage message);

    /**
     * <p>getMessages.</p>
     *
     * @return a list of {@link SentMessage} objects as strings.
     */
    List<SentMessage> getMessages();
}
