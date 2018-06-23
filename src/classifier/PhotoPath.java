package classifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import sources.Source;


public class PhotoPath {
	// public String sourceId;
	public String source; // usually URL
	public String location; // interior path in the source without name
	public String name; // name of photo with extension
	
	
	public	PhotoPath() {
		source = "";
		location = "";
		name = "";
	}
	

	public PhotoPath(String source, String location,String name){
		// sourceId="";
		this.source=source;
		this.location=location;
		this.name=name;
	} // End of constructor


	public PhotoPath(Path source, Path file){
		// sourceId="";
		this.source=source.toString();
		/* System.out.println("source = "+this.source);
		System.out.println("file = "+file.toString());
		Path locRel=source.relativize(file); 
		System.out.println("locRel = "+locRel); */
		Path locRelParent=source.relativize(file).getParent();
		if (locRelParent==null)
			location="";
		else 
		// System.out.println("locRelParent = "+locRelParent);
		location=locRelParent.toString();
		name = file.getFileName().toString();
	}
	
	

	public Path getPath() {
		String pathString=getPathString();
		// System.out.println("getPath pathString = "+pathString);
		return Paths.get(pathString);
	} // End of getPath

	public String getPathString() {
		String result = source;
		if (!(location.isEmpty() || location.trim().isEmpty()))
			result += File.separator + location;
		result += File.separator + name;
		return result;
	} // End of getPathString

	public void show() {
		// System.out.println("sourceId = "+sourceId);
		System.out.println("source = "+source);	
		System.out.println("location = "+location);	
		System.out.println("name = "+name);	
	} // End of show
	

	
// these procedures must be transfered 	to other classes
	
	/*boolean exists() {
		File file = getPath().toFile();
		return file.exists();
	}*/

	
		 
	public String copy(String newLocation, String catalog, Map<String, String> date) { // The editor said "
		// static context "
		String result = "";
		String year = date.get("year");
		String month = date.get("month");
		String day = date.get("day");

		Path TargetPath = Paths.get(newLocation + File.separator + catalog);
		if (Files.notExists(TargetPath)) {
			try {
				Files.createDirectory(TargetPath);
			} catch (IOException e) {
				result = "No copied. Mistake in the creation of the catalog directory  " + e;
				System.out.println(result);
				return result;
			}
		}

		Path TargetPathYear = TargetPath.resolve(Paths.get(year));
		if (Files.notExists(TargetPathYear)) {
			try {
				Files.createDirectory(TargetPathYear);
			} catch (IOException e) {
				result = "No copied. Mistake in the creation of the year directory" + e;
				System.out.println(result);
				return result;
			}
		}

		Path TargetPathMonth = TargetPathYear.resolve(Paths.get(month));
		if (Files.notExists(TargetPathMonth)) {
			try {
				Files.createDirectory(TargetPathMonth);
			} catch (IOException e) {
				result = "No copied. Mistake in the creation of the month directory  " + e;
				System.out.println(result);
				return result;
			}
		}

		Path TargetPathDay = TargetPathMonth.resolve(Paths.get(day));
		if (Files.notExists(TargetPathDay)) {
			try {
				Files.createDirectory(TargetPathDay);
			} catch (IOException e) {
				result = "No copied. Mistake in the creation of the day directory  " + e;
				System.out.println(result);
				return result;
			}
		}
	 
		Source sourceObject = Sources.getObject(source);
		result=sourceObject.downloadPhoto(location,name,TargetPathDay.toString());
		if (result.isEmpty())
		result = "Copied. New path: " + TargetPathDay.resolve(name).toString();
		else result= "No copied: "+result;
		
		
		/*
		 Old variant 
		 Path NewPath = TargetPathDay.resolve(name);
		// System.out.println("NewPath = " + NewPath.toString());
		try {
			Files.copy(getPath(), NewPath);
		} catch (IOException e) {
			result = "No copied. Mistake in the creation of the new photo file   " + e;
			System.out.println(result);
			return result;
		}

		// System.out.println("Copied " + name);
		result = "Copied. New path: " + NewPath.toString();*/
		
		
		return result;
	} // End of copy

	public String getReference() {
		//hidden=true
		return String.format("<a href='%s' download>photo</a>",getPathString());
	}
	
} // End of class

