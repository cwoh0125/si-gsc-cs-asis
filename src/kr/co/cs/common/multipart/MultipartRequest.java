package kr.co.cs.common.multipart;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;

public class MultipartRequest {
	private static final int DEFAULT_MAX_POST_SIZE = 30; // 30 Mega

	private String upfileDir = null;

	private ServletRequest req;
	private File dir;
	private String sfilenm;
	private int maxLen;

	private Hashtable parameters = new Hashtable(); // name - value
	private Hashtable files = new Hashtable(); // name - UploadedFile

	private final static Logger errlogger = LogManager.getLogger("process.etc");
	
	public MultipartRequest(ServletRequest req, String flag) throws IOException {
		this(req, flag, "", DEFAULT_MAX_POST_SIZE);
	}

	public MultipartRequest(ServletRequest req, String flag, int maxLen)
			throws IOException {
		this(req, flag, "", maxLen);
	}

	public MultipartRequest(ServletRequest req, String flag, String fnm)
			throws IOException {
		this(req, flag, fnm, DEFAULT_MAX_POST_SIZE);
	}

	public MultipartRequest(ServletRequest req, String flag, String fnm,
			int maxLen) // flag - 경로명
			throws IOException {

		if ("WIN".equals(ComUtil.getOsMinName())) {
			upfileDir = Const.SAVEDFILE_LOCALPATH;
		} else {
			upfileDir = Const.SAVEDFILE_UNIXPATH;
		}

		sfilenm = fnm;
		String uploadDir = "";
		if (!"".equals(flag))
			uploadDir = flag; // 경로명이 오면
		else
			uploadDir = upfileDir;

		// Sanity check values
		if (req == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (uploadDir == null) {
			throw new IllegalArgumentException("saveDirectory cannot be null");
		}
		if (maxLen <= 0) {
			throw new IllegalArgumentException("maxPostSize must be positive");
		}

		// Save the request, dir, and max size
		this.req = req;
		dir = new File(uploadDir);
		this.maxLen = maxLen * 1024 * 1024;

		// Check saveDirectory is truly a directory
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + uploadDir);
		}
		// Check saveDirectory is writable
		if (!dir.canWrite()) {
			throw new IllegalArgumentException("Not writable: " + uploadDir);
		}
		// Now parse the request saving data to "parameters" and "files";
		// write the file contents to the saveDirectory
		readRequest();
	}

	public Enumeration getParameterNames() {
		return parameters.keys();
	}

	public Enumeration getFileNames() {
		//System.out.println("files.toString() => " + files.toString());
		return files.keys();
	}

	public String getParameter(String name) {
		try {
			Object param = (Object) parameters.get(name);

			if (param == null) {
				return (null);
			} else if (param instanceof String) {
				if ("".equals((String) param)) {
					return (null);
				} else {
					return ((String) param);
				}
			} else if (param instanceof String[]) {
				if ("".equals(((String[]) param)[0])) {
					return (null);
				} else {
					return (((String[]) param)[0]);
				}
			}
			return ((String) param);
		} catch (Exception e) {
			return (null);
		}
	}

	public String[] getParameterValues(String name) {
		Object param = (Object) parameters.get(name);

		if (param == null) {
			return (null);
		} else if (param instanceof String) {
			String str[] = new String[1];

			str[0] = (String) param;
			return (str);
		} else if (param instanceof String[]) {
			return ((String[]) param);
		}
		return ((String[]) null);
	}

	public String getFilesystemName(String name) {
		try {
			UploadedFile file = (UploadedFile) files.get(name);

			return (file.getFilesystemName()); // may be null
		} catch (Exception e) {
			
			return null;
		}
	}
	
	public String getSrcName(String name) {
		try {
			UploadedFile file = (UploadedFile) files.get(name);

			return (file.getSrcName()); // may be null
		} catch (Exception e) {
			return null;
		}
	}	

	public String getContentType(String name) {
		try {
			UploadedFile file = (UploadedFile) files.get(name);

			return (file.getContentType()); // may be null
		} catch (Exception e) {
			return (null);
		}
	}

	public File getFile(String name) {
		try {
			UploadedFile file = (UploadedFile) files.get(name);

			return (file.getFile()); // may be null
		} catch (Exception e) {
			return (null);
		}
	}

	protected void readRequest() throws IOException {
		String type = req.getContentType();

		if ((type == null)
				|| (!type.toLowerCase().startsWith("multipart/form-data"))) {
			throw new IOException(
					"Posted content type isn't multipart/form-data");
		}

		// Check the content length to prevent denial of service attacks
		int len = req.getContentLength();

		if (len > maxLen) {
			throw new IOException("Content length: " + len + ", Limit: "
					+ maxLen);
		}

		// Get the boundary string; it's included in the content type.
		// Should look something like "------------------------12012133613061"
		String boundary = extractBoundary(type);
		if (boundary == null) {
			throw new IOException("Separation boundary was not specified");
		}

		// Construct the special input stream we'll read from
		MultipartInputStreamHandler in = new MultipartInputStreamHandler(req
				.getInputStream(), boundary, len);

		// Read the first line, should be the first boundary
		String line = in.readLine();
		if (line == null) {
			throw new IOException("Corrupt form data: premature ending");
		}

		// Verify that the line is the boundary
		if (!line.startsWith(boundary)) {
			throw new IOException("Corrupt form data: no leading boundary");
		}

		// Now that we're just beyond the first boundary, loop over each part
		boolean done = false;
		while (!done) {
			done = readNextPart(in, boundary);
		}
	}

	protected boolean readNextPart(MultipartInputStreamHandler in,
			String boundary) throws IOException {
		// Read the first line, should look like this:
		// content-disposition: form-data; name="field1"; filename="file1.txt"
		String line = in.readLine();
		if (line == null) {
			// No parts left, we're done
			return (true);
		}

		// Parse the content-disposition line
		String[] dispInfo = extractDispositionInfo(line);
		String disposition = dispInfo[0];
		String name = dispInfo[1];
		String filename = dispInfo[2];
		String srcFileName ="";
		// Now onto the next line. This will either be empty
		// or contain a Content-Type and then an empty line.
		line = in.readLine();
		if (line == null) {
			// No parts left, we're done
			return (true);
		}

		// Get the content type, or null if none specified
		String contentType = extractContentType(line);
		if (contentType != null) {
			// Eat the empty line
			line = in.readLine();
			if ((line == null) || (line.length() > 0)) { // line should be
															// empty
				throw new IOException("Malformed line after content type: "
						+ line);
			}
		} else {
			// Assume a default content type
			contentType = "application/octet-stream";
		}

		// Now, finally, we read the content(end after reading the boundary)
		if (filename == null) {
			// This is a parameter
			String value = readParameter(in, boundary);

			Object oldValue = parameters.get(name);
			if (oldValue == null) {
				parameters.put(name, value);
			} else if (oldValue instanceof String) {
				String[] newValues = new String[2];

				newValues[0] = value;
				newValues[1] = (String) oldValue;

				parameters.put(name, newValues);
			} else if (oldValue instanceof String[]) {
				String[] oldValues = (String[]) oldValue;
				String[] newValues = new String[oldValues.length + 1];

				newValues[0] = value;
				for (int i = 0; i < oldValues.length; i++) {
					newValues[i + 1] = oldValues[i];
				}

				parameters.put(name, newValues);
			}
		} else {
			
			srcFileName = filename;
			
			String imgFile = getParameter("imgFile");
			
			if ("Y".equals(imgFile)){
				filename = "IMG_" + ComUtil.getCurDateTime("yyyyMMddHHmmssSSS");				
			}else{			
				// This is a file
				if (!"".equals(sfilenm)) {
					filename = sfilenm;
				} else {
					filename = filename.replaceAll(" ", "");
					filename = filename.replaceAll("-", "");
					filename = filename.replaceAll("_", "");
					filename = filename
							+ ComUtil.getCurDateTime("yyyyMMddHHmmssSSS");
				}
			}
			
			/*
			System.out.println("1####################");
			System.out.println("srcFileName =>" + srcFileName);
			System.out.println("filename =>" + filename);
			System.out.println("1####################"); 		
			*/

			readAndSaveFile(in, boundary, filename);
			// if("unknown".equals(filename)) {
			if (filename.startsWith("unknown")) { // kku추가
				files.put(name, new UploadedFile(null, null, null, null));
			} else {
				files.put(name, new UploadedFile(dir.toString(), filename, contentType, srcFileName));
			}

		}
		return (false); // there's more to read
	}

	protected String readParameter(MultipartInputStreamHandler in,
			String boundary) throws IOException {
		StringBuffer sbuf = new StringBuffer();
		String line;

		while ((line = in.readLine()) != null) {
			if (line.startsWith(boundary)) {
				break;
			}
			sbuf.append(line + "\r\n"); // add the \r\n in case there are many
										// lines
		}
		if (sbuf.length() == 0) {
			return (null); // nothing read
		}
		sbuf.setLength(sbuf.length() - 2); // cut off the last line's \r\n

		return (sbuf.toString()); // no URL decoding needed
	}

	protected void readAndSaveFile(MultipartInputStreamHandler in,
			String boundary, String filename) throws IOException {
		OutputStream out = null;
		
		try {
			if (filename.startsWith("unknown")) { // kku추가
				out = new ByteArrayOutputStream();
			} else {
				File f = new File(dir + File.separator + filename);
				out = new BufferedOutputStream(new FileOutputStream(f), 8 * 1024); // 8K
			}

			byte[] bbuf = new byte[100 * 1024]; // 100K
			int result;
			String line;

			// ServletInputStream.readLine() has the annoying habit of
			// adding a \r\n to the end of the last line.
			// Since we want a byte-for-byte transfer, we have to cut those chars.
			boolean rnflag = false;
			while ((result = in.readLine(bbuf, 0, bbuf.length)) != -1) {
				// Check for boundary
				if (result > 2 && bbuf[0] == '-' && bbuf[1] == '-') { // quick
																		// pre-check
					line = new String(bbuf, 0, result, "ISO8859-1");
					if (line.startsWith(boundary)) {
						break;
					}
				}
				// Are we supposed to write \r\n for the last iteration?
				if (rnflag) {
					out.write('\r');
					out.write('\n');
					rnflag = false;
				}
				// Write the buffer, postpone any ending \r\n
				if ((result >= 2) && (bbuf[result - 2] == '\r')
						&& (bbuf[result - 1] == '\n')) {
					out.write(bbuf, 0, result - 2); // skip the last 2 chars
					rnflag = true; // make a note to write them on the next
									// iteration
				} else {
					out.write(bbuf, 0, result);
				}
			}
		} catch (Exception e) {
			errlogger.debug("MultipartRequest Exception ::" + e.getMessage());
		} finally {
			//out.flush();
			out.close();
		}
	}

	// Extracts and returns the boundary token from a line.
	//
	private String extractBoundary(String line) {
		int index = line.indexOf("boundary=");

		if (index == -1) {
			return null;
		}
		String boundary = line.substring(index + 9); // 9 for "boundary="

		// The real boundary is always preceeded by an extra "--"
		boundary = "--" + boundary;

		return boundary;
	}

	// Extracts and returns disposition info from a line, as a String array
	// with elements: disposition, name, filename. Throws an IOException
	// if the line is malformatted.
	//
	private String[] extractDispositionInfo(String line) throws IOException {
		// Return the line's data as an array: disposition, name, filename
		String[] retval = new String[3];

		// Convert the line to a lowercase string without the ending \r\n
		// Keep the original line for error messages and for variable names.
		String origline = line;
		line = origline.toLowerCase();

		// Get the content disposition, should be "form-data"
		int start = line.indexOf("content-disposition: ");
		int end = line.indexOf(";");
		if (start == -1 || end == -1) {
			throw new IOException("Content disposition corrupt: " + origline);
		}
		String disposition = line.substring(start + 21, end);
		if (!disposition.equals("form-data")) {
			throw new IOException("Invalid content disposition: " + disposition);
		}

		// Get the field name
		start = line.indexOf("name=\"", end); // start at last semicolon
		end = line.indexOf("\"", start + 7); // skip name=\"
		if (start == -1 || end == -1) {
			throw new IOException("Content disposition corrupt: " + origline);
		}
		String name = origline.substring(start + 6, end);

		// Get the filename, if given
		String filename = null;
		start = line.indexOf("filename=\"", end + 2); // start after name
		end = line.indexOf("\"", start + 10); // skip filename=\"
		if (start != -1 && end != -1) { // note the !=
			filename = origline.substring(start + 10, end);
			// The filename may contain a full path. Cut to just the filename.
			int slash = Math.max(filename.lastIndexOf('/'), filename
					.lastIndexOf('\\'));
			if (slash > -1) {
				filename = filename.substring(slash + 1); // past last slash
			}
			if ("".equals(filename)) {
				filename = "unknown"; // sanity check
			}
		}

		// Return a String array: disposition, name, filename
		retval[0] = disposition;
		retval[1] = name;
		retval[2] = filename;

		return (retval);
	}

	// Extracts and returns the content type from a line, or null if the
	// line was empty. Throws an IOException if the line is malformatted.
	//
	private String extractContentType(String line) throws IOException {
		String contentType = null;

		// Convert the line to a lowercase string
		String origline = line;
		line = origline.toLowerCase();

		// Get the content type, if any
		if (line.startsWith("content-type")) {
			int start = line.indexOf(" ");
			if (start == -1) {
				throw new IOException("Content type corrupt: " + origline);
			}
			contentType = line.substring(start + 1);
		} else if (line.length() != 0) { // no content type, so should be
											// empty
			throw new IOException("Malformed line after disposition: "
					+ origline);
		}

		return contentType;
	}
}

