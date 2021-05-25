package com.arcare.api.converter.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.arcare.api.converter.service.DynamicParserService;
import com.arcare.api.converter.vo.ProcessResultStatusVO;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.text.GStringTemplateEngine;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Service("DynamicParserServiceImpl")
public class DynamicParserServiceImpl implements DynamicParserService {

	private final Logger logger = LoggerFactory.getLogger(DynamicParserServiceImpl.class);
	
	@Override
	public ProcessResultStatusVO<Object> executeMethod(String apiName,String dsl, Object metadata) {
		logger.info("dsl "+dsl);
		logger.info("metadata "+metadata);
		ProcessResultStatusVO<Object> resultSet = new ProcessResultStatusVO<>();
		ClassLoader parent = this.getClass().getClassLoader();
		try (GroovyClassLoader loader = new GroovyClassLoader(parent)) {
			Class<?> groovyClass = loader.parseClass(dsl);
			GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
			Object[] args = { metadata };
			Object result = groovyObject.invokeMethod(apiName, args);
			resultSet.setResult(result);
			return resultSet;
		} catch (Exception e) {
			logger.error("",e);
			resultSet.addErrorMsg("execute fail" + e.getMessage());
		}
		return resultSet;
	}

	@Override
	public ProcessResultStatusVO<String> bindTemplate(String dsl, Map<String, Object> map) {
		ProcessResultStatusVO<String> resultSet = new ProcessResultStatusVO<>();
		try {
			logger.info("map "+map);
			logger.info("dsl "+dsl);
			GStringTemplateEngine eng=new groovy.text.GStringTemplateEngine();
			String template=eng.createTemplate(dsl).make(map).toString();
			logger.info("template "+template);
			resultSet.setResult(template);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("",e);
			resultSet.addErrorMsg("bindTemplate fail " + e.getMessage());
		}
		return resultSet;
	}


}
