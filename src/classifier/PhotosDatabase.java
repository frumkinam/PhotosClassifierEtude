package classifier;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;



public class PhotosDatabase {
public static Connection connection;
public         Statement statement;

public	PhotosDatabase(String location, String name) {
		try {
			if (connection == null) {
				String sDriverName = "org.sqlite.JDBC";
				Class.forName(sDriverName);
				final String databaseName = name + ".db";
				String sJdbc = "jdbc:sqlite";
				String sDbUrl = sJdbc + ":" + location + File.separator + databaseName;
				connection = DriverManager.getConnection(sDbUrl);
			}
			statement = connection.createStatement();
		} catch (Exception e) {
			System.out.println(e);
		}
	} // End of constructor
	
	void createTable(String name, String... s) {
		if (s.length % 2 == 1) {
			System.out.println("Variable number of arguments is odd. " + "Table creation is impossible.");
			return;
		}
		String ExecutedString = "CREATE TABLE IF NOT EXISTS " + name;
		ExecutedString += " (id integer primary key autoincrement";
		for (int m = 0; m < s.length / 2; m++) {
			ExecutedString += ", " + s[2 * m] + " " + s[2 * m + 1];
		}
		ExecutedString += ")";
		// ExecutedString = ExecutedString + ")";

		try {
			statement.executeUpdate(ExecutedString);
		} catch (Exception e) {
			System.out.println(e);
		}
	} // End of CreateTableIfNotExists

	void executeUpdate(String executedString) {
		try {
			statement.executeUpdate(executedString);
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("executedString = " + executedString);
		}
	} // End of executeUpdate
	

