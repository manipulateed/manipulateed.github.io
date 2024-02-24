package ncu.im3069.demo.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import ncu.im3069.demo.app.CommentHelper;
import ncu.im3069.demo.app.Comment;
import ncu.im3069.tools.JsonReader;
import javax.servlet.annotation.WebServlet;

@WebServlet("/api/comment.do")
public class CommentController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private CommentHelper Comh =  CommentHelper.getHelper();

    public CommentController() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * 處理Http Method請求POST方法（新增資料）
     *
     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）
     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        
        /** 取出經解析到JSONObject之Request參數 */
        String scene_id = jso.getString("scene_id");
        String context = jso.getString("context");
        String user_id = jso.getString("user_id");
       
       
        if (!user_id.isEmpty()) {
            Comment m = new Comment(context, Integer.parseInt(user_id));
	        JSONObject data = Comh.createCommentBySceneId(Integer.parseInt(scene_id), m);
	        
	        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	        JSONObject resp = new JSONObject();
	        resp.put("status", "200");
	        resp.put("message", "成功! 輸入評論資料...");
	        resp.put("response", data);
	        
	        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	        jsr.response(resp, response);
        }
        else {
        	/** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'非會員或管理員不可評論！\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
       
    }
    
}
