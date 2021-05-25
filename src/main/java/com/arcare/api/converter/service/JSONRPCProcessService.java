package com.arcare.api.converter.service;

import org.springframework.http.ResponseEntity;
/**
 * 
 * @author FUHSIANG_LIU
 * process -> invoke -> request
 */
public interface JSONRPCProcessService {
	public ResponseEntity<?> process(String json);
}
