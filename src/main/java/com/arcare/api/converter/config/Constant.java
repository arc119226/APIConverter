package com.arcare.api.converter.config;
/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public class Constant {
	
	//backbone db key
	public static final String BACKBONE_DB_DRIVER = "db.driver";
	public static final String BACKBONE_DB_JDBC_URL = "db.jdbc";
	public static final String BACKBONE_DB_USER = "db.user";
	public static final String BACKBONE_DB_PASSWORD = "db.password";
	
	//jsonrpc 2.0 method
	public static final String METHOD_SEQUENCE = "sequence";
	public static final String METHOD_PARALLEL = "parallel";
	//TODO async
	public static final String METHOD_TASK = "task";
	
	//request key
	//for task
	public static final String STR_TASK_ID="strTaskId";
	public static final String STR_ACCESS_TOKEN="strAccessToken";
	//for api
	public static final String STR_API_ID="strApiId";

	public static final String STR_HEADER="strHeader";
	public static final String STR_DATA="strData";
	
	//result key
	public static final String RESULT="result";
	
	//api
	//support type
	public static final String API_TYPE_JDBC="jdbc";
	public static final String API_TYPE_JSONRPC="jsonrpc";
	public static final String API_TYPE_IBMMQ="ibmmq";
	public static final String API_TYPE_FILE="file";
	public static final String API_TYPE_HTTP="http";
	public static final String API_TYPE_TEMPLATE="template";
	
	//support action
	//template
	public static final String API_ACTION_TEMPLATE_MAKE = API_TYPE_TEMPLATE+"_make";
	//sql
	public static final String API_ACTION_JDBC_INSERT = API_TYPE_JDBC+"_insert";
	public static final String API_ACTION_JDBC_UPDATE = API_TYPE_JDBC+"_update";
	public static final String API_ACTION_JDBC_DELETE = API_TYPE_JDBC+"_delete";
	public static final String API_ACTION_JDBC_QUERY = API_TYPE_JDBC+"_query";
	public static final String API_ACTION_JDBC_EXECUTE = API_TYPE_JDBC+"_execute";
	//ibmmq
	public static final String API_ACTION_IBMMQ_SEND = API_TYPE_IBMMQ+"_send";
	public static final String API_ACTION_IBMMQ_RECEIVE = API_TYPE_IBMMQ+"_receive";
	//jsonrpc
	public static final String API_ACTION_JSONRPC_CALL = API_TYPE_JSONRPC+"_call";
	//file
	public static final String API_ACTION_FILE_UPLOAD = API_TYPE_FILE+"_upload";
	public static final String API_ACTION_FILE_DOWNLOAD = API_TYPE_FILE+"_download";
	//http
	public static final String APIACTION_HTTP_GET = API_TYPE_HTTP+"_get";
	public static final String APIACTION_HTTP_POST = API_TYPE_HTTP+"_post";
	
	//prop
	//http
	public static final String PROP_HTTP_MEDIA_TYPE="prop_"+API_TYPE_HTTP+"_media_type";
	public static final String PROP_HTTP_URL="prop_"+API_TYPE_HTTP+"_url";
	
	//file
	public static final String PROP_FILE_WEBROOT_URL="prop_"+API_TYPE_FILE+"_webroot_url";
	public static final String PROP_FILE_ROOT_DIR="prop_"+API_TYPE_FILE+"_root_dir";
	
	//SQL JDBC
	public static final String PROP_JDBC_URL="prop_"+API_TYPE_JDBC+"_url";
	public static final String PROP_JDBC_DRIVER="prop_"+API_TYPE_JDBC+"_driver";
	public static final String PROP_JDBC_USER="prop_"+API_TYPE_JDBC+"_user";
	public static final String PROP_JDBC_PASSWORD="prop_"+API_TYPE_JDBC+"_password";
	
	//ibmmq
	public static final String PROP_IBMMQ_HOST="prop_"+API_TYPE_IBMMQ+"_host";
	public static final String PROP_IBMMQ_PORT="prop_"+API_TYPE_IBMMQ+"_port";
	public static final String PROP_IBMMQ_MANAGER="prop_"+API_TYPE_IBMMQ+"_manager";
	public static final String PROP_IBMMQ_CHANNEL="prop_"+API_TYPE_IBMMQ+"_channel";
	public static final String PROP_IBMMQ_QUEU_NAME="prop_"+API_TYPE_IBMMQ+"_queue_name";
	public static final String PROP_IBMMQ_CLIENT_TYPE="prop_"+API_TYPE_IBMMQ+"_client_type";
	public static final String PROP_IBMMQ_APPLICATION_NAME="prop_"+API_TYPE_IBMMQ+"_application_name";
	public static final String PROP_IBMMQ_USER_AUTHENTICATION_MQSP="prop_"+API_TYPE_IBMMQ+"_user_authentication_mqcsp";
	public static final String PROP_IBMMQ_USERID="prop_"+API_TYPE_IBMMQ+"_userid";
	public static final String PROP_IBMMQ_PASSWORD="prop_"+API_TYPE_IBMMQ+"_password";
	
	//jsonrpc
	public static final String PROP_JSONRPC_URL="prop_"+API_TYPE_JSONRPC+"_url";
	
	//template
	public static final String PROP_TEMPLATE_TYPE="prop_"+API_TYPE_TEMPLATE+"_make_type";

}
