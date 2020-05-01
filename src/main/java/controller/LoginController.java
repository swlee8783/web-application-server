package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController implements Controller {
	private static final Logger log = 
			LoggerFactory.getLogger(CreateUserController.class);

	@Override
	public void service(HttpRequest req, HttpResponse res) {
		// TODO Auto-generated method stub
    	User user = DataBase.findUserById(req.getParameter("userId"));
    	if(user == null) {
    		log.debug("Login Failed");
    		res.addHeader("Set-Cookie", "logined=false");
    	}
    	else if(user.getPassword().equals(req.getParameter("password"))) {
    		log.debug("Login Success");
    		res.addHeader("Set-Cookie", "logined=true");
    		res.sendRedirect("/index.html");
    	} 
    	else {
    		log.debug("Login Failed");
    		res.sendRedirect("/index.html");
    	}
	}

}
