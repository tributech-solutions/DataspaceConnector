package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

import de.fraunhofer.isst.dataspaceconnector.model.SentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * <p>MessageRepository interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Repository
public interface MessageRepository extends JpaRepository<SentMessage, UUID> {
}
