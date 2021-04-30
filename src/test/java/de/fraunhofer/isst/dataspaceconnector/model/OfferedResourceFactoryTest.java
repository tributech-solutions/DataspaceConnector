package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OfferedResourceFactoryTest {

    private OfferedResourceFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new OfferedResourceFactory();
    }

    @Test
    public void default_title_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ResourceFactory.DEFAULT_TITLE);
    }

    @Test
    public void default_description_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ResourceFactory.DEFAULT_DESCRIPTION);
    }

    @Test
    public void default_keywords_is_DSC() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(Collections.singletonList("DSC"), ResourceFactory.DEFAULT_KEYWORDS);
    }

    @Test
    public void default_publisher_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ResourceFactory.DEFAULT_PUBLISHER);
    }

    @Test
    public void default_language_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ResourceFactory.DEFAULT_LANGUAGE);
    }

    @Test
    public void default_licence_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ResourceFactory.DEFAULT_LICENCE);
    }

    @Test
    public void default_sovereign_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ResourceFactory.DEFAULT_SOVEREIGN);
    }

    @Test
    public void default_endpointDocumentation_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ResourceFactory.DEFAULT_ENDPOINT_DOCS);
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
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_representationsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(0, result.getRepresentations().size());
    }

    @Test
    public void create_validDesc_contractsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(0, result.getContracts().size());
    }

    @Test
    public void create_validDesc_keywordsDefault() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(1, result.getKeywords().size());
    }

    @Test
    public void create_validDesc_catalogsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(0, result.getCatalogs().size());
    }

    /**
     * title.
     */

    @Test
    public void create_nullTitle_defaultTitle() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setTitle(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_TITLE, result.getTitle());
    }

    @Test
    public void update_differentTitle_setTitle() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setTitle("Random Title");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getTitle(), resource.getTitle());
    }

    @Test
    public void update_differentTitle_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setTitle("Random Title");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameTitle_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * description.
     */

    @Test
    public void create_nullDescription_defaultDescription() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setDescription(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_DESCRIPTION, result.getDescription());
    }

    @Test
    public void update_differentDescription_setDescription() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setDescription("Random Description");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getDescription(), resource.getDescription());
    }

    @Test
    public void update_differentDescription_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setDescription("Random Description");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameDescription_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * keywords.
     */

    @Test
    public void create_nullKeywords_defaultKeywords() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setKeywords(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertTrue(ResourceFactory.DEFAULT_KEYWORDS.containsAll(result.getKeywords()));
        assertEquals(ResourceFactory.DEFAULT_KEYWORDS.size(), result.getKeywords().size());
    }

    @Test
    public void update_differentKeywords_setKeywords() {
        /* ARRANGE */
        final var keywords = new ArrayList<String>();
        keywords.add("Default");
        keywords.add("Key");

        final var desc = new OfferedResourceDesc();
        desc.setKeywords(keywords);

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getKeywords(), resource.getKeywords());
    }

    @Test
    public void update_differentKeywords_returnTrue() {
        /* ARRANGE */
        final var keywords = new ArrayList<String>();
        keywords.add("Default");
        keywords.add("Key");

        final var desc = new OfferedResourceDesc();
        desc.setKeywords(keywords);

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameKeywords_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * publisher.
     */

    @Test
    public void create_nullPublisher_defaultPublisher() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setPublisher(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_PUBLISHER, result.getPublisher());
    }

    @Test
    public void update_differentPublisher_setPublisher() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setPublisher(URI.create("RandomPublisher"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getPublisher(), resource.getPublisher());
    }

    @Test
    public void update_differentPublisher_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setPublisher(URI.create("RandomPublisher"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_samePublisher_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }


    /**
     * language.
     */

    @Test
    public void create_nullLanguage_defaultLanguage() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLanguage(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_DESCRIPTION, result.getLanguage());
    }

    @Test
    public void update_differentLanguage_setLanguage() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLanguage("Random Language");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getLanguage(), resource.getLanguage());
    }

    @Test
    public void update_differentLanguage_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLanguage("Random Language");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameLanguage_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * licence.
     */

    @Test
    public void create_nullLicence_defaultLicence() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLicence(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_LICENCE, result.getLicence());
    }

    @Test
    public void update_differentLicence_setLicence() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLicence(URI.create("RandomLicence"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getLicence(), resource.getLicence());
    }

    @Test
    public void update_differentLicence_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLicence(URI.create("RandomLicence"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameLicence_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * sovereign.
     */

    @Test
    public void create_nullSovereign_defaultSovereign() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSovereign(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_SOVEREIGN, result.getSovereign());
    }

    @Test
    public void update_differentSovereign_setSovereign() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSovereign(URI.create("RandomSovereign"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getSovereign(), resource.getSovereign());
    }

    @Test
    public void update_differentSovereign_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSovereign(URI.create("RandomSovereign"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameSovereign_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * endpointDocumentation.
     */

    @Test
    public void create_nullEndpointDocumentation_defaultEndpointDocumentation() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setEndpointDocumentation(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_ENDPOINT_DOCS, result.getEndpointDocumentation());
    }

    @Test
    public void update_differentEndpointDocumentation_setEndpointDocumentation() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setEndpointDocumentation(URI.create("RandomEndpointDocumentation"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getEndpointDocumentation(), resource.getEndpointDocumentation());
    }

    @Test
    public void update_differentEndpointDocumentation_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setEndpointDocumentation(URI.create("RandomEndpointDocumentation"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameEndpointDocumentation_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }
    
    /**
     * additional.
     */

    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var representation = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), representation.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var representation = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(representation, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var representation = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(representation, new OfferedResourceDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * version
     */

    @Test
    public void create_version_is_1() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(1, result.getVersion());
    }

    @Test
    public void update_same_sameVersion() {
        /* ARRANGE */
        var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(1, resource.getVersion());
    }

    @Test
    public void update_different_incrementVersion() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setTitle("RANDOM TITLE");

        var resource = factory.create(desc);

        /* ACT */
        factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(2, resource.getVersion());
    }

    /**
     * update inputs.
     */

    @Test
    public void update_nullOfferedResource_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null, new OfferedResourceDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var contract = factory.create(new OfferedResourceDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(contract, null));
    }
}
