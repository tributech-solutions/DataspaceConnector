package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ArtifactView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend.ArtifactBFFService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend.CommonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/artifacts")
public final class ArtifactController extends BaseResourceController<Artifact,
        ArtifactDesc, ArtifactView, CommonService<Artifact, ArtifactDesc, ArtifactView>> {
    @RequestMapping(value = "{id}/data", method = RequestMethod.GET)
    public ResponseEntity<Object> getData(@Valid @PathVariable final UUID id) {
        final var currentEndpointId = getCurrentEndpoint(id);
        final var artifactService = ((ArtifactBFFService) this.getService());
        return ResponseEntity.ok(artifactService.getData(currentEndpointId));
    }

    private EndpointId getCurrentEndpoint(final UUID id) {
        var basePath = ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toString();

        final var index = basePath.lastIndexOf(id.toString());
        // -1 so that the / gets also removed
        basePath = basePath.substring(0, index - 1);

        return new EndpointId(basePath, id);
    }
}