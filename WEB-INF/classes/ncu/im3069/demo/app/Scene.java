package ncu.im3069.demo.app;

import java.util.*;

import org.json.*;

public class Scene {
	
    private int id;

    private String name;

    private String address;

    private String phone;
    
    private String detail;
    
    private String opentime;


    private ArrayList<Comment> list = new ArrayList<Comment>();
    
    private ArrayList<String> images = new ArrayList<String>();

    private CommentHelper Comh = CommentHelper.getHelper();	
    
    private SceneHelper Sceh = SceneHelper.getHelper();

	public Scene(String name, String address, String detail, String opentime, String phone, ArrayList<String> images) {
		// TODO Auto-generated constructor stub
		setName(name);
		setAddress(address);
		setDetail(detail);
		setOpentime(opentime);
		setPhone(phone);
		setImages(images);
	}


	public Scene(int id, String name, String address, String detail, String opentime, String phone, ArrayList<String> images) {
		setId(id);
		setName(name);
		setAddress(address);
		setDetail(detail);
		setOpentime(opentime);
		setPhone(phone);
		getSceneCommentFromDB();
		setImages(images);
	}


	public int getId() {
		return this.id;
	}


	public String getName() {
		return this.name;
	}
	
	public ArrayList<String> getImages(){
		return this.images;
	}


	public String getAddress() {
		return this.address;
	}
 
	public String getDetail() {
		return this.detail;
	}
 
	public String getPhone() {
		return this.phone;
	}
	
	public String getOpentime() {
		return this.opentime;
	}
	
	public void setId(int id) {
        this.id = id;
    }
	
	public void setImages(ArrayList<String> images) {
		this.images = images;
	}
	
	public void setName(String name) {
        this.name = name;
    }
	
	public void setAddress(String address) {
        this.address = address;
    }
	
	public void setDetail(String detail) {
        this.detail = detail;
    }
	
	public void setOpentime(String opentime) {
        this.opentime = opentime;
    }
	
	public void setPhone(String phone) {
        this.phone = phone;
    }
	
	public ArrayList<Comment> getSceneComment(){
		return this.list;
	}
	
	private void getSceneCommentFromDB() {
		ArrayList<Comment> comments = Comh.getSceneCommentBySceneId(this.id);
		this.list = comments;
	}

	public JSONObject getSceneData() {
		JSONArray result = new JSONArray();

        for(int i=0 ; i < this.images.size() ; i++) {
            result.put(this.images.get(i));
        }
        /** 透過JSONObject將所需之資料全部進行封裝*/
        JSONObject jso = new JSONObject();
        jso.put("id", getId());
        jso.put("name", getName());
        jso.put("address", getAddress());
        jso.put("detail", getDetail());
        jso.put("opentime", getOpentime());
        jso.put("phone", getPhone());
        jso.put("images", result);
        return jso;
    }
	
	public JSONArray getSceneCommentData() {
	        JSONArray result = new JSONArray();

	        for(int i=0 ; i < this.list.size() ; i++) {
	            result.put(this.list.get(i).getCommentData());
	        }

	        return result;
	}
	 
	public JSONObject getSceneAllInfo() {
	        JSONObject jso = new JSONObject();
	        jso.put("Scene_info", getSceneData());
	        jso.put("Scene_Comment_info", getSceneCommentData());

	        return jso;
	}
	
	//* FUNCTION
	
	public JSONObject update() {
		/** 新建一個JSONObject用以儲存更新後之資料 */
		JSONObject data = new JSONObject();
		data = Sceh.update(this);
		return data;
	}
}
