package controller;

import http.HttpRequest;
import http.HttpResponse;

public abstract class AbstractController implements Controller {
	@Override
	public void service(HttpRequest req, HttpResponse res) {

		if (req.getMethod().equals("POST")) {
			doPost(req, res);
		}
		else {
			doGet(req, res);
		}
	}
	
	protected void doPost(HttpRequest req, HttpResponse res) {
		
	}
	
	protected void doGet(HttpRequest req, HttpResponse res) {
		
	}
}
