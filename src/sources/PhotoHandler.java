package sources;

import classifier.PhotoPath;

public interface PhotoHandler {
	void setPath(PhotoPath photoPath);

	void setPhotoBytes(byte[] photoBytes);

	void process();
}
