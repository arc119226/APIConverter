package com.arcare.api.converter.service.impl;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Service("IBMMQRequestServiceImpl")
public class IBMMQRequestServiceImpl implements RequestService {

	private final Logger logger = LoggerFactory.getLogger(IBMMQRequestServiceImpl.class);
	@Autowired
	@Qualifier("DynamicParserServiceImpl")
	private DynamicParserService dynamicParserService;

	@Override
	public JSONRPCResponse request(JSONRPCRequest request, Api config) {
		@SuppressWarnings("unchecked")
		ProcessResultStatusVO<String> result = this.dynamicParserService.bindTemplate(String.class.cast(config.getApiMeta()),request.readStrDataInParamByMap());
		if(!result.getResult().isPresent()) {
			return new JSONRPCErrorResponse(request,-32002,JSONRPCError.ERR_32002_BIND_ERROR);
		}

		if(Constant.API_ACTION_IBMMQ_SEND.equals(config.getApiAction())){
			String msg=result.getResult().get();
			try {
				JmsConnectionFactory jmsCf=this.getConnectionFactory(config);
				JMSContext context = jmsCf.createContext();
				
				String queueName=config.getValueByPropertiesName(Constant.PROP_IBMMQ_QUEU_NAME);
				Queue destination = context.createQueue("queue:///" +  queueName);
				TextMessage message = context.createTextMessage(msg);
				JMSProducer producer = context.createProducer();
				producer.send(destination, message);
				JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
				JsonElement element = new GsonBuilder().create().toJsonTree(config.getApiAction()+" finish");
				resp.setResult(element);
				return resp;
			} catch (Exception e) {
				logger.error("",e);
				return new JSONRPCErrorResponse(request,-32701,JSONRPCError.ERR_32701_JMS_ERROR+e.getMessage());
			}
		}else if(Constant.API_ACTION_IBMMQ_RECEIVE.equals(config.getApiAction())){
			try {
				JmsConnectionFactory jmsCf=this.getConnectionFactory(config);
				JMSContext context = jmsCf.createContext();
				String queueName=config.getValueByPropertiesName(Constant.PROP_IBMMQ_QUEU_NAME);
				Queue destination = context.createQueue("queue:///" +  String.valueOf(queueName));
				
				JMSConsumer consumer = context.createConsumer(destination); // autoclosable
				try {
					Message receivedMessage = consumer.receive(15l);
					JsonElement element = new GsonBuilder().create().toJsonTree(receivedMessage.getBody(String.class));
					JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
					resp.setResult(element);
					return resp;
				}catch(Exception e) {
					logger.error("",e);
					JsonElement element = new GsonBuilder().create().toJsonTree(null);
					JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
					resp.setResult(element);
					return resp;
				}
			} catch (Exception e) {
				logger.error("",e);
				return new JSONRPCErrorResponse(request,-32701,JSONRPCError.ERR_32701_JMS_ERROR+e.getMessage());
			}
		}
		return new JSONRPCErrorResponse(request,-32001,JSONRPCError.ERR_32001_NOT_SUPPORT_THIS_API_ACTION+config.getApiAction());
	}
	
	private JmsConnectionFactory getConnectionFactory(Api config) throws Exception{
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		JmsConnectionFactory cf = ff.createConnectionFactory();
		String host=config.getValueByPropertiesName(Constant.PROP_IBMMQ_HOST);
		String port=config.getValueByPropertiesName(Constant.PROP_IBMMQ_PORT); //config.getApiPropertiesList().stream().filter(it->Constant.api_port.equals(it.getPropName())).findFirst().get().getPropValue();
		String channel=config.getValueByPropertiesName(Constant.PROP_IBMMQ_CHANNEL);
		String clientType=config.getValueByPropertiesName(Constant.PROP_IBMMQ_CLIENT_TYPE);
		String manager=	config.getValueByPropertiesName(Constant.PROP_IBMMQ_MANAGER);
		String applicationName=config.getValueByPropertiesName(Constant.PROP_IBMMQ_APPLICATION_NAME);
		String userAuthenticationMqcsp=config.getValueByPropertiesName(Constant.PROP_IBMMQ_USER_AUTHENTICATION_MQSP);
		String userid=config.getValueByPropertiesName(Constant.PROP_IBMMQ_USERID);
		String password=config.getValueByPropertiesName(Constant.PROP_IBMMQ_PASSWORD);
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME,host);
		cf.setIntProperty(WMQConstants.WMQ_PORT, Integer.valueOf(port));
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL,channel);
		cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, Integer.valueOf(clientType)/*WMQConstants.WMQ_CM_CLIENT*/);
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, manager);
		cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, applicationName);
		cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, Boolean.valueOf(userAuthenticationMqcsp));
		cf.setStringProperty(WMQConstants.USERID, userid);
		cf.setStringProperty(WMQConstants.PASSWORD,password);
		return cf;
	}
	
	public static void main(String[] args) {
		 String HOST = "192.168.10.139"; 
		 int PORT = 1414; 
		 String CHANNEL = "ARC.DEV.SVRCONN"; 
		 String QMGR = "Q1"; 
		 String QUEUE_NAME = "Q1"; 
		 String APP_USER = "arc"; 
		 String APP_PASSWORD = ""; 
		// Create a connection factory
		try {
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();
			// Set the properties
			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
			cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "APIGW");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, false);
			cf.setStringProperty(WMQConstants.USERID, APP_USER);
			cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
			JMSContext context = cf.createContext();
			Queue destination = context.createQueue("queue:///" + QUEUE_NAME);
			//Set up the message
			long uniqueNumber = System.currentTimeMillis() % 1000;
			TextMessage message = context.createTextMessage("Your lucky number today is " + uniqueNumber);
			JMSProducer producer = context.createProducer();
			producer.send(destination, message);
			JMSConsumer consumer = context.createConsumer(destination); // autoclosable
			JsonElement element = new GsonBuilder().create().toJsonTree(consumer.receiveNoWait().getBody(String.class));
			System.out.println(element);
			JsonElement element1 = new GsonBuilder().create().toJsonTree(null);
			System.out.println(element1);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