// A class to hold information about an uploaded file.
//
class UploadedFile { 
	private String dir;
	private String filename;
	private String type;
	private String srcFileName;

	UploadedFile(String dir, String filename, String type, String srcFileName) {
		this.dir = dir;
		this.filename = filename;
		this.type = type;
		this.srcFileName = srcFileName;
		
		/*
		System.out.println("2####################");
		System.out.println("srcFileName =>" + srcFileName);
		System.out.println("filename =>" + filename);
		System.out.println("2####################");
		*/ 		
	}

	public String getContentType() {
		return (type);
	}

	public String getFilesystemName() {
		return (filename);
	}
	
	public String getSrcName() {
		return (srcFileName);
	}	

	public File getFile() {
		if (dir == null || filename == null) {
			return (null);
		} else {
			return (new File(dir + File.separator + filename));
		}
	}
}

// A class to aid in reading multipart/form-data from a ServletInputStream.
// It keeps track of how many bytes have been read and detects when the
// Content-Length limit has been reached. This is necessary since some
// servlet engines are slow to notice the end of stream.
//
class MultipartInputStreamHandler {
	ServletInputStream in;
	String boundary;
	int totalExpected;
	int totalRead = 0;
	byte[] buf = new byte[8 * 1024];

	public MultipartInputStreamHandler(ServletInputStream in, String boundary,
			int totalExpected) {
		this.in = in;
		this.boundary = boundary;
		this.totalExpected = totalExpected;
	}

