package ncu.im3069.demo.app;

import org.json.JSONObject;

public class Admin {
	/** id，編號 */
    private int id;
    
    /** email，電子郵件信箱 */
    private String email;
    
    /** name，姓名 */
    private String name;
    
    /** password，密碼 */
    private String password;
    
    private String sex;
    
    private String idcard;
    

    private AdminHelper Admh =  AdminHelper.getHelper();
    
    public Admin(String name, String email, String password, String sex, String idcard) {
        setEmail(email);
        setName(name);
        setPassword(password);
        setSex(sex);
        setIdCard(idcard);
    }

    public Admin(int id, String name, String email, String password, String sex, String idcard) {
    	setId(id);
    	setEmail(email);
        setName(name);
        setPassword(password);
        setSex(sex);
        setIdCard(idcard);
    }
    
    public int getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }
    
    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }
    
    public String getSex() {
        return this.sex;
    }
    
    public String getIdCard() {
        return this.idcard;
    }
    
    public void setId(int id) {
    	this.id = id;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setEmail(String email) {
    	this.email = email;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    public void setSex(String sex) {
    	this.sex = sex;
    }
    
    public void setIdCard(String idcard) {
    	this.idcard = idcard;
    }
    
    
    public JSONObject getAdminData() {
        /** 透過JSONObject將所需之資料全部進行封裝*/ 
        JSONObject jso = new JSONObject();
        jso.put("id", getId());
        jso.put("name", getName());
        jso.put("email", getEmail());
        jso.put("password", getPassword());
        jso.put("sex", getSex());
        jso.put("idcard", getIdCard());
        
        return jso;
    }
    
  //* FUNCTION
  	
  	public JSONObject update() {
		/** 新建一個JSONObject用以儲存更新後之資料 */
		JSONObject data = new JSONObject();
		data = Admh.update(this);
		return data;
	}

}
