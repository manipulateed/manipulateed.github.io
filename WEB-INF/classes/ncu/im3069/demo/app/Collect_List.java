package ncu.im3069.demo.app;
import java.util.*;

import org.json.*;

public class Collect_List {

    //收藏清單id
    private int id;
    //收藏清單名稱
    private String name;
    //收藏清單內容
    private ArrayList<CollectionItem> list = new ArrayList<CollectionItem>();
    // ch，CollectionHelper 之物件與 Collection 相關之資料庫方法（Sigleton）

    //建構式for建立收藏清單資料(產生一個新的收藏清單)
    public Collect_List(String name){
        this.name = name;       
    }

 
    //建構式for修改收藏清單資料(修改資料庫已存在的收藏清單)
    public Collect_List(int id, String name){
        this.id = id;
        this.name = name;
//        getCollectSceneFromDB();
    }

    //setId(設定收藏清單編號)
    public void setId(int id) {
        this.id = id;
    }

    
    //getId(取得收藏清單編號])
    public int getId() {
        return this.id;
    }

    //setName
    public void setName(String name) {
        this.name = name;
    }

    //getName
    public String getName() {
        return this.name;
    }
  
    
    //取得該名收藏清單之所有資料並封裝於JSONObject物件內
    public ArrayList<CollectionItem> getCollectionScene() {
        return this.list;
    }
 
    //取得收藏清單基本資料
    public JSONObject getCollectListData() {
        JSONObject jso = new JSONObject();
        jso.put("id", getId());
        jso.put("name", getName());
        

        return jso;
    }

    //取得收藏景點基本資料
    public JSONArray getCollectSceneData() {
        JSONArray result = new JSONArray();

        for(int i=0 ; i < this.list.size() ; i++) {
            result.put(this.list.get(i).getCollectionData());
            //getData名稱可能不一樣，要跟scene的對一下
        }

        return result;
    }

    
    //取得收藏所有資訊
    public JSONObject getCollectListAllInfo() {
        JSONObject jso = new JSONObject();
        jso.put("collect_List_info", getCollectListData());
        jso.put("scene_info", getCollectSceneData());

        return jso;
    }

    
    
    /*setCollectSceneId(設定訂單產品編號)
    public void setCollectSceneId(JSONArray data) {
        for(int i=0 ; i < this.list.size() ; i++) {
            this.list.get(i).setId((int) data.getLong(i));
        }
    }*/
    
    
    /**
     * 更新收藏清單資訊至資料庫
     *
     * @return 更新後的收藏清單資訊（JSON 格式）
     
    public JSONObject update() {
    	
        // 在此處添加將收藏清單資訊同步到資料庫的邏輯
        // 你可以使用 CollectListHelper 或其他資料庫相關的類別來執行更新操作

        // 假設使用 CollectListHelper 來更新資料庫
        CollectListHelper clh = CollectListHelper.getHelper();
        JSONObject updateResult = clh.updateCollectionList(this);

        // 返回更新後的收藏清單資訊
        return updateResult;
    }*/
    
    

}
