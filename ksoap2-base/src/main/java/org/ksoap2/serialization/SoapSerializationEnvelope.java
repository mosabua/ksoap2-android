/*
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ksoap2.serialization;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.SoapFault12;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author Stefan Haustein
 *
 *         This class extends the SoapEnvelope with Soap Serialization functionality.
 */
public class SoapSerializationEnvelope extends SoapEnvelope
{
	protected static final int QNAME_TYPE = 1;
	protected static final int QNAME_NAMESPACE = 0;
	protected static final int QNAME_MARSHAL = 3;
	private static final String ANY_TYPE_LABEL = "anyType";
	private static final String ARRAY_MAPPING_NAME = "Array";
	private static final String NULL_LABEL = "null";
	private static final String NIL_LABEL = "nil";
	private static final String HREF_LABEL = "href";
	private static final String ID_LABEL = "id";
	private static final String ROOT_LABEL = "root";
	private static final String TYPE_LABEL = "type";
	private static final String ITEM_LABEL = "item";
	private static final String ARRAY_TYPE_LABEL = "arrayType";
	static final Marshal DEFAULT_MARSHAL = new DM();
	public Hashtable properties = new Hashtable();

	Hashtable idMap = new Hashtable();
	Vector multiRef; // = new Vector();

	/**
	 * Set this variable to true if you don't want that type definitions for complex types/objects
	 * are automatically generated (with type "anyType") in the XML-Request, if you don't call the
	 * Method addMapping. This is needed by some Servers which have problems with these type-definitions.
	 */
	public boolean implicitTypes;

	/**
	 * Set this variable to true for compatibility with what seems to be the default encoding for
	 * .Net-Services. This feature is an extremely ugly hack. A much better option is to change the
	 * configuration of the .Net-Server to standard Soap Serialization!
	 */

	public boolean dotNet;

	/**
	 * Map from XML qualified names to Java classes
	 */

	protected Hashtable qNameToClass = new Hashtable();

	/**
	 * Map from Java class names to XML name and namespace pairs
	 */

	protected Hashtable classToQName = new Hashtable();

	/**
	 * Set to true to add and ID and ROOT label to the envelope. Change to false for compatibility with WSDL.
	 */
	protected boolean addAdornments = true;

	public SoapSerializationEnvelope(int version)
	{
		super(version);
		addMapping(enc, ARRAY_MAPPING_NAME, PropertyInfo.VECTOR_CLASS);
		DEFAULT_MARSHAL.register(this);
	}

	/**
	 * @return the addAdornments
	 */
	public boolean isAddAdornments()
	{
		return addAdornments;
	}

	/**
	 * @param addAdornments
	 *            the addAdornments to set
	 */
	public void setAddAdornments(boolean addAdornments)
	{
		this.addAdornments = addAdornments;
	}

    /**
     * Set the bodyOut to be empty so that no un-needed xml is create. The null value for bodyOut will
     * cause #writeBody to skip writing anything redundant.
     * @param emptyBody
     * @see "http://code.google.com/p/ksoap2-android/issues/detail?id=77"
     */
    public void setBodyOutEmpty(boolean emptyBody) {
        if (emptyBody) {
            bodyOut = null;
        }
    }

