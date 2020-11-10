package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * This class provides a model to handle agreed resource contracts.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Data
@Entity
@Table
public class SentMessage {
    @Id
    @GeneratedValue
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("message")
    @Column(columnDefinition = "BYTEA")
    private String message;

    /**
     * <p>Constructor for SentMessage.</p>
     */
    public SentMessage() {

    }

    /**
     * <p>Constructor for SentMessage.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public SentMessage(String message) {
        this.message = message;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.util.UUID} object.
     */
    public UUID getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.util.UUID} object.
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>message</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMessage() {
        return message;
    }

    /**
     * <p>Setter for the field <code>message</code>.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
