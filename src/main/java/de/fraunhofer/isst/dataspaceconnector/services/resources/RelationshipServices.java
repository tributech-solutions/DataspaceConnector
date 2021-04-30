package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

public final class RelationshipServices {

    @Service
    @NoArgsConstructor
    public static class RuleContractLinker extends NonOwningRelationService<ContractRule, Contract,
            RuleService, ContractService> {

        @Override
        protected final List<Contract> getInternal(final ContractRule owner) {
            return owner.getContracts();
        }
    }


    @Service
    @NoArgsConstructor
    public static class ArtifactRepresentationLinker
            extends NonOwningRelationService<Artifact, Representation, ArtifactService,
            RepresentationService> {

        @Override
        protected final List<Representation> getInternal(final Artifact owner) {
            return owner.getRepresentations();
        }
    }


    @Service
    @NoArgsConstructor
    public static class RepresentationOfferedResourceLinker
            extends NonOwningRelationService<Representation, OfferedResource,
            RepresentationService, OfferedResourceService> {

        @Override
        protected final List<OfferedResource> getInternal(final Representation owner) {
            return (List<OfferedResource>) (List<?>) owner.getResources();
        }
    }


    @Service
    @NoArgsConstructor
    public static class RepresentationRequestedResourceLinker
            extends NonOwningRelationService<Representation, RequestedResource,
            RepresentationService, RequestedResourceService> {

        @Override
        protected final List<RequestedResource> getInternal(final Representation owner) {
            return (List<RequestedResource>) (List<?>) owner.getResources();
        }
    }


    @Service
    @NoArgsConstructor
    public static class OfferedResourceCatalogLinker
            extends NonOwningRelationService<OfferedResource, Catalog, OfferedResourceService,
            CatalogService> {

        @Override
        protected final List<Catalog> getInternal(final OfferedResource owner) {
            return owner.getCatalogs();
        }
    }


    @Service
    @NoArgsConstructor
    public static class RequestedResourceCatalogLinker
            extends NonOwningRelationService<RequestedResource, Catalog, RequestedResourceService,
            CatalogService> {

        @Override
        protected final List<Catalog> getInternal(final RequestedResource owner) {
            return owner.getCatalogs();
        }
    }


    @Service
    @NoArgsConstructor
    public static class ContractOfferedResourceLinker
            extends NonOwningRelationService<Contract, OfferedResource, ContractService,
            OfferedResourceService> {

        @Override
        protected final List<OfferedResource> getInternal(final Contract owner) {
            return (List<OfferedResource>) (List<?>) owner.getResources();
        }
    }


    @Service
    @NoArgsConstructor
    public static class ContractRequestedResourceLinker
            extends NonOwningRelationService<Contract, RequestedResource, ContractService,
            RequestedResourceService> {

        @Override
        protected final List<RequestedResource> getInternal(final Contract owner) {
            return (List<RequestedResource>) (List<?>) owner.getResources();
        }
    }


    @Service
    @NoArgsConstructor
    public static class AgreementArtifactLinker
            extends OwningRelationService<Agreement, Artifact, AgreementService, ArtifactService> {

        @Override
        protected final List<Artifact> getInternal(final Agreement owner) {
            return owner.getArtifacts();
        }
    }


    @Service
    @NoArgsConstructor
    public static class ArtifactAgreementLinker
            extends NonOwningRelationService<Artifact, Agreement, ArtifactService,
            AgreementService> {

        @Override
        protected final List<Agreement> getInternal(final Artifact owner) {
            return owner.getAgreements();
        }
    }
}
