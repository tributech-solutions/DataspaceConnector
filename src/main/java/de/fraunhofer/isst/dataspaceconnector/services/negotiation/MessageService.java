package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

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
     * @param message a {@link de.fraunhofer.iais.eis.Message} object as string.
     */
    void addMessage(String message);

    /**
     * <p>getMessages.</p>
     *
     * @return a list of {@link de.fraunhofer.iais.eis.Message} objects as strings.
     */
    List<String> getMessages();
}
