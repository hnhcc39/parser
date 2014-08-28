灵活自由的json、xml解析 如有疑问，可以与我联系:hnhcc39@sina.com

public static void main(String[] args) throws ParserException {
    //传统模式json
    String json = "{\"ParserTest\":{\"name\":\"姓名\",\"agePage\":19}}";
    Converter conver = new JsonConverter();
    System.out.println("传统模式json转换");
    ParserTest test = parse(conver,json);
    //输出json
    conver = new JsonConverter(true);
    json = conver.write(test);
    System.out.println("精简模式json转换");
    test = parse(conver,json);

    conver = new JsonConverter(false,true);
    json = conver.write(test);
    System.out.println("传统+api模式json转换");
    test = parse(conver,json);
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
