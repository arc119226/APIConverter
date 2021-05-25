package com.arcare.api.converter.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.arcare.api.converter.config.Constant;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.service.DynamicParserService;
import com.arcare.api.converter.service.RequestService;
import com.arcare.api.converter.util.RestUtil;
import com.arcare.api.converter.vo.JSONRPCError;
import com.arcare.api.converter.vo.JSONRPCErrorResponse;
import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;
import com.arcare.api.converter.vo.JSONRPCSuccessResponse;
import com.arcare.api.converter.vo.ProcessResultStatusVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service("JSONRPCRequestServiceImpl")
public class JSONRPCRequestServiceImpl implements RequestService {

	private final Logger logger = LoggerFactory.getLogger(JSONRPCRequestServiceImpl.class);
	@Autowired
	@Qualifier("DynamicParserServiceImpl")
	private DynamicParserService dynamicParserService;
	
	@Override
	public JSONRPCResponse request(JSONRPCRequest request, Api config) {
		@SuppressWarnings("unchecked")
		ProcessResultStatusVO<String> result = this.dynamicParserService.bindTemplate(String.class.cast(config.getApiMeta()),request.readStrDataInParamByMap());
		if(!result.getResult().isPresent()) {
			return new JSONRPCErrorResponse(request,-32002,JSONRPCError.ERR_32002_BIND_ERROR);//參數無效
		}
		
		if(Constant.API_ACTION_JSONRPC_CALL.equals(config.getApiAction())){
			JsonElement jsonElement;
			try {
				if(null==config.getValueByPropertiesName(Constant.PROP_JSONRPC_URL)) {
					return new JSONRPCErrorResponse(request,-32007,JSONRPCError.ERR_32007_API_URL_NOT_EXISTS);
				}
				jsonElement=new JsonParser().parse(result.getResult().get());
				if(jsonElement.isJsonObject()){
					//single
					JSONRPCRequest singleRequest = new ObjectMapper().readValue(result.getResult().get(), JSONRPCRequest.class);

					String respStr=this.processSingle(singleRequest,config.getValueByPropertiesName(Constant.PROP_JSONRPC_URL));
					
					JsonElement element = this.parseResp(singleRequest, respStr);
					JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
					resp.setResult(element);
					
					return resp;
				}else if(jsonElement.isJsonArray()){
					//betch
					List<JSONRPCRequest> requestList = new ObjectMapper().readValue(result.getResult().get(), new TypeReference<List<JSONRPCRequest>>(){});
					List<JsonElement> respList=this.processMulti(requestList,config.getValueByPropertiesName(Constant.PROP_JSONRPC_URL));
					
					JsonElement element = new GsonBuilder().create().toJsonTree(respList);
					
					JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
					resp.setResult(element);
					return resp;
				}
			} catch (Exception e) {
				logger.error("",e);
				return new JSONRPCErrorResponse(request,-32700,JSONRPCError.ERR_32700_PARSE_ERROR);
			}
		}
		return new JSONRPCErrorResponse(request,-32001,JSONRPCError.ERR_32001_NOT_SUPPORT_THIS_API_ACTION);
	}
	
	
	private JsonElement parseResp(JSONRPCRequest request,String json) {
		try {
			return new JsonParser().parse(json);
		}catch(Exception e) {
			logger.error("",e);
			return new GsonBuilder().create().toJsonTree(new JSONRPCErrorResponse());
		}
	}
	
	private String processSingle(JSONRPCRequest request,String api){
		String apiName = api;
		logger.info(String.format("API {%s} BEGIN", apiName));
		long startTime = System.currentTimeMillis();
		try {
			StringBuilder sb = new StringBuilder(apiName);
			logger.info(String.format("API {%s} URL: %s", apiName, sb.toString()));
			logger.info(String.format("API {%s} INPUT: %s",apiName,request));
			HttpHeaders headers = new HttpHeaders();
			// header;
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<JSONRPCRequest> entity = new HttpEntity<>(request, headers);
			RestTemplate restTemplate = RestUtil.getRestTemplate(apiName);
			ResponseEntity<String> response = restTemplate.postForEntity(apiName, entity,String.class);
			logger.info(String.format("API {%s} OUTPUT: %s", apiName, response.getBody()));
			return response.getBody();
		} catch (Exception e) {
			logger.error(String.format("API {%s} EXCEPTION: %s", apiName, e.getMessage()));
			return null;
		} finally {
			logger.info(String.format("API {%s} END (%s ms)", apiName, (System.currentTimeMillis() - startTime)));
		}
	}
	
	private List<JsonElement> processMulti(List<JSONRPCRequest> list,String api){
		List<JsonElement> respList=new CopyOnWriteArrayList<>();
		list.stream().forEach(request->{
			String respJsonStr=this.processSingle(request, api);
			respList.add(this.parseResp(request, respJsonStr));
		});
		return respList;
	}
}
