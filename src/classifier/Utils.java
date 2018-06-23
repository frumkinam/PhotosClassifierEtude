package classifier;

import java.util.Date;
import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import dispatcher.RequestExecution;

public class Utils {
	private static final String[] Monthes = {"January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December"};
	
	public static String correctPathString(String path) {
		String result = "";
		String withoutSpaces = path.replaceAll("\\s", "");
		for (int m = 0; m < withoutSpaces.length(); m++)
			if ((withoutSpaces.charAt(m) != '/') && (withoutSpaces.charAt(m) != '\\'))
				result += withoutSpaces.charAt(m);
			else
				result += '/';
		return result;
	}  // End of correctPathString
	
	public static ArrayList<String> correctPathStrings(ArrayList<String> A) {
		ArrayList<String> result = new ArrayList<String>();
		for (int m = 0; m < A.size(); m++)
			result.add(correctPathString(A.get(m)));
		return result;
	} // End of correctPathStrings
	
public static String arrayListToString(ArrayList<String> al) {
		String result = "";
		int alsL = al.size();
		for (int m = 0; m < alsL - 1; m++)
			result += al.get(m) + ", ";
		if (alsL > 0)
			result += al.get(alsL - 1);
		return result;
	} // End of arrayListToString


	public static ArrayList<String> stringToArrayList(String string) {
		ArrayList<String> result = new ArrayList<String>();
		System.out.println("string with comma = " + string);
		if (!string.isEmpty()) {
			String[] array = string.split(",");
			for (int m = 0; m < array.length; m++)
				result.add(array[m]);
			// result = (ArrayList<String>) Arrays.asList(string.split(","));
		}
		return result;
	} // End of stringToArrayList


public static  ArrayList<String> leftWithoutRight(ArrayList<String> left, ArrayList<String> right) {
	ArrayList<String> result = new ArrayList<String>();
	for (int m = 0; m < left.size(); m++)
		if (!right.contains(left.get(m)))
			result.add(left.get(m));
	return result;
} // End of leftWithoutRight

public static  void showArrayList(ArrayList<String> list, String name) {
	System.out.println(name);
	for (int m = 0; m < list.size(); m++)
		System.out.print(list.get(m) + " ");
	System.out.println("");
} // End of showArrayList

/*public static String arrayListToString(ArrayList<String> al) {
	String result = "";
	int alsL = al.size();
	for (int m = 0; m < alsL - 1; m++)
		result += al.get(m) + ", ";
	if (alsL > 0)
		result += al.get(alsL - 1);
	return result;
} // End of arrayListToString
*/
public static  String jsonElement(String status, String path) {
	return "{ \"status\" : \" "+ status + "\", \"path\" : \"" +path+ "\"}";
	} 

public static  String jsonElement(String firstName, String firstValue, String secondName, String secondValue) {
	return "{\"" + firstName  + "\" : \""+ firstValue + " \", \"" +  secondName +  "\" : \"" + secondValue + "\"}";
	} 

	public static String HashTableListToJson(ArrayList<Hashtable<String, String>> al, String firstKey,
			String secondKey) {
		int n = al.size();
		String result = "[";
		for (int m = 0; m < n - 1; m++) {
			result += jsonElement(firstKey, al.get(m).get(firstKey), secondKey, al.get(m).get(secondKey)) + ",";
		}
		if (n > 0)
			result += jsonElement(firstKey, al.get(n - 1).get(firstKey), secondKey, al.get(n - 1).get(secondKey));
		result += "]";
		return result;
	}

	public static String jsonListIntoBrackets(ArrayList<String> al) {
		return "[" + arrayListToString(al) + "]";
	}
	
	public static String dateFromBrowser(String dateString) {
		System.out.println("dateString= "+dateString);
		String result = null;
		SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy:MM:dd");
		Date date;
		try {
			date = (Date) dateFormate.parse(dateString);
			// System.out.println(date.toString());
			result = Long.toString(date.getTime());
		} catch (ParseException e) {
			System.out.println("Date from browser has not been parsed with " + dateFormate);
			e.printStackTrace();
		} // End of try-catch
		return result;
	} // End of dateFromBrowser
	
	public static Map<String,String> dateForPhotoPlacing(String dateFromDB){
		Map<String, String> map = new HashMap<String, String>();
		Date date = new Date(Long.valueOf(dateFromDB));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		map.put("year", dateFormat.format(date));
		dateFormat = new SimpleDateFormat("MM");
		int index = Integer.parseInt(dateFormat.format(date));
		map.put("month",Monthes[index - 1]);
		dateFormat = new SimpleDateFormat("dd");
		map.put("day",dateFormat.format(date));
		return map;
	} // End of dateForPhotoPlacing
	
} // End of Utils class
