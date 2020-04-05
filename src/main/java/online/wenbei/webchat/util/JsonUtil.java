package online.wenbei.webchat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author: cityuu#163.com
 * @Date: 2018-12-10 16:11
 * @version: v1.0
 * @Description: JSON 格式化工具
 */
public class JsonUtil {

    private static SerializeConfig mapping = new SerializeConfig();
    private static SimpleDateFormatSerializer formatSerializer = new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss");
    private static final SerializerFeature[] features;

    static {
        features = new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullStringAsEmpty};
    }

    public JsonUtil() {
    }

    public static <T> T parseToClass(String jsonStr, Class<?> clazz) {
        T javaObject = (T) JSON.toJavaObject(JSON.parseObject(jsonStr), clazz);
        return javaObject;
    }

    public static String parseToJSON(Object object) {
        return JSON.toJSONString(object, configMapping(), new SerializerFeature[0]);
    }

    public static String parseUnicodeJSON(Object object) {
        return JSON.toJSONString(object, new SerializerFeature[]{SerializerFeature.BrowserCompatible});
    }

    public static String parseJSONString(Object object, SimplePropertyPreFilter filter) {
        return JSON.toJSONString(object, filter, new SerializerFeature[0]);
    }

    public static String parseJSONString(Object object, String formatDate) {
        return JSON.toJSONString(object, configMapping(formatDate), new SerializerFeature[0]);
    }

    public static <T> List<T> parseJSONList(String jsonString, Class pojoClass) {
        return JSONObject.parseArray(jsonString,pojoClass);
    }

    public static Map<Object, Object> getMapJSON(String jsonString) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Map<Object, Object> parseJSONMap = new LinkedHashMap(jsonObject);
        return parseJSONMap;
    }

    public static Map<byte[], byte[]> getByteMapByJSON(String jsonString) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Map<byte[], byte[]> parseJSONMap = new LinkedHashMap(jsonObject);
        return parseJSONMap;
    }


    private static SerializeConfig configMapping() {
        mapping.put(Date.class, formatSerializer);
        return mapping;
    }

    private static SerializeConfig configMapping(String format) {
        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer(format));
        return mapping;
    }

    public static SimplePropertyPreFilter filterProperty(Class<?> className, String... param) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(className, param);
        return filter;
    }

    /**
     * 字符串处理成Long list
     * @param values
     * @return
     */
    public static List<Long> convertToLongList(String values){
        List<Long> userIdsLongArr = new ArrayList<>();
        String[] userIdsStringArr = values.split(",");
        for(String ss:userIdsStringArr){
            if(!StringUtils.isEmpty(ss)){
                userIdsLongArr.add(Long.valueOf(ss));
            }
        }
        return userIdsLongArr;
    }

    /**
     * String list
     * @param values
     * @return
     */
    public static List<String> convertToStringList(String values){
        List<String> userIdsLongArr = new ArrayList<>();
        String[] userIdsStringArr = values.split(",");
        for(String ss:userIdsStringArr){
            if(!StringUtils.isEmpty(ss)){
                userIdsLongArr.add(ss);
            }
        }
        return userIdsLongArr;
    }
}
