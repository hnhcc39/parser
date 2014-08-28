package com.internal.util.xml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
public class XmlWriter {
	
	private StringBuffer buf = new StringBuffer();
	private Stack<Object> calls = new Stack<Object>();
	private DateFormat format;
	
    public String writer(long n) {
        return String.valueOf(n);
    }

    public String writer(double d) {
        return String.valueOf(d);
    }

    public String writer(char c) {
        return "\"" + c + "\"";
    }
    
    public String writer(boolean b) {
        return String.valueOf(b);
    }
    
	public String writer(Object object){
		buf.setLength(0);
        value(object);
        return buf.toString();
	}
	
	private void value(Object object) {
        if (object == null || cyclic(object)) {
            add(null);
        } else {
            calls.push(object);
            if (object instanceof Class<?>) string(object);
            else if (object instanceof Boolean) bool(((Boolean) object).booleanValue());
            else if (object instanceof Number) add(object);
            else if (object instanceof String) string(object);
            else if (object instanceof Character) string(object);
            else if (object instanceof Map<?, ?>) map((Map<?, ?>)object);
            else if (object.getClass().isArray()) array(object);
            else if (object instanceof Iterator<?>) array((Iterator<?>)object);
            else if (object instanceof Collection<?>) array(((Collection<?>)object).iterator());
            else if (object instanceof Date) date((Date)object);
            else bean(object);
            calls.pop();
        }
    }
	
    private boolean cyclic(Object object) {
        Iterator<Object> it = calls.iterator();
        while (it.hasNext()) {
            Object called = it.next();
            if (object == called) return true;
        }
        return false;
    }
    
    private void bean(Object object) {
        BeanInfo info;
        boolean addedSomething = false;
        try {
        	add("\n<"+object.getClass().getSimpleName()+">\n");
            info = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (int i = 0; i < props.length; ++i) {
                PropertyDescriptor prop = props[i];
                String name = prop.getName();
                Method accessor = prop.getReadMethod();
                if (!"class".equals(name) && accessor != null) {
                    if (!accessor.isAccessible()) accessor.setAccessible(true);
                    Object value = accessor.invoke(object, (Object[])null);
                    if (value == null) continue;
                    if (addedSomething) add('\n');

//                    if (useApiStyle) {
//                    	name = StringUtils.toUnderlineStyle(name);
//                    }

                    add(name, value);
                    addedSomething = true;
                }
            }
            Field[] ff = object.getClass().getFields();
            for (int i = 0; i < ff.length; ++i) {
                Field field = ff[i];
                Object value = field.get(object);
                if (value == null) continue;
                if (addedSomething) add('\n');
                add(field.getName(), value);
                addedSomething = true;
            }
            add("\n</"+object.getClass().getSimpleName()+">");
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.getCause().printStackTrace();
            ite.printStackTrace();
        } catch (IntrospectionException ie) {
            ie.printStackTrace();
        }
    }
    
    private void add(String name, Object value) {
        add("<"+name+">");
        value(value);
        add("</"+name+">");
    }

    private void map(Map<?, ?> map) {
        add("{");
        Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) it.next();
            value(e.getKey());
            add(":");
            value(e.getValue());
            if (it.hasNext()) add(',');
        }
        add("}");
    }
    
    private void array(Iterator<?> it) {
        add("\n<list>");
        while (it.hasNext()) {
            value(it.next());
            if (it.hasNext()) add("\n");
        }
        add("\n</list>");
    }

    private void array(Object object) {
        add("\n<list>");
        int length = Array.getLength(object);
        for (int i = 0; i < length; ++i) {
            value(Array.get(object, i));
            if (i < length - 1) add('\n');
        }
        add("\n</list>");
    }

    private void bool(boolean b) {
        add(b ? "true" : "false");
	}

    private void date(Date date) {
        if (this.format == null) {
            this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        }
        add("\"");
        add(format.format(date));
        add("\"");
    }

	private void string(Object obj) {
        CharacterIterator it = new StringCharacterIterator(obj.toString());
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (Character.isISOControl(c)) {
                unicode(c);
            } else {
                add(c);
            }
        }
    }

    private void add(Object obj) {
        buf.append(obj);
    }

    private void add(char c) {
        buf.append(c);
    }

    static char[] hex = "0123456789ABCDEF".toCharArray();

    private void unicode(char c) {
        add("\\u");
        int n = c;
        for (int i = 0; i < 4; ++i) {
            int digit = (n & 0xf000) >> 12;
            add(hex[digit]);
            n <<= 4;
        }
    }
}
