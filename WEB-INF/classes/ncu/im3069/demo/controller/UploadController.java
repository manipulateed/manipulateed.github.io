
package ncu.im3069.demo.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import org.json.*;
@WebServlet("/upload.do") 
@MultipartConfig(
		location = "D:\\Desktop\\中央大學課程\\大三上\\SA\\專題\\Tripy\\NCU_MIS_SA\\images"
	)
public class UploadController extends HttpServlet{
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8"); // 為了處理中文檔名
		
		Part part = request.getPart("photo");
	    String message = "";
	    String name = "";
	    try {
	    	name = getFileName(part);
	        part.write(name);
	        message = "File uploaded successfully!";
	    } catch (Exception e) {
	        message = "Failed to upload file: " + e.getMessage();
	    }
	    System.out.println(name);
	    name = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());

        // 將 JSON 物件轉換為字串並回應
        response.getWriter().write(name);

    }

    private String getFileName(Part part){ //取得檔案名稱(?)
            String contentDisposition = part.getHeader("content-disposition");

            if(!contentDisposition.contains("filename=")){
                return null;
            }

            int beginIndex = contentDisposition.indexOf("filename=") + 10;
            int endIndex = contentDisposition.length()-1;

            return contentDisposition.substring(beginIndex, endIndex);

        }
	
}
