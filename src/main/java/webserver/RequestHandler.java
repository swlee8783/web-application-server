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
import http.HttpRequest;
import http.HttpResponse;

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
            HttpRequest req = new HttpRequest(in);
            HttpResponse res = new HttpResponse(out);
        	
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
                
        	//GET 방식 회원가입 구현
            if(req.getMethod().equals("GET") && req.getPath().equals("/user/create")) {
            	User user = new User(req.getParameter("userId"), req.getParameter("password"), 
            			req.getParameter("name"), req.getParameter("email"));
            	log.debug("User: {}", user);
            	path = "/index.html";
            }
            
            int c_length = 0;
            String cookie = "";
            
    		// LOGIN 구현
            if(req.getPath().equals("/user/login")) {
            	User user = DataBase.findUserById(req.getParameter("userId"));
            	if(user == null) {
            		log.debug("Login Failed");
            		DataOutputStream dos = new DataOutputStream(out);
            		responseLoginFailed302Header(dos);
            	}
            	else if(user.getPassword().equals(LoginQueryString.get("password"))) {
            		log.debug("Login Success");
            		res.sendRedirect("/index.html");
            	} 
            	else {
            		log.debug("Login Failed");
            		DataOutputStream dos = new DataOutputStream(out);
            		responseLoginFailed302Header(dos);
            	}
            }
            
            // LIST로 이동시 회원정보 출력
            if(req.getPath().equals("/user/list")) {
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
            	
            }
    		
            //POST 방식 회원가입 기능 구현
    		if(req.getPath().equals("/user/create")) {
    			query = IOUtils.readData(br, c_length);
        		Map<String, String> queryString = HttpRequestUtils.parseQueryString(query);
        		User user = new User(queryString.get("userId"), queryString.get("password"), queryString.get("name"), queryString.get("email"));
        		DataBase.addUser(user);
        		
        		log.debug("User: {}", user);
                res.sendRedirect("/index.html");
            }
    		
    		//CSS 적용
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
}
