package dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import classifier.PhotosDatabase;
import classifier.SystemParameters;

public class Communication {
    public boolean success;
	HttpServletRequest request;
	HttpServletResponse response;
	HttpSession session;
	String id; 
	PhotosDatabase db;
	ObjectMapper om;
	Map<String, Object> map;
	// String forBrowser;
	// String forServer;
	
	
	Communication(HttpServletRequest request, HttpServletResponse response)
			throws JsonParseException, JsonMappingException, IOException {
		this.request = request;
		this.response = response;

		om = new ObjectMapper();
		map = new HashMap<String, Object>();

		session = request.getSession(true);
		id = (String) session.getAttribute("id");
		// id = "1"; 
	} // End of constructor
	
	
	void initGet() {

		if (id == null) {
			map.put("command","error");
			map.put("cause", "the user id has not received");
			send();
			success = false;
			return;
		}

		SystemParameters sp = new SystemParameters();
		db = new PhotosDatabase(sp.databaseLocation, sp.databaseName);

		success = true;

	} // End of initPost

	
	
	void initPost() throws JsonParseException, JsonMappingException, IOException {

		if (id == null) {
			map.put("command", "error");
			map.put("cause", "the user id has not received");
			send();
			success = false;
			return;
		}

		String forServer = request.getParameter("forServer");
		if (forServer == null) {
			map.put("command", "error");
			map.put("cause", "the message from user has not received");
			send();
			success = false;
			return;
		}

		map = om.readValue(forServer, new TypeReference<Map<String, String>>() {
		});

		SystemParameters sp = new SystemParameters();
		db = new PhotosDatabase(sp.databaseLocation, sp.databaseName);

		success = true;

	} // End of initPost

	
	void send() {
		try {
			String jsonString = om.writeValueAsString(map);
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // End of send


} // End of class

/*
 The rules of message construction
 A message to the browser consists of two or more fields
 The name of first field is "command"
 If command=error, then the field "cause" contains the error cause 
 
A message to server 
 "forServer"
 
 
*/
 

