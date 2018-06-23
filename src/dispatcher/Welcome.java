package dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

/**
 * Servlet implementation class Welcome
 */
@WebServlet("/Welcome")
public class Welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Welcome() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("doPost ! ");
		String jsonString = request.getParameter("data");
		System.out.println("jsonString = "+jsonString);
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		SystemParameters sp = new SystemParameters();
		PhotosDatabase db = new PhotosDatabase(sp.databaseLocation, sp.databaseName);
		Users users = new Users(db);
		try {
			map = om.readValue(jsonString, new TypeReference<Map<String, String>>() {
			});
			System.out.println("map = " + map);
			String page = (String) map.get("page");
			// System.out.println("page = "+page);
			switch (page) {
			case "welcome": { // the request has come from Entrance
				String login = (String) map.get("login"); 
				String password = (String) map.get("password");
				User user = users.getUser(login);
				map.clear();
				if (user == null) {
					System.out.println("login = "+login);
					map.put("command", "repeat");
					map.put("data", "The submitted login is not  found. Repeat input, please.");
				} else {
					user.show();
					System.out.println("password = "+password);
					System.out.println("userpassword = "+user.password);
					if (password.compareTo(user.password) != 0) {
						map.put("command", "repeat");
						map.put("data", "The submitted password is wrong. Repeat input, please.");
					} else {
						System.out.println("login = " + user.login);
						HttpSession session = request.getSession(true);
						// session.setAttribute("login", user.login);
						String id = Long.toString(user.id);
						session.setAttribute("id", id);
						map.put("command", "newpage");
						if (new Sources(db, id).isEmpty())
							map.put("data", "sources.html");
						else
							map.put("data", "operations.html");
					}

				} // End of first if-else

				break;
			}
			case "registration": {
				users.addUser((String) map.get("login"), (String) map.get("password"), (String) map.get("language"));
				map.clear();
				map.put("command", "newpage");
				map.put("data", "sources.html");
				break;
			}

			} // End of switch

		} catch (IOException e) {
			e.printStackTrace();
		}

		jsonString = om.writeValueAsString(map);
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(jsonString);

	} // End of doPost

} // End of Servlet

/*
System.out.println("a= "+jsonArray);
		char[] c=jsonArray.toCharArray();
		System.out.println(c[0]);
	     response.setContentType("text/plain");
		 response.setCharacterEncoding("UTF-8");
		 PrintWriter out=response.getWriter();
		 // response.getWriter().write("hello!");
		 out.write("hello!");
 
 
 */
