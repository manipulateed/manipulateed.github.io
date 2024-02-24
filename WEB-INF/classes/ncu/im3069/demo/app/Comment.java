package ncu.im3069.demo.app;

import org.json.*;


public class Comment {
	
	private int id;
	
	private String context;
	
	private int user_id;
	private Member_Helper mh = Member_Helper.getHelper();
	public Comment(String context, int user_id) {
		setContext(context);
		setUser_Id(user_id);
		// TODO Auto-generated constructor stub
	}
	
	public Comment(int id, String context, int user_id) {
		setId(id);
		setContext(context);
		setUser_Id(user_id);
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public void setUser_Id(int user_id){
		this.user_id = user_id;
	}
	
	public int getId() {
		return id;
	}
	public String getContext() {
		return context;
	}
	public int getUser_Id() {
		return user_id;
	}
	
	public JSONObject getCommentData() {
        /** 透過JSONObject將所需之資料全部進行封裝*/
		Member_ m = mh.getById(this.user_id);
        JSONObject jso = new JSONObject();
        jso.put("Context", getContext());
        jso.put("User", m.getMemberData());

        return jso;
    }
	
}











