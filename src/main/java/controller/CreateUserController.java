package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class CreateUserController extends AbstractController {
	private static final Logger log = 
			LoggerFactory.getLogger(CreateUserController.class);
	
	@Override
	public void doPost(HttpRequest req, HttpResponse res) {
		// TODO Auto-generated method stub
    	User user = new User(req.getParameter("userId"), req.getParameter("password"), 
    			req.getParameter("name"), req.getParameter("email"));
    	log.debug("User: {}", user);
		DataBase.addUser(user);
        res.sendRedirect("/index.html");
	}

}
