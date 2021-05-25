package com.arcare.api.converter.vo;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public class JSONRPCErrorResponse extends JSONRPCResponse {

	private String jsonrpc="2.0";

	private JSONRPCError error=new JSONRPCError();

	private Integer id;

	public JSONRPCErrorResponse(){

	}

	public JSONRPCErrorResponse(JSONRPCRequest request,Integer code,String msg){
		this.id=request.getId();
		error.setCode(code);
		error.setMessage(msg);
	}
	
	public JSONRPCErrorResponse(JSONRPCRequest request){
		this.id=request.getId();
	}
	
	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public JSONRPCError getError() {
		return error;
	}

	public void setError(JSONRPCError error) {
		this.error = error;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
