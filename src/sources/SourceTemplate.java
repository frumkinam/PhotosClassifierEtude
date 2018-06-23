package sources;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;



public abstract class SourceTemplate implements Source{
SourceItem initialTtem;	
	
	private void recursiveTraversal(SourceItem item, PhotoHandler handler) {
		// System.out.println("currentUrl= "+url.toString());
		if (item.isFinal())
			{ if (item.isPhoto())
			  { // item.show();
			   handler.setPath(item.getPhotoPath());
			   handler.setPhotoBytes(item.getPhotoBytes());
			   handler.process();
			 }
			}
		else {
			ArrayList<SourceItem> children = item.getChildren();
			// for (int k = 0; k < children.size(); k++) children.get(k).show(); 
			for (int m = 0; m < children.size(); m++)
				recursiveTraversal(children.get(m),handler);
		}
	} // End of recursiveTraversal

	public void traversal(PhotoHandler handler) {
		recursiveTraversal(initialTtem, handler);
	}

	
	private SourceItem pathToItem (String location, String name) {
		SourceItem item = initialTtem;
		System.out.println("In pathToItem  location="+ location + ", name= "+name);
		String stringPath=name;
		location=location.trim();
		// if (!location.isEmpty()) 
			stringPath=location + File.separator + stringPath;
		System.out.println("In pathToItem  stringPath= "+stringPath);
		Path path = Paths.get(stringPath);
		int length = path.getNameCount();
		for (int m = 0; m < length; m++) {
			item = item.down(path.getName(m).toString());
			if (item == null) {
				break;
			}
		}
		return item;	
	} // End of pathToItem 
	
	public boolean contains(String location, String name) {
		return (pathToItem (location,name)!=null);
	}
	
	public byte [] getPhotoBytes(String location, String name) {
		return pathToItem(location,name).getPhotoBytes();
	}
	
	public String downloadPhoto(String location, String name, String targetFolder) {
		SourceItem item = pathToItem(location, name);

		if (item == null)
			return "the path " + location + File.separator + name + " not found";
		
		return item.downloadPhoto(targetFolder);
	}
	
} // End of class



	/*boolean contains(String location, String name) {
		boolean result = true;
		SourceItem item = initialTtem;
		Path path = Paths.get(location + File.separator + name);
		int length = path.getNameCount();
		for (int m = 0; m < length; m++) {
			item = item.down(path.getName(m).toString());
			if (item == null) {
				result = false;
				break;
			}
		}
		return result;
	} // End of contains
	*/
	
	
	
	
	/*public boolean exists(URL url) {
		return false;
	}*/

	
