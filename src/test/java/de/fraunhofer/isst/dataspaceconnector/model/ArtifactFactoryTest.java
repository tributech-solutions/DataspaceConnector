package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jose4j.base64url.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ArtifactFactoryTest {

    private ArtifactFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new ArtifactFactory();
    }

    @Test
    public void default_title_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ArtifactFactory.DEFAULT_TITLE);
    }

    @Test
    public void default_remoteId_is_genesis() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create("genesis"), ArtifactFactory.DEFAULT_REMOTE_ID);
    }

    @Test
    public void default_autoDownload_is_false() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertFalse(ArtifactFactory.DEFAULT_AUTO_DOWNLOAD);
    }

    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    public void create_validDesc_creationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_agreementsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertEquals(0, result.getAgreements().size());
    }

    @Test
    public void create_validDesc_representationsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertEquals(0, result.getRepresentations().size());
    }
    
    /**
     * remoteId.
     */

    @Test
    public void create_nullRemoteId_defaultRemoteId() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setRemoteId(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ArtifactFactory.DEFAULT_REMOTE_ID, result.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_setRemoteId() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setRemoteId(URI.create("uri"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertEquals(desc.getRemoteId(), artifact.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_returnTrue() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setRemoteId(URI.create("uri"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameRemoteId_returnFalse() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * title.
     */

    @Test
    public void create_nullTitle_defaultTitle() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setTitle(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ArtifactFactory.DEFAULT_TITLE, result.getTitle());
    }

    @Test
    public void update_differentTitle_setTitle() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setTitle("Random Title");

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertEquals(desc.getTitle(), artifact.getTitle());
    }

    @Test
    public void update_differentTitle_returnTrue() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setTitle("Random Title");

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameTitle_returnFalse() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        assertFalse(result);
    }
    
    /**
     * autoDownload.
     */

    @Test
    public void update_differentAutomatedDownload_setAutomatedDownload() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAutomatedDownload(true);

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertEquals(desc.isAutomatedDownload(), artifact.isAutomatedDownload());
    }

    @Test
    public void update_differentAutomatedDownload_returnTrue() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAutomatedDownload(true);

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameAutomatedDownload_returnFalse() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAutomatedDownload(true);

        final var artifact = factory.create(desc);

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertFalse(result);
    }
    
    /**
     * additional.
     */

    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), artifact.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * update inputs.
     */

    @Test
    public void update_nullArtifact_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null, new ArtifactDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var contract = factory.create(new ArtifactDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(contract, null));
    }

    /**
     * num accessed
     */

    @Test
    public void create_num_accessed_is_0 () {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertEquals(0, result.getNumAccessed());
    }

    /**
     * access url
     */

    @Test
    public void create_nullAccessUrl_isLocalData() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(null);

        /* ACT */
        final var result = (ArtifactImpl) factory.create(desc);

        /* ASSERT */
        assertTrue(((ArtifactImpl)result).getData() instanceof LocalData);
    }

    @Test
    public void create_setEmptyAccessUrl_isLocalData() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://"));

        /* ACT */
        final var result = (ArtifactImpl) factory.create(desc);

        /* ASSERT */
        assertTrue(((ArtifactImpl)result).getData() instanceof LocalData);
    }

    @Test
    public void create_setAccessUrl_isRemoteData() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));

        /* ACT */
        final var result = (ArtifactImpl) factory.create(desc);

        /* ASSERT */
        assertTrue(((ArtifactImpl)result).getData() instanceof RemoteData);
    }

    @Test
    public void update_setAccessUrlNull_changeToLocalData() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));

        final var artifact = factory.create(desc);

        /* ACT */
        factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        assertTrue(((ArtifactImpl)artifact).getData() instanceof LocalData);
    }

    @Test
    public void update_setAccessUrl_changeToRemoteData() throws MalformedURLException {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(((ArtifactImpl)artifact).getData() instanceof RemoteData);
    }

    /**
     * local data
     */

    @Test
    public void create_nullValue_returnEmpty() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setValue(null);

        /* ACT */
        final var result = (ArtifactImpl) factory.create(desc);

        /* ASSERT */
        assertNull(((LocalData)((ArtifactImpl)result).getData()).getValue());
    }

    @Test
    public void update_setValue_returnValue() {
        /* ARRANGE */
        final var artifact = (ArtifactImpl) factory.create(new ArtifactDesc());

        final var desc = new ArtifactDesc();
        desc.setValue("Some Value");

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(Arrays.equals(Base64.decode(desc.getValue()), ((LocalData)((ArtifactImpl)artifact).getData()).getValue()));
    }

    @Test
    public void update_differentValue_returnTrue() {
        /* ARRANGE */
        final var artifact = (ArtifactImpl) factory.create(new ArtifactDesc());

        final var desc = new ArtifactDesc();
        desc.setValue("Random Value");

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameValue_returnFalse() {
        /* ARRANGE */
        final var artifact = (ArtifactImpl) factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * access url
     */

    @Test
    public void update_differentAccessUrl_setAccessUrl() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        final var data = (RemoteData)((ArtifactImpl)artifact).getData();
        assertEquals(desc.getAccessUrl(), data.getAccessUrl());
    }

    /**
     * username
     */

    @Test
    public void create_nullUsername_nullUsername() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setUsername(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        final var data = (RemoteData)((ArtifactImpl)result).getData();
        assertNull(data.getUsername());
    }

    @Test
    public void update_differentUsername_setUsername() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setUsername("Random Username");

        final var artifact = factory.create(desc);

        final var updateDesc = new ArtifactDesc();
        updateDesc.setAccessUrl(new URL("https://localhost:8080/"));
        updateDesc.setUsername("Random Different Username");

        /* ACT */
        factory.update(artifact, updateDesc);

        /* ASSERT */
        final var data = (RemoteData)((ArtifactImpl)artifact).getData();
        assertEquals(updateDesc.getUsername(), data.getUsername());
    }

    @Test
    public void update_differentUsername_returnTrue() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setUsername("Random Username");

        final var artifact = factory.create(desc);

        final var updateDesc = new ArtifactDesc();
        updateDesc.setAccessUrl(new URL("https://localhost:8080/"));
        updateDesc.setUsername("Random Different Username");

        /* ACT */
        final var result = factory.update(artifact, updateDesc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameUsername_returnFalse() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setUsername("Random Username");

        final var artifact = factory.create(desc);

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * password
     */

    @Test
    public void create_nullPassword_nullPassword() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setPassword(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        final var data = (RemoteData)((ArtifactImpl)result).getData();
        assertNull(data.getPassword());
    }

    @Test
    public void update_differentPassword_setPassword() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setPassword("Random Password");

        final var artifact = factory.create(desc);

        final var updateDesc = new ArtifactDesc();
        updateDesc.setAccessUrl(new URL("https://localhost:8080/"));
        updateDesc.setPassword("Random Different Password");

        /* ACT */
        factory.update(artifact, updateDesc);

        /* ASSERT */
        final var data = (RemoteData)((ArtifactImpl)artifact).getData();
        assertEquals(updateDesc.getPassword(), data.getPassword());
    }

    @Test
    public void update_differentPassword_returnTrue() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setPassword("Random Password");

        final var artifact = factory.create(desc);

        final var updateDesc = new ArtifactDesc();
        updateDesc.setAccessUrl(new URL("https://localhost:8080/"));
        updateDesc.setPassword("Random Different Password");

        /* ACT */
        final var result = factory.update(artifact, updateDesc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_samePassword_returnFalse() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setPassword("Random Password");

        final var artifact = factory.create(desc);

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertFalse(result);
    }
}
