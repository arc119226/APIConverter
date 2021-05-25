package com.arcare.api.converter.vo;

import java.util.HashMap;
import java.util.Map;

import com.arcare.api.converter.config.Constant;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public class JSONRPCRequest {

	@JsonProperty("jsonrpc")
	private String jsonrpc = "2.0";

	@JsonProperty("method")
	private String method = "sequence";

	@JsonProperty("params")
	private Object params;

	@JsonProperty("id")
	private Integer id = 1;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "JSONRPCRequest [jsonrpc=" + jsonrpc + ", method=" + method + ", params=" + params + ", id=" + id + "]";
	}
	/**
	 * 以map 結構毒入params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> readRequestParamsByMap(){
		if(this.getParams()==null) {
			return new HashMap<>();
		}
		return (Map<String, Object>) this.getParams();
	}
	
	/**
	 * suctmize for this system
	 * @return
	 */

	public String readTaskId(){
		if(this.readRequestParamsByMap().get(Constant.STR_TASK_ID)==null) {
			return null;
		}
		return (String) this.readRequestParamsByMap().get(Constant.STR_TASK_ID);
	}
	
	public String readApiId() {
		if(this.readRequestParamsByMap().get(Constant.STR_API_ID)==null) {
			return null;
		}
		return (String) this.readRequestParamsByMap().get(Constant.STR_API_ID);
	}
	
	public String readAccessToken() {
		if(this.readRequestParamsByMap().get(Constant.STR_ACCESS_TOKEN)==null) {
			return null;
		}
		return (String) this.readRequestParamsByMap().get(Constant.STR_ACCESS_TOKEN);
	}
	
	public Map<String,Object> readStrDataInParamByMap(){
		if(this.getParams()==null) {
			return new HashMap<>();
		}
		return (Map<String, Object>) this.readRequestParamsByMap().get(Constant.STR_DATA);
	}
	
	public Map<String,String> readStrHeaderInParamByMap(){
		if(this.getParams()==null) {
			return new HashMap<>();
		}
		return (Map<String, String>) this.readRequestParamsByMap().get(Constant.STR_HEADER);
	}
	
}
