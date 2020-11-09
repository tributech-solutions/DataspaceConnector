package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

import de.fraunhofer.iais.eis.Message;

import java.util.List;

/**
 * <p>ResourceContractService interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public interface MessageService {
    /**
     * <p>addMessage.</p>
     *
     * @param message a {@link de.fraunhofer.iais.eis.Message} object.
     */
    void addMessage(Message message);

    /**
     * <p>getMessages.</p>
     *
     * @return a list of {@link de.fraunhofer.iais.eis.Message} objects.
     */
    List<Message> getMessages();
}
