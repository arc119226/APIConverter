package com.arcare.api.converter.service;

import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public interface InvokeService {
	public JSONRPCResponse invoke(JSONRPCRequest request);
}
