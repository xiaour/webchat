package online.wenbei.webchat.util;

import org.springframework.util.DigestUtils;

/**
 * @Author: cityuu#163.com
 * @Date: 2019-08-29 11:40
 * @version: v1.0
 * @Description:
 */
public class MD5Util {

    //盐，用于混交md5
    private static final String slat = "webchat@2019";

    /**
     * 生成md5
     * @return
     */
    public static String getMD5(String str) {
        String base = str +"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

}
