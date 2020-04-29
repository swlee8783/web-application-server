package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;

public class RequestLine {
	private static final Logger log = LoggerFactory.getLogger(RequestLine.class);
	
	private String method;
	private String path;
	private Map<String, String> params = new HashMap<String, String>();
	
	public RequestLine(String requestLine) {
		log.debug("Request line: {}", requestLine);
		String [] tokens = requestLine.split(" ");
		
		if(tokens.length != 3) {
			throw new IllegalArgumentException(requestLine + "not fit");
		}
		
		method = tokens[0];
		//log.debug("Method: {}", method);
		
		Integer index = tokens[1].indexOf("?");
		
		if ("POST".equals(method) || index == -1) {
			path = tokens[1];
			return;
		}

		path = tokens[1].substring(0, index);
		params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getPath() {
		return path;
	}
	
	public Map<String, String> getParams(){
		return params;
	}
}