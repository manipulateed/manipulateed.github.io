package ncu.im3069.demo.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import ncu.im3069.demo.app.CityHelper;
import ncu.im3069.tools.JsonReader;
import javax.servlet.annotation.WebServlet;

@WebServlet("/api/city.do")
public class CityController extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    private CityHelper Cith =  CityHelper.getHelper();

    public CityController() {
        super();
    }
    
    /**
     * 處理 Http Method 請求 GET 方法（新增資料）
     *
     * @param request Servlet 請求之 HttpServletRequest 之 Request 物件（前端到後端）
     * @param response Servlet 回傳之 HttpServletResponse 之 Response 物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /** 透過 JsonReader 類別將 Request 之 JSON 格式資料解析並取回 */
		JsonReader jsr = new JsonReader(request);
        /** 取出經解析到 JsonReader 之 Request 參數 */
        String id = jsr.getParameter("id");

        /** 新建一個 JSONObject 用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        if(!id.isEmpty()) {       	
        
	        JSONObject query = Cith.getById(Integer.parseInt(id));
	      	resp.put("status", "200");
	      	resp.put("message", "單一城市資料取得成功");
	      	resp.put("response", query);
	      	
	        /** 透過 JsonReader 物件回傳到前端（以 JSONObject 方式） */
	        jsr.response(resp, response);
        }
        else {
	        JSONObject query = Cith.getAll();
	      	resp.put("status", "200");
	      	resp.put("message", "多個城市資料取得成功");
	      	resp.put("response", query);
	      	
	        /** 透過 JsonReader 物件回傳到前端（以 JSONObject 方式） */
	        jsr.response(resp, response);
        }
	}
    
}