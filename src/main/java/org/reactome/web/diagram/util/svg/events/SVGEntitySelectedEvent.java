package org.reactome.web.diagram.util.svg.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.util.svg.handlers.SVGEntitySelectedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGEntitySelectedEvent extends GwtEvent<SVGEntitySelectedHandler> {
    public static Type<SVGEntitySelectedHandler> TYPE = new Type<>();

    private String selected;

    public SVGEntitySelectedEvent(String selected) {
        this.selected = selected;
    }

    @Override
    public Type<SVGEntitySelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SVGEntitySelectedHandler handler) {
        handler.onSVGEntitySelected(this);
    }

    public String getSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return "SVGEntitySelectedEvent{" +
                "selected='" + selected + '\'' +
                '}';
    }
}
