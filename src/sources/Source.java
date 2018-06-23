package sources;

public interface Source {
	void traversal(PhotoHandler handler);

	boolean contains(String location, String name);

	byte[] getPhotoBytes(String location, String name);

	String downloadPhoto(String location, String name, String targetFolder); // empty output means the success
	                                                        // otherwise it describes the failure

}