	// Reads the next line of input. Returns null to indicate the end
	// of stream.
	//
	public String readLine() throws IOException {
		StringBuffer sbuf = new StringBuffer();
		int result;
		String line;

		do {
			result = this.readLine(buf, 0, buf.length); // this.readLine() does
														// +=
			if (result != -1) {
				sbuf.append(new String(buf, 0, result)); // , "ISO8859-1"));
			}
		} while (result == buf.length); // loop only if the buffer was filled

		if (sbuf.length() == 0) {
			return (null); // nothing read, must be at the end of stream
		}

		sbuf.setLength(sbuf.length() - 2); // cut off the trailing \r\n
		return (sbuf.toString());
	}

	// A pass-through to ServletInputStream.readLine() that keeps track
	// of how many bytes have been read and stops reading when the
	// Content-Length limit has been reached.
	//
	public int readLine(byte b[], int off, int len) throws IOException {
		if (totalRead >= totalExpected) {
			return (-1);
		} else {
			int result = in.readLine(b, off, len);

			if (result > 0) {
				totalRead += result;
			}
			return (result);
		}
	}

	public static String toKSC5601(String str)
			throws UnsupportedEncodingException {
		if (str == null) {
			return (null);
		}
		return (new String(str.getBytes("8859_1"), "KSC5601"));
	}
}
