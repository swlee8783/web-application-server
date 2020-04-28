package util;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

import http.HttpRequest;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET_getMethod() throws Exception{
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
    	HttpRequest request = new HttpRequest(in);
    	
    	assertEquals("GET", request.getMethod());
    }
    
    @Test
    public void request_GET_getPath() throws Exception{
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
    	HttpRequest request = new HttpRequest(in);

    	assertEquals("/user/create", request.getPath());
    }
    
    @Test
    public void request_GET_getHeader() throws Exception{
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
    	HttpRequest request = new HttpRequest(in);
    	
    	assertEquals("keep-alive", request.getHeader("Connection"));
    }
    
    @Test
    public void request_GET_getParameter() throws Exception{
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
    	HttpRequest request = new HttpRequest(in);

    	assertEquals("javajigi", request.getParameter("userId"));
    }
    
    @Test
    public void request_POST_getMethod() throws Exception{
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
    	HttpRequest request = new HttpRequest(in);
    	
    	assertEquals("POST", request.getMethod());
    }
    
    @Test
    public void request_POST_getPath() throws Exception{
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
    	HttpRequest request = new HttpRequest(in);
    	
    	assertEquals("/user/create", request.getPath());
    }
    
    @Test
    public void request_POST_getHeader() throws Exception{
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
    	HttpRequest request = new HttpRequest(in);
    	
    	assertEquals("keep-alive", request.getHeader("Connection"));
    }
    
    @Test
    public void request_POST_getParameter() throws Exception{
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
    	HttpRequest request = new HttpRequest(in);
    	
    	assertEquals("javajigi", request.getParameter("userId"));
    }
}
