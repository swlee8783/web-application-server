package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	private DataOutputStream dos = null;
	
	public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
        //body = sb.toString().getBytes();
        //response200Header(dos, body.length);
        //responseBody(dos, body);
	}
	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public void forward(String path) {
		try {
			byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
			if (path.endsWith(".css")) {
				headers.put("Content-Type", "text/css");
			} else if (path.endsWith(".js")) {
				headers.put("Content-Type", "application/javascript");
			} else {
				headers.put("Content-Type", "text/html;charset=utf-8");
			}
			headers.put("Content-Length", body.length + "");
			response200Header(body.length);
			responseBody(body);
		}  catch (IOException e) {
            log.error(e.getMessage());
        }
	}
    
    private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
	public void sendRedirect(String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            processHeaders();
            dos.writeBytes("Location: " + path + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	private void processHeaders() {
		try {
			Iterator<Map.Entry<String, String>> iteratorE = headers.entrySet().iterator();
			while(iteratorE.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iteratorE.next();
				String key = entry.getKey();
				String value = entry.getValue();
				dos.writeBytes(key + ": " + value + " \r\n");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}