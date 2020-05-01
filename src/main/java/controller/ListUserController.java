package controller;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import util.HttpRequestUtils;

public class ListUserController implements Controller{
	private static final Logger log = 
			LoggerFactory.getLogger(CreateUserController.class);

	@Override
	public void service(HttpRequest req, HttpResponse res) {
		// TODO Auto-generated method stub
    	Map<String, String> cookies = HttpRequestUtils.parseCookies(req.getHeader("Cookie"));
    	Boolean isLogined = Boolean.parseBoolean(cookies.get("logined"));
    	if(isLogined) {
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
            res.forwardBody(sb.toString());          		
    	}
	}

}
