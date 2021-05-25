package com.arcare.api.converter.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Service("HttpRequestServiceImpl")
public class HttpRequestServiceImpl implements RequestService {
	
	private final Logger logger = LoggerFactory.getLogger(HttpRequestServiceImpl.class);

	@Autowired
	@Qualifier("DynamicParserServiceImpl")
	private DynamicParserService dynamicParserService;

	@Override
	public JSONRPCResponse request(JSONRPCRequest request, Api config) {
		Map<String, Object> strData=request.readStrDataInParamByMap();
		//do render
		ProcessResultStatusVO<String> result = this.dynamicParserService.bindTemplate(String.class.cast(config.getApiMeta()),strData);
		if(!result.getResult().isPresent()) {
			return new JSONRPCErrorResponse(request,-32002,JSONRPCError.ERR_32002_BIND_ERROR);//參數無效
		}
		
		java.lang.reflect.Type type = new TypeToken<Map<String, String>>(){}.getType();
		Map<String, String> renderData = new Gson().fromJson(result.getResult().get(), type);
		Map<String,Object> requestParamsMap=request.readRequestParamsByMap();
		requestParamsMap.put(Constant.STR_DATA, renderData);

		HttpEntity<String> response=this.sendHttpRequest(request, config);
		if(response==null) {
			return new JSONRPCErrorResponse(request,-32002,JSONRPCError.ERR_32709_HTTP_CALL_API_FAIL);//參數無效
		}else {
			JSONRPCSuccessResponse success=new JSONRPCSuccessResponse(request);
			response.getHeaders().getContentType();
			if(MediaType.APPLICATION_JSON_UTF8.equals(response.getHeaders().getContentType())||
			   MediaType.APPLICATION_JSON.equals(response.getHeaders().getContentType())) {
				JsonElement json=new JsonParser().parse(response.getBody());
				success.setResult(json);
				return success;
			}
			// other
			JsonElement element = new GsonBuilder().create().toJsonTree(response);
			success.setResult(element);
			return success;
		}
	}
	/**
	 * 送出請求
	 * @param request
	 * @param api
	 * @return
	 */
	private HttpEntity<String> sendHttpRequest(JSONRPCRequest request,Api api){
		long startTime = System.currentTimeMillis();
		String apiName = api.getApiName();
		try {
			logger.info(String.format("API {%s} BEGIN", apiName));
			
			Map<String, String> strHeaders=request.readStrHeaderInParamByMap();
			Map<String, Object> strData=request.readStrDataInParamByMap();

			StringBuilder apiUrlStr = new StringBuilder(api.getValueByPropertiesName(Constant.PROP_HTTP_URL));
			logger.info(String.format("API {%s} URL: %s", apiName, apiUrlStr.toString()));
			logger.info(String.format("API {%s} INPUT: %s",apiName,request));

			RestTemplate restTemplate = RestUtil.getRestTemplate(apiUrlStr.toString());
			if(Constant.APIACTION_HTTP_POST.equals(api.getApiAction())) {
				MultiValueMap<String,String> data=new LinkedMultiValueMap<>();
				strData.forEach((key,value)->{
					data.add(key, value.toString());
				});
				HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(data, this.prepareHeaders(strHeaders, api));
				ResponseEntity<String> response = restTemplate.postForEntity(apiUrlStr.toString(), entity,String.class);
				logger.info(String.format("API {%s} OUTPUT: %s", apiName, response.getBody()));
				return response;
			}else if(Constant.APIACTION_HTTP_GET.equals(api.getApiAction())) {
				MultiValueMap<String,String> header=new LinkedMultiValueMap<>();
				this.prepareHeaders(strHeaders, api).forEach((k,v)->{
					header.put(k, v);
				});
				HttpEntity<String> entity = new HttpEntity<>(header);
				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrlStr.toString());
				strData.forEach((key,value)->{
					builder.queryParam(key, value);
				});
				HttpEntity<String> response = restTemplate.exchange(
				        builder.build().encode().toUri(), 
				        HttpMethod.GET, entity, String.class);  
				logger.info(String.format("API {%s} OUTPUT: %s", apiName, response.getBody()));
				return response;
			}else {
				throw new Exception("not support this http method.");
			}
		} catch (Exception e) {
			logger.error(String.format("API {%s} EXCEPTION: %s", apiName, e.getMessage()));
			return null;
		} finally {
			logger.info(String.format("API {%s} END (%s ms)", apiName, (System.currentTimeMillis() - startTime)));
		}
	}
	
	/**
	 * 依據 request content type 準備header
	 * @param strHeaders
	 * @param config
	 * @return
	 */
	private HttpHeaders prepareHeaders(Map<String, String> strHeaders,Api config) {
		HttpHeaders headers = new HttpHeaders();
		String defaultMediaType=config.getValueByPropertiesName(Constant.PROP_HTTP_MEDIA_TYPE);
		if(defaultMediaType!=null) {
			if("application/json;charset=UTF-8".equalsIgnoreCase(defaultMediaType)) {
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			}else if("application/json".equalsIgnoreCase(defaultMediaType)) {
				headers.setContentType(MediaType.APPLICATION_JSON);
			}else if("multipart/form-data".equalsIgnoreCase(defaultMediaType)) {//non ascii / large data
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			}else if("application/x-www-form-urlencoded".equalsIgnoreCase(defaultMediaType)) {//ascii/simple data
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			}
		}
		//add other header
		strHeaders.forEach((k,v)->{
			headers.add(k, v);
		});
		return headers;
	}
}
