package dispatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import classifier.PhotoForBrowser;
import classifier.PhotoPath;
import classifier.Utils;

public class Imitator {
	
	ArrayList<String>  getPathsForMarking(){
		ArrayList<String>  paths = new  ArrayList<String>();
		    paths.add("http://localhost:8080/PhotosClassifier/PhotosRepository/TempNathan/Altay/0_1517ee_dd5ad894_orig.JPG");
			paths.add("http://localhost:8080/PhotosClassifier/PhotosRepository/TempOleg/Kitchen/DSC04978.JPG");
			paths.add("http://localhost:8080/PhotosClassifier/PhotosRepository/TempSasha/Landscapes/LeninaStreet/DSC04970.JPG");
			paths.add("http://localhost:8080/PhotosClassifier/PhotosRepository/TempNathan/DSC04824.JPG");
			paths.add("http://localhost:8080/PhotosClassifier/PhotosRepository/TempOleg/LateAutumn/DSC04916.JPG");
			paths.add("http://localhost:8080/PhotosClassifier/PhotosRepository/Photos/2018/January/01/IMG_4827.JPG");
			paths.add("http://localhost:8080/PhotosClassifier/PhotosRepository/Photos/2016/April/07/DSC04969.JPG");
    return paths;
	} //End of ComparedPhotosPage

	static String LocalToWEB(String pathString)
	{ 	String shortPathString="D:/Programming/TomcatProjects/PhotosClassifier/WebContent";
		Path path =  Paths.get(shortPathString);
		Path longPath=Paths.get(pathString);
		String suffix=path.relativize(longPath).toString();
		return Utils.correctPathString("http://localhost:8080/PhotosClassifier/"+suffix);
	}

	public static void changePathsForBrowser(ArrayList<PhotoForBrowser> A) {
		for (int m = 0; m < A.size(); m++) {
			PhotoForBrowser photo = A.get(m);
			String path = photo.getPath();
			path = LocalToWEB(path);
			photo.setPath(path);
			A.set(m, photo);
		} // End of for
	} // End of changePathsForBrowser
	
} // End of class
