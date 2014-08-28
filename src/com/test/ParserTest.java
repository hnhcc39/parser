package com.test;

import com.internal.exception.ParserException;
import com.internal.field.ApiField;
import com.internal.mapping.Converter;
import com.internal.parser.json.JsonConverter;
/**
 * @author Administrator
 * @date 2014-4-14
 * @project parser
 * @package com.test
 * @package ParserTest.java
 * @version [版本号]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ParserTest extends Perlet{
	
	@ApiField("agePage")
	private Integer agePage;
	
	public Integer getAgePage() {
		return agePage;
	}
	
	public void setAgePage(Integer agePage) {
		this.agePage = agePage;
		
	}
	
	public ParserTest(Integer agePage,String name) {
		super();
		this.agePage = agePage;
		super.setName(name);
	}

	public ParserTest() {
		super();
	}

	@Override
	public String toString() {
		return "ParserTest [agePage=" + agePage + ", name=" +  super.getName() + "]";
	}

	public static void main(String[] args) throws ParserException {
		//传统模式json
		String json = "{\"ParserTest\":{\"name\":\"\u600e\u4e48\u7231\u4f60\u4e00\u8f88\u5b50\",\"agePage\":19}}";
		Converter c = new JsonConverter();
		System.out.println("传统模式json转换");
		ParserTest test = parse(c,json);
		//输出json
		c = new JsonConverter(true);
		json = c.write(test);
		System.out.println("精简模式json转换");
		test = parse(c,json);
		
		c = new JsonConverter(false,true);
		json = c.write(test);
		System.out.println("传统+api模式json转换");
		test = parse(c,json);
	}
	
	public static ParserTest parse(Converter c, String json) throws ParserException
	{
		System.out.println("输出json:"+json);
		//输出map
		System.out.println("输出map:"+c.parseMap(json));
		//转换成对象
		ParserTest csadsa = c.parse(json, ParserTest.class);
		System.out.println("输出对象:"+csadsa);
		return csadsa;
	}
	
	
	public static String getRandomNumber(int count) {
		char[] chars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
				'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
				'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer str = new StringBuffer();
		int max = 1 << 30;
		for (int i = 0; i < max; i++) {
			Long number = Math.round(Math.random() * chars.length);
			if (number.intValue() == chars.length) {
				continue;
			}
			if (str.length() >= count) {
				break;
			}
			str.append(chars[number.intValue()]);
		}
		return String.valueOf(str);
	}
}
