package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;
import util.IOUtils;
import db.DataBase;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            
            // line이 null값을 가질 경우에 대한 예외 처리
            if (line == null) {
            	return;
            }
            String [] tokens = line.split(" ");
            String url = tokens[1];
            String path = url;
        	String query = "";
        	byte [] body = null;
                   
            if(url.startsWith("/user/create")) {
	            String [] tokens_2 = url.split("\\?");
	            path = tokens_2[0];
	            if (tokens_2.length > 1) {
	            	String args = tokens_2[1];
	            	Map<String, String> queryString = HttpRequestUtils.parseQueryString(args);
	                User user = new User(queryString.get("userId"), queryString.get("password"), queryString.get("name"), queryString.get("email"));
	                log.debug("User: {}", user);
	                path = "/index.html";
	            };
            }
            
            int c_length = 0;
            String cookie = "";
            
    		while(!"".equals(line)) {
            	log.info(line);
            	if (line.startsWith("Content-Length")) {
            		String [] post_resources = line.split(" ");
            		c_length = Integer.parseInt(post_resources[1]);
            		log.debug("Content-Length Found: {}", c_length);
            	}
            	else if(line.startsWith("Cookie")) {
            		String [] cookie_resources = line.split(" ");
            		cookie = cookie_resources[1];
            	}
            	line = br.readLine();
            }
    		
            if(url.equals("/user/login")) {
            	query = IOUtils.readData(br, c_length);
            	Map<String, String> LoginQueryString = HttpRequestUtils.parseQueryString(query);
            	User user = DataBase.findUserById(LoginQueryString.get("userId"));
            	if(user == null) {
            		log.debug("Login Failed");
            		DataOutputStream dos = new DataOutputStream(out);
            		responseLoginFailed302Header(dos);
            	}
            	else if(user.getPassword().equals(LoginQueryString.get("password"))) {
            		log.debug("Login Success");
            		DataOutputStream dos = new DataOutputStream(out);
            		responseLoginSuccess302Header(dos);
            	} 
            	else {
            		log.debug("Login Failed");
            		DataOutputStream dos = new DataOutputStream(out);
            		responseLoginFailed302Header(dos);
            	}
            }
            
            if(url.startsWith("/user/list")) {
            	Map<String, String> UserList = HttpRequestUtils.parseCookies(cookie);
            	if(Boolean.parseBoolean(UserList.get("logined"))==true) {
            		Collection<User> users = DataBase.findAll();
            		StringBuilder sb = new StringBuilder();
            		sb.append("<table border='1'>");
            		for(User user : users) {
            			sb.append("<tr>");
            			sb.append("<td>ID</td>");
            			sb.append("<td>NAME</td>");
            			sb.append("<td>EMAIL</td>");
            			sb.append("</tr>");
            			sb.append("<tr>");
            			sb.append("<td>" + user.getUserId() + "</td>");
            			sb.append("<td>" + user.getName() + "</td>");
            			sb.append("<td>" + user.getEmail().replace("%40", "@") + "</td>");
            			sb.append("</tr>");
            		}
            		sb.append("</table>");
            		
                    DataOutputStream dos = new DataOutputStream(out);
                    //byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
                    body = sb.toString().getBytes();
                    response200Header(dos, body.length);
                    responseBody(dos, body);
            		
            	}
            	else {
            		
            	}
            	
            }
    		
    		if(url.startsWith("/user/create")) {
    			query = IOUtils.readData(br, c_length);
        		Map<String, String> queryString = HttpRequestUtils.parseQueryString(query);
        		User user = new User(queryString.get("userId"), queryString.get("password"), queryString.get("name"), queryString.get("email"));
        		DataBase.addUser(user);
        		
        		log.debug("User: {}", user);
        		
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos);
            }
    		
    		String [] mimeType = path.split("\\.");
			DataOutputStream dos = new DataOutputStream(out);
            body = Files.readAllBytes(new File("./webapp" + path).toPath());
            //response200Header(dos,body.length);
            response200HeaderMimeType(dos, body.length, mimeType[mimeType.length - 1]);
            responseBody(dos, body);

    		

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void responseLoginSuccess302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void responseLoginFailed302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");            
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("Set-Cookie: logined=false\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
      
    private void response200HeaderMimeType(DataOutputStream dos, int lengthOfBodyContent, String mimeType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/" + mimeType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    
  private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
      try {
          dos.writeBytes("HTTP/1.1 200 OK \r\n");
          dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
          dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
          dos.writeBytes("\r\n");
      } catch (IOException e) {
          log.error(e.getMessage());
      }
  }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
