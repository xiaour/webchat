package online.wenbei.webchat.util;

/**
 * @Author: cityuu#163.com
 * @Date: 2019-08-29 11:43
 * @version: v1.0
 * @Description:
 */
public class ResultData {
    private static final int SUCCESS_CODE = 0;
    private static final int ERROR_CODE = 1;
    private static final String SUCCESS_MSG = "success";
    private static final String ERROR_MSG = "error";
    private int code;
    private String msg;
    private Object data;
    private Long timestamp;

    private ResultData() {
    }

    public static ResultData success() {
        ResultData resultData = new ResultData();
        resultData.setCode(0);
        resultData.setMsg("success");
        resultData.setTimestamp(System.currentTimeMillis());
        return resultData;
    }

    public static ResultData success(Object data) {
        ResultData resultData = new ResultData();
        resultData.setCode(0);
        resultData.setMsg("success");
        resultData.setTimestamp(System.currentTimeMillis());
        resultData.setData(data);
        return resultData;
    }

    public static ResultData success(Object data, String msg) {
        ResultData resultData = new ResultData();
        resultData.setMsg(msg);
        resultData.setCode(0);
        resultData.setTimestamp(System.currentTimeMillis());
        resultData.setData(data);
        return resultData;
    }

    public static ResultData error() {
        ResultData resultData = new ResultData();
        resultData.setCode(1);
        resultData.setTimestamp(System.currentTimeMillis());
        resultData.setMsg("error");
        return resultData;
    }

    public static ResultData error(String msg) {
        ResultData resultData = new ResultData();
        resultData.setCode(1);
        resultData.setMsg(msg);
        resultData.setTimestamp(System.currentTimeMillis());
        return resultData;
    }

    public static ResultData result(Integer code, Object data, String msg) {
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        resultData.setData(data);
        resultData.setMsg(msg);
        resultData.setTimestamp(System.currentTimeMillis());
        return resultData;
    }

    public static ResultData result(Integer code, String msg) {
        return result(code, "", msg);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }



}
