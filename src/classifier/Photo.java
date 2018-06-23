package classifier;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
// --------------------------------------------------
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.BasicFileAttributes;

import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.sanselan.Sanselan;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import sources.Source;

public class Photo extends PhotoPath {
	
	// PhotoPath path;
	String hash;
	long date;
	String metadata;
	String original;
	byte [] photoBytes;
	
	
	
private static final String[] Monthes = { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };
 
	
	Photo() {
		super();
		hash = "";
		date = 0;
		metadata = "";
		original = "";
		photoBytes = null;
	}

	
	
	Photo(Path source, Path filePath) {
		super(source, filePath);
		computeHashCode();
		extractMetadata();
		// show();
	} // End of constructor
    
    
   public void setPath(PhotoPath path) { 
	   source = path.source;
		location = path.location;
		name = path.name;
   } // End of copyPath
   
   public void setPhotoBytes(byte [] photoBytes) {this.photoBytes=photoBytes;}
   
   public void process(){
	   computeHashCode();
	   extractMetadata();
	   show();
   }
   
   
	void extractMetadata() {

		try {
			IImageMetadata metadata = Sanselan.getMetadata(photoBytes);
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			// TiffField field
			// =jpegMetadata.findEXIFValue(TiffConstants.EXIF_TAG_CREATE_DATE);
			createDate(jpegMetadata);
		} catch (ImageReadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		metadata="nothing"; // temporary !!!
	} // End of extractMetadata

    
    private String extractTextBetweenApostrophes(String text) {
        String result = text;
        int index = result.indexOf("'");
        result = result.substring(index + 1);
        index = result.indexOf("'");
        result = result.substring(0, index);
        return result;
    } // End of extractTextBetweenApostrophes
    
   
    
	private void createDate(JpegImageMetadata jpegMetadata) {
		// TiffField field
		// =jpegMetadata.findEXIFValue(TiffConstants.EXIF_TAG_CREATE_DATE);
		TiffField field = jpegMetadata.findEXIFValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
		// System.out.println("field = "+field.toString());
		if (field == null) {
			// createDateByFile();
			date =0; // temporary !!!
			return;
		}
		String s = extractTextBetweenApostrophes(field.toString());
		// System.out.println("date+time = " + s);
		SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		Date date;
		try {
			date = (Date) dateFormate.parse(s);
			// System.out.println(date.toString());
			this.date = date.getTime();
		} catch (ParseException e) {
			System.out.println("Нераспаршена с помощью " + dateFormate);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // End of try-catch

		/*
		 * int index = s.indexOf(" "); s = s.substring(0, index); System.out.println(s);
		 * String[] YearMonthDay = new String[3]; YearMonthDay = s.split(":"); Year =
		 * YearMonthDay[0]; index = Integer.parseInt(YearMonthDay[1]); Month =
		 * Monthes[index - 1]; Day = YearMonthDay[2];
		 */
		// System.out.println(ss[0]);
	} // End of createYearMonthDay

	/*private void createDateByFile() {
		try {
			// FileTime time = Files.getCreationTime(Location); wrong !!
			FileTime time = Files.getLastModifiedTime(getPath());
			// here the more simple and natural way must exist !!
			date = time.toMillis();
			
			 * Date d = new Date(TimeInMLS); SimpleDateFormat dateFormat = new
			 * SimpleDateFormat("yyyy"); Year = dateFormat.format(d); dateFormat = new
			 * SimpleDateFormat("MM"); int index = Integer.parseInt(dateFormat.format(d));
			 * Month = Monthes[index - 1]; // Month = dateFormat.format(d); dateFormat = new
			 * SimpleDateFormat("dd"); Day = dateFormat.format(d);
			 
		} catch (IOException e) {
			System.out.println("Mistake while creating date by file" + e);
		}
	} // End of createYearMonthDayByFile
    */
    
	void computeHashCode() {
		
		MessageDigest md5;
		StringBuffer hexString = new StringBuffer();

		try {
			md5 = MessageDigest.getInstance("md5");
			md5.reset();
			md5.update(photoBytes);
			byte messageDigest[] = md5.digest();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			// System.out.println("hash = " + hexString);
			hash = hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}
	} // End of computeHashCode
    
    
	
    
    
	/*public static boolean isPhoto(Path Location) {
		String fileName = Location.getFileName().toString();
		int lastPointIndex = fileName.lastIndexOf(".");
		if (lastPointIndex <= 0 || lastPointIndex >= fileName.length()) {
			return false;
		}
		String nameExtension = fileName.substring(lastPointIndex + 1);
		// further the checking will be significantly more complicated
		// probably all the possible extensions will be retrived from external file
		// if (NameExtension != "JPG") return false; // Doesn't work !

		if (nameExtension.compareTo("JPG") != 0) {
			System.out.println("not a photo - " + fileName);
			return false;
		}
		return true;
	} // End of isPhoto
*/
    
	public void registrate(PhotosDatabase db, String userId, String sourceId) {
		original = getDouble(db, userId);
		String sql = "INSERT INTO Photos (userid,sourceid,location,name,hash,date,metadata,original)" + String
				.format("Values(%1s, %2s,'%3s','%4s','%5s',%6s, '%7s',",userId,sourceId,location,name,hash,date,metadata);
		if (original == null)
			sql += "NULL)";
		else
			sql += original + ")";
		db.executeUpdate(sql);
		
	} // End of registrate
   
	public String getDouble(PhotosDatabase db, String userId) {
		String execString = String.format("SELECT id FROM Photos WHERE userid=%1s AND hash='%2s' AND ORIGINAL IS NULL",
				userId, hash);
		ArrayList<String> ids = db.arrayListOfColumn(execString);
		if (ids.isEmpty())
			return null;
		return ids.get(0);
	} // End of getDouble
	
	public boolean isRegistrated(PhotosDatabase db, String userId, String sourceId) {
		String where = String.format("userid=%1s AND sourceid=%2s AND location='%3s' AND name='%4s'", 
				userId, sourceId, location, name);
		return db.tableCount("Photos", where) > 0;
	} // End of isRegistrated
    

 
	public boolean isEqual(Photo photo) {
		return (hash.compareTo(photo.hash) == 0);
	 // return (getPathString().compareTo(photo.getPathString()) == 0);
	} // End of isEqual

	
	public void show() {
		System.out.println(toString());
	} // End of show


	public String toString() {
		Date d = new Date(date);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		// String dateString = dateFormat.format(d);
		return "Photo: #source=" + source +" location=" +location + " name=" + name + " #hash=" + hash + 
				" #date=" + dateFormat.format(d) + " #metadata=" +  metadata + " #original=" + original;
	} // End of toString

void setHash(String hash){this.hash=hash;}



} // End of class Photo






