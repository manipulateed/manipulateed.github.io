package ncu.im3069.demo.app;
import java.util.*;
import java.util.Date;
import org.json.*;

public class ItineraryList {

	//行程清單id
    private int id;
    //行程清單名稱
    private String name;
    //行程開始時間
    private Date start;
    //行程結束時間
    private Date end;

    

    //協作者清單
    private ArrayList<Member_> collaboratorList = new ArrayList<Member_>();
    //行程清單內容
    private ArrayList<ItineraryItem> list = new ArrayList<ItineraryItem>();
    
    
    // ih，ItineraryHelper 之物件與 Itinerary 相關之資料庫方法（Sigleton）
    private ItineraryListHelper ilh = ItineraryListHelper.getHelper();
    private ItineraryItemHelper ih = ItineraryItemHelper.getHelper();

    //建構式for建立行程清單資料(產生一個新的行程清單)
    public ItineraryList(String name, Date start, Date end, ArrayList<Member_> collaboratorList){
        setName(name);
        setStart(start);
        setEnd(end);
        setCollaborator(collaboratorList);
    }

 
    //建構式for修改行程清單資料(修改資料庫已存在的行程清單)
    public ItineraryList(int id, String name, Date start, Date end){
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        
        getItinerarySceneFromDB();
        getcollaboratorUsersFromDB();
    }

    
    //setId(設定行程清單編號)
    public void setId(int id) {
        this.id = id;
    }

    
    //getId(取得行程清單編號])
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
    
    //setStart(設定行程開始時間)
    public void setStart(Date start) {
        this.start = start;
    }

    
    //getId(取得行程開始時間])
    public Date getStart() {
        return this.start;
    }
    
    //setEnd(設定行程結束時間)
    public void setEnd(Date end) {
        this.end = end;
    }

    
    //getEnd(取得行程結束時間])
    public Date getEnd() {
        return this.end;
    }
    
    //setCollaborator(設定協作者)
    public void setCollaborator(ArrayList<Member_> collaboratorList) {
        this.collaboratorList = collaboratorList;
    }

    
    //getCollaborator(取得協作者])
    public ArrayList<Member_> getCollaborator() {
        return this.collaboratorList;
       
    }


    //取得該名行程清單之所有資料並封裝於JSONObject物件內
    public ArrayList<ItineraryItem> getItineraryScene() {
        return this.list;
    }

    /**
     * 從 DB 中取得行程景點(list)
     * ih=ItineraryHelper
     */
    private void getItinerarySceneFromDB() {
        ArrayList<ItineraryItem> data = ih.getAllByItineraryListID_(this.id);
        this.list = data;
    }
    
    /**
     * 從 DB 中取得協作者(collaboratorList)
     * colabh=CollaboratorHelper
     */
    private void getcollaboratorUsersFromDB() {
        ArrayList<Member_> data = ilh.getCollaboratorsByItineraryListId(this.id);
        this.collaboratorList = data;
    }

 
    //取得行程清單基本資料
    public JSONObject getItineraryListData() {
        JSONObject jso = new JSONObject();
        jso.put("id", getId());
        jso.put("name", getName());
        jso.put("start", getStart());
        jso.put("end", getEnd());
        

        return jso;
    }

    //取得行程景點基本資料
    public JSONArray getItinerarySceneData() {
        JSONArray result = new JSONArray();

        for(int i=0 ; i < this.list.size() ; i++) {
            result.put(this.list.get(i).getItineraryItemData());
            //getData名稱可能不一樣，要跟scene的對一下
        }

        return result;
    }
    
    
    //檢視協作者
    public JSONArray getCollaboratorData() {
        JSONArray result = new JSONArray();

        for(int i=0 ; i < this.collaboratorList.size() ; i++) {
            result.put(this.collaboratorList.get(i).getId());
        }

        return result;
    } 

    
    //取得行程所有資訊
    public JSONObject getItineraryListAllInfo() {
        JSONObject jso = new JSONObject();
        jso.put("collect_List_info", getItineraryListData());
        jso.put("scene_info", getItinerarySceneData());
        jso.put("collaborator_info", getCollaboratorData());

        return jso;
    }

    
    
    /*setCollectSceneId(設定訂單產品編號)
    public void setCollectSceneId(JSONArray data) {
        for(int i=0 ; i < this.list.size() ; i++) {
            this.list.get(i).setId((int) data.getLong(i));
        }
    }*/
    
    
    /**
     * 更新行程清單資訊至資料庫
     *
     * @return 更新後的行程清單資訊（JSON 格式）
     
    public JSONObject update() {
    	
        // 在此處添加將行程清單資訊同步到資料庫的邏輯
        // 你可以使用 CollectListHelper 或其他資料庫相關的類別來執行更新操作

        // 假設使用 CollectListHelper 來更新資料庫
        CollectListHelper clh = CollectListHelper.getHelper();
        JSONObject updateResult = clh.updateCollectionList(this);

        // 返回更新後的行程清單資訊
        return updateResult;
    }*/
    
    

}
