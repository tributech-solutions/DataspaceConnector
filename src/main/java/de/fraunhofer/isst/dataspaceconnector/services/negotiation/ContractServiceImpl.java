package de.fraunhofer.isst.dataspaceconnector.services.negotiation;

import de.fraunhofer.isst.dataspaceconnector.model.resource.ResourceContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <p>ContractServiceImpl interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class ContractServiceImpl implements ContractService{
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ContractServiceImpl.class);

    private ContractRepository contractRepository;

    @Autowired
    public ContractServiceImpl(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @Override
    public UUID addContract(ResourceContract contract) {
        contractRepository.save(contract);
        return contract.getPid();
    }

    @Override
    public ResourceContract getContract(UUID uuid) {
        return null;
    }

    @Override
    public List<ResourceContract> getContracts() {
        return contractRepository.findAll();
    }
}
