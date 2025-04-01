package kr.co.cs.common.upload;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.tobesoft.xplatform.data.ColumnHeader;
import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.tx.HttpPlatformRequest;
import com.tobesoft.xplatform.tx.HttpPlatformResponse;

/**
 * 화일을 저장하고 화일 정보를 데이터셋으로 리턴한다.
 * 업로드 파라미터명 : upfile
 * 데이터셋명 : Dataset00
 * 컬럼1 : fileName //화일명
 * 컬럼2 : fileSize //화일사이즈
 * 컬럼3 : fileType //화일타입
 * @author LeeKiEun
 *
 */
public class UploadServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long SIZE_LIMIT = 100000 * 1024 * 1024L; // 업로드 사이즈 제한. 5104857600000

	private static final int TEMP_SIZE_LIMIT = 10000 * 1024; // 업로드시 사용할 임시 메모리 제한. 
	private DiskFileItemFactory factory; 
	private ServletFileUpload upload; // 팩토리로 부터 서블릿 파일업로드 객체 얻기	
	
	//Logger logger = Logger.getRootLogger();
	//private final static Logger logger = LogManager.getLogger("process.test");
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		try {
			
			System.out.println("===========file upload start=============");
			/** Initialize **/
			String realFileName = getRealFileName();
			//String contextRealPath = request.getSession().getServletContext().getRealPath("/");
			String filePath="";
			
			//logger.debug("File upload Start~!!!!");
			System.out.println("------------------doPost START---------------");
			System.out.println("* Start!!----- "+realFileName);
			
			
			if("WIN".equals(ComUtil.getOsMinName())) {
				filePath = Const.SAVEDFILE_LOCALPATH;
			} else {
				filePath = Const.SAVEDFILE_UNIXPATH;
			}			
			
			//filePath = /APPDATA/SMILE2/FILE_BOX/SAVED/
			
			String savePath = filePath + getRealFileName().substring(0,6)+"/";
			//String savePath = contextRealPath + "upfolder\\" + getRealFileName().substring(0,6);
			System.out.println("savePath::"+savePath);
			//폴더생성
		
			File newFolder = new File(savePath);
			File newFolder_temp = new File(savePath + "/temp");
			/*
			if (!newFolder.isDirectory()){
				newFolder.mkdir();				
				newFolder_temp.mkdir();
			}
			*/
			if (!newFolder.isDirectory()){
				newFolder.mkdir();
			}
			
			//apache commons fileupload 가 10KB를 넘는 파일 처리시 메모리에서 처리 하지 않고 Disk에서 temp디렉토리를 생성함
			//해당 temp디렉토리가 생성 되지않아 업로드시 오류가 나는것으로 보여 진다.
			//해결 방안 수동으로 월마다 temp폴더를 생성 or  아래처럼 처리함
			if(!newFolder_temp.isDirectory()){
				newFolder_temp.mkdir();
			}
			
			factory = new DiskFileItemFactory();
			//System.out.println("* Factory 취득");
			// 바로 디스크에 저장되는것이 아니라 메모리에 먼저 저장을 해둔다.
			System.out.println("TEMP_SIZE_LIMIT::"+TEMP_SIZE_LIMIT);
			factory.setSizeThreshold(TEMP_SIZE_LIMIT); // 임시 업로드할 사이즈를 제한한다.
			
			factory.setRepository(new File(savePath + "temp")); // 임시디렉토리를 지정한다.

			upload = new ServletFileUpload(factory); // 업로드 객체를 얻는다.
			
			System.out.println("File SIZE_LIMIT:"+SIZE_LIMIT);
			upload.setSizeMax(SIZE_LIMIT); // 최대 업로드 사이즈를 지정한다.
	
			
			upload.setHeaderEncoding("UTF-8"); // 파일명을 인코딩해준다.
			
	
			List items = upload.parseRequest(request); // 아이템을  얻는다.
	
			System.out.println("* 아이템을 가져온다"+items.toString());

			Iterator iter = items.iterator(); // iterator로 변경한다.

			List savedFiles = new ArrayList();

			HttpPlatformRequest req = new HttpPlatformRequest(request);
			req.receiveData();
			PlatformData resData = new PlatformData();
			VariableList resVarList = resData.getVariableList();
			
			//System.out.println("* 아이템에서 파일 추출 및 저장 시작");			
			
			try {

				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next(); // 아이템 얻기
					if (item.isFormField()) { // 파일형식이면 false 파라미터면 true

						String fieldName = item.getFieldName(); // 필드명을 얻는다. 
						String value = item.getString("UTF-8");
						//System.out.println("* fieldName="+fieldName+", value="+value);

					} else { // 파일형식이면

						if (item.getSize() > 0) {

							FileInfo fileInfo = new FileInfo();

							String name = item.getName(); // 파일명 얻기
							String fileName = name.substring(name.lastIndexOf("\\") + 1);//파일명을 얻는다.
							long fileSize = item.getSize(); // 파일 사이즈를 얻는다.

							//File file = new File(savePath + fileName); // 기본경로+파일명으로 생성한다.
							File file = new File(savePath +  realFileName); // 기본경로+파일명으로 생성한다.
							
							item.write(file); // 파일 저장.

							fileInfo.setFileName(fileName);
							fileInfo.setFileSize(fileSize);
							savedFiles.add(fileInfo);

						}

					}
				}//while

				
				//응답메세지 작성
				DataSet ds = new DataSet("Dataset00");
				ds.addColumn(new ColumnHeader("fileName", DataTypes.STRING));
				ds.addColumn(new ColumnHeader("fileSize", DataTypes.LONG));
				ds.addColumn(new ColumnHeader("fileType", DataTypes.STRING));
				ds.addColumn(new ColumnHeader("filePath", DataTypes.STRING));
				
				

				FileInfo fileInfo = null;

				Iterator fileIter = savedFiles.iterator();
				String sFileName="";
				String sFileSize="";
				String sFileType="";
				
				while (fileIter.hasNext()) {

					fileInfo = (FileInfo) fileIter.next();
					int row = ds.newRow();

					ds.set(row, "fileName", fileInfo.getFileName());
					ds.set(row, "fileSize", fileInfo.getFileSize());
					ds.set(row, "fileType", fileInfo.getFileType());
					ds.set(row, "filePath", savePath);
					
					
					sFileName=fileInfo.getFileName();
					sFileSize=String.valueOf(fileInfo.getFileSize());
					sFileType=fileInfo.getFileType();					

				}
				resData.addDataSet(ds);
				resVarList.add("ErrorCode", 200);
				resVarList.add("ErrorMsg", "성공|" + sFileName + "|" + sFileSize  + "|" + realFileName + "|" + savePath);
				
				//System.out.println("* 아이템에서 파일 추출 및 저장 끝");

			} catch (Exception e) {
				resVarList.add("ErrorCode", 500);
				resVarList.add("ErrorMsg", e);
				//logger.debug("에러=" + e);
				System.out.println("Exception:::"+e);
			}

			HttpPlatformResponse res = new HttpPlatformResponse(response);
			res.setData(resData);
			res.sendData();

		} catch (Exception e) {
			//logger.debug("에러발생:" + e);
			System.out.println("Exception:::"+e);
		}
		 
	}// doPost
	
	public String getRealFileName(){
		long currentTime = System.currentTimeMillis();
		SimpleDateFormat simDf = new SimpleDateFormat("yyyyMMddHHmmss");
		int randomNumber = (int)(Math.random()*100000);
		String uniqueFileName = simDf.format(new Date(currentTime)) + "" + randomNumber ;
		return uniqueFileName;
	}
	
}// end
