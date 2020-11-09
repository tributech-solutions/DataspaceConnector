package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * <p>ResourceContractRepository interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Repository
public interface ContractRepository extends JpaRepository<ResourceContract, UUID> {
}
