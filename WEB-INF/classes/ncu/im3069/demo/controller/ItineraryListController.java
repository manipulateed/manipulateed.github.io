package ncu.im3069.demo.controller;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;
import java.text.SimpleDateFormat;
import ncu.im3069.demo.app.ItineraryItemHelper;
import ncu.im3069.demo.app.ItineraryListHelper;
import ncu.im3069.demo.app.ItineraryList;
import ncu.im3069.demo.app.Member_;
import ncu.im3069.demo.app.Member_Helper;

import ncu.im3069.tools.JsonReader;

import javax.servlet.annotation.WebServlet;

@WebServlet("/api/ItineraryList.do")
public class ItineraryListController extends HttpServlet {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    /** sch，SceneHelper 之物件與 Scene 相關之資料庫方法（Sigleton） */
    private Member_Helper mh =  Member_Helper.getHelper();
    private ItineraryItemHelper ih =  ItineraryItemHelper.getHelper();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /** ilh，ItineraryListHelper 之物件與 ItineraryList 相關之資料庫方法（Sigleton） */
	private ItineraryListHelper ilh =  ItineraryListHelper.getHelper();

    public ItineraryListController() {
        super();
    }

    /**
     * 處理 Http Method 請求 GET 方法（顯示行程清單）
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
        int user_id = Integer.parseInt(jsr.getParameter("user_id"));

        /** 新建一個 JSONObject 用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();

        /** 判斷該字串是否存在，若存在代表要取回個別訂單之資料，否則代表要取回全部資料庫內之資料 */
        if (!String.valueOf(id).isEmpty()) {
          /** 透過 ItineraryListHelper 物件的 getByID() 方法自資料庫取回該筆資料，回傳之資料為 JSONObject 物件 */
          JSONObject query = ilh.getById(id);
          resp.put("status", "200");
          resp.put("message", "單筆行程清單資料取得成功");
          resp.put("response", query);
        }
        else {
          /** 透過 ItineraryListHelper 物件之 getAll() 方法取回所有訂單之資料，回傳之資料為 JSONObject 物件 */
          JSONObject query = ilh.getAll(user_id);
          resp.put("status", "200");
          resp.put("message", "所有行程清單資料取得成功");
          resp.put("response", query);
        }

        /** 透過 JsonReader 物件回傳到前端（以 JSONObject 方式） */
        jsr.response(resp, response);
	}

    /**
     * 處理 Http Method 請求 POST 方法（新增行程清單）
     *
     * @param request Servlet 請求之 HttpServletRequest 之 Request 物件（前端到後端）
     * @param response Servlet 回傳之 HttpServletResponse 之 Response 物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    /** 透過 JsonReader 類別將 Request 之 JSON 格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();

        /** 取出經解析到 JSONObject 之 Request 參數 */
        String name = jso.getString("name");
        JSONArray Collaborators = jso.getJSONArray("Collaborators");
        int user_id = Integer.parseInt(jso.getString("user_id"));
       
        String start_= jso.getString("start");
        String end_= jso.getString("end");
       
        java.util.Date d = null;
        java.util.Date d_ = null;
        try {
        	d = format.parse(start_);
        	d_ = format.parse(end_);
        } catch (Exception e) {
        	e.printStackTrace();
        } 
        
        ArrayList<Member_> m = new  ArrayList<Member_>();
        for (int i = 0; i < Collaborators.length(); i++) {
            // 根據需要從資料庫中取得協作者的資訊，例如使用 UserHelper
            int collaborator_Id =  Integer.parseInt(Collaborators.getJSONObject(i).getString("collaborator"));
            Member_ collaborator = mh.getById(collaborator_Id);
            m.add(collaborator);
        }
        /** 建立一個新的ItineraryList物件 */
        ItineraryList il = new ItineraryList(name, d, d_, m); 
        
        
        /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 */
        if(name.isEmpty()) {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
        
        /** 透過ItineraryListHelper物件的checkDuplicate()檢查該用戶行程清單是否有重複 */
        else if (!ilh.checkDuplicate(il,user_id)) {
            /** 透過ItineraryListHelper物件的createItineraryListByUserId()方法新建一個行程清單至資料庫 */
            JSONObject result = ilh.createItineraryListByUserId(il,user_id);
          
        
            /* 將每一筆Itinerary細項取得出來 
            for(int i=0 ; i < Itinerary.length() ; i++) {
            	//get 各 Itinerary(scene)Id
            	String scene_id = Itinerary.getString(i);

            	// 透過 SceneHelper(sch) 物件之 getById()，取得景點的資料並加進行程清單物件裡 
            	Scene sc = sch.getById(scene_id);
            	il.addItineraryScene(sc);
            }*/

         
            /** 設定回傳回來的行程清單編號 */
            il.setId((int) result.getLong("ItineraryList_id"));

            /** 新建一個 JSONObject 用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "行程清單新增成功！");
            resp.put("response", il.getItineraryListAllInfo());

            /** 透過 JsonReader 物件回傳到前端（以 JSONObject 方式） */
            jsr.response(resp, response);
                   
        }
        else {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'新增行程清單失敗，此行程清單名稱重複！\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
	}//end post
	
	
	
	/**
     * 處理 Http Method 請求 DELETE 方法（刪除行程清單）
     *
     * @param request Servlet 請求之 HttpServletRequest 之 Request 物件（前端到後端）
     * @param response Servlet 回傳之 HttpServletResponse 之 Response 物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
	protected void doDelete (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    /** 透過 JsonReader 類別將 Request 之 JSON 格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        
               
        /** 取出經解析到JSONObject之Request參數 */
        int id = jso.getInt("id");
        
        /** 透過ItineraryListHelper物件的deleteItineraryList()方法至資料庫刪除該行程清單，回傳之資料為JSONObject物件 */
        JSONObject query = ilh.deleteItineraryList(id);
        JSONObject query1 = ih.deleteItineraryItemByID(id);
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "行程清單刪除成功！");
        resp.put("response1", query1);
        resp.put("response", query);

        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        jsr.response(resp, response);    
       
	}
	
	
	/**
     * 處理Http Method請求PUT方法（更新/編輯）
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
        int id =jso.getInt("id");
        //int id = Integer.parseInt(jso.getString("id"));
        String name = jso.getString("name");
        int user_id = Integer.parseInt(jso.getString("user_id"));
        // String end_= jso.getString("end");
        // String start_= jso.getString("start");
        java.util.Date d = null;
        java.util.Date d_ = null;
        // try {
        // 	d = format.parse(start_);
        // 	d_ = format.parse(end_);
        // } catch (Exception e) {
        // 	e.printStackTrace();
        // } 
        
        /** 透過傳入之參數，新建一個以這些參數之ItineraryList物件 */
        ItineraryList il = new ItineraryList(id, name, d, d_);
        
        
        /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 */
        if(name.isEmpty()) {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
        
        /** 透過ItineraryListHelper物件的checkDuplicate()檢查該用戶行程清單是否有重複 */
        else if (!ilh.checkDuplicate(il,user_id)) {
                 
            /** 透過ItineraryListHelper物件的editItineraryList()方法至資料庫編輯該行程清單，回傳之資料為JSONObject物件 */
        	JSONObject query = ilh.editItineraryList(il);

        
        	/** 新建一個JSONObject用於將回傳之資料進行封裝 */
        	JSONObject resp = new JSONObject();
        	resp.put("status", "200");
        	resp.put("message", "成功編輯行程清單! 更新行程清單資料...");
        	resp.put("response", query);
        
        	/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        	jsr.response(resp, response); 
            
        }            
    }//end put


}//end all class
