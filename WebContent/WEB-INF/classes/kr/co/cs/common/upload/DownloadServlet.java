 package kr.co.cs.common.upload;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.net.URLEncoder;
 import javax.servlet.Servlet;
 import javax.servlet.ServletException;
 import javax.servlet.ServletOutputStream;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.apache.log4j.Logger;
 
 public class DownloadServlet extends HttpServlet
   implements Servlet
 {
   Logger logger = Logger.getRootLogger();
 
   public void service(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
   {
     this.logger.debug("다운로드 프로세스");
     System.out.println("===========> download start");
 
     String name = request.getParameter("file");
     String realfile = request.getParameter("realfile");
     String realpath = request.getParameter("realpath") == null ? "" : request.getParameter("realpath");
 
     String encodingYn = request.getParameter("encodingYn") == null ? "" : request.getParameter("encodingYn");
 
     if (realpath.indexOf("/APPDATA/SMILE2/FILE_BOX") == -1) {
       res.sendRedirect("wrongpage");
     }
     else {
       String filename = "";
       if ("Y".equals(encodingYn))
         filename = new String(name.getBytes("euc-kr"), "euc-kr");
       else {
         filename = new String(name.getBytes("iso8859-1"), "UTF-8");
       }
 
       byte[] buffer = new byte[1024];
       ServletOutputStream out = null;
       BufferedInputStream in = null;
       try
       {
         File file = new File(realpath + "/" + realfile);
 
         if (file.exists()) {
           out = res.getOutputStream();
           res.setContentType("utf-8");
           res.setContentType("application/octet;charset=utf-8");
           res.setHeader("Accept-Ranges", "bytes");
           res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF8"));
           long len = file.length();
           res.setContentLength((int)len);
           in = new BufferedInputStream(new FileInputStream(file));
           int n = 0;
           while ((n = in.read(buffer, 0, 1024)) != -1)
             out.write(buffer, 0, n);
         }
         else {
           this.logger.debug("파일 없슴");
           res.sendRedirect("unknownfile");
         }
       } catch (Exception e) {
         e.printStackTrace();
 
         if (in != null) try { in.close(); } catch (Exception localException1) {
           } if (out != null) try { out.close();
           }
           catch (Exception localException2)
           {
           }
       }
       finally
       {
         if (in != null) try { in.close(); } catch (Exception localException3) {
           } if (out != null) try { out.close();
           }
           catch (Exception localException4)
           {
           }
       }
     }
   }
 }
