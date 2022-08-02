package org.ksoap2.serialization;

/**
 * Common inteface for classes which want to serialize attributes to outgoing soap message
 *
 * @author robocik
 */
public interface HasAttributes {
    int getAttributeCount();

    void getAttributeInfo(int index, AttributeInfo info);

    void getAttribute(int index, AttributeInfo info);

    void setAttribute(AttributeInfo info);
}
