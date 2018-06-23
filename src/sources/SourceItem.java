package sources;

import java.net.URL;
import java.util.ArrayList;
import classifier.PhotoPath;

public interface SourceItem {
	boolean isFinal();
	boolean isPhoto();
	ArrayList<SourceItem> getChildren();
	URL getURL();
	byte [] getPhotoBytes();
	PhotoPath getPhotoPath();
	String downloadPhoto(String targetFolder); // empty output means the success 
	                                           // otherwise it describes the failure  
	void show();
	SourceItem down(String name);
}
