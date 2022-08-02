package org.ksoap2.serialization;
/**
 * Interface for classes requiring inner text of xml  tags
 *
 * @author satansly
 */
public interface HasInnerText {
     /**
     * Gets the inner text of xml tags
     */
    Object getInnerText();

    /**
     * @param s String to be set as inner text for an outgoing soap object
     */
    void setInnerText(Object s);
}
