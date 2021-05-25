package com.arcare.api.converter.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.arcare.api.converter.config.Constant;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.service.DynamicParserService;
import com.arcare.api.converter.service.RequestService;
import com.arcare.api.converter.util.Base64DecodeUtil;
import com.arcare.api.converter.vo.JSONRPCError;
import com.arcare.api.converter.vo.JSONRPCErrorResponse;
import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;
import com.arcare.api.converter.vo.JSONRPCSuccessResponse;
import com.arcare.api.converter.vo.ProcessResultStatusVO;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

@Service("FileRequestServiceImpl")
public class FileRequestServiceImpl implements RequestService {

	private final Logger logger = LoggerFactory.getLogger(FileRequestServiceImpl.class);
	
	@Autowired
	@Qualifier("DynamicParserServiceImpl")
	private DynamicParserService dynamicParserService;
	
	@Override
	public JSONRPCResponse request(JSONRPCRequest request, Api config) {
		logger.info("request service start");
		@SuppressWarnings("unchecked")
		ProcessResultStatusVO<String> bindResult = this.dynamicParserService.bindTemplate(config.getApiMeta(),request.readStrDataInParamByMap());
		if(!bindResult.getResult().isPresent()) {
			return new JSONRPCErrorResponse(request,-32002,JSONRPCError.ERR_32002_BIND_ERROR);
		}else {
			logger.info("bindResult "+bindResult.getResult().get());
		}
		
		if(Constant.API_ACTION_FILE_UPLOAD.equals(config.getApiAction())){
			try {
				String base64=bindResult.getResult().get();
				String[] base64Arr = base64.split(",");
				String dataType=base64Arr[0];
				
				String[] typeArr=dataType.split(";");
				String data=typeArr[0];//data:image/png;
				String type=typeArr[1];//type == "base64"
				
				logger.info(String.format("dataType: %s, data %s, type %s", dataType,data,type));
				
				String base64Str=base64Arr[1];
				byte[] bytes=Base64DecodeUtil.decodeToByte(base64Str);
				
				String uuid=UUID.randomUUID().toString();
				
				String rootDir=config.getValueByPropertiesName(Constant.PROP_FILE_ROOT_DIR);
				
				if(null==rootDir
				   ||!new File(rootDir).isDirectory()) {
					boolean result=new File(rootDir).mkdirs();
					if(!result) {
						return new JSONRPCErrorResponse(request,-32703,JSONRPCError.ERR_32703_CREATE_DIR_FAIL);
					}
				}
				File target=new File(rootDir+File.separator+uuid+"."+data.split("/")[1]);
				
				logger.info(String.format("target %s", target.getAbsolutePath()));
				
				com.google.common.io.Files.write(bytes,target);
				JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
				Map<String,String> resultMap=new HashMap<>();
				
				String url=config.getValueByPropertiesName(Constant.PROP_FILE_WEBROOT_URL);

				if(null==url) {
					return new JSONRPCErrorResponse(request,-32706,JSONRPCError.ERR_32706_URL_NOT_DEFINE);
				}

				resultMap.put("resourceUrl", url+target.getName());
				JsonElement element = new GsonBuilder().create().toJsonTree(resultMap);
				resp.setResult(element);
				return resp;
			} catch (IOException e) {
				logger.error("",e);
				return new JSONRPCErrorResponse(request,-32702,JSONRPCError.ERR_32702_FILE_UPLOAD_ERR);
			}
		}else if(Constant.API_ACTION_FILE_DOWNLOAD.equals(config.getApiAction())) {
			String fileName = bindResult.getResult().get();
			
			String rootDir=config.getValueByPropertiesName(Constant.PROP_FILE_ROOT_DIR);
			
			if(null == rootDir) {
				return new JSONRPCErrorResponse(request,-32708,JSONRPCError.ERR_32708_DIR_NOTEXIST);
			}
			
			File file = new File(rootDir,fileName);
			if(!file.exists()) {
				return new JSONRPCErrorResponse(request,-32702,JSONRPCError.ERR_32704_FILE_DOWNLOAD_ERR);
			}
			try {
				Path path = file.toPath();
				byte[] fileContent = Files.readAllBytes(path);
			    String mimeType = Files.probeContentType(path);
				String fileBase64 = Base64DecodeUtil.encodeToString(fileContent);
				JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
				Map<String,String> resultMap=new HashMap<>();
				resultMap.put("file", "data:"+mimeType+";base64."+fileBase64);
				JsonElement element = new GsonBuilder().create().toJsonTree(resultMap);
				resp.setResult(element);
				return resp;
			} catch (IOException e) {
				logger.error("",e);
				return new JSONRPCErrorResponse(request,-32702,JSONRPCError.ERR_32704_FILE_DOWNLOAD_ERR);
			}
		}
		return new JSONRPCErrorResponse(request,-32001,JSONRPCError.ERR_32001_NOT_SUPPORT_THIS_API_ACTION);//參數無效;
	}
}
