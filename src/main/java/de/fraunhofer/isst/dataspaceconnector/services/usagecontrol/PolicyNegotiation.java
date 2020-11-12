package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class handles the policy negotiation status.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class PolicyNegotiation {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(PolicyNegotiation.class);

    private boolean status;

    /**
     * <p>Constructor for PolicyNegotiation. Will be set to true on connector start, but can be turned of with the
     * according endpoint in {@link de.fraunhofer.isst.dataspaceconnector.controller.MainController}.</p>
     */
    @Autowired
    public PolicyNegotiation() {
        status = true;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
