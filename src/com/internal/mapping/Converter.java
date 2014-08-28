package com.internal.mapping;

import java.util.Map;

import com.internal.exception.ParserException;


/**
 * 动态格式转换器。
 */
public interface Converter{

	/**
	 * 把字符串转换为响应对象。
	 * 
	 * @param <T> 领域泛型
	 * @param result 响应字符串
	 * @param clazz 领域类型
	 * @return 响应对象
	 */
	public <T extends Object> T parse(String result, Class<T> clazz) throws ParserException;
	
	/**
	 * 把字符串转换为Map对象。
	 * 
	 * @param result 响应字符串
	 * @return
	 * @throws ParserException
	 */
	public Map<?,?> parseMap(String result) throws ParserException;
	
	/**
	 * 将对象转换为字符串
	 * @param object 对象
	 * @return 字符串
	 */
	public String write(Object object);

}
