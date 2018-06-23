package dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import classifier.PhotosDatabase;
import classifier.Sources;
import classifier.SystemParameters;
import classifier.User;
import classifier.Users;
import classifier.Utils;

/**
 * Servlet implementation class SetSources
 */
@WebServlet("/SetSources")
public class SetSources extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
     
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetSources() {
        super();
        // TODO Auto-generated constructor stub
    }

 
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession(true);
	    String id= (String) session.getAttribute("id");
	    System.out.println("user id = " +  id);
	    // String id="1"; // temporary
	     SystemParameters sp = new SystemParameters();
		PhotosDatabase db = new PhotosDatabase(sp.databaseLocation, sp.databaseName);
		Users users=new Users(db);
	    String login=users.getLoginById(id);
	    String sources=getSources(db,id);
 
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("login", login);
		map.put("sources", sources);
			
		String jsonString = om.writeValueAsString(map);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(jsonString);
		
		// response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub; doGet(request, response);
		Interaction I=new Interaction(request,response);
		   if(!I.success) return;
		   // String action = (String) I.map.get("command");
		
		System.out.println("========================================");   
		ArrayList<String> oldSources = new Sources(I.db, I.id).getSources();
		showArrayList(oldSources, "oldSources");
	    		
		// String sourcesString = (String) I.map.get("sources");
		String sourcesString=request.getParameter("sources");
		// String [] sourcesArray= sourcesString.split(",");
		
		ArrayList<String> newSourcesInit = new ArrayList<String>(Arrays.asList(sourcesString.split(",")));
		ArrayList<String> newSources=Utils.correctPathStrings(newSourcesInit);
		showArrayList(newSources, "newSources");

		   // ArrayList<String> newSources= (ArrayList<String>) Arrays.asList(sourcesArray);
		   //System.out.println(newSources.get(0));
		   I.map.clear();
   
		switch (I.command) {
		case "check": { 
			break;
		}
		case "execute": {
			Sources sources = new Sources(I.db, I.id);

			ArrayList<String> deleted=leftWithoutRight(oldSources,newSources);
			//System.out.println("deleted="+deleted.toArray().toString());
			showArrayList(deleted,"deleted");
			sources.delete(deleted);
			
			ArrayList<String> added=leftWithoutRight(newSources,oldSources);
			showArrayList(added,"added");
			//System.out.println("added="+added.toArray().toString());
			sources.add(added);
			
			I.map.put("command","continue");
			I.send();
			break;
		}
		case "repeat": {
			break;
		}
		} // End of switch

			/*
			map.put("login", login);
			map.put("sources", sources);*/
								
	} // End of doPost

	
	String getSources(PhotosDatabase db,String id) {
		Sources sources = new Sources(db, id);
		ArrayList<String> als = sources.getSources();
		String result = "";
		int alsL = als.size();
		for (int m = 0; m < alsL - 1; m++)
			result += als.get(m) + ", ";
		if (alsL > 0)
			result += als.get(alsL - 1);
		return result;
	} // End of getSources
		
	
	String getSources(Interaction I) {
		return arrayListToString(new Sources(I.db, I.id).getSources());
	} // End of getSources
	
	String arrayListToString(ArrayList<String> al) {
		String result = "";
		int alsL = al.size();
		for (int m = 0; m < alsL - 1; m++)
			result += al.get(m) + ", ";
		if (alsL > 0)
			result += al.get(alsL - 1);
		return result;
	} // End of arrayListToString
	 
	
	ArrayList<String> leftWithoutRight(ArrayList<String> left, ArrayList<String> right) {
		ArrayList<String> result = new ArrayList<String>();
		for (int m = 0; m < left.size(); m++)
			if (!right.contains(left.get(m)))
				result.add(left.get(m));
		return result;
	} // End of leftWithoutRight
	

	void showArrayList(ArrayList<String> list, String name) {
		System.out.println(name);
		for (int m = 0; m < list.size(); m++)
			System.out.print(list.get(m) + " ");
		System.out.println("");
	} // End of showArrayList
	
	/*ArrayList<String> deleteWhitespace(ArrayList<String> A) {
		ArrayList<String> result = new ArrayList<String>();
		for (int m = 0; m < A.size(); m++)
			result.add(A.get(m).replaceAll("\\s", ""));
		return result;
	}// End of deleteWhitespace  */	
	
	
} // End of Servlet


/*  Old variants
 * String forBrowser="pete,mike,kate";
 
// String forBrowser="";
PrintWriter out = response.getWriter();
out.write(forBrowser);
System.out.println("Class = " +  request.getClass());*/