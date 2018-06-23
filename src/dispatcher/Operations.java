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
		map = new HashMap<String, RequestExecution>();
		
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

		// ---  support the selecting situation -----------			
			
		map.put("selecting", new RequestExecution() {
			public void execute(Interaction I) {
				I.state="selecting";
				I.map.put("command","newpage");
				I.map.put("newpage","query.html");
			} // End of execute
		} // End of object
		);
		
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
		
		
		
		map.put("selecting:query:querytags",queryTags);
		
		map.put("selecting:query:all", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				I.session.setAttribute("sample","all");
				I.map.put("command","newpage");
				I.map.put("newpage","execution.html");	
			} // End of execute
		} // End of object
		);
		
		map.put("selecting:query:withouttags", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				I.session.setAttribute("sample","withouttags");
				I.map.put("command", "newpage");
				I.map.put("newpage", "execution.html");
			} // End of execute
		} // End of object
		);
		
		map.put("selecting:query:bytags", new RequestExecution() {
			public void execute(Interaction I) {
				I.saveDates();
				// I.session.setAttribute("command",I.command);
				String tags = I.request.getParameter("tags");
				// System.out.println("tags = " + tags);
				I.session.setAttribute("tags",tags);
				I.session.setAttribute("sample","bytags");
				I.map.put("command","newpage");
				I.map.put("newpage","execution.html");
			} // End of execute
		} // End of object
		);
		
		map.put("selecting:execution:operation", new RequestExecution() {
			public void execute(Interaction I) {
				I.map.put("command","prompt");
				I.map.put("operation","Selecting photos and saving them in the target directory");
				I.map.put("parameters","path to the sample,sample name");
			} // End of execute
		} // End of object
		);
		
		
		map.put("selecting:execution:protocol", new RequestExecution() {
			public void execute(Interaction I) {
				 System.out.println("protocol");
				I.map.put("command","continue");
				String parameters=I.request.getParameter("parameters");
				ArrayList<String> par =Utils.stringToArrayList(parameters);
				String location = par.get(0);
				String catalog = par.get(1);
				 System.out.println("location = " + location);
				 System.out.println("catalog = " + catalog);
				Photos photos = new Photos(I.db,I.id);
				String startdate = (String) I.session.getAttribute("startdate");
				String finishdate = (String) I.session.getAttribute("finishdate");
				String tags = (String) I.session.getAttribute("tags");
				String command=(String) I.session.getAttribute("sample");
					try {
						ArrayList<Hashtable<String, String>> sample = photos.selecting(command, startdate,finishdate,tags);
						I.map.put("protocol",photos.copySelectedPhotos(location,catalog,sample));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			} // End of execute
		} // End of object
		);
		
	
		
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

		
		// ---  support the assembling situation -----------		
		
		map.put("assembling", new RequestExecution() {
			public void execute(Interaction I) {
				I.map.put("command","newpage");
				I.map.put("newpage","execution.html");
				I.state = "assembling";
			} // End of execute
		} // End of object
		);

		map.put("assembling:execution:operation", new RequestExecution() {
			public void execute(Interaction I) {
				I.map.put("command", "prompt");
				I.map.put("operation", "Assembling all photos into the only directory");
				I.map.put("parameters","path to the external sample directory,sample name");
			} // End of execute
		} // End of object
		);

		map.put("assembling:execution:protocol", new RequestExecution() {
			public void execute(Interaction I) {
				Photos photos = new Photos(I.db, I.id);
				I.map.put("command", "continue");
				// ---- get location and catalog name ----
				String parameters = I.request.getParameter("parameters");
				ArrayList<String> par = Utils.stringToArrayList(parameters);
				String location = par.get(0);
				String catalog = par.get(1);
				System.out.println("location = " + location);
				System.out.println("catalog = " + catalog);
				ArrayList<Hashtable<String, String>> sample = photos.assembling();
				I.map.put("protocol", photos.copySelectedPhotos(location, catalog, sample));
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
		
	
		
		//***************************
				
	} // End of constructor

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
		
		/*// System.out.println("========================================");
		// System.out.println("command = "+I.command); 
		switch (I.command) {
		case "values": { 
			Photos photos=new Photos(I.db,I.id);
			String login =new Users(I.db).getLoginById(I.id);
			I.map.put("command","continue");
			I.map.put("user",login);
			I.map.put("totalNumber",photos.totalNumber());
			I.map.put("doublesNumber",photos.numberOfDoubles());
			I.send();
			break;
		}
		
		// scanning sources and modifying the table of photos in the database
		case "scanning": {
			// Photos photos=new Photos(I.db,I.id);
			//photos.scanning();
			I.map.put("command","newpage");
			I.map.put("newpage","execution.html");
			// I.map.put("totalNumber",photos.totalNumber());
			// I.map.put("doublesNumber",photos.numberOfDoubles());
			I.state="scanning";
			I.send();
			break;
		}
		
		// searching deleted photos in the sources
		case "searchingDeleted": {
			Photos photos=new Photos(I.db,I.id);
			photos.searchingDeleted();
			I.map.put("command","refresh");
			I.map.put("totalNumber",photos.totalNumber());
			I.map.put("doublesNumber",photos.numberOfDoubles());
			I.send();
			break;
		}
	// Creating and editing tags
		case "tags": { // is not necessary
			I.state="tags";
			I.map.put("command","newpage");
			I.map.put("newpage","tags.html");
			I.send();
			break;
		}
		// marking photos while viewing
		case "marking": {
			I.state="marking";
			I.map.put("command","newpage");
			I.map.put("newpage","query.html");
			I.send();
			break;
		}
		// selecting photos and forming the new directory of selected photos
		case "selecting": {
			I.state="selecting";
			I.map.put("command","newpage");
			I.map.put("newpage","query.html");
			I.send();
			break;
		}
        // the page requires the name of servlet which must be used in get and post commands 
		case "getservlet": {
			I.state=			
					"selecting";
			I.map.put("command","newpage");
			I.map.put("newpage","query.html");
			I.send();
			break;
		}
		// the first request from the page "execution"
		case "operation": {
			System.out.println("=========== doGet operation  1 =============");	
			formOperation(I);
			I.state=			
					"selecting";
			I.map.put("command","newpage");
			I.map.put("newpage","query.html");
			I.send();
			break;
		}
		// the second request from the page "execution"
		case "protocol": {
			formProtocol(I);
			I.state=			
					"selecting";
			I.map.put("command","newpage");
			I.map.put("newpage","query.html");
			I.send();
			break;
		}
		} // End of switch
*/
	} // End of doGet

	
	/* void formOperation(Interaction I){
		System.out.println("=========== doGet operation 2 =============");	
		switch (I.state) {
		case "scanning":{
			I.map.put("command","continue");
			I.map.put("operation","Scanning sources and registrating photos");
			break;
		}
		
		} // End of switch
	} // End of formOperation
	
	
	void formProtocol(Interaction I){
		switch (I.state) {
		case "scanning":{
			Photos photos=new Photos(I.db,I.id);
			I.map.put("command","continue");
			I.map.put("protocol",photos.scanning());
			//I.map.put("totalNumber",photos.totalNumber());
			// I.map.put("doublesNumber",photos.numberOfDoubles());
			break;
		}
		
		} // End of switch 
	}	
	 
	 */
	
	
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
		//System.out.println("command = "+I.command); 

		/*switch (I.command) {
		// from query page
		case "bytags": { 
			Photos photos=new Photos(I.db,I.id);
			String login =new Users(I.db).getLoginById(I.id);
			I.map.put("command","continue");
			I.map.put("user",login);
			I.map.put("totalNumber",photos.totalNumber());
			I.map.put("doublesNumber",photos.numberOfDoubles());
			I.send();
			break;
		}
	
		} // End of switch
*/		
	} // End of doPost

} // End of class
