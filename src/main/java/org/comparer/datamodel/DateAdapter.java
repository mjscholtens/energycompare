package org.comparer.datamodel;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import java.util.*;

/**
 * 
 * @author Marijn Scholtens & Bob Reimink
 * This class is used by the webservice for correctly reading the date in the message.
 */
public class DateAdapter extends XmlAdapter<String, Date> {
	
	@Override
	public String marshal(Date arg0) throws Exception {
		
		return Long.toString(arg0.getTime());
	}

	@Override
	public Date unmarshal(String arg0) throws Exception {
		return new Date(Long.parseLong(arg0));
	}

}
