package org.ksoap2.serialization;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by robocik on 2015-09-25.
 */
public interface ValueWriter
{
    void write(XmlSerializer writer) throws IOException;
}
