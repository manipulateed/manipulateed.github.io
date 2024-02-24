package ncu.im3069.demo.controller;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;
import ncu.im3069.demo.app.CollectionItemHelper;
import ncu.im3069.demo.app.CollectListHelper;
import ncu.im3069.demo.app.Collect_List;
import ncu.im3069.tools.JsonReader;

import javax.servlet.annotation.WebServlet;

@WebServlet("/api/Collect_List.do")
public class CollectListController extends HttpServlet {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    private CollectionItemHelper cih =  CollectionItemHelper.getHelper();


    /** clh，CollectListHelper 之物件與 collectList 相關之資料庫方法（Sigleton） */
	private CollectListHelper clh =  CollectListHelper.getHelper();

    public CollectListController() {
        super();
    }

    /**
     * 處理 Http Method 請求 GET 方法（顯示收藏清單）
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
        String user_id = jsr.getParameter("user_id");

        /** 新建一個 JSONObject 用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();

        /** 判斷該字串是否存在，若存在代表要取回個別訂單之資料，否則代表要取回全部資料庫內訂單之資料 */
        if (!id.isEmpty()) {
          /** 透過 collectListHelper 物件的 getByID() 方法自資料庫取回該筆收藏之資料，回傳之資料為 JSONObject 物件 */
          JSONObject query = clh.getById(id);
          resp.put("status", "200");
          resp.put("message", "單筆收藏清單資料取得成功");
          resp.put("response", query);
        }
        else if(!user_id.isEmpty()){
          /** 透過 collectListHelper 物件之 getAll() 方法取回所有訂單之資料，回傳之資料為 JSONObject 物件 */
          JSONObject query = clh.getAll(Integer.parseInt(user_id));
          resp.put("status", "200");
          resp.put("message", "所有收藏清單資料取得成功");
          resp.put("response", query);
        }

        /** 透過 JsonReader 物件回傳到前端（以 JSONObject 方式） */
        jsr.response(resp, response);
	}

    /**
     * 處理 Http Method 請求 POST 方法（新增收藏清單）
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
        int user_id = Integer.parseInt(jso.getString("user_id"));

        /** 建立一個新的collectList物件 */
        Collect_List cl = new Collect_List(name); 
        
        
        /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 */
        if(name.isEmpty()) {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
        
        /** 透過CollectListHelper物件的checkDuplicate()檢查該用戶收藏清單是否有重複 */
        else if (!clh.checkDuplicate(cl,user_id)) {
            /** 透過CollectListHelper物件的createCollectionListByUserId()方法新建一個收藏清單至資料庫 */
            JSONObject result = clh.createCollectionListByUserId(cl,user_id);      

            /** 新建一個 JSONObject 用於將回傳之資料進行封裝 */
            JSONObject resp = new JSONObject();
            resp.put("status", "200");
            resp.put("message", "收藏清單新增成功！");
            resp.put("response", result);

            /** 透過 JsonReader 物件回傳到前端（以 JSONObject 方式） */
            jsr.response(resp, response);
        }
        else {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'新增收藏清單失敗，此收藏清單名稱重複！\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
	}//end post
	
	
	
	/**
     * 處理 Http Method 請求 DELETE 方法（刪除收藏清單）
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
        
        /** 透過CollectListHelper物件的deleteCollectionList()方法至資料庫刪除該收藏清單，回傳之資料為JSONObject物件 */
        JSONObject query = clh.deleteCollectionList(id);
        JSONObject query1 = cih.deleteCollectionItemByID(id);
        
        /** 新建一個JSONObject用於將回傳之資料進行封裝 */
        JSONObject resp = new JSONObject();
        resp.put("status", "200");
        resp.put("message", "收藏清單刪除成功！");
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
        int id = jso.getInt("id");
        String name = jso.getString("name");
        int user_id = jso.getInt("user_id");
 
        
        /** 透過傳入之參數，新建一個以這些參數之CollectList物件 */
        Collect_List cl = new Collect_List(id, name);
        
        
        /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 */
        if(name.isEmpty()) {
            /** 以字串組出JSON格式之資料 */
            String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";
            /** 透過JsonReader物件回傳到前端（以字串方式） */
            jsr.response(resp, response);
        }
        
        /** 透過CollectListHelper物件的checkDuplicate()檢查該用戶收藏清單是否有重複 */
        else if (!clh.checkDuplicate(cl, user_id)) {
                 
            /** 透過CollectListHelper物件的editCollectList()方法至資料庫編輯該收藏清單，回傳之資料為JSONObject物件 */
        	JSONObject query = clh.editCollectionList(cl);
      
        	/** 新建一個JSONObject用於將回傳之資料進行封裝 */
        	JSONObject resp = new JSONObject();
        	resp.put("status", "200");
        	resp.put("message", "成功編輯收藏清單! 更新收藏清單資料...");
        	resp.put("response", query);
        
        	/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
        	jsr.response(resp, response); 
            
        }            
    }//end put


}//end all class
