package com.arcare.api.converter.vo;

import com.google.gson.JsonElement;

/**
 * 
 * @author FUHSIANG_LIU
 *
 * @param <T>
 */
public class JSONRPCSuccessResponse extends JSONRPCResponse {

	private String jsonrpc="2.0";
	
	private JsonElement result;
	
	private Integer id;
	
	public JSONRPCSuccessResponse(JSONRPCRequest request){
		this.id=request.getId();
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public JsonElement getResult() {
		return result;
	}

	public void setResult(JsonElement result) {
		this.result = result;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
