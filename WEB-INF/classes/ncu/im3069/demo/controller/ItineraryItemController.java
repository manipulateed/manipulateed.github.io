package ncu.im3069.demo.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import ncu.im3069.tools.JsonReader;
import ncu.im3069.demo.app.ItineraryItem;
import ncu.im3069.demo.app.ItineraryItemHelper;
import ncu.im3069.demo.app.Scene;
import javax.servlet.annotation.WebServlet;

@WebServlet("/api/ItineraryItem.do")
public class ItineraryItemController extends HttpServlet{
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** collih，CollectionItemHelper之物件與CollectionItem相關之資料庫方法（Sigleton） */
    private ItineraryItemHelper itih = ItineraryItemHelper.getHelper();
    
    public ItineraryItemController() {
    	super();
    }
    
    /**
     * 處理Http Method請求POST方法（新增資料）
     *
     * @param request Servlet請求之HttpServletRequest之Request物件（前端到後端）--> 從後端request前端
     * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
    	/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        
        /** 取出經解析到JSONObject之Request參數 */
        int id = jso.getInt("scene_id");
        String address = jso.getString("address");
        String detail = jso.getString("detail");
        String name = jso.getString("name");
        String phone = jso.getString("phone");
        String opentime = jso.getString("opentime");
        String image = jso.getString("images");
        ArrayList<String> images = new ArrayList<String>();
        if(!image.isEmpty()) {
            String[] images_ = image.split(","); 
            for (int i = 0; i < images_.length; i++) {
            	images.add(images_[i]);
            }
        }
        /** 建立一個新的景點物件 */
        Scene m = new Scene(id, name, address, detail, opentime, phone, images);
        
        /** 取得list_id */
        int list_id = jso.getInt("itineraryItemList_id");
        String date = jso.getString("date"); 
        int order = jso.getInt("order");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d_ = null;
        try {    
        	d_ = format.parse(date);
        } catch (Exception e) {
        	e.printStackTrace();
        } 
        System.out.println(d_);       
        /** 建立一個新的收藏物件 */
        ItineraryItem iti = new ItineraryItem(m, d_, order);
        
        /** 透過itineraryItemHelper物件的createItineraryItemByItineraryListID()方法新建一個行程至資料庫 */
        JSONObject data = itih.createItineraryItemByItineraryListID(list_id, iti);
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "成功加入行程...");
        resp.put("response", data);
        
        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        jsr.response(resp, response);
        
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
        JsonReader jsr = new JsonReader(request);
        String list_id = jsr.getParameter("list_id");
        String item_id = jsr.getParameter("id");
        String date = jsr.getParameter("date");
        if (list_id.isEmpty()) {
       	 /** 透過ItineraryItemHelper物件的getByCollectionID()方法自資料庫取回該名之資料，回傳之資料為JSONObject物件 */
           JSONObject query = itih.getByItineraryItemID(Integer.parseInt(item_id));
           JSONObject resp = new JSONObject();
           resp.put("status", "200");
           resp.put("message", "收藏行程資料取得成功");
           resp.put("response", query);
           jsr.response(resp, response);
       }
       else {   
    	   JSONObject query = new JSONObject();
    	   if(!date.equals("") || !date.isEmpty()) {
    		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	        java.util.Date d_ = null;
    	        try {    
    	        	d_ = format.parse(date);
    	        } catch (Exception e) {
    	        	e.printStackTrace();
    	        } 
    	        query = itih.getAllByItineraryListID(d_,Integer.parseInt(list_id));
    	   }else {
    		   	query = itih.getAllByItineraryListID(Integer.parseInt(list_id));
    	   }
           /** 透過CollectionItemHelper物件之getAllByCollectionListId()方法取回所有景點之資料，回傳之資料為JSONObject物件 */
       		
       	
           /** 新建一個JSONObject用於將回傳之資料進行封裝 */
           JSONObject resp = new JSONObject();
           resp.put("status", "200");
           resp.put("message", "所有會員資料取得成功");
           resp.put("response", query);
   
           /** 透過JsonReader物件回傳到前端（以JSONArray方式） */
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
        
        /** 透過MemberHelper物件的deleteByID()方法至資料庫刪除該名會員，回傳之資料為JSONObject物件 */
        JSONObject query = itih.deleteItineraryItemByID(id);
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "行程景點移除成功！");
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
        int sc_id = jso.getInt("scene_id");
 	
        //Itinerary
        int iti_id = jso.getInt("iti_id");        
        String date = jso.getString("date");   
        int date_order = jso.getInt("order"); 
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d_ = null;
        try {    
        	d_ = format.parse(date);
        } catch (Exception e) {
        	e.printStackTrace();
        } 
        ItineraryItem iti = new ItineraryItem(iti_id, sc_id, d_, date_order);
        JSONObject data = iti.update();
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "成功! 更新行程順序...");
        resp.put("response", data);
        
        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        jsr.response(resp, response);
    }  
    
}