
import java.net.MalformedURLException;
import java.net.URL;

public class TomcatSource extends SourceTemplate{
	
	class TomcatItem extends UrlSourceItem  {
		
		TomcatItem(URL url){super(url);}
		
		TomcatItem child(String line) {
			TomcatItem result = null;
			if (line.length() > file.length())
				try {
					result = new TomcatItem(new URL(protocol, host, port, line));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			return result;
		} // End of child
		
	} // End of class TomcatItem
	
	
	
	TomcatSource(URL url){
		initialTtem=new TomcatItem(url);
	} // End of constructor

	

} //End of class TomcatSource
