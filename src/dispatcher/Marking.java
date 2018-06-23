package dispatcher;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import classifier.PhotoForBrowser;
import classifier.Photos;
import classifier.Relation;
import classifier.Tags;
import classifier.User;
import classifier.Users;
import classifier.Utils;

/**
 * Servlet implementation class Marking
 */
@WebServlet("/Marking")
public class Marking extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Marking() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		// String name = request.getPathInfo();
		/*String id = request.getParameter("id");
		System.out.println("doGet");
		   System.out.println(id);*/
		// response.sendRedirect(arg0);
		
		Interaction I = new Interaction(request,response);
		if(!I.success) return;
		
		switch (I.command) {
		// send all tags to the query page for selecting
		case "querytags": {
			Tags tags= new Tags(I.db,I.id);
			String tagsJson=tags.jsonSampleForMarking();
			   // I.map.clear();
			   I.map.put("command","continue");
			   I.map.put("tags",tagsJson);
			   I.send();
			break;
		}
		// all the photos defined by the interval of date
		case "all": {
			saveDates(request,I);
			// I.session.setAttribute("command",I.command);
			I.map.put("command","newpage");
			 I.map.put("newpage","marking.html");
			 I.send();
			break;
		}
		
		case "withouttags": {
			saveDates(request,I);
			// I.session.setAttribute("command",I.command);
			I.map.put("command","newpage");
			 I.map.put("newpage","marking.html");
			 I.send();
			break;
		}
		
		// send data to the marking page
		case "formarking": {
			Photos photos= new Photos(I.db,I.id);
			ArrayList<PhotoForBrowser> photosForBrowser =formSampleForMarking(I,photos);
			// Imitator.changePathsForBrowser(photosForBrowser);
			String jsonPhotosForBrowser=photos.SampleForMarkingToJson(photosForBrowser);
			  Tags tags= new Tags(I.db,I.id);
			   Relation relation=new Relation(I.db,I.id);
			   I.map.put("command","continue");
			   I.map.put("photos",jsonPhotosForBrowser);
			   I.map.put("tags",tags.jsonSampleForMarking());
			   I.map.put("relation",relation.jsonSampleForMarking(photosForBrowser));
			   I.send();
		}
		
		} // End of switch

		
	} // End of doGet

	
	ArrayList<PhotoForBrowser> formSampleForMarking(Interaction I, Photos photos)
			throws JsonParseException, JsonMappingException, IOException {
		String startdate = (String) I.session.getAttribute("startdate");
		String finishdate = (String) I.session.getAttribute("finishdate");
		switch (I.previousCommand) {
		case "all": {
			return photos.sampleForMarking(startdate, finishdate);
		}

		case "withouttags": {
			return photos.sampleForMarkingWithoutTags(startdate, finishdate);

		}

		case "bytags": {
			String tags = (String) I.session.getAttribute("tags");
			return photos.sampleForMarking(startdate, finishdate, tags);

		}
		} // End of switch

		return null;
	} // End of formSampleForMarking
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		System.out.println("doPost");
		Interaction I = new Interaction(request,response);
		if(!I.success) return;
		// System.out.println(I.command);
		switch (I.command) {
		// the command from the query page
		case "bytags": { 
			saveDates(request,I);
			// I.session.setAttribute("command",I.command);
			String tags = request.getParameter("tags");
			System.out.println("tags = " + tags);
			I.session.setAttribute("tags",tags);
			I.map.put("command","newpage");
			I.map.put("newpage","marking.html");
			I.send();
			break;
		}
		
		// command from the marking page
		case "savephototags": { 
			String photoid=request.getParameter("photoid");
			System.out.println("photoid = "+photoid);
			String tagids=request.getParameter("tagids");
			System.out.println("tagids = "+tagids);
			Relation relation = new Relation(I.db,I.id);
			relation.handleMarking(photoid, tagids);
			I.map.put("command","continue");
			I.send();
			break;
		}
		
		} // End of switch
		
		// String action = request.getParameter("command");
		
		
	} // End of doPost
	
	void  saveDates (HttpServletRequest request, Interaction I) {
		String date=request.getParameter("startdate");
		I.session.setAttribute("startdate", date);
		//System.out.println("startdate from browser = "+date);
		//System.out.println("startdate for searching = "+ Utils.dateFromBrowser(date));
		date=request.getParameter("finishdate");
		I.session.setAttribute("finishdate", date);
		// System.out.println("finishdate from browser = "+date);
		// System.out.println("finishdate for searching = "+ Utils.dateFromBrowser(date));
	}	// End of saveDates

} // End of class
