package ncu.im3069.demo.app;
import java.util.ArrayList;

import org.json.JSONObject;

public class CollectionItem {
	/*id 收藏編號*/
	private int id;
	
	/*id 收藏清單編號*/
//	private int list_id;
	
	/*scene_id 景點*/
	private Scene sc;

	//private int scene_id;
	
	/*sh SceneHelper*/
	private SceneHelper sceh = SceneHelper.getHelper();
	/**
     * 實例化（Instantiates）一個新的（new）CollectionItem 物件<br>
     * 採用多載（overload）方法進行，此建構子用於建立訂單細項時
     *
     * @param sc 景點物件
     */
	public CollectionItem(Scene sc) {
		this.sc = sc;
	}

	/**
     * 實例化（Instantiates）一個新的（new）CollectionItem 物件<br>
     * 採用多載（overload）方法進行，此建構子用於修改收藏景點時
     *
     * @param collectionItem_id 收藏景點編號
     * @param scene 景點物件
     */
	public CollectionItem(int id, int scene_id) {
		setId(id);
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
        
//        String image = jso.getJSONArray("data").getJSONObject(0).getJSONObject("Scene_info").getJSONArray("images").getString(0);
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
     * 設定收藏編號
     */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
     * 取得收藏編號
     *
     * @return int 回傳收藏編號
     */
	public int getId() {
		return this.id;
	}
	
	/**
     * 取得收藏資料
     *
     * @return JSONObject 回傳收藏資料
     */
	
	public JSONObject getCollectionData() {
		JSONObject data = new JSONObject();
		data.put("id", getId());
		data.put("scene", getScene().getSceneData());
		
		return data;
	}
	
}
