package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

/*
    NOTE: All entities in our model use n to m relationships. Due to the use of JPA
    only one entity can be the owner of the relationship and only the owner may persist
    changes to the relationship. This means when both sides should be capable of persisting
    the CASCADING Annotation needs to be used. Here lies the problem. When getting an entity
    and all related entities you could modify its children. With the missing setter only
    the Factories are capable of changing but you could still change the children. And
    the decision if an entity should be persisted should only be with the Service responsible
    for the specific entity.
    Due to this the ownership of the relationship leaks into the class design. The upside
    is that the leakage stops here and you only need to care about the different linker
    if you are adding or removing relationships.
    The following class is basically a proxy class calling the right RelationshipService.
 */

/**
 * Creates a parent-children relationship between two types of resources.
 * Implements the non-owning side of a relationship.
 * @param <K> The type of the parent resource.
 * @param <W> The type of the child resource. (The owning side)
 * @param <T> The service type for the parent resource.
 * @param <X> The service type for the child resource.
 */
public abstract class NonOwningRelationService<
        K extends AbstractEntity,
        W extends AbstractEntity,
        T extends BaseEntityService<K, ?>,
        X extends BaseEntityService<W, ?>
        > extends AbstractRelationService<K, W, T, X> {

    /*
       NOTE: For some reason Spring does not find the owningService when the services are
       set. As long this does not create a problem, do not touch this.
     */
    /**
     * The service response for the inverse of this relation.
     */
    @Autowired
    private OwningRelationService<W, K, ?, ?> owningService;

    @Override
    protected final void addInternal(final UUID ownerId, final Set<UUID> entities) {
        final var set = Set.of(ownerId);
        entities.forEach(id -> owningService.add(id, set));
    }

    @Override
    public final void removeInternal(final UUID ownerId, final Set<UUID> entities) {
        final var set = Set.of(ownerId);
        entities.stream().peek(id -> owningService.remove(id, set)).close();
    }

    @Override
    public final void replaceInternal(final UUID ownerId, final Set<UUID> entities) {
        final var set = Set.of(ownerId);
        final var allRelations =
                getOneService().getAll(Pageable.unpaged()).stream().map(AbstractEntity::getId)
                               .collect(Collectors.toList());
        allRelations.stream().peek(id -> owningService.remove(id, set)).close();
        add(ownerId, entities);
    }
}
