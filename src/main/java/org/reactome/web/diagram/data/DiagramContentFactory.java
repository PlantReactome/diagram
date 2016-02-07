package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.data.graph.raw.EventNode;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.graph.raw.SubpathwayNode;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.layout.Diagram;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.MapSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class DiagramContentFactory {

    public static DiagramContent getDiagramContent(Diagram diagram) {
        DiagramContent content = new DiagramContent();

        //Read and set general pathway information
        content.stableId = diagram.getStableId();
        content.displayName = diagram.getDisplayName();
        content.dbId = diagram.getDbId();
        content.forNormalDraw = diagram.getForNormalDraw();
        content.isDisease = diagram.getIsDisease();

        content.cache(diagram.getNodes());
        content.cache(diagram.getNotes());
        content.cache(diagram.getEdges());
        content.cache(diagram.getLinks());
        content.cache(diagram.getCompartments());
        content.cache(diagram.getShadows());

        content.minX = diagram.getMinX().doubleValue();
        content.maxX = diagram.getMaxX().doubleValue();
        content.minY = diagram.getMinY().doubleValue();
        content.maxY = diagram.getMaxY().doubleValue();

        return content.init();
    }

    public static void fillGraphContent(DiagramContent content, Graph graph) {
        GraphObjectFactory.content = content;

        for (EntityNode node : graph.getNodes()) {
            GraphObjectFactory.getOrCreateDatabaseObject(node);
        }
        for (EventNode edge : graph.getEdges()) {
            GraphObjectFactory.getOrCreateDatabaseObject(edge);
        }

        for (EntityNode node : graph.getNodes()) {
            GraphObject obj = content.getDatabaseObject(node.getDbId());
            if (obj instanceof GraphPhysicalEntity) {
                GraphPhysicalEntity pe = (GraphPhysicalEntity) obj;
                for (DiagramObject diagramObject : getDiagramObjects(node.getDiagramIds())) {
                    pe.addDiagramObject(diagramObject);
                    diagramObject.setGraphObject(pe);
                }

                List<GraphPhysicalEntity> parents = getDatabaseObjects(node.getParents());
                pe.addParent(parents);

                List<GraphPhysicalEntity> children = getDatabaseObjects(node.getChildren());
                pe.addChildren(children);
            } else if (obj instanceof GraphPathway) {
                GraphPathway pathway = (GraphPathway) obj;
                for (DiagramObject diagramObject : getDiagramObjects(node.getDiagramIds())) {
                    pathway.addDiagramObject(diagramObject);
                    diagramObject.setGraphObject(pathway);
                }

                //TODO: Need to keep parents and/or children?
            }
        }

        for (EventNode edge : graph.getEdges()) {
            GraphReactionLikeEvent event = (GraphReactionLikeEvent) content.getDatabaseObject(edge.getDbId());

            for (DiagramObject diagramObject : getDiagramObjects(edge.getDiagramIds())) {
                event.addDiagramObject(diagramObject);
                diagramObject.setGraphObject(event);
            }

            List<GraphPhysicalEntity> inputs = getDatabaseObjects(edge.getInputs());
            event.setInputs(inputs);

            List<GraphPhysicalEntity> outputs = getDatabaseObjects(edge.getOutputs());
            event.setOutputs(outputs);

            List<GraphPhysicalEntity> catalysts = getDatabaseObjects(edge.getCatalysts());
            event.setCatalysts(catalysts);

            List<GraphPhysicalEntity> activators = getDatabaseObjects(edge.getActivators());
            event.setActivators(activators);

            List<GraphPhysicalEntity> inhibitors = getDatabaseObjects(edge.getInhibitors());
            event.setInhibitors(inhibitors);

            List<GraphPhysicalEntity> requirements = getDatabaseObjects(edge.getRequirements());
            event.setRequirements(requirements);

            List<GraphReactionLikeEvent> preceding = getDatabaseObjects(edge.getPreceding());
            event.setPrecedingEvents(preceding);

            List<GraphReactionLikeEvent> following = getDatabaseObjects(edge.getFollowing());
            event.setFollowingEvents(following);
        }

        if (graph.getSubpathways() != null) {
            for (SubpathwayNode subpathway : graph.getSubpathways()) {
                GraphSubpathway sp = GraphObjectFactory.getOrCreateDatabaseObject(subpathway);
                for (Long event : subpathway.getEvents()) {
                    sp.addContainedEvent((GraphEvent) content.getDatabaseObject(event));
                }
            }
        }
    }

    private static List<DiagramObject> getDiagramObjects(List<Long> ids) {
        List<DiagramObject> rtn = new ArrayList<>();
        if (ids != null) {
            for (Long id : ids) {
                rtn.add(GraphObjectFactory.content.getDiagramObject(id));
            }
        }
        return rtn;
    }

    private static <T extends GraphObject> List<T> getDatabaseObjects(List<Long> dbIds) {
        List<T> rtn = new ArrayList<>();
        if (dbIds != null) {
            for (Long dbId : dbIds) {
                //noinspection unchecked
                T t = (T) GraphObjectFactory.content.getDatabaseObject(dbId);
                if (t == null) {
                    Console.error("There is no information for " + dbId);
                } else {
                    rtn.add(t);
                }
            }
        }
        return rtn;
    }

    public static void fillInteractorsContent(DiagramContext context, RawInteractors rawInteractors) {
        DiagramContent content = context.getContent();
        InteractorsContent interactors = context.getInteractors();
        MapSet<String, GraphObject> identifierMap = content.getIdentifierMap();
        String resource = rawInteractors.getResource();
        //The next line creates an empty entry in the cache
        interactors.getOrCreateRawInteractorCachedResource(resource);
        for (RawInteractorEntity interactorEntity : rawInteractors.getEntities()) {
            String acc = interactorEntity.getAcc();
            interactors.cacheInteractors(resource, acc, interactorEntity.getCount(), identifierMap);
            for (RawInteractor rawInteractor : interactorEntity.getInteractors()) {
                interactors.cache(resource, acc, rawInteractor);
            }
        }
    }
}
