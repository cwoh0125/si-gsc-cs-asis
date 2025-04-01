package kr.co.cs.common.upload;

public class FileInfo {
	
	private String fileName;
	private long fileSize;
	private String fileType;
	
	public String getFileName() {
		return fileName;
	}
	public long getFileSize() {
		return fileSize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	
	
}
