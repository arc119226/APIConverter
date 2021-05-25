package com.arcare.api.converter.service.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.arcare.api.converter.config.Constant;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.service.DynamicParserService;
import com.arcare.api.converter.service.RequestService;
import com.arcare.api.converter.vo.JSONRPCError;
import com.arcare.api.converter.vo.JSONRPCErrorResponse;
import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;
import com.arcare.api.converter.vo.JSONRPCSuccessResponse;
import com.arcare.api.converter.vo.ProcessResultStatusVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Service("TemplateRequestServiceImpl")
public class TemplateRequestServiceImpl implements RequestService {

	private final Logger logger = LoggerFactory.getLogger(TemplateRequestServiceImpl.class);
	
	@Autowired
	@Qualifier("DynamicParserServiceImpl")
	private DynamicParserService dynamicParserService;
	
	@Override
	public JSONRPCResponse request(JSONRPCRequest request, Api config) {

		ProcessResultStatusVO<String> bindResult = this.dynamicParserService.bindTemplate(config.getApiMeta(),request.readStrDataInParamByMap());

		if(bindResult==null||!bindResult.getResult().isPresent()) {
			return new JSONRPCErrorResponse(request,-32002,JSONRPCError.ERR_32002_BIND_ERROR);
		}

		if(Constant.API_ACTION_TEMPLATE_MAKE.equals(config.getApiAction())){
			String result=bindResult.getResult().get();
			
			if("map".equals(config.getValueByPropertiesName(Constant.PROP_TEMPLATE_TYPE))) {
				JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
				Type type = new TypeToken<Map<String, Object>>(){}.getType();
				Map<String, Object> queryResult = new Gson().fromJson(result, type);
				JsonElement element = new GsonBuilder().create().toJsonTree(queryResult);
				resp.setResult(element);
				return resp;
			}else if("list".equals(config.getValueByPropertiesName(Constant.PROP_TEMPLATE_TYPE))) {
				JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
				Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();
				List<Map<String, Object>> queryResult = new Gson().fromJson(result, type);
				JsonElement element = new GsonBuilder().create().toJsonTree(queryResult);
				resp.setResult(element);
				return resp;
			}

		}

		return new JSONRPCErrorResponse(request,-32001,JSONRPCError.ERR_32001_NOT_SUPPORT_THIS_API_ACTION);//參數無效
	

	}

}
