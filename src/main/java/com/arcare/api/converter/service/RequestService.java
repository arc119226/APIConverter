package com.arcare.api.converter.service;

import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;

public interface RequestService {
	public JSONRPCResponse request(JSONRPCRequest request,Api config);
}
