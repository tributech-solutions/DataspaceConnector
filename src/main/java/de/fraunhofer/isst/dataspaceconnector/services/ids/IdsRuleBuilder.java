package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.net.URI;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The base class for constructing an ids rule from a DSC rule.
 *
 * @param <T> The ids rule type.
 */
@RequiredArgsConstructor
public class IdsRuleBuilder<T extends Rule> extends AbstractIdsBuilder<ContractRule, T> {

    /**
     * The service for deserializing strings to ids rules.
     */
    private final @NonNull DeserializationService deserializer;

    /**
     * The type of the rule to be build. Needed for the deserializer.
     */
    private final @NonNull Class<T> ruleType;

    @Override
    protected final T createInternal(final ContractRule rule, final URI baseUri,
                                     final int currentDepth, final int maxDepth)
            throws ConstraintViolationException {
        final var idsRule = deserializer.getRule(rule.getValue());
        final var selfLink = getAbsoluteSelfLink(rule, baseUri);
        var newRule = rule.getValue();

        // Note: Infomodel deserializer sets autogen ID, when ID is missing in original rule value.
        // If autogen ID not present in original rule value, it's equal to rule not having ID
        if (idsRule.getId() == null || rule.getValue().indexOf(idsRule.getId().toString()) == -1) {
            // No id has been set for this rule. Thus, no references can be found.
            // Inject the real id.
            newRule = newRule.substring(0, newRule.indexOf("{") + 1)
                    + "\"@id\": \""
                    + selfLink + "\","
                    + newRule
                    .substring(newRule.indexOf("{") + 1);
        } else {
            // The id has been set, there may be references.
            // Search for the id and replace everywhere.
            newRule = newRule.replace(idsRule.getId().toString(), selfLink.toString());

        }

        return deserializer.getRule(newRule, ruleType);
    }
}
