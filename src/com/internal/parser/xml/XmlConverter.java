package com.internal.parser.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.internal.exception.ParserException;
import com.internal.mapping.Converter;
import com.internal.mapping.Converters;
import com.internal.mapping.Reader;
import com.internal.util.xml.XmlReader;
import com.internal.util.xml.XmlWriter;

/**
 * JSON格式转换器。
 */
public class XmlConverter implements Converter {
	
	private boolean simplify;
	
	public XmlConverter() {
		super();
	}

	public XmlConverter(boolean simplify) {
		super();
		this.simplify = simplify;
	}

	@Override
	public String write(Object object) {
		return new XmlWriter().writer(object);
	}
	
	@Override
	public <T extends Object> T parse(String result, Class<T> clazz) throws ParserException {
		XmlReader reader = new XmlReader();
		if(simplify){
			result = "<"+clazz.getSimpleName()+">" + result + "</"+clazz.getSimpleName()+">";
		}
		Element root = reader.getRootElementFromString(result);
		return getModelFromXML(root, clazz);
	}
	
	@Override
	public Map<?, ?> parseMap(String result) throws ParserException 
	{
		XmlReader reader = new XmlReader();
		Element root = reader.getRootElementFromString(result);
		return reader.readXml(root,null);
	}
	
	private <T> T getModelFromXML(final Element element, Class<T> clazz) throws ParserException {
		if (element == null)
			return null;

		return Converters.convert(clazz, new Reader() {
			XmlReader reader = new XmlReader();
			@Override
			public boolean hasReturnField(Object name) {
				Element childE = reader.getChildElement(element, (String) name);
				return childE != null;
			}

			@Override
			public Object getPrimitiveObject(Object name) {
				return reader.getChildElementValue(element, (String) name);
			}

			@Override
			public Object getObject(Object name, Class<?> type) throws ParserException {
				Element childE = reader.getChildElement(element, (String) name);
				if (childE != null) {
					return getModelFromXML(childE, type);
				} else {
					return null;
				}
			}

			@Override
			public List<?> getListObjects(Object listName, Object itemName, Class<?> subType) throws ParserException {
				List<Object> list = null;
				Element listE = reader.getChildElement(element, (String) listName);
				if (listE != null) {
					list = new ArrayList<Object>();
					List<Element> itemEs = reader.getChildElements(listE, (String) itemName);
					for (Element itemE : itemEs) {
						Object obj = null;
						String value = reader.getElementValue(itemE);

						if (String.class.isAssignableFrom(subType)) {
							obj = value;
						} else if (Long.class.isAssignableFrom(subType)) {
							obj = Long.valueOf(value);
						} else if (Integer.class.isAssignableFrom(subType)) {
							obj = Integer.valueOf(value);
						} else if (Boolean.class.isAssignableFrom(subType)) {
							obj = Boolean.valueOf(value);
						} else if (Date.class.isAssignableFrom(subType)) {
							DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							try {
								obj = format.parse(value);
							} catch (ParseException e) {
								throw new ParserException(e);
							}
						} else {
							obj = getModelFromXML(itemE, subType);
						}
						if (obj != null) list.add(obj);
					}
				}
				return list;
			}
		});
	}
}
