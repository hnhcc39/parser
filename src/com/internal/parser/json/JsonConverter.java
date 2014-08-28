package com.internal.parser.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.internal.exception.ParserException;
import com.internal.mapping.Converter;
import com.internal.mapping.Converters;
import com.internal.mapping.Reader;
import com.internal.util.json.ExceptionErrorListener;
import com.internal.util.json.JSONReader;
import com.internal.util.json.JSONValidatingReader;
import com.internal.util.json.JSONWriter;

/**
 * JSON格式转换器。
 */
/**
 * @author Administrator
 * @date 2014-4-15
 * @project parser
 * @package com.internal.parser.json
 * @package JsonConverter.java
 * @version [版本号]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class JsonConverter implements Converter {

	private boolean simplify;
	
	private boolean useApiStyle;
	
	/**
	 * 默认不采用精简版json，不采用api下划线模式
	 * @see #JsonConverter(boolean, boolean)
	 */
	public JsonConverter() {
		super();
	}

	/**
	 * 是否采用精简版json
	 * @param simplify 是否采用精简版json，默认不采用api下划线模式
	 * @see #JsonConverter(boolean, boolean)
	 */
	public JsonConverter(boolean simplify) {
		super();
		this.simplify = simplify;
		this.useApiStyle = false;
	}
	/**
	 * 根据是否是精简版json及是否采用api模式构造
	 * @param simplify 是否采用精简版json
	 * @param useApiStyle 是否采用api下划线模式
	 */
	public JsonConverter(boolean simplify, boolean useApiStyle) {
		super();
		this.simplify = simplify;
		this.useApiStyle = useApiStyle;
	}
	
	@Override
	public String write(Object object) {
		return new JSONWriter(simplify,useApiStyle).write(object);
	}
	
	@Override
	public Map<?, ?> parseMap(String result) throws ParserException {
		JSONReader reader = new JSONValidatingReader(new ExceptionErrorListener());
		Object rootObj = reader.read(result);
		if (rootObj instanceof Map<?, ?>) {
			Map<?, ?> rootJson = (Map<?, ?>) rootObj;
			if(simplify){
				return rootJson;
			}
			else{
				Collection<?> values = rootJson.values();
				for (Object rspObj : values) {
					if (rspObj instanceof Map<?, ?>) {
						Map<?, ?> rspJson = (Map<?, ?>) rspObj;
						return rspJson;
					}
				}
			}
		}
		return null;
	}

	@Override
	public <T extends Object> T parse(String result, Class<T> clazz) throws ParserException 
	{
		return fromJson(parseMap(result), clazz);
	}
	
	/**
	 * 把JSON格式的数据转换为对象。
	 * 
	 * @param <T> 泛型领域对象
	 * @param json JSON格式的数据
	 * @param clazz 泛型领域类型
	 * @return 领域对象
	 */
	public <T> T fromJson(final Map<?, ?> json, Class<T> clazz) throws ParserException {
		return Converters.convert(clazz, new Reader() {
			@Override
			public boolean hasReturnField(Object name) {
				return json.containsKey(name);
			}

			@Override
			public Object getPrimitiveObject(Object name) {
				return json.get(name);
			}

			@Override
			public Object getObject(Object name, Class<?> type) throws ParserException {
				Object tmp = json.get(name);
				if (tmp instanceof Map<?, ?>) {
					Map<?, ?> map = (Map<?, ?>) tmp;
					return fromJson(map, type);
				} else {
					return null;
				}
			}

			@Override
			public List<?> getListObjects(Object listName, Object itemName, Class<?> subType) throws ParserException {
				List<Object> listObjs = null;

				Object listTmp = json.get(listName);
				if (listTmp instanceof Map<?, ?>) {
					Map<?, ?> jsonMap = (Map<?, ?>) listTmp;
					Object itemTmp = jsonMap.get(itemName);
					if(itemTmp == null && listName != null ) {
						String listNameStr = listName.toString();
						itemTmp = jsonMap.get(listNameStr.substring(0, listNameStr.length()-1));
					}
					if (itemTmp instanceof List<?>) {
						listObjs = new ArrayList<Object>();
						List<?> tmpList = (List<?>) itemTmp;
						for (Object subTmp : tmpList) {
							if (subTmp instanceof Map<?, ?>) {// object
								Map<?, ?> subMap = (Map<?, ?>) subTmp;
								Object subObj = fromJson(subMap, subType);
								if (subObj != null) {
									listObjs.add(subObj);
								}
							} else if (subTmp instanceof List<?>) {// array
								// TODO not support yet
							} else {// boolean, long, double, string, null
								listObjs.add(subTmp);
							}
						}
					}
				}
				//精简版JSON格式
				if (listTmp instanceof List<?>) {
					listObjs = new ArrayList<Object>();
					List<?> listObj = (List<?>) listTmp;
					for (Object tmp : listObj) {
						if (tmp instanceof Map<?, ?>) {// object
							Map<?, ?> subMap = (Map<?, ?>) tmp;
							Object subObj = fromJson(subMap, subType);
							if (subObj != null) {
								listObjs.add(subObj);
							}
						} else if (tmp instanceof List<?>) {// array
							// TODO not support yet
						} else {// boolean, long, double, string, null
							listObjs.add(tmp);
						}
					}
				}

				return listObjs;
			}
		});
	}
}
