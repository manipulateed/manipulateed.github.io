package ncu.im3069.demo.app;

import java.util.ArrayList;

import java.util.Date;
import org.json.JSONObject;
public class ItineraryItem {
	
	/** id, 行程編號 */
    private int id;
	
    /** sc, 景點 */
    private Scene sc;
    
    /** date, 行程日期 */
    private Date date;
    
    /** date_order, 行程順序 */
    private int date_order;
    
    private SceneHelper sceh = SceneHelper.getHelper();
    private ItineraryItemHelper itih = ItineraryItemHelper.getHelper();
    
    /**
     * 實例化（Instantiates）一個新的（new）ItineraryItem 物件<br>
     * 採用多載（overload）方法進行，此建構子用於修改行程時
     *
     * @param ItineraryList_Itinerary_id 行程編號
     * @param Scene 景點
     * @param date 行程日期
     * @param date_order 行程順序
     */
    public ItineraryItem(int id, int scene_id, Date date, int date_order ) {
    	this.id = id;
    	getScene(scene_id);
    	setDate(date);
    	setDate_order(date_order);
    }
    
    public ItineraryItem(Scene sc, Date date, int date_order ) {
    	this.sc = sc;
    	setDate(date);
    	setDate_order(date_order);
    }
    
    //dopost
    public ItineraryItem(int scene_id, Date date, int date_order ) {
    	setDate(date);
    	setDate_order(date_order);
    	getScene(scene_id);
    }
    
    /**
     * 從 DB 中取得景點
     */
	private void getScene(int scene_id) {

		JSONObject jso = sceh.getById(scene_id); //從sch呼叫getbyid
		
		 /** 取出經解析到JSONObject之Request參數 */
        String address = jso.getJSONArray("data").getJSONObject(0).getJSONObject("Scene_info").getString("address");
        String detail = jso.getJSONArray("data").getJSONObject(0).getJSONObject("Scene_info").getString("detail");
        String name = jso.getJSONArray("data").getJSONObject(0).getJSONObject("Scene_info").getString("name");
        String phone = jso.getJSONArray("data").getJSONObject(0).getJSONObject("Scene_info").getString("phone");
        String opentime = jso.getJSONArray("data").getJSONObject(0).getJSONObject("Scene_info").getString("opentime");
        
//        String image = jso.getJSONArray("data").getJSONObject(0).getJSONObject("Scene_info").getString("images");
        ArrayList<String> images = new ArrayList<String>();
//        if(image != null) {
//            String[] images_ = image.split(","); 
//            for (int i = 0; i < images_.length; i++) {
//            	images.add(images_[i]);
//            }
//        }
        /** 建立一個新的會員物件 */
        this.sc = new Scene(scene_id, name, address, detail, opentime, phone, images);
	}
    
    /**
     * 取得景點
     *
     * @return Scene 回傳景點
     */
    public Scene getScene() {
    	return this.sc;
    }
    
    /**
     * 設定行程編號
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 取得行程編號
     *
     * @return int 回傳行程編號
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * 設定行程日期
     */
    public void setDate(Date date){
    	this.date = date;
    }
    
    /**
     * 取得行程編號
     *
     * @return String 回傳行程日期
     */
    public Date getDate() {
    	return this.date;
    }
    
    /**
     * 設定行程順序
     */
    public void setDate_order(int date_order) {
    	this.date_order = date_order;
    }
    
    /**
     * 取得行程順序
     *
     * @return int 回傳行程日期
     */
    public int getDate_order() {
    	return this.date_order;
    }
    
    /**
     * 更新會員資料
     *
     * @return the JSON object 回傳SQL更新之結果與相關封裝之資料
     */
    public JSONObject update() {
        /** 新建一個JSONObject用以儲存更新後之資料 */
        JSONObject data = new JSONObject();
        
        /** 檢查該名會員是否已經在資料庫 */
        if(this.id != 0) {
            /** 透過itineraryItemHelper物件，更新目前之行程資料置資料庫中 */
            data = itih.update(this);
        }
        
        return data;
    }
    
    /**
     * 取得行程資料
     *
     * @return JSONObject 回傳行程資料
     */
    public JSONObject getItineraryItemData() {
    	JSONObject data = new JSONObject();
    	data.put("id",getId());
    	data.put("scene", getScene().getSceneData());
    	data.put("date",getDate());
    	data.put("date_order", getDate_order());
    	
    	return data;
    }
    
    
}