	 void insert(String tableName, String... s) {
	        // String ExecutedString = "INSERT INTO Photos (path,hash) VALUES('";
	        //ExecutedString += path + "','" + hash + "')";
	        String executedString = String.format("INSERT INTO %s ", tableName);
	        String fields = "(";
	        for (int m = 0; m < s.length / 2 - 1; m++) {
	            fields += s[2 * m] + ",";
	        }
	        fields += s[s.length - 2] + ")";
	        String values = "VALUES('";
	        for (int m = 0; m < s.length / 2 - 1; m++) {
	            values += s[2 * m + 1] + "','";
	        }
	        values += s[s.length-1] + "')";
	        executedString += fields + " " + values;
	        System.out.println(executedString);
	        try {
	            statement.executeUpdate(executedString);
	        } catch (SQLException e) {
	            System.out.println(e);
	        };
	    } // End of insert	


	
	public ArrayList<String[]> SqlToArrayList(String sql) {
		ResultSet rs = null;
		ArrayList<String[]> result = new ArrayList<String[]>();
		int maxRows = 9999;
		try {
			rs = statement.executeQuery(sql);
			if (rs == null)
				result = null;
			else {
				ResultSetMetaData md = rs.getMetaData();
				int colCount = md.getColumnCount();
				int rowCount = 0;
				while (rs.next() && rowCount++ < maxRows) {
					String[] fields = new String[colCount];
					for (int i = 0; i < colCount; i++) {
						fields[i] = rs.getString(i + 1);
					}
					result.add(fields);
				}
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("Query [" + sql + "] execution failed. " + e.getMessage());
		}
		return result;
	} // End of SqlToArrayList

	
	public ArrayList<String> arrayListOfColumn(String sql) {
		ResultSet rs = null;
		ArrayList<String> result = new ArrayList<String>();
		int maxRows = 9999;
		try {
			rs = statement.executeQuery(sql);
			if (rs == null)
				result = null;
			else {
				int rowCount = 0;
				while (rs.next() && rowCount++ < maxRows) {
					result.add(rs.getString(1));
				}
			}
			 rs.close();
		} catch (SQLException e) {
			System.out.println("Query [" + sql + "] execution failed. " + e.getMessage());
		}
		return result;
	} // End of SqlToArrayList

	public long tableCount(String tableName, String where) {
		String executedString = String.format("SELECT count(rowid) FROM %1s WHERE %2s", tableName, where);
		long result = 0;
		try {
			ResultSet rs = statement.executeQuery(executedString);
			if (rs.next()) {
				result = rs.getLong(1);
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		;
		return result;
	} // End of tableCount
	
	
	public long totalLength(String tableName, String userIdString) {
		String where = "userid=" + userIdString;
		return tableCount(tableName, where);
	} // End of tableLength

	
	
	boolean isTableEmpty(String tableName, String userIdString) {
		return (totalLength(tableName, userIdString) > 0);
	} // End of isTableEmpty
	
	
	public ArrayList<Hashtable<String,String>> SqlToHashTableList(String sql,String...s) {
		ResultSet rs = null;
		ArrayList< Hashtable<String,String>> result = new ArrayList< Hashtable<String,String>>();
		int maxRows = 9999;
		try {
			rs = statement.executeQuery(sql);
			if (rs == null)
				result = null;
			else {
				ResultSetMetaData md = rs.getMetaData();
				int colCount = md.getColumnCount();
				int rowCount = 0;
				while (rs.next() && rowCount++ < maxRows) {
					Hashtable<String,String> table = new Hashtable<String,String>();
					for (int i = 0; i < colCount; i++) {
						table.put(s[i],rs.getString(i + 1)) ;
					}
					result.add(table);
				}
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("Query [" + sql + "] execution failed. " + e.getMessage());
		}
		return result;
	} // End of SqlToArrayList	
	
	
	
} // End of class PhotosDatabase


//----------------- variants -----------------------------------------------------------------
/*
String [] SqlToStringArray(String sql){
    
}



	public static ArrayList<String[]> DbQuery( String sql, boolean addHeaders, boolean getResults, Object[] params) {
	//  play.Logger.warn(Application.CheckMemoryLeak()+"before DbQuery "+sql);
	  Connection cnn=Application.getGonnection();
	  
	  Statement stmt = null;
	  ResultSet rs = null;
	  ArrayList<String[]> ret = new ArrayList<String[]>();
	  ;
	  int maxRows = 9999;
	  
	  try {
	       if (params!=null) sql = String.format(sql, params);
	   stmt = cnn.createStatement();
	   Logger.debug("DBQuery complete statement." +sql+" params:"+Arrays.toString(params));
	     
	  } catch (Exception e1) {
	   Logger.error("DBQuery failed statement." +sql+" params:"+Arrays.toString(params) +" exception:"+e1.getMessage());
	  }
	      
	  try {
	   if (!getResults){
	    stmt.execute(sql);
	    ret=null;
	   } else 
	    {
	    rs = stmt.executeQuery(sql);
	    if (rs == null) {
	     ret=null;
	    }

	    ResultSetMetaData md = rs.getMetaData();
	    int colCount = md.getColumnCount();
	    if (addHeaders) {
	     String[] headers = new String[colCount];
	     for (int i = 0; i < colCount; i++) {
	      headers[i] = md.getColumnName(i + 1);
	     }
	     ret.add(headers);
	    }
	    int rowCount = 0;

	    while (rs.next() && rowCount++ < maxRows) {
	     String[] fields = new String[colCount];
	     for (int i = 0; i < colCount; i++) {
	      fields[i] = rs.getString(i + 1);
	     }
	     ret.add(fields);
	    }

	    }
	   } catch (SQLException e) {
	    Logger.error("Query [" + sql + "] execution failed. " + e.getMessage());
	   }
	  try {
	   if (stmt != null)
	    stmt.close();
	   if (rs != null)
	    rs.close();
	  } catch (Exception e) {
	   Logger.error("Error closing DB objects. stmt is "+(stmt==null? "null":"not null")+"  rs is "+(rs==null? "null":"not null"));
	  }
	//  play.Logger.warn(Application.CheckMemoryLeak()+"After DbQuery "+sql);
	  return ret;
	 }
	
	
	public static Hashtable<String,String> DbParams( String sql ) {
		  Hashtable<String,String> ret=new Hashtable<String,String>();
		  ResultSet rs=null;
		  try {
		   rs = Application.getGonnection().createStatement().executeQuery(sql);
		   if (rs != null) {
		    while (rs.next()) {
		     ret.put(rs.getString(1), rs.getString(2));
		    }
		    rs.close();
		   }
		  } catch (Exception e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } 
		  return ret;
		 }
	
	
	*/
	
