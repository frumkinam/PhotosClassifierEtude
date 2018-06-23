package sources;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import classifier.PhotoPath;
import classifier.Utils;

// This is the simple variant of the SourceItem implementation
// useful for Tomcat and Nginx sources

public abstract class UrlSourceItem implements SourceItem {
	URL url;
	String protocol;
	String host;
	int port;
	String file;

private static final String[] PhotoExtensions = {"JPG", "jpg", "RAW", "DNG", "PSD", "TIFF"};
	
	UrlSourceItem(URL url){
		this.url=url;
		protocol = url.getProtocol();
		host = url.getHost();
		port = url.getPort();
		file = url.getFile();
	} // End of constructor
	
	public boolean isFinal() {return file.contains(".");}	
	
	public boolean isPhoto() {
		int lastPointIndex = file.lastIndexOf(".");
		if (lastPointIndex <= 0 || lastPointIndex >= file.length()) {
			return false;
		}
		String nameExtension = file.substring(lastPointIndex + 1);
		return Arrays.asList(PhotoExtensions).contains(nameExtension);
	} // End of isPhoto
	
   abstract UrlSourceItem child(String line);
	
	public  ArrayList<SourceItem> getChildren()  {
			ArrayList<SourceItem> result = new ArrayList<SourceItem>();
			BufferedReader bin;
			try {
				bin = new BufferedReader(new InputStreamReader(url.openStream()));
			
			String line;
			while ((line = bin.readLine()) != null) {
				// System.out.println(line);
				int index = line.indexOf("href=");
					if (index >= 0) {
						line = line.substring(index + "href=".length() + 1);
						index = line.indexOf("\"");
						line = line.substring(0, index);
						UrlSourceItem currentChild=child(line);
						if (currentChild != null) result.add(currentChild);
						// new URL(protocol, host, port,line)
						// if(line.compareTo("../")!=0)
  						 // result.add(new UrlSourceItem(new URL(protocol, host, port,file+line)));
					} // End of second if
				// } // End of first if
			} // End of while
			bin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		} // End of getChildren
	
	public URL getURL() {return url;}
	
	public byte[] getPhotoBytes() {
		byte[] result = null;
		InputStream is;
		try {
			is = url.openStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			// buffer.flush();
			result = buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	} // End of getPhotoBytes
	
	public PhotoPath getPhotoPath() {
		PhotoPath result = null;
		Path path = Paths.get(file);
		// String root = path.getRoot().toString(); // does not work !
		String root = path.getName(0).toString();
		int length = path.getNameCount();
		// System.out.println("length = " + length +   "  #file = "+ file);
		String location="";
		if (length>2) location =Utils.correctPathString(path.subpath(1, length - 1).normalize().toString());
		location=location.trim();
		// location = path.subpath(1, length - 1).normalize().toString();
		String name = path.getFileName().toString();
		try {
			String source = new URL(protocol, host, port, "/"+ root).toString();
			result = new PhotoPath(source, location, name);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Exception !!! length = " + length +   "  #file = "+ file);
		}
		return result;
	} // End of getPhotoPath
	
	
	public String downloadPhoto(String targetFolder) {
		Path path = Paths.get(file);
		String name = path.getFileName().toString();
		byte [] photoArray=getPhotoBytes(); 
		if (photoArray==null) return "the byte array was not received";
		String targetPath=targetFolder+ File.separator +name;
		try {
		FileOutputStream stream = new FileOutputStream(targetPath);
		    stream.write(photoArray);
		    stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "mistakes while writing the byte array "+e;
		} 
		return "";
	} // End of downloadPhoto
	
	public void show() {
		System.out.println("--- url source item ---");
		System.out.println("url = " + url);	
		System.out.println("protocol = " + protocol);	
		System.out.println("port = " + port);
		System.out.println("file = " + file);
	}

	

private void pageProcessing(Object result, Visitor visitor) {
	BufferedReader bin;
	try {
		bin = new BufferedReader(new InputStreamReader(url.openStream()));
	String line;
	while ((line = bin.readLine()) != null) {
		// System.out.println(line);
		int index = line.indexOf("href=");
			if (index >= 0) {
				line = line.substring(index + "href=".length() + 1);
				index = line.indexOf("\"");
				line = line.substring(0, index);
				if (visitor.needBreak(result,line)) break; 
			} // End of if
		} // End of while
	bin.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
} // End of pageProcessing

	public UrlSourceItem down(String name) {
		
		class ItemWrapper {
			public UrlSourceItem item;
			public String name;
			public boolean found;
			ItemWrapper(String name) { 
				item = null; this.name=name; found=false;
			}
			void set(UrlSourceItem item) {
				this.item = item;
			}
			
		} // End of class ItemWrapper
		
		ItemWrapper wrapper = new ItemWrapper(name);
		
		pageProcessing(wrapper, new Visitor() {
			public boolean needBreak(Object wrapper, Object line) {
				( (ItemWrapper) wrapper).set(child((String) line));
				UrlSourceItem item =  ((ItemWrapper)  wrapper).item;
				String name = ( (ItemWrapper)  wrapper).name;
				boolean result=false;
				if (item != null) {
					Path path = Paths.get(item.file);
					String fileName = path.getFileName().toString();
					if (fileName.equals(name)) {
						((ItemWrapper) wrapper).found = true;
						result = true;
					} // End of if
				} // End of if
		   	  return result;
			} // End of needBreak
		} // End of visitor
		);
		
		if (wrapper.found)
		return wrapper.item;
		else return null;
		
	} // End of down
	
	
} // End of class 


/*
 
 pageProcessing(result, new Visitor() {
			public boolean needBreak(Object result, Object line) {
				((CurrentResult) result).set(child((String) line));
				UrlSourceItem yyy=(CurrentResult) result.item;
				return (result.item != null);
			} // End of 
		} // End of visitor
		);

 
  



switch (length) {
		case 1: {
			
			break;
		}
		case 2: {
			
			break;
		}
		default:{}
				
		} // end of switch
		

*/