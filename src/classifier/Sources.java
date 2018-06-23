package classifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import sources.Source;
import sources.NginxSource;

public class Sources {
	PhotosDatabase db;
	String userIdString;
	
public	Sources (PhotosDatabase db, String userIdString) {
		this.db = db;
		this.userIdString=userIdString;
		db.createTable("Sources","userid","numeric","path","text","deleted","text");
		
	} // End of constructor

public static Source  getObject(String path) {
		// temporary !!!
		// it must be the recognizing procedure !!
		return new NginxSource(path);
	} 

	public void delete(String path) {
		String executedString = String.format("UPDATE Sources SET deleted='yes' WHERE userid=%1s AND path='%2s'",
				userIdString, path);
		db.executeUpdate(executedString);
	}
	
	public void add(String path) {
		String where = String.format("userid=%1s AND path='%2s'", userIdString, path);
		if (db.tableCount("Sources", where) > 0) {
			String executedString = String.format("UPDATE Sources SET deleted='no' WHERE " + where);
			db.executeUpdate(executedString);
		} else
			db.insert("Sources", "userid", userIdString, "path", path, "deleted", "no");
	} // End of add

	public void add(String[] sources) {
		for (int m = 0; m < sources.length; m++)
			add(sources[m]);
	} // End of addSources
		
	public void add(ArrayList<String> sources) {
		for (int m = 0; m < sources.size(); m++)
			add(sources.get(m));
	} // End of addSources
	
	public void delete(ArrayList<String> sources) {
		for (int m = 0; m < sources.size(); m++)
			delete(sources.get(m));
	} // End of addSources
	
	public ArrayList<String> getSources() {
		String executedString = String.format("SELECT path from Sources WHERE userid=%s AND deleted='no'", userIdString);
		return db.arrayListOfColumn(executedString);
	} // End of getSources
	
	public ArrayList<Hashtable<String,String>> getIdsAndSources() {
		String sql = String.format("SELECT id,path from Sources WHERE userid=%s AND deleted='no'",userIdString);
		return db.SqlToHashTableList(sql,"sourceid","source");
	} // End of getSources
	
	public boolean isEmpty() {
		long sl = db.totalLength("Sources", userIdString);
		return (sl == 0);
	} // End of isEmpty
		
	/*	public ArrayList<String> getSources() {
			ArrayList<String> SourcesStrings = new ArrayList<String>();
			String executedString = String.format("SELECT path from Sources WHERE userid=%s", userIdString);
			SourcesStrings  = db.arrayListOfColumn(executedString); 
			return SourcesStrings;
		} // End of getSources
*/

	
	
		
	/*	public boolean hasSources() {
			boolean result = false;
			String executedString = String.format("SELECT * from Sources WHERE userid=%s", userIdString);
			try {
				ResultSet rs = db.statement.executeQuery(executedString);
				if (rs.next())
					result = true;
				rs.close();
			} catch (SQLException e) {
				System.out.println(e);
			}
			return result;
		} // End of hasSources
*/	 
	
}
