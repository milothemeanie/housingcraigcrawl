package org.milo.craigcrawl.utils;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XmlUtils
{
	  public static <T> T load(final String xml, final Class<T> clazz) throws JAXBException
	    {

		  StringReader reader = new StringReader(xml);
	        try
	        {
	            final JAXBContext context = JAXBContext.newInstance(clazz);
	            final Unmarshaller um = context.createUnmarshaller();

	            @SuppressWarnings("unchecked")
	            final T resource = (T) um.unmarshal(reader);

	            return resource;
	        } // try

	        finally
	        {
	        	reader.close();
	        } // finally
	    }
}
