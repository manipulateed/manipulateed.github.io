package ncu.im3069.demo.controller;

import java.io.IOException;

import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;
import ncu.im3069.demo.app.Scene;
import ncu.im3069.demo.app.SceneHelper;
import ncu.im3069.demo.app.CityHelper;
import ncu.im3069.demo.app.CommentHelper;
import ncu.im3069.tools.JsonReader;
import javax.servlet.annotation.WebServlet;
@WebServlet("/api/scene.do")

public class SceneController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private SceneHelper Sceh =  SceneHelper.getHelper();
	private CityHelper Cith =  CityHelper.getHelper();
	private CommentHelper Comh = CommentHelper.getHelper();

	public SceneController() {
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
        String address = jso.getString("address");
        String detail = jso.getString("detail");
        String name = jso.getString("name");
        String phone = jso.getString("phone");
        String opentime = jso.getString("opentime");
        String city = jso.getString("city");
        int city_id = Cith.getCityIdByCityName(city);
        
       //String image = jso.getString("images");

        ArrayList<String> images = new ArrayList<String>();
//        if(!image.isEmpty()) {
//            String[] images_ = image.split(","); 
//            for (int i = 0; i < images_.length; i++) {
//            	images.add(images_[i]);
//            }
//        }
        Scene m = new Scene(name, address, detail, opentime, phone, images);
        
        /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 */
        if(address.isEmpty() || detail.isEmpty() || name.isEmpty() || opentime.isEmpty() || phone.isEmpty()) {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
        else if (!Sceh.checkDuplicate(m)) {
            JSONObject data = Sceh.createByCityId(city_id, m);
            
            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "成功! 新增景點資料...");
            resp.put("response", data);
            
            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
            jsr.response(resp, response);
        }
        else {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'新增景點失敗，此景點名稱重複！\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
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
        String id = jsr.getParameter("id");
        String keyword = jsr.getParameter("keyword");

        if (id.isEmpty() && keyword.isEmpty()) {
            JSONObject query = Sceh.getAll();
            
            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "所有景點資料取得成功");
            resp.put("response", query);
    
            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
            jsr.response(resp, response);
        }
        else if(!id.isEmpty() && keyword.isEmpty()){
            JSONObject query = Sceh.getById(Integer.parseInt(id));
            
            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "景點資料取得成功");
            resp.put("response", query);
    
            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
            jsr.response(resp, response);
        }
        else {
            JSONObject query = Sceh.getByKeyWord(keyword);
            
            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "景點資料取得成功");
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
        
        JSONObject query1 = Sceh.deleteByID(id);
        Comh.deleteBySceneId(id);
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "景點移除成功！");
        resp.put("response", query1);

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
        String address = jso.getString("address");
        String detail = jso.getString("detail");
        String name = jso.getString("name");
        String phone = jso.getString("phone");
        String opentime = jso.getString("opentime");
        String image = jso.getString("images");
        ArrayList<String> images = new ArrayList<String>();
        if(image != null) {
            String[] images_ = image.split(","); 
            for (int i = 0; i < images_.length; i++) {
            	images.add(images_[i]);
            }
        }
        
              
        Scene m = new Scene(id, name, address, detail, opentime, phone, images);
        
        JSONObject data = m.update();
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "成功! 更新景點資料...");
        resp.put("response", data);
        
        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        jsr.response(resp, response);
    }

}
