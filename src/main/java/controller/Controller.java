package controller;

import http.HttpRequest;
import http.HttpResponse;

public interface Controller {
	void service(HttpRequest req, HttpResponse res);
}
