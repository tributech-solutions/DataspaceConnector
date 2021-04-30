package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ProhibitionImpl;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleFactory;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ContractRuleFactory.class, IdsProhibitionBuilder.class,
        DeserializationService.class, SerializerProvider.class})
public class IdsProhibitionBuilderTest {

    @Autowired
    private ContractRuleFactory contractRuleFactory;

    @Autowired
    private IdsProhibitionBuilder idsProhibitionBuilder;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    @Test
    public void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> idsProhibitionBuilder.create(null));
    }

    @Test
    public void create_ruleWithoutId_returnRuleWithNewId() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithoutId());

        /* ACT */
        final var idsRule = idsProhibitionBuilder.create(rule);

        /* ASSERT */
        assertEquals(ProhibitionImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));

        assertEquals(1, idsRule.getAction().size());
        assertEquals(Action.USE, idsRule.getAction().get(0));
        assertNull(idsRule.getConstraint());
        assertEquals(1, idsRule.getDescription().size());
        assertEquals("prohibit-access", idsRule.getDescription().get(0).getValue());
    }

    @Test
    public void create_ruleWithId_returnRuleWithReplacedId() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithId());

        /* ACT */
        final var idsRule = idsProhibitionBuilder.create(rule);

        /* ASSERT */
        assertEquals(ProhibitionImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));

        assertEquals(1, idsRule.getAction().size());
        assertEquals(Action.USE, idsRule.getAction().get(0));
        assertNull(idsRule.getConstraint());
        assertEquals(1, idsRule.getDescription().size());
        assertEquals("prohibit-access", idsRule.getDescription().get(0).getValue());
    }

    @Test
    public void create_invalidRuleJson_throwIllegalArgumentException() {
        /* ARRANGE */
        final var json = "{\"not\": \"a rule\"}";
        final var rule = getContractRule(json);

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> idsProhibitionBuilder.create(rule));
    }

    @Test
    public void create_ruleJsonWithInvalidType_throwIllegalArgumentException() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithInvalidType());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> idsProhibitionBuilder.create(rule));
    }

    @Test
    public void create_ruleJsonWithMissingAction_returnRuleWithMissingAction() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithMissingAction());

        /* ACT */
        final var idsRule = idsProhibitionBuilder.create(rule);

        /* ASSERT */
        assertEquals(ProhibitionImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));
        assertNull(idsRule.getAction());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    @SneakyThrows
    private ContractRule getContractRule(final String value) {
        final var ruleDesc = new ContractRuleDesc();
        ruleDesc.setTitle("title");
        ruleDesc.setValue(value);
        final var rule = contractRuleFactory.create(ruleDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(rule, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(rule, date);

        return rule;
    }

    private String getRuleWithId() {
        return "{\n"
                + "    \"@type\" : \"ids:Prohibition\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/prohibition/ff1b43b9-f3b1-44b1"
                + "-a826-2efccc199a76\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"prohibit-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:USE\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";
    }

    private String getRuleWithoutId() {
        return "{\n"
                + "    \"@type\" : \"ids:Prohibition\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"prohibit-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:USE\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";
    }

    private String getRuleWithInvalidType() {
        return "{\n"
                + "    \"@type\" : \"ids:Representation\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/prohibition/ff1b43b9-f3b1-44b1"
                + "-a826-2efccc199a76\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"prohibit-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:USE\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";
    }

    private String getRuleWithMissingAction() {
        return "{\n"
                + "    \"@type\" : \"ids:Prohibition\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/prohibition/ff1b43b9-f3b1-44b1"
                + "-a826-2efccc199a76\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"prohibit-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";
    }

}
