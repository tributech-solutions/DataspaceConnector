package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ArtifactTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ContractTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RepresentationTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ResourceTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RuleTemplate;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for building entity templates.
 */
@Log4j2
public final class TemplateUtils {

    private TemplateUtils() {
        // not used
    }

    /**
     * Build resource template from ids resource.
     *
     * @param resource The ids resource.
     * @return The resource template.
     */
    public static ResourceTemplate<RequestedResourceDesc> getResourceTemplate(
            final Resource resource) {
        return MappingUtils.fromIdsResource(resource);
    }

    /**
     * Build a list of representation templates from ids resource.
     *
     * @param resource  The ids resource.
     * @param artifacts List of requested artifacts (remote id).
     * @param download  Indicated whether the artifact will be downloaded automatically.
     * @param accessUrl The access url to fetch the data.
     * @return List of representation templates.
     */
    public static List<RepresentationTemplate> getRepresentationTemplates(final Resource resource,
                                                                          final List<URI> artifacts,
                                                                          final boolean download,
                                                                          final URI accessUrl) {
        final var list = new ArrayList<RepresentationTemplate>();

        // Iterate over all representations.
        final var representationList = resource.getRepresentation();
        try {
            for (final var representation : Utils.requireNonNull(representationList,
                    ErrorMessages.LIST_NULL)) {
                final var template = MappingUtils.fromIdsRepresentation(representation);
                final var artifactTemplates = getArtifactTemplates(representation,
                        artifacts, download, accessUrl);

                // Representation is only saved if it contains requested artifacts.
                if (!artifactTemplates.isEmpty()) {
                    template.setArtifacts(artifactTemplates);
                    list.add(template);
                }
            }
        } catch (IllegalArgumentException exception) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Resource %s does not contain any representations.",
                        resource.getId()));
            }
        }

        return list;
    }

    /**
     * Build a list of artifact templates from ids representation.
     *
     * @param representation     The ids representation.
     * @param requestedArtifacts List of requested artifacts (remote ids).
     * @param download           Indicated whether the artifact will be downloaded automatically.
     * @param remoteUrl          The provider's url for receiving artifact request messages.
     * @return List of artifact templates.
     */
    public static List<ArtifactTemplate> getArtifactTemplates(final Representation representation,
                                                              final List<URI> requestedArtifacts,
                                                              final boolean download,
                                                              final URI remoteUrl) {
        final var list = new ArrayList<ArtifactTemplate>();

        // Iterate over all artifacts.
        final var artifactList = representation.getInstance();

        try {
            for (final var artifact : Utils.requireNonNull(artifactList, ErrorMessages.LIST_NULL)) {
                final var id = artifact.getId();

                // Artifact is only saved if it has been requested.
                if (requestedArtifacts.contains(id)) {
                    final var template = MappingUtils.fromIdsArtifact((Artifact) artifact,
                            download, remoteUrl);
                    list.add(template);
                }
            }
        } catch (IllegalArgumentException exception) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Representation %s does not contain any artifacts.",
                        representation.getId()));
            }
        }

        return list;
    }

    /**
     * Build a list of contract templates from ids resource.
     * NOTE: Keep method for later usage.
     *
     * @param resource The ids resource.
     * @return List of contract templates.
     */
    private static List<ContractTemplate> getContractTemplates(final Resource resource) {
        final var list = new ArrayList<ContractTemplate>();

        // Iterate over all contract offers.
        final var contractList = resource.getContractOffer();
        for (final var contract : contractList) {
            final var contractTemplate = MappingUtils.fromIdsContract(contract);

            contractTemplate.setRules(getRuleTemplates(contract));
            list.add(contractTemplate);
        }

        return list;
    }

    /**
     * Build a list of rule templates from ids contract.
     *
     * @param contract The ids contract.
     * @return List of rule templates.
     */
    private static List<RuleTemplate> getRuleTemplates(final Contract contract) {
        final var list = new ArrayList<RuleTemplate>();
        final var rules = PolicyUtils.extractRulesFromContract(contract);

        for (final var rule : rules) {
            final var template = MappingUtils.fromIdsRule(rule);
            list.add(template);
        }

        return list;
    }
}
