package com.amway.pojo.request;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class RequestSpecificationDTO {
	
	private Map<String , String > headers;
	private String module;
	private String uri;
	private String contentType;
	private String method;
	private String proxy;
	private Map<String , String > queryParam;
	private Map<String , Object > formParam;
	private Object requestObject;
	private Boolean urlEncoding;
	private  JSONObject requestBody;
	private JSONArray requestArrayBody;

}
