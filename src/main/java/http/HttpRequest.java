package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	private String method;
	private String path;
	
	public HttpRequest(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();
			
			if(line == null) {
				return;
			}
			
			RequestLine(line);
			
			line = br.readLine();
			
			while(!line.equals("")){
				log.debug("Header: {}", line);
				String[] tokens = line.split(":");
				log.debug(tokens[0]);
				log.debug(tokens[1]);
				headers.put(tokens[0].trim(), tokens[1].trim());
				line = br.readLine();
			}
			
			if ("POST".equals(method)) {
				String query = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
				params = HttpRequestUtils.parseQueryString(query);
			}
		}
		catch(IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void RequestLine(String requestLine) {
		log.debug("Request line: {}", requestLine);
		String [] tokens = requestLine.split(" ");
		method = tokens[0];
		
		if ("POST".equals(method)) {
			path = tokens[1];
			return;
		}
		
		Integer index = tokens[1].indexOf("?");
		
		if (index == -1) {
			path = tokens[1];
		} else {
			path = tokens[1].substring(0, index);
			params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
		}		
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getHeader(String field_name) {
		return headers.get(field_name);
	}
	
	public String getParameter(String param_name) {
		return params.get(param_name);
	}
}