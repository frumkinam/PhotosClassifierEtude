package dispatcher;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classifier.Tags;
import classifier.Utils;

/**
 * Servlet implementation class SetTags
 */
@WebServlet("/SetTags")
public class SetTags extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetTags() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		Communication cm=new Communication(request,response);
		   cm.initGet(); 
		   if(!cm.success) return;
		   // String command = (String) cm.map.get("command");
		   Tags tags= new Tags(cm.db,cm.id);
		   // String tagsJson=tags.getTags();
		   String tagsJson=tags.jsonSampleForMarking();
		   cm.map.clear();
		   cm.map.put("command","ok");
		   cm.map.put("tags",tagsJson);
		   cm.send();
		   System.out.println("doGet");
		   System.out.println(tagsJson);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		Communication cm=new Communication(request,response);
		cm.initPost(); 
		if(!cm.success) return;
		String tagsString = (String) cm.map.get("tags");
		System.out.println("doPost");
		System.out.println("--------------------");
		System.out.println("tagsString = "+tagsString);
		Tags tags= new Tags(cm.db,cm.id);
		     tags.sampleFromMarking(tagsString );
		     tags.show("fromBrowser");
		     tags.handleMarking(tagsString);
		     String tagsJson=tags.jsonSampleForMarking();
		     System.out.println(tagsJson);
		     tags.show("toBrowser");
		 cm.map.clear();
		 cm.map.put("command","ok");
		 cm.map.put("tags",tagsJson);
		 cm.send();
	} // End of doPost

} // End of servlet