	public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		bodyIn = null;
		parser.nextTag();
		if (parser.getEventType() == XmlPullParser.START_TAG && parser.getNamespace().equals(env)
				&& parser.getName().equals("Fault"))
		{
			SoapFault fault;
			if (this.version < SoapEnvelope.VER12) {
				fault = new SoapFault(this.version);
			} else {
				fault = new SoapFault12(this.version);
			}
			fault.parse(parser);
			bodyIn = fault;
		}
		else
		{
			while (parser.getEventType() == XmlPullParser.START_TAG)
			{
				String rootAttr = parser.getAttributeValue(enc, ROOT_LABEL);

				Object o = read(parser, null, -1, parser.getNamespace(), parser.getName(),
						PropertyInfo.OBJECT_TYPE);
				if ("1".equals(rootAttr) || bodyIn == null)
					bodyIn = o;
				parser.nextTag();
			}
		}
	}

	/** Read a SoapObject. This extracts any attributes and then reads the object as a KvmSerializable. */
	protected void readSerializable(XmlPullParser parser, SoapObject obj) throws IOException,
			XmlPullParserException
	{
		for (int counter = 0; counter < parser.getAttributeCount(); counter++)
		{
			String attributeName = parser.getAttributeName(counter);
			String value = parser.getAttributeValue(counter);
			((SoapObject) obj).addAttribute(attributeName, value);
		}
		readSerializable(parser, (KvmSerializable) obj);
	}

	/** Read a KvmSerializable. */
	protected void readSerializable(XmlPullParser parser, KvmSerializable obj) throws IOException,
			XmlPullParserException
	{
		int testIndex = -1; // inc at beg. of loop for perf. reasons
		int propertyCount = obj.getPropertyCount();
		PropertyInfo info = new PropertyInfo();
		while (parser.nextTag() != XmlPullParser.END_TAG)
		{
			String name = parser.getName();
			int countdown = propertyCount;
			// I don't really understand what's going on in this "while(true)"
			// clause. The structure surely is wrong "while(true)" with a break is
			// pretty much always because the person who wrote it couldn't figure out what
			// it was really supposed to be doing.
			// So, here's a little CYA since I think the code is only broken for
			// implicitTypes
			if (!implicitTypes || !(obj instanceof SoapObject))
			{
				while (true)
				{
					if (countdown-- == 0)
					{
						throw new RuntimeException("Unknown Property: " + name);
					}
					if (++testIndex >= propertyCount)
					{
						testIndex = 0;
					}
					obj.getPropertyInfo(testIndex, properties, info);
					if (info.namespace == null && name.equals(info.name) || info.name == null
							&& testIndex == 0 || name.equals(info.name)
							&& parser.getNamespace().equals(info.namespace))
					{
						break;
					}
				}
				obj.setProperty(testIndex, read(parser, obj, testIndex, null, null, info));
			}
			else
			{
				// I can only make this work for SoapObjects - hence the check above
				// I don't understand namespaces well enough to know whether it is correct in the next line...
				((SoapObject) obj).addProperty(parser.getName(), read(parser, obj, obj.getPropertyCount(),
						((SoapObject) obj).getNamespace(), name, PropertyInfo.OBJECT_TYPE));
			}
		}
		parser.require(XmlPullParser.END_TAG, null, null);
	}

	/**
	 * If the type of the object cannot be determined, and thus no Marshal class can handle the object, this
	 * method is called. It will build either a SoapPrimitive or a SoapObject
	 *
	 * @param parser
	 * @param typeNamespace
	 * @param typeName
	 * @return unknownObject wrapped as a SoapPrimitive or SoapObject
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	protected Object readUnknown(XmlPullParser parser, String typeNamespace, String typeName)
			throws IOException, XmlPullParserException
	{
		String name = parser.getName();
		String namespace = parser.getNamespace();

        // cache the attribute info list from the current element before we move on
        Vector attributeInfoVector = new Vector();
        for (int attributeCount = 0; attributeCount < parser.getAttributeCount(); attributeCount ++)
        {
            AttributeInfo attributeInfo = new AttributeInfo();
            attributeInfo.setName(parser.getAttributeName(attributeCount));
            attributeInfo.setValue(parser.getAttributeValue(attributeCount));
            attributeInfo.setNamespace(parser.getAttributeNamespace(attributeCount));
            attributeInfo.setType(parser.getAttributeType(attributeCount));
            attributeInfoVector.addElement(attributeInfo);
        }

		parser.next(); // move to text, inner start tag or end tag
		Object result = null;
		String text = null;
		if (parser.getEventType() == XmlPullParser.TEXT)
		{
			text = parser.getText();
            SoapPrimitive sp = new SoapPrimitive(typeNamespace, typeName, text);
			result = sp;
              // apply all the cached attribute info list before we add the property and descend further for parsing
            for (int i = 0; i < attributeInfoVector.size(); i++) {
                sp.addAttribute((AttributeInfo) attributeInfoVector.elementAt(i));
            }
			parser.next();
		}
		else if (parser.getEventType() == XmlPullParser.END_TAG)
		{
            SoapObject so = new SoapObject(typeNamespace, typeName);
            // apply all the cached attribute info list before we add the property and descend further for parsing
            for (int i = 0; i < attributeInfoVector.size(); i++) {
                so.addAttribute((AttributeInfo) attributeInfoVector.elementAt(i));
            }
			result = so;
		}

		if (parser.getEventType() == XmlPullParser.START_TAG)
		{
			if (text != null && text.trim().length() != 0)
			{
				throw new RuntimeException("Malformed input: Mixed content");
			}
			SoapObject so = new SoapObject(typeNamespace, typeName);
            // apply all the cached attribute info list before we add the property and descend further for parsing
            for (int i = 0; i < attributeInfoVector.size(); i++) {
                so.addAttribute((AttributeInfo) attributeInfoVector.elementAt(i));
            }

			while (parser.getEventType() != XmlPullParser.END_TAG)
			{
				so.addProperty(parser.getName(), read(parser, so, so.getPropertyCount(), null, null,
						PropertyInfo.OBJECT_TYPE));
				parser.nextTag();
			}
			result = so;
		}
		parser.require(XmlPullParser.END_TAG, namespace, name);
		return result;
	}

	private int getIndex(String value, int start, int dflt)
	{
		if (value == null)
			return dflt;
		return value.length() - start < 3 ? dflt : Integer.parseInt(value.substring(start + 1,
				value.length() - 1));
	}

	protected void readVector(XmlPullParser parser, Vector v, PropertyInfo elementType) throws IOException,
			XmlPullParserException
	{
		String namespace = null;
		String name = null;
		int size = v.size();
		boolean dynamic = true;
		String type = parser.getAttributeValue(enc, ARRAY_TYPE_LABEL);
		if (type != null)
		{
			int cut0 = type.indexOf(':');
			int cut1 = type.indexOf("[", cut0);
			name = type.substring(cut0 + 1, cut1);
			String prefix = cut0 == -1 ? "" : type.substring(0, cut0);
			namespace = parser.getNamespace(prefix);
			size = getIndex(type, cut1, -1);
			if (size != -1)
			{
				v.setSize(size);
				dynamic = false;
			}
		}
		if (elementType == null)
			elementType = PropertyInfo.OBJECT_TYPE;
		parser.nextTag();
		int position = getIndex(parser.getAttributeValue(enc, "offset"), 0, 0);
		while (parser.getEventType() != XmlPullParser.END_TAG)
		{
			// handle position
			position = getIndex(parser.getAttributeValue(enc, "position"), 0, position);
			if (dynamic && position >= size)
			{
				size = position + 1;
				v.setSize(size);
			}
			// implicit handling of position exceeding specified size
			v.setElementAt(read(parser, v, position, namespace, name, elementType), position);
			position++;
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, null, null);
	}

	/**
	 * Builds an object from the XML stream. This method is public for usage in conjuction with Marshal
	 * subclasses. Precondition: On the start tag of the object or property, so href can be read.
	 */

	public Object read(XmlPullParser parser, Object owner, int index, String namespace, String name,
			PropertyInfo expected) throws IOException, XmlPullParserException
	{
		String elementName = parser.getName();
		String href = parser.getAttributeValue(null, HREF_LABEL);
		Object obj;
		if (href != null)
		{
			if (owner == null)
				throw new RuntimeException("href at root level?!?");
			href = href.substring(1);
			obj = idMap.get(href);
			if (obj == null || obj instanceof FwdRef)
			{
				FwdRef f = new FwdRef();
				f.next = (FwdRef) obj;
				f.obj = owner;
				f.index = index;
				idMap.put(href, f);
				obj = null;
			}
			parser.nextTag(); // start tag
			parser.require(XmlPullParser.END_TAG, null, elementName);
		}
		else
		{
			String nullAttr = parser.getAttributeValue(xsi, NIL_LABEL);
			String id = parser.getAttributeValue(null, ID_LABEL);
			if (nullAttr == null)
				nullAttr = parser.getAttributeValue(xsi, NULL_LABEL);
			if (nullAttr != null && SoapEnvelope.stringToBoolean(nullAttr))
			{
				obj = null;
				parser.nextTag();
				parser.require(XmlPullParser.END_TAG, null, elementName);
			}
			else
			{
				String type = parser.getAttributeValue(xsi, TYPE_LABEL);
				if (type != null)
				{
					int cut = type.indexOf(':');
					name = type.substring(cut + 1);
					String prefix = cut == -1 ? "" : type.substring(0, cut);
					namespace = parser.getNamespace(prefix);
				}
				else if (name == null && namespace == null)
				{
					if (parser.getAttributeValue(enc, ARRAY_TYPE_LABEL) != null)
					{
						namespace = enc;
						name = ARRAY_MAPPING_NAME;
					}
					else
					{
						Object[] names = getInfo(expected.type, null);
						namespace = (String) names[0];
						name = (String) names[1];
					}
				}
				// be sure to set this flag if we don't know the types.
				if (type == null)
				{
					implicitTypes = true;
				}
				obj = readInstance(parser, namespace, name, expected);
				if (obj == null)
					obj = readUnknown(parser, namespace, name);
			}
			// finally, care about the id....
			if (id != null)
			{
				Object hlp = idMap.get(id);
				if (hlp instanceof FwdRef)
				{
					FwdRef f = (FwdRef) hlp;
					do
					{
						if (f.obj instanceof KvmSerializable)
							((KvmSerializable) f.obj).setProperty(f.index, obj);
						else
							((Vector) f.obj).setElementAt(obj, f.index);
						f = f.next;
					}
					while (f != null);
				}
				else if (hlp != null)
					throw new RuntimeException("double ID");
				idMap.put(id, obj);
			}
		}

		parser.require(XmlPullParser.END_TAG, null, elementName);
		return obj;
	}

	/**
	 * Returns a new object read from the given parser. If no mapping is found, null is returned. This method
	 * is used by the SoapParser in order to convert the XML code to Java objects.
	 */
	public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected)
			throws IOException, XmlPullParserException
	{
		Object obj = qNameToClass.get(new SoapPrimitive(namespace, name, null));
		if (obj == null)
			return null;
		if (obj instanceof Marshal)
			return ((Marshal) obj).readInstance(parser, namespace, name, expected);
		else if (obj instanceof SoapObject)
		{
			obj = ((SoapObject) obj).newInstance();
		}
		else if (obj == SoapObject.class)
		{
			obj = new SoapObject(namespace, name);
		}
		else
		{
			try
			{
				obj = ((Class) obj).newInstance();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.toString());
			}
		}
		// ok, obj is now the instance, fill it....
		if (obj instanceof SoapObject)
			readSerializable(parser, (SoapObject) obj);
		else if (obj instanceof KvmSerializable)
			readSerializable(parser, (KvmSerializable) obj);
		else if (obj instanceof Vector)
			readVector(parser, (Vector) obj, expected.elementType);
		else
			throw new RuntimeException("no deserializer for " + obj.getClass());
		return obj;
	}

	/**
	 * Returns a string array containing the namespace, name, id and Marshal object for the given java object.
	 * This method is used by the SoapWriter in order to map Java objects to the corresponding SOAP section
	 * five XML code.
	 */
	public Object[] getInfo(Object type, Object instance)
	{
		if (type == null)
		{
			if (instance instanceof SoapObject || instance instanceof SoapPrimitive)
				type = instance;
			else
				type = instance.getClass();
		}
		if (type instanceof SoapObject)
		{
			SoapObject so = (SoapObject) type;
			return new Object[] { so.getNamespace(), so.getName(), null, null };
		}
		if (type instanceof SoapPrimitive)
		{
			SoapPrimitive sp = (SoapPrimitive) type;
			return new Object[] { sp.getNamespace(), sp.getName(), null, DEFAULT_MARSHAL };
		}
		if ((type instanceof Class) && type != PropertyInfo.OBJECT_CLASS)
		{
			Object[] tmp = (Object[]) classToQName.get(((Class) type).getName());
			if (tmp != null)
				return tmp;
		}
		return new Object[] { xsd, ANY_TYPE_LABEL, null, null };
	}

	/**
	 * Defines a direct mapping from a namespace and name to a java class (and vice versa), using the given
	 * marshal mechanism
	 */
	public void addMapping(String namespace, String name, Class clazz, Marshal marshal)
	{
		qNameToClass
				.put(new SoapPrimitive(namespace, name, null), marshal == null ? (Object) clazz : marshal);
		classToQName.put(clazz.getName(), new Object[] { namespace, name, null, marshal });
	}

	/**
	 * Defines a direct mapping from a namespace and name to a java class (and vice versa)
	 */
	public void addMapping(String namespace, String name, Class clazz)
	{
		addMapping(namespace, name, clazz, null);
	}

	/**
	 * Adds a SoapObject to the class map. During parsing, objects of the given type (namespace/name) will be
	 * mapped to corresponding copies of the given SoapObject, maintaining the structure of the template.
	 */
	public void addTemplate(SoapObject so)
	{
		qNameToClass.put(new SoapPrimitive(so.namespace, so.name, null), so);
	}

	/**
	 * Response from the soap call. Pulls the object from the wrapper object and returns it.
	 *
	 * @since 2.0.3
	 * @return response from the soap call.
	 * @throws SoapFault
	 */
	public Object getResponse() throws SoapFault
	{
		if (bodyIn instanceof SoapFault)
		{
			throw (SoapFault) bodyIn;
		}
		KvmSerializable ks = (KvmSerializable) bodyIn;

		if(ks.getPropertyCount()==0){
			return null;
		}else if(ks.getPropertyCount()==1){
			return ks.getProperty(0);
		}else{
			Vector ret = new Vector();
			for(int i=0;i<ks.getPropertyCount();i++){
				ret.add(ks.getProperty(i));
			}
			return ret;
		}
		//return ks.getPropertyCount() == 0 ? null : ks.getProperty(0);
	}

	/**
	 * @deprecated Please use the getResponse going forward
	 * @see #getResponse()
	 */
	public Object getResult()
	{
		KvmSerializable ks = (KvmSerializable) bodyIn;
		return ks.getPropertyCount() == 0 ? null : ks.getProperty(0);
	}

	/**
	 * Serializes the request object to the given XmlSerliazer object
	 *
	 * @param writer
	 *            XmlSerializer object to write the body into.
	 */
	public void writeBody(XmlSerializer writer) throws IOException
	{
		// allow an empty body without any tags in it
		// see http://code.google.com/p/ksoap2-android/issues/detail?id=77
		if (bodyOut != null) {
            multiRef = new Vector();
            multiRef.addElement(bodyOut);
            Object[] qName = getInfo(null, bodyOut);
            writer.startTag((dotNet) ? "" : (String) qName[QNAME_NAMESPACE], (String) qName[QNAME_TYPE]);
            if (dotNet)
            {
                writer.attribute(null, "xmlns", (String) qName[QNAME_NAMESPACE]);
            }
            if (addAdornments)
            {
                writer.attribute(null, ID_LABEL, qName[2] == null ? ("o" + 0) : (String) qName[2]);
                writer.attribute(enc, ROOT_LABEL, "1");
            }
            writeElement(writer, bodyOut, null, qName[QNAME_MARSHAL]);
            writer.endTag((dotNet) ? "" : (String) qName[QNAME_NAMESPACE], (String) qName[QNAME_TYPE]);
        }
	}

	/**
	 * Writes the body of an SoapObject. This method write the attributes and then calls
	 * "writeObjectBody (writer, (KvmSerializable)obj);"
	 */
	public void writeObjectBody(XmlSerializer writer, SoapObject obj) throws IOException
	{
		SoapObject soapObject = (SoapObject) obj;
		int cnt = soapObject.getAttributeCount();
		for (int counter = 0; counter < cnt; counter++)
		{
			AttributeInfo attributeInfo = new AttributeInfo();
			soapObject.getAttributeInfo(counter, attributeInfo);
			writer.attribute(attributeInfo.getNamespace(), attributeInfo.getName(), attributeInfo.getValue()
					.toString());
		}
		writeObjectBody(writer, (KvmSerializable) obj);
	}

	/**
	 * Writes the body of an KvmSerializable object. This method is public for access from Marshal subclasses.
	 */
	public void writeObjectBody(XmlSerializer writer, KvmSerializable obj) throws IOException
	{
		int cnt = obj.getPropertyCount();
		PropertyInfo info = new PropertyInfo();
		for (int i = 0; i < cnt; i++)
		{
			Object prop = obj.getProperty(i);
			if(!(prop instanceof SoapObject)) {
				// prop is a PropertyInfo
				obj.getPropertyInfo(i, properties, info);
				if ((info.flags & PropertyInfo.TRANSIENT) == 0)
				{
					writer.startTag(info.namespace, info.name);
					writeProperty(writer, obj.getProperty(i), info);
					writer.endTag(info.namespace, info.name);
				}
			} else {
				// prop is a SoapObject
				SoapObject nestedSoap = (SoapObject)prop;
				writer.startTag(nestedSoap.getNamespace(), nestedSoap.getName());
				writeObjectBody(writer, nestedSoap);
				writer.endTag(nestedSoap.getNamespace(), nestedSoap.getName());
			}
		}
	}

	protected void writeProperty(XmlSerializer writer, Object obj, PropertyInfo type) throws IOException
	{
		if (obj == null)
		{
			writer.attribute(xsi, version >= VER12 ? NIL_LABEL : NULL_LABEL, "true");
			return;
		}
		Object[] qName = getInfo(null, obj);
		if (type.multiRef || qName[2] != null)
		{
			int i = multiRef.indexOf(obj);
			if (i == -1)
			{
				i = multiRef.size();
				multiRef.addElement(obj);
			}
			writer.attribute(null, HREF_LABEL, qName[2] == null ? ("#o" + i) : "#" + qName[2]);
		}
		else
		{
			if (!implicitTypes || obj.getClass() != type.type)
			{
				String prefix = writer.getPrefix((String) qName[QNAME_NAMESPACE], true);
				writer.attribute(xsi, TYPE_LABEL, prefix + ":" + qName[QNAME_TYPE]);
			}
			writeElement(writer, obj, type, qName[QNAME_MARSHAL]);
		}
	}

	private void writeElement(XmlSerializer writer, Object element, PropertyInfo type, Object marshal)
			throws IOException
	{
		if (marshal != null)
			((Marshal) marshal).writeInstance(writer, element);
		else if (element instanceof SoapObject)
			writeObjectBody(writer, (SoapObject) element);
		else if (element instanceof KvmSerializable)
			writeObjectBody(writer, (KvmSerializable) element);
		else if (element instanceof Vector)
			writeVectorBody(writer, (Vector) element, type.elementType);
		else
			throw new RuntimeException("Cannot serialize: " + element);
	}

	protected void writeVectorBody(XmlSerializer writer, Vector vector, PropertyInfo elementType)
			throws IOException
	{
		String itemsTagName = ITEM_LABEL;
		String itemsNamespace = null;

		if (elementType == null)
		{
			elementType = PropertyInfo.OBJECT_TYPE;
		}
		else if (elementType instanceof PropertyInfo)
		{
			if (elementType.name != null)
			{
				itemsTagName = elementType.name;
				itemsNamespace = elementType.namespace;
			}
		}

		int cnt = vector.size();
		Object[] arrType = getInfo(elementType.type, null);
		
		// This removes the arrayType attribute from the xml for arrays(required for most .Net services to work)
		if(!implicitTypes)
		{
             writer.attribute(enc, ARRAY_TYPE_LABEL, writer.getPrefix((String) arrType[0], false) + ":"
    				+ arrType[1] + "[" + cnt + "]");
		}
		
		boolean skipped = false;
		for (int i = 0; i < cnt; i++)
		{
			if (vector.elementAt(i) == null)
				skipped = true;
			else
			{
				writer.startTag(itemsNamespace, itemsTagName);
				if (skipped)
				{
					writer.attribute(enc, "position", "[" + i + "]");
					skipped = false;
				}
								
				writeProperty(writer, vector.elementAt(i), elementType);
								
				writer.endTag(itemsNamespace, itemsTagName);
			}
		}
	}

}
