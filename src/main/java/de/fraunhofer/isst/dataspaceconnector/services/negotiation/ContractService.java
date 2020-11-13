package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

import de.fraunhofer.isst.dataspaceconnector.model.resource.ResourceContract;

import java.util.List;
import java.util.UUID;

/**
 * <p>ResourceContractService interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public interface ContractService {
    /**
     * <p>addContract.</p>
     *
     * @param contract a {@link ResourceContract} object.
     */
    void addContract(ResourceContract contract);

    /**
     * <p>getContract.</p>
     *
     * @return a {@link ResourceContract} object.
     */
    ResourceContract getContract(UUID uuid);

    /**
     * <p>getContracts.</p>
     *
     * @return a list of {@link ResourceContract} objects.
     */
    List<ResourceContract> getContracts();
}
