package com.arcare.api.converter.vo;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public class JSONRPCError {

	public static final String ERR_32001_NOT_SUPPORT_THIS_API_ACTION="not support this api_action ";
	public static final String ERR_32002_BIND_ERROR="bind error";
	public static final String ERR_32003_API_ID_IS_NULL="strApiId is null";
	public static final String ERR_32004_TASK_ID_IS_NULL="strTaskId is null";
	public static final String ERR_32005_API_CONFIG_NOT_EXISTS="api config not exists ";
	public static final String ERR_32006_NOT_SUPORT_THIS_API_TYPE="not support this api_type ";
	public static final String ERR_32007_API_URL_NOT_EXISTS="api_url not exists";
	public static final String ERR_32008_NOT_SUPPORT_THIS_METHOD="not support this method ";
	public static final String ERR_32009_TASK_CONFIG_NOT_EXISTS="task config not exists";
	public static final String ERR_32010_TASK_CONFIG_DETAIL_NOT_EXISTS="task config detail not exists";
	public static final String ERR_32011_ACCESS_TOKEN_NOT_VALID="access token not valid";

	public static final String ERR_32700_PARSE_ERROR="parse error";
	public static final String ERR_32701_JMS_ERROR="JMS error ";
	public static final String ERR_32702_FILE_UPLOAD_ERR="file upload error";
	public static final String ERR_32703_CREATE_DIR_FAIL="create dir fail";
	public static final String ERR_32704_FILE_DOWNLOAD_ERR="file download error";
	public static final String ERR_32705_NO_MAIN_TASK = "main task not define";
	public static final String ERR_32706_URL_NOT_DEFINE ="url not define";
	public static final String ERR_32708_DIR_NOTEXIST="dir not exists";
	
	public static final String ERR_32709_HTTP_CALL_API_FAIL="call http api fail";
	
	public static final String ERR_32010_AUTH_API_CONFIG_NOT_EXISTS="auth api config not exists";
	
	public static final String ERR_32010_EXECUTE_FAIL="execute method fail";
	
	private Integer code=-32600;

	private String message="Invalid Request";

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
