package com.arcare.api.converter.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.arc.api.converter.config.Constant;
import com.arcare.api.converter.dao.BackboneDAO;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.service.InvokeService;
import com.arcare.api.converter.service.RequestService;
import com.arcare.api.converter.vo.JSONRPCError;
import com.arcare.api.converter.vo.JSONRPCErrorResponse;
import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Service("APIInvokeServiceImpl")
public class APIInvokeServiceImpl implements InvokeService {

	private final Logger logger = LoggerFactory.getLogger(APIInvokeServiceImpl.class);
	
	@Autowired
	@Qualifier("BackboneDAOImpl")
	private BackboneDAO backboneDAO;
	
	@Autowired
	@Qualifier("SQLRequestServiceImpl")
	private RequestService sQLInvokeService;
	
	@Autowired
	@Qualifier("JSONRPCRequestServiceImpl")
	private RequestService jSONRPCInvokeService;
	
	@Autowired
	@Qualifier("IBMMQRequestServiceImpl")
	private RequestService iBMMQInvokeService;
	
	@Autowired
	@Qualifier("FileRequestServiceImpl")
	private RequestService fileRequestServiceImpl;
	
	@Autowired
	@Qualifier("HttpRequestServiceImpl")
	private RequestService httpRequestServiceImpl;
	
	@Autowired
	@Qualifier("TemplateRequestServiceImpl")
	private RequestService templateRequestServiceImpl;
	
	private Map<String,RequestService> functionMap;
	
	@PostConstruct
	public void init() {
		logger.info("init functionMap");
		functionMap=new HashMap<>();
		this.functionMap.put(Constant.API_TYPE_TEMPLATE, templateRequestServiceImpl);
		this.functionMap.put(Constant.API_TYPE_JDBC, sQLInvokeService);
		this.functionMap.put(Constant.API_TYPE_JSONRPC, jSONRPCInvokeService);
		this.functionMap.put(Constant.API_TYPE_IBMMQ, iBMMQInvokeService);
		this.functionMap.put(Constant.API_TYPE_FILE, fileRequestServiceImpl);
		this.functionMap.put(Constant.API_TYPE_HTTP, httpRequestServiceImpl);
	}

	@Override
	public JSONRPCResponse invoke(JSONRPCRequest request) {
		logger.info("process invoke");
		if(false== (request.getParams() instanceof Map)) {
			logger.error("not support");
			return new JSONRPCErrorResponse(request);//not support
		}
		@SuppressWarnings("unchecked")
		Map<String,Object> map = Map.class.cast(request.getParams());
		
		if(map.get(Constant.STR_API_ID)==null) {
			return new JSONRPCErrorResponse(request,-32003,JSONRPCError.ERR_32003_API_ID_IS_NULL);
		}
		
		String apiUUID = (String) map.get(Constant.STR_API_ID);

		Optional<Api> configOpt = this.backboneDAO.queryApiByUUID(apiUUID);

		if(!configOpt.isPresent()) {
			return new JSONRPCErrorResponse(request,-32005,JSONRPCError.ERR_32005_API_CONFIG_NOT_EXISTS);//not support
		}
		
		logger.info("api :"+configOpt.get());
		if(null != functionMap.get(configOpt.get().getApiType())){
			return functionMap.get(configOpt.get().getApiType()).request(request, configOpt.get());
		}
		return new JSONRPCErrorResponse(request,-32006,JSONRPCError.ERR_32006_NOT_SUPORT_THIS_API_TYPE+configOpt.get().getApiType());
	}
}
