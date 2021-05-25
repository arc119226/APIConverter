package com.arcare.api.converter.service;

import java.util.Map;

import com.arcare.api.converter.vo.ProcessResultStatusVO;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public interface DynamicParserService {
	public ProcessResultStatusVO<String> bindTemplate(String dsl,Map<String,Object> metadata);
	public ProcessResultStatusVO<Object> executeMethod(String apiName,String dsl,Object metadata);
}
