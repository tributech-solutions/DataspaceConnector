package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32C;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates an artifact.
 */
@Component
public final class ArtifactFactory implements AbstractFactory<Artifact, ArtifactDesc> {

    /**
     * Default remote id assigned to all artifacts.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * Default remote address assigned to all artifacts.
     */
    public static final URI DEFAULT_REMOTE_ADDRESS = URI.create("genesis");

    /**
     * Default title assigned to all artifacts.
     */
    public static final String DEFAULT_TITLE = "";

    /**
     * Default download setting assigned to all artifacts.
     */
    public static final boolean DEFAULT_AUTO_DOWNLOAD = false;

    /**
     * Default constructor.
     */
    public ArtifactFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new artifact.
     * @param desc The description of the new artifact.
     * @return The new artifact.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    public Artifact create(final ArtifactDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var artifact = new ArtifactImpl();
        artifact.setAgreements(new ArrayList<>());
        artifact.setRepresentations(new ArrayList<>());

        update(artifact, desc);

        return artifact;
    }

    /**
     * Update an artifact.
     * @param artifact The artifact to be updated.
     * @param desc     The new artifact description.
     * @return True if the artifact has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    public boolean update(final Artifact artifact, final ArtifactDesc desc) {
        Utils.requireNonNull(artifact, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedRemoteId = updateRemoteId(artifact, desc.getRemoteId());
        final var hasUpdatedRemoteAddress = updateRemoteAddress(artifact, desc.getRemoteAddress());
        final var hasUpdatedTitle = updateTitle(artifact, desc.getTitle());
        final var hasUpdatedAutoDownload = updateAutoDownload(artifact, desc.isAutomatedDownload());
        final var hasUpdatedData = updateData(artifact, desc);
        final var hasUpdatedAdditional = this.updateAdditional(artifact, desc.getAdditional());

        return hasUpdatedRemoteId || hasUpdatedRemoteAddress || hasUpdatedTitle
               || hasUpdatedAutoDownload || hasUpdatedData || hasUpdatedAdditional;
    }

    private boolean updateRemoteId(final Artifact artifact, final URI remoteId) {
        final var newUri =
                MetadataUtils.updateUri(artifact.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(artifact::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateRemoteAddress(final Artifact artifact, final URI remoteAddress) {
        final var newUri = MetadataUtils
                .updateUri(artifact.getRemoteAddress(), remoteAddress, DEFAULT_REMOTE_ADDRESS);
        newUri.ifPresent(artifact::setRemoteAddress);

        return newUri.isPresent();
    }

    private boolean updateTitle(final Artifact artifact, final String title) {
        final var newTitle = MetadataUtils.updateString(artifact.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(artifact::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateAutoDownload(final Artifact artifact, final boolean autoDownload) {
        if (artifact.isAutomatedDownload() != autoDownload) {
            artifact.setAutomatedDownload(autoDownload);
            return true;
        }

        return false;
    }

    private boolean updateAdditional(final Artifact artifact,
                                     final Map<String, String> additional) {
        final var newAdditional =
                MetadataUtils
                        .updateStringMap(artifact.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(artifact::setAdditional);

        return newAdditional.isPresent();
    }

    private boolean updateData(final Artifact artifact, final ArtifactDesc desc) {
        boolean hasChanged;
        if (isRemoteData(desc)) {
            hasChanged = updateRemoteData((ArtifactImpl) artifact, desc.getAccessUrl(),
                                          desc.getUsername(), desc.getPassword());
        } else {
            hasChanged = updateLocalData((ArtifactImpl) artifact, desc.getValue());
        }

        return hasChanged;
    }

    private static boolean isRemoteData(final ArtifactDesc desc) {
        return desc.getAccessUrl() != null && desc.getAccessUrl().getPath().length() > 0;
    }

    private boolean updateLocalData(final ArtifactImpl artifact, final String value) {
        final var newData = new LocalData();
        final var data = value == null ? null : value.getBytes(StandardCharsets.UTF_16);
        newData.setValue(data);

        final var oldData = artifact.getData();
        if (oldData instanceof LocalData) {
            if (!oldData.equals(newData)) {
                artifact.setData(newData);
                updateByteSize(artifact, data);
                return true;
            }
        } else {
            artifact.setData(newData);
            updateByteSize(artifact, data);
            return true;
        }

        return false;
    }

    private boolean updateRemoteData(final ArtifactImpl artifact, final URL accessUrl,
                                     final String username, final String password) {
        final var newData = new RemoteData();
        newData.setAccessUrl(accessUrl);
        newData.setUsername(username);
        newData.setPassword(password);

        final var oldData = artifact.getData();
        if (oldData instanceof RemoteData) {
            if (!oldData.equals(newData)) {
                artifact.setData(newData);
                return true;
            }
        } else {
            artifact.setData(newData);
            return true;
        }

        return false;
    }

    /**
     * Update the byte and checksum of an artifact. This will not update
     * the actual data.
     * @param artifact The artifact which byte and checksum needs to be
     *                recalculated.
     * @param bytes The data.
     * @return true if the artifact has been modified.
     */
    public boolean updateByteSize(final Artifact artifact, final byte[] bytes) {
        var hasChanged = false;
        final var checkSum = calculateChecksum(bytes);

        if (bytes != null && artifact.getByteSize() != bytes.length) {
            artifact.setByteSize(bytes.length);
            hasChanged = true;
        }

        if (artifact.getCheckSum() != checkSum) {
            artifact.setCheckSum(checkSum);
            hasChanged = true;
        }

        return hasChanged;
    }

    private long calculateChecksum(final byte[] bytes) {
        if (bytes == null) {
            return 0;
        }

        final var checksum = new CRC32C();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }
}
