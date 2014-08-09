package org.ksoap2.serialization;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.ksoap2.*;
import org.ksoap2.transport.mock.*;

public class MarshalHashtableTest extends TestCase {

    private static final String VALUE1 = "value1";
    private static final String KEY1_NAME = "key1";

    // should work on a read instance.  But too complicated.
    
    public void testWriteInstance() throws IOException {
        MarshalHashtable marshalHashtable = new MarshalHashtable();
        SoapSerializationEnvelope serializationEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        marshalHashtable.register(serializationEnvelope);
        MockXmlSerializer writer = new MockXmlSerializer();
        Hashtable hashTable = prefilledHashTable();
        marshalHashtable.writeInstance(writer, hashTable, null);

        // the mock just appends the bits together
        assertEquals(KEY1_NAME + ";" + VALUE1, writer.getOutputText());
        assertEquals(MockXmlSerializer.PREFIX + ":string;" + MockXmlSerializer.PREFIX + ":string",
                writer.getPropertyType());
        assertEquals("item;key;value", writer.getStartTag());
        assertEquals("key;value;item", writer.getEndtag());
        
        writer = new MockXmlSerializer();
        hashTable = prefilledHashTable();
        hashTable.put("key2", new Integer(12));
        marshalHashtable.writeInstance(writer, hashTable, null);

        assertEquals("key2;12;"+KEY1_NAME + ";" + VALUE1, writer.getOutputText());
        assertEquals(MockXmlSerializer.PREFIX + ":string;" + MockXmlSerializer.PREFIX + ":int;" +
                MockXmlSerializer.PREFIX + ":string;" + MockXmlSerializer.PREFIX + ":string", writer.getPropertyType());
        assertEquals("item;key;value;item;key;value", writer.getStartTag());
        assertEquals("key;value;item;key;value;item", writer.getEndtag());
    }

    private Hashtable prefilledHashTable() {
        Hashtable hashtable = new Hashtable();
        hashtable.put(KEY1_NAME, VALUE1);
        return hashtable;
    }

}
