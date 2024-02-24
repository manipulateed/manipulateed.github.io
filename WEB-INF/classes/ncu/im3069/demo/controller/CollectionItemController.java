package ncu.im3069.demo.controller;

import java.io.*;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import ncu.im3069.tools.JsonReader;
import ncu.im3069.demo.app.CollectionItem;
import ncu.im3069.demo.app.CollectionItemHelper;
import ncu.im3069.demo.app.Scene;

import javax.servlet.annotation.WebServlet;


@WebServlet("/api/collectionItem.do")
public class CollectionItemController extends HttpServlet{
	
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** collih，CollectionItemHelper之物件與CollectionItem相關之資料庫方法（Sigleton） */
    private CollectionItemHelper collih =  CollectionItemHelper.getHelper();
    
    public CollectionItemController() {
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
        
        /*
         * JSONObject sceneObject = jso.getJSONObject("scene");
         * int scene_id = sceneObject.getInt("id");
        */
        
        /** 取出經解析到JSONObject之Request參數 */
        int id = Integer.parseInt(jso.getString("id"));
        String address = jso.getString("address");
        String detail = jso.getString("detail");
        String name = jso.getString("name");
        String phone = jso.getString("phone");
        String opentime = jso.getString("opentime");
        ArrayList<String> images = new ArrayList<String>();
        /** 建立一個新的景點物件 */
        Scene m = new Scene(id, name, address, detail, opentime, phone, images);
        
        /** 取得list_id */
        int list_id = jso.getInt("collectionItemList_id");
     
        /** 建立一個新的收藏物件 */
        CollectionItem coli = new CollectionItem(m);
        
        /** 透過CollectionItemHelper物件的checkDuplicate()檢查該收藏景點是否有重複 */
    	if(!collih.checkDuplicate(coli, list_id)) {
    		/** 透過CollectionItemHelper物件的createCollectionItemBycollectionListID()方法新建一個會員至資料庫 */
            JSONObject data = collih.createCollectionItemByCollectionListID(list_id, coli);
            
            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "成功加入收藏...");
            resp.put("response", data);
            
            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
            jsr.response(resp, response);
    	}
    	else {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'新增景點失敗，此景點重複！\', \'response\': \'\'}";
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
        String list_id = jsr.getParameter("list_id");
        String item_id = jsr.getParameter("id");
        if (list_id.isEmpty()) {
        	 /** 透過ItineraryItemHelper物件的getByCollectionID()方法自資料庫取回該名之資料，回傳之資料為JSONObject物件 */
            JSONObject query = collih.getByCollectionID(item_id);
            
            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "收藏景點資料取得成功");
            resp.put("response", query);
    
            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
            jsr.response(resp, response);
        }
        else {   
            /** 透過CollectionItemHelper物件之getAllByCollectionListId()方法取回所有景點之資料，回傳之資料為JSONObject物件 */
            
        	JSONObject query = collih.getAllByCollectionListID(list_id);
        	
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
        
        JSONObject query = collih.deleteCollectionItemByID(id);
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "收藏景點移除成功！");
        resp.put("response", query);

        /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        jsr.response(resp, response);
    }
   	
}