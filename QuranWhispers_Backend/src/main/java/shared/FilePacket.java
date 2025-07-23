package shared;

import java.io.Serializable;

public class FilePacket implements Serializable {
    private String email;
    private String reciterName;
    private String surah;
    private String ayah;
    private String filename;
    private byte[] fileData;

    public FilePacket(String email, String reciterName, String surah, String ayah, String filename, byte[] fileData) {
        this.email = email;
        this.reciterName = reciterName;
        this.surah = surah;
        this.ayah = ayah;
        this.filename = filename;
        this.fileData = fileData;
    }

    public String getEmail() { return email; }
    public String getReciterName() { return reciterName; }
    public String getSurah() { return surah; }
    public String getAyah() { return ayah; }
    public String getFilename() { return filename; }
    public byte[] getFileData() { return fileData; }
}
