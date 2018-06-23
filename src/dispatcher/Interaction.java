package dispatcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
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

/*
This class is dedicated to the standard actions, providing the interaction 
between the browser, server and database.
These actions include:

1) getting instance of database object
2) reading variables (strings) "id" and "state" from session
3) reading variable "command" (previous command) from session
4) reading variable (string) "command" from request object
5) writing the new command into the session
6) sending map in json form by response object

 The difference between the forms of request and response data 
 is explained by the notation of the "get" and "post" commands in jquery.
 We can send the data from browser as a JavaScript object,
 but we receive the data by browser as a json string. 
 
 */

public class Interaction {

	public boolean success;
	HttpServletRequest request;
	HttpServletResponse response;
	HttpSession session;
	PhotosDatabase db;
	String id;
	String state;
	String command;
	String previousCommand;
	ObjectMapper om;
	Map<String, Object> map;
	// String forBrowser;
	// String forServer;

	Interaction(HttpServletRequest request, HttpServletResponse response)
			throws JsonParseException, JsonMappingException, IOException {
		this.request = request;
		this.response = response;

		// demoRequestMethods();
		// demoRequestHeaders();

		om = new ObjectMapper();
		map = new HashMap<String, Object>();

		SystemParameters sp = new SystemParameters();
		db = new PhotosDatabase(sp.databaseLocation, sp.databaseName);

		session = request.getSession(true);
		id = (String) session.getAttribute("id");
		// id = "1"; // temporary
		state = (String) session.getAttribute("state");
		previousCommand = (String) session.getAttribute("command");

		if (id == null) {
			map.put("command", "error");
			map.put("cause", "the user id has not received");
			send();
			success = false;
			return;
		}

		command = request.getParameter("command");
		session.setAttribute("command", command);
		if (command == null) {
			map.put("command", "error");
			map.put("cause", "the message from user has not received");
			send();
			success = false;
			return;
		}
		success = true;
		// System.out.println("action = " + getAction());

	} // End of constructor

	void send() {
		try {
			if (state != null)
				session.setAttribute("state", state);
			String jsonString = om.writeValueAsString(map);
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // End of send

	/*
	 * The difference between the procedures "getAction()" and
	 * "getAction(String ownName)" is the following. The procedure "getAction()"
	 * creates the action string consisting generally of three components: situation
	 * (state) name, page name and command. If the situation is undefined
	 * (state=null) then the action string consists of page name and command. 
	 * 
	 * The procedure "getAction(String ownName)" creates the action string the described
	 * way in all cases except of the case when the request has come from the page
	 * with the name "ownName.html". Call the page with the name "ownName.html" the
	 * base page. If we debug project from the base page and write "ownName" into
	 * the web.xml file then at the beginning the base page is loaded by the address
	 * "http://localhost:8080/PhotosClassifier/" .
	 * 
	 * In the case of base page the procedure "getAction(String ownName)" check if
	 * the page name coincides with "ownName.html" or with the name
	 * "PhotosClassifier". If one of these conditions performs, then the procedure
	 * "getAction(String ownName)" includes into the action string a command only.
	 * Otherwise the procedure creates the same result as the "getAction()" does.
	 * Attention: the procedures remove the "tail" ".html" and consider the name of
	 * page without suffix.
	 */

	String getAction() {
		String locator = request.getHeader("referer");
		File file = new File(locator);
		String name = file.getName();
		int lastPointIndex = name.lastIndexOf(".");
		if (!(lastPointIndex <= 0 || lastPointIndex >= name.length()))
			name = name.substring(0, lastPointIndex);
		// name is the page name without "html"
		if (state == null)
			return name + ":" + command;
		else
			return state + ":" + name + ":" + command;
	} // End of getAction

	
	String getAction(String ownName) {
		String locator = request.getHeader("referer");
		File file = new File(locator);
		String name = file.getName();
		int lastPointIndex = name.lastIndexOf(".");
		if (!(lastPointIndex <= 0 || lastPointIndex >= name.length()))
			name = name.substring(0, lastPointIndex);
		// System.out.println("situation = " + state);
		// System.out.println("name = " + name);
		// System.out.println("command = " + command);
      if ( (name.compareTo("PhotosClassifier") == 0) || (name.compareTo(ownName) == 0))
    	  return command;
		if (state == null)
			return name + ":" + command;
		else
			return state + ":" + name + ":" + command;
	} // End of getAction
	
	
	
	void saveDates() {
		String date = request.getParameter("startdate");
		session.setAttribute("startdate", date);
		// System.out.println("startdate from browser = "+date);
		// System.out.println("startdate for searching = "+
		// Utils.dateFromBrowser(date));
		date = request.getParameter("finishdate");
		session.setAttribute("finishdate", date);
		// System.out.println("finishdate from browser = "+date);
		// System.out.println("finishdate for searching = "+
		// Utils.dateFromBrowser(date));
	} // End of saveDates

	void demoRequestMethods() {
		System.out.println("ContentType = " + request.getContentType());
		System.out.println("ContextPath = " + request.getContextPath());
		System.out.println("getLocalAddr = " + request.getLocalAddr());
		System.out.println("getLocale = " + request.getLocale());
		System.out.println("LocalName = " + request.getLocalName());
		System.out.println("LocalPort = " + request.getLocalPort());
		System.out.println("QueryString = " + request.getQueryString());
		System.out.println("RemoteHost = " + request.getRemoteHost());
		System.out.println("RequestURI = " + request.getRequestURI());
		System.out.println("RequestURL = " + request.getRequestURL());
		System.out.println("PathInfo = " + request.getPathInfo());
		System.out.println("Protocol = " + request.getProtocol());
		System.out.println("Scheme = " + request.getScheme());
		System.out.println("Where = " + request.getHeader("referer"));
	} // End of demoRequestMethods

	void demoRequestHeaders() {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			System.out.println(key + " = " + value);
		}
	} // End of demoRequestHeaders

} // End of class
