package org.reactome.web.diagram.util.svg;

import com.google.gwt.regexp.shared.RegExp;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class SVGUtil {

    private static final String STID_PATTERN = "R-[A-Z]{3}-[0-9]{3,}(\\.[0-9]+)?";
    private static RegExp regExp = RegExp.compile(STID_PATTERN);

    public static List<OMElement> getAnnotatedOMElements(OMSVGSVGElement svg) {
        List<OMElement> rtn = new ArrayList<>();
        for (OMElement child : svg.getElementsByTagName(new OMSVGGElement().getTagName())) {
            if(isAnnotated(child.getId())) {
                rtn.add(child);
            }
        }
        return rtn;
    }

    public static boolean isAnnotated(String input) {
        return input != null && regExp.test(input);
    }

    /***
     * Takes as input a string like "OVERVIEW-R-SSS-NNNNNNN"
     * or "REGION-R-SSS-NNNNNN, filters out the first part and
     * keeps only the stable identifier.
     *
     * @param identifier the id of the SVG element
     * @return the stable identifier
     */
    public static String keepStableId(String identifier) {
        String rtn = null;
        if(identifier!=null && !identifier.isEmpty()) {
            rtn = identifier.substring(identifier.indexOf('-') + 1);
        }
        return rtn;
    }

    /*
     * Checks whether two transform matrices are the same
     */

    /***
     * Checks whether two transformation matrices are equal.
     *
     * @param m1 the first transformation matrix
     * @param m2 the second transformation matrix
     * @return True only in case the two matrices are equal
     */
    public static boolean areEqual(OMSVGMatrix m1, OMSVGMatrix m2) {
        boolean rtn;
        if (m1 == null || m2 == null ) {
            rtn = false;
        } else if (m1 == m2) {
            rtn = true;
        } else if (
                m1.getA() != m2.getA() ||
                m1.getB() != m2.getB() ||
                m1.getC() != m2.getC() ||
                m1.getD() != m2.getD() ||
                m1.getE() != m2.getE() ||
                m1.getF() != m2.getF()
                ) {
            rtn = false;
        } else {
            rtn = true;
        }
        return rtn;
    }
}