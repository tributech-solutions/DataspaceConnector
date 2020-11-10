package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

import de.fraunhofer.isst.dataspaceconnector.model.SentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>MessageServiceImpl interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class MessageServiceImpl implements MessageService {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    private MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void addMessage(SentMessage message) {
        messageRepository.save(message);
    }

    @Override
    public List<SentMessage> getMessages() {
        return messageRepository.findAll();
    }
}
