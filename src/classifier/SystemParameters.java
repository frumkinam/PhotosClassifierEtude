package classifier;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SystemParameters {
public	String databaseLocation;
public	String databaseName;
public  String [] extension;
  

public  SystemParameters() {
    	String tempPath="D:/Programming/TomcatProjects";
        try {
            String xmlFilePath = tempPath + File.separator + "PhotosClassifierParameters.xml";
            final File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("DatabaseLocation");
            // System.out.println(nodeList);
            Node node = nodeList.item(0);
            databaseLocation= node.getTextContent();
            // ----------------------------------------  
            nodeList = doc.getElementsByTagName("DatabaseName");
            // System.out.println(nodeList);
            node = nodeList.item(0);
            databaseName= node.getTextContent();
            // ----------------------------------------  
            nodeList = doc.getElementsByTagName("extension");
            int NumberOfElements = nodeList.getLength();
            extension = new String[NumberOfElements];
            for (int m = 0; m < NumberOfElements; m++) {
                node = nodeList.item(m);
                extension[m] = node.getTextContent();
                // System.out.println(extension[m]); 
            } // End of for
            // ----------------------------------------  
          
        } catch (ParserConfigurationException | SAXException
                | IOException ex) {
        	System.out.println(ex);
        } // End of catch

    } // End of constructor
    
}// End of class 
    
// wrong variants
//usersPath="D:/Programming/TomcatProjects/";
		// System.out.println("My location:"+new File("test").getAbsolutePath());
		//	servletContext.getResourceAsStream("DefaultParameters.xml");
		//ServletContext context;
	    //InputStream is = context.getResourceAsStream("DefaultParameters.xml");
	//      final File xmlFile = new File("WEB-INF/DefaultParameters.xml");

