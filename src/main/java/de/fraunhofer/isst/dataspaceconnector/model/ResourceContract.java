package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iais.eis.Contract;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.net.URI;
import java.util.UUID;

/**
 * This class provides a model to handle agreed resource contracts.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public class ResourceContract {
    @Id
    @GeneratedValue
    @JsonProperty("pid")
    private UUID pid;

    @JsonProperty("resourceId")
    private URI resourceId;

    @JsonProperty("contract")
    @Column(columnDefinition = "BYTEA")
    private Contract contract;

    /**
     * <p>Constructor for ResourceContract.</p>
     */
    public ResourceContract() {
    }

    /**
     * <p>Constructor for ResourceContract.</p>
     *
     * @param pid a {@link java.util.UUID} object.
     * @param resourceId a {@link java.util.UUID} object.
     * @param contract a {@link de.fraunhofer.iais.eis.Contract} object.
     */
    public ResourceContract(UUID pid, URI resourceId, Contract contract) {
        this.pid = pid;
        this.resourceId = resourceId;
        this.contract = contract;
    }

    /**
     * <p>Getter for the field <code>pid</code>.</p>
     *
     * @return a {@link java.util.UUID} object.
     */
    public UUID getPid() {
        return pid;
    }

    /**
     * <p>Setter for the field <code>pid</code>.</p>
     *
     * @param pid a {@link java.util.UUID} object.
     */
    public void setPid(UUID pid) {
        this.pid = pid;
    }

    /**
     * <p>Getter for the field <code>resourceId</code>.</p>
     *
     * @return a {@link java.util.UUID} object.
     */
    public URI getResourceId() {
        return resourceId;
    }

    /**
     * <p>Setter for the field <code>resourceId</code>.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     */
    public void setResourceId(URI resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * <p>Getter for the field <code>contract</code>.</p>
     *
     * @return a {@link de.fraunhofer.iais.eis.Contract} object.
     */
    public Contract getContract() {
        return contract;
    }

    /**
     * <p>Setter for the field <code>contract</code>.</p>
     *
     * @param contract a {@link de.fraunhofer.iais.eis.Contract} object.
     */
    public void setContract(Contract contract) {
        this.contract = contract;
    }
}
