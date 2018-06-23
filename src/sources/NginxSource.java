package sources;

import java.net.MalformedURLException;
import java.net.URL;

public class NginxSource extends SourceTemplate{
	
	class NginxItem extends UrlSourceItem {
		
		NginxItem(URL url){super(url);}
		
		NginxItem child(String line) {
			NginxItem result = null;
			if (line.compareTo("../") != 0)
				try {
					result = new NginxItem(new URL(protocol, host, port, file + line));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			return result;
		}

		
	} // End of class NginxItem
	
	
	
public	NginxSource(String path){
		try {
			initialTtem=new NginxItem(new URL(path));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // End of constructor

	

} //End of class NginxSource
