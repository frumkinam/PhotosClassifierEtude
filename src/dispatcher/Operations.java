package dispatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import classifier.Photos;
import classifier.Sources;
import classifier.Tags;
import classifier.Users;
import classifier.Utils;

/**
 * Servlet implementation class Operations
 */
@WebServlet("/Operations")
public class Operations extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Map<String,RequestExecution> map;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    
	public Operations() {
		super();
		//----  this map contains all variants of request execution ----
		map = new HashMap<String, RequestExecution>();
		
		// --- the page of operation situation creation -----
		
		map.put("values", new RequestExecution() {
			public void execute(Interaction I) {
				Photos photos = new Photos(I.db, I.id);
				String login = new Users(I.db).getLoginById(I.id);
				I.map.put("command", "continue");
				I.map.put("user", login);
				I.map.put("totalNumber",photos.totalNumber());
				I.map.put("doublesNumber",photos.numberOfDoubles());
			} // End of execute
		} // End of object
		);
		
		// ---  support the scanning situation -----------		
		
		map.put("scanning", new RequestExecution() {
			public void execute(Interaction I) {
				I.map.put("command","newpage");
				I.map.put("newpage","execution.html");
				I.state="scanning";	
			} // End of execute
		} // End of object
		);
		
		map.put("scanning:execution:operation", new RequestExecution() {
			public void execute(Interaction I) {
			I.map.put("command","continue");
			I.map.put("operation","Scanning sources and registrating photos");	
			} // End of execute
		} // End of object
		);
		
		map.put("scanning:execution:protocol", new RequestExecution() {
			public void execute(Interaction I) {
				Photos photos=new Photos(I.db,I.id);
				I.map.put("command","continue");
				I.map.put("protocol",photos.scanning());
			} // End of execute
		} // End of object
		);

// ---  Support searching deleted photos ----	
		
		map.put("searchingDeleted", new RequestExecution() {
			public void execute(Interaction I) {
				I.map.put("command","newpage");
				I.map.put("newpage","execution.html");
				I.state="searchingDeleted";	
			
				// I.map.put("command","refresh");
				//I.map.put("totalNumber",photos.totalNumber());
				// I.map.put("doublesNumber",photos.numberOfDoubles());
			} // End of execute
		} // End of object
		);

		
		map.put("searchingDeleted:execution:operation", new RequestExecution() {
			public void execute(Interaction I) {
			I.map.put("command","continue");
			I.map.put("operation","Searching deleted photos and removing their registration records");	
			} // End of execute
		} // End of object
		);
		
		map.put("searchingDeleted:execution:protocol", new RequestExecution() {
			public void execute(Interaction I) {
				Photos photos=new Photos(I.db,I.id);
				I.map.put("command","continue");
				I.map.put("protocol",photos.searchingDeleted());
			} // End of execute
		} // End of object
		);

// --- Support tags edition		-------
		
		map.put("tags", new RequestExecution() {
			public void execute(Interaction I) {
				I.state = "tags";
				I.map.put("command", "newpage");
				I.map.put("newpage", "tags.html");
			} // End of execute
		} // End of object
		);

		map.put("marking", new RequestExecution() {
			public void execute(Interaction I) {
				I.state="marking";
				I.map.put("command","newpage");
				I.map.put("newpage","query.html");
			} // End of execute
		} // End of object
		);

		// --- this object is used for marking and download situations ---		
		RequestExecution queryTags=new RequestExecution() {
			public void execute(Interaction I) {
		  		Tags tags= new Tags(I.db,I.id);
					String tagsJson;
					try {
						tagsJson = tags.jsonSampleForMarking();
						I.map.put("tags",tagsJson);
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					   I.map.put("command","continue");
				} // End of execute
			}; // End of object	
		
		
// ---  support the marking situation -----------	
		
		map.put("marking:query:querytags",queryTags);
		
		map.put("marking:query:all", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				I.map.put("command","newpage");
				I.map.put("newpage","marking.html");	
			} // End of execute
		} // End of object
		);
		
		map.put("marking:query:withouttags", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				I.map.put("command", "newpage");
				I.map.put("newpage", "marking.html");
			} // End of execute
		} // End of object
		);
		
		
		map.put("marking:query:bytags", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				// I.session.setAttribute("command",I.command);
				String tags = I.request.getParameter("tags");
				// System.out.println("tags = " + tags);
				I.session.setAttribute("tags",tags);
				I.map.put("command","newpage");
				I.map.put("newpage","marking.html");
			} // End of execute
		} // End of object
		);

		

		
		// ---- support download  situation ---
	
		map.put("download", new RequestExecution() {
			public void execute(Interaction I) {
				I.state="download";
				I.map.put("command","newpage");
				I.map.put("newpage","query.html");
			} // End of execute
		} // End of object
		);
		
			
		
		
		map.put("download:query:querytags",queryTags);
		
		map.put("download:query:all", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				I.session.setAttribute("sample","all");
				I.map.put("command","newpage");
				I.map.put("newpage","download.html");	
				System.out.println(" !!!***!!!  download:query:all");;
			} // End of execute
		} // End of object
		);
		
		map.put("download:query:withouttags", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				I.session.setAttribute("sample","withouttags");
				I.map.put("command", "newpage");
				I.map.put("newpage", "download.html");
			} // End of execute
		} // End of object
		);
		
		map.put("download:query:bytags", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				String tags = I.request.getParameter("tags");
				// System.out.println("tags = " + tags);
				I.session.setAttribute("tags",tags);
				I.session.setAttribute("sample","bytags");
				I.map.put("command","newpage");
				I.map.put("newpage","download.html");
			} // End of execute
		} // End of object
		);
		
		map.put("download:download:protocol", new RequestExecution() {
			public void execute(Interaction I) {
				 System.out.println("protocol");
				I.map.put("command","continue");
				Photos photos = new Photos(I.db,I.id);
				String startdate = (String) I.session.getAttribute("startdate");
				String finishdate = (String) I.session.getAttribute("finishdate");
				String tags = (String) I.session.getAttribute("tags");
				String command=(String) I.session.getAttribute("sample");
					try {
						ArrayList<Hashtable<String, String>> sample = photos.selecting(command, startdate,finishdate,tags);
						I.map.put("protocol",photos.sampleToReferences(sample));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			} // End of execute
		} // End of object
		);
		

				
	} // End of constructor

	
	// ********************************************************************************
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		Interaction I = new Interaction(request, response);
		if (!I.success) return;
		String action=I.getAction("operations");
		System.out.println("action = " + action);
		map.get(action).execute(I);
		I.send();
	} // End of doGet


	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("=========== doPost =============");
		Interaction I = new Interaction(request, response);
		if (!I.success) return;
		String action=I.getAction("operations");
		System.out.println("action = " + action);
		map.get(action).execute(I);
		I.send();
	
	} // End of doPost

} // End of class
