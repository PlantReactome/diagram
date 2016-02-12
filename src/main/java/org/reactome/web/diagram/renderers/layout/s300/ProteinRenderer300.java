package org.reactome.web.diagram.renderers.layout.s300;

import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.category.ShapeCategory;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.abs.ProteinAbstractRenderer;
import org.reactome.web.diagram.renderers.layout.abs.TextRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;
import java.util.Objects;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ProteinRenderer300 extends ProteinAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.draw(ctx, item, factor, offset);
        Node node = (Node) item;
        drawAttachments(ctx, node, factor, offset, true);
        drawSummaryItems(ctx, node, factor, offset);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.highlight(ctx, item, factor, offset);
        Node node = (Node) item;
        drawAttachments(ctx, node, factor, offset, false);
        drawSummaryItems(ctx, node, factor, offset);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        drawProteinDetails(ctx, item, factor, offset);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        Node node = (Node) item;
        if (node.getNodeAttachments() != null) {
            for (NodeAttachment attachment : node.getNodeAttachments()) {
                if (ShapeCategory.isHovered(attachment.getShape(), pos)) {
                    return new HoveredItem(node.getId(), attachment);
                }
            }
        }

        SummaryItem interactorsSummary = node.getInteractorsSummary();
        if (interactorsSummary != null) {
            if (ShapeCategory.isHovered(interactorsSummary.getShape(), pos)) {
                return new HoveredItem(node.getId(), interactorsSummary);
            }
        }
        return super.getHovered(item, pos);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    private void drawProteinDetails(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){
        ctx.save();

        Node node = (Node) item;
        GraphObject graphObject = node.getGraphObject();
        if(graphObject instanceof GraphEntityWithAccessionedSequence) {
            //The image size is supposed to fit the height of the box (and it is a SQUARE)
            DiagramBox box = (new DiagramBox(node.getProp())).transform(factor, offset);

            GraphEntityWithAccessionedSequence pe = (GraphEntityWithAccessionedSequence) graphObject;
            if (pe.getProteinImage() != null) {
                Coordinate pos = CoordinateFactory.get(box.getMinX(), box.getMinY());
                double delta = box.getHeight();
                ctx.drawImage(pe.getProteinImage(), pos.getX(), pos.getY(), delta, delta);
            }

            box = box.splitHorizontally(box.getHeight()).get(1); //box is now the remaining of item box removing the image
            TextRenderer textRenderer = new TextRenderer(RendererProperties.INTERACTOR_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);

            String details = pe.getDetails();
            if (details == null) {
                if (Objects.equals(node.getDisplayName(), pe.getIdentifier())) {
                    textRenderer.drawTextMultiLine(ctx, node.getDisplayName(), NodePropertiesFactory.get(box));
                } else {
                    List<DiagramBox> vBoxes = box.splitVertically(box.getHeight() * 0.6);
                    textRenderer.drawTextMultiLine(ctx, node.getDisplayName(), NodePropertiesFactory.get(vBoxes.get(0)));
                    textRenderer.drawTextMultiLine(ctx, pe.getIdentifier(), NodePropertiesFactory.get(vBoxes.get(1)));
                }
            } else {
                List<DiagramBox> vBoxes = box.splitVertically(box.getHeight() * 0.3, box.getHeight() * 0.5);
                //If there is not details it means that we can use the whole right half of the box to write the alias
                ctx.setFont(RendererProperties.getFont(RendererProperties.INTERACTOR_FONT_SIZE));
                DiagramBox aliasBox = vBoxes.get(0);

                textRenderer.drawTextMultiLine(ctx, node.getDisplayName(), NodePropertiesFactory.get(aliasBox));

                double fontSize = 3 * factor;
                ctx.setFont(RendererProperties.getFont(fontSize));
                textRenderer = new TextRenderer(fontSize, RendererProperties.NODE_TEXT_PADDING);
                textRenderer.drawTextSingleLine(ctx, pe.getIdentifier(), vBoxes.get(1).getCentre());

                DiagramBox detailsBox = vBoxes.get(2);
                textRenderer.drawPreformattedText(ctx, details, NodePropertiesFactory.get(detailsBox));
            }
        }
        ctx.restore();
    }
}
