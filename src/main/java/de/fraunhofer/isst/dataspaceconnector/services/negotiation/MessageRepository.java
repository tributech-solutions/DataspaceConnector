package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

import de.fraunhofer.iais.eis.Message;
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
public interface MessageRepository extends JpaRepository<Message, UUID> {
}
