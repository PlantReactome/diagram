package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.reactome.web.diagram.handlers.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramViewer extends IsWidget, HasHandlers, RequiresResize {

    HandlerRegistration addAnalysisResetHandler(AnalysisResetHandler handler);

    HandlerRegistration addCanvasNotSupportedEventHandler(CanvasNotSupportedHandler handler);

    HandlerRegistration addDatabaseObjectSelectedHandler(GraphObjectSelectedHandler handler);

    HandlerRegistration addDatabaseObjectHoveredHandler(GraphObjectHoveredHandler handler);

    HandlerRegistration addDiagramLoadedHandler(ContentLoadedHandler handler);

    HandlerRegistration addDiagramObjectsFlaggedHandler(DiagramObjectsFlaggedHandler handler);

    HandlerRegistration addDiagramObjectsFlagResetHandler(DiagramObjectsFlagResetHandler handler);

    HandlerRegistration addFireworksOpenedHandler(FireworksOpenedHandler handler);

    HandlerRegistration addInteractorHoveredHandler(InteractorHoveredHandler handler);

    void flagItems(String identifier);

    void highlightItem(String stableIdentifier);

    void highlightItem(Long dbIdentifier);

    void loadDiagram(String stId);

    void loadDiagram(Long dbId);

    void resetAnalysis();

    void resetFlaggedItems();

    void resetHighlight();

    void resetSelection();

    void selectItem(String stableIdentifier);

    void selectItem(Long dbIdentifier);

    void setAnalysisToken(String token, String resource);

    void setVisible(boolean visible);

}
