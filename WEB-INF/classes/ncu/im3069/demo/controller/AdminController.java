package ncu.im3069.demo.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import ncu.im3069.demo.app.AdminHelper;
import ncu.im3069.demo.app.Admin;
import ncu.im3069.tools.JsonReader;
import javax.servlet.annotation.WebServlet;
@WebServlet("/api/admin.do")

public class AdminController extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private AdminHelper Admh = AdminHelper.getHelper();
	
	public AdminController() {
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
	        //看是登入還是註冊
	        String action = request.getParameter("action");
	        
	        
	      //觸發登入
	         if(action.equals("login")) {
	          String email = jso.getString("email");
	          String password = jso.getString("password");
	          
	          if(email.isEmpty() || password.isEmpty()) {
	              /** 以字串組出JSON格式之資料 */
	              String resp = "{\"status\": \'300\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
	              /** 透過JsonReader物件回傳到前端（以字串方式） */
	              jsr.response(resp, response);
	          }
	          else if (Admh.getByEmail(email, password).has("error")) {
	              // 代表發生了錯誤
	              Admh.getByEmail(email, password);
	              
	              String resp = "{\"status\": \'400\' , \"message\": \'查無此帳號或是密碼輸入錯誤！\', \'response\': \'\'}";
	              jsr.response(resp, response);
	          }
	          else {
	           /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	              JSONObject data = Admh.getByEmail(email,password);
	              JSONObject resp = new JSONObject();
	              
	              resp.put("status", "200");
	              resp.put("message", "成功登入!");
	              resp.put("response", data);
	              
	              /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	              jsr.response(resp, response);
	          }	        	          
	         }	        
	        //觸發新增管理者
	        else {  
		        /** 取出經解析到JSONObject之Request參數 */
		        String email = jso.getString("email");
		        String password = jso.getString("password");
		        String name = jso.has("name") ? jso.getString("name") : null;
		        String sex = jso.has("sex") ? jso.getString("sex") : null;
		        String idcard = jso.has("idcard") ? jso.getString("idcard") : null;
		        
		        /** 建立一個新的會員物件 */
		        Admin m= new Admin(name, email, password, sex, idcard);
		        
		        /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 */
		        if(email.isEmpty() || password.isEmpty() || name.isEmpty() || sex.isEmpty() || idcard.isEmpty()) {
		            /** 以字串組出JSON格式之資料 */
		            String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
		            /** 透過JsonReader物件回傳到前端（以字串方式） */
		            jsr.response(resp, response);
		        }
		        else if (!Admh.checkDuplicate(m)) {
		            JSONObject data = Admh.create(m);
		            
		            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
		            JSONObject resp = new JSONObject();
		            resp.put("status", "200");
		            resp.put("message", "成功! 註冊會員資料...");
		            resp.put("response", data);
		            
		            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
		            jsr.response(resp, response);
		        }
		        else {
		            /** 以字串組出JSON格式之資料 */
		            String resp = "{\"status\": \'400\', \"message\": \'新增帳號失敗，此E-Mail帳號重複！\', \'response\': \'\'}";
		            /** 透過JsonReader物件回傳到前端（以字串方式） */
		            jsr.response(resp, response);
		        }
		      }
	        
	    	}

	    /**
	     * 處理Http Method請求GET方法（取得資料）
	     *
	     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）
	     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
	     * @throws ServletException the servlet exception
	     * @throws IOException Signals that an I/O exception has occurred.
	     */
	    public void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
	        JsonReader jsr = new JsonReader(request);
	        /** 若直接透過前端AJAX之data以key=value之字串方式進行傳遞參數，可以直接由此方法取回資料 */
	        String email = jsr.getParameter("email");
	        String password = jsr.getParameter("password");
	        
	        if (email.isEmpty()) {
	            JSONObject query = Admh.getAll();
	            
	            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	            JSONObject resp = new JSONObject();
	            resp.put("status", "200");
	            resp.put("message", "所有管理員資料取得成功");
	            resp.put("response", query);
	    
	            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	            jsr.response(resp, response);
	        }
	        else {
	            JSONObject query = Admh.getByEmail(email,password);
	            
	            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	            JSONObject resp = new JSONObject();
	            resp.put("status", "200");
	            resp.put("message", "管理員資料取得成功");
	            resp.put("response", query);
	    
	            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	            jsr.response(resp, response);
	        }
	    }

	    /**
	     * 處理Http Method請求DELETE方法（刪除）
	     *
	     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）
	     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
	     * @throws ServletException the servlet exception
	     * @throws IOException Signals that an I/O exception has occurred.
	     */
	    public void doDelete(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
	        JsonReader jsr = new JsonReader(request);
	        JSONObject jso = jsr.getObject();
	        
	        /** 取出經解析到JSONObject之Request參數 */
	        int id = jso.getInt("id");
	        
	        JSONObject query = Admh.deleteById(id);
	        
	        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	        JSONObject resp = new JSONObject();
	        resp.put("status", "200");
	        resp.put("message", "管理員移除成功！");
	        resp.put("response", query);

	        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	        jsr.response(resp, response);
	    }

	    /**
	     * 處理Http Method請求PUT方法（更新）
	     *
	     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）
	     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
	     * @throws ServletException the servlet exception
	     * @throws IOException Signals that an I/O exception has occurred.
	     */
	    public void doPut(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
	        JsonReader jsr = new JsonReader(request);
	        JSONObject jso = jsr.getObject();
	        
	        /** 取出經解析到JSONObject之Request參數 */
	        int id = jso.getInt("id");
	        String email = jso.getString("email");
	        String password = jso.getString("password");
	        String name = jso.getString("name");
	        String sex = jso.getString("sex");
	        String idcard = jso.getString("idcard");

	        Admin m = new Admin(id, name, email, password, sex, idcard);
	        
	        JSONObject data = m.update();
	        
	        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
	        JSONObject resp = new JSONObject();
	        resp.put("status", "200");
	        resp.put("message", "成功! 更新管理員資料...");
	        resp.put("response", data);
	        
	        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
	        jsr.response(resp, response);
	    }

}
