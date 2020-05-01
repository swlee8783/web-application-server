package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;
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
            String path = defaultPath(req.getPath());
            
            if("/user/create".equals(path)) {//회원가입 구현
            	createUser(req, res);
            }
            else if("/user/login".equals(path)) {// LOGIN 구현
            	login(req, res);
            }            
            else if("/user/list".equals(path)) {// LIST로 이동시 회원정보 출력
            	listUser(req, res);
            }
        	else {
        		res.forward(path);
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private String defaultPath(String path) {
    	if (path == "/") {
    		return "/index.html";
    	}
    	return path;
    }
}
