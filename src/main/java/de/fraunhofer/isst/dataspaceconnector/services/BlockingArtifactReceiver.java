package de.fraunhofer.isst.dataspaceconnector.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jose4j.base64url.Base64;
import org.springframework.stereotype.Component;

/**
 * Performs an artifact request for an artifact. All functions will block till the request is
 * completed.
 */
@Component
@RequiredArgsConstructor
public class BlockingArtifactReceiver implements ArtifactRetriever {

    /**
     * Used for sending an artifact request message.
     */
    private final @NonNull MessageService messageService;

    /**
     * Used for accessing artifacts and their data.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream retrieve(final UUID artifactId, final URI recipient,
                                final URI transferContract) {
        return retrieve(artifactId, recipient, transferContract, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream retrieve(final UUID artifactId, final URI recipient,
                                final URI transferContract, final QueryInput queryInput) {
        final var artifact = artifactService.get(artifactId);
        final var response = messageService
                .sendArtifactRequestMessage(recipient, artifact.getRemoteId(), transferContract,
                                            queryInput);
        final var data = MessageUtils.extractPayloadFromMultipartMessage(response);
        return new ByteArrayInputStream(Base64.decode(data));
    }
}
