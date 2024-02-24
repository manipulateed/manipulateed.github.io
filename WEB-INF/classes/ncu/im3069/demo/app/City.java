package ncu.im3069.demo.app;

import java.util.*;

import org.json.*;
public class City {
	
	private int id;
	
	private String name;
	
	private ArrayList<Scene> scenes = new ArrayList<Scene>();
	
	private SceneHelper Sceh = SceneHelper.getHelper();
	
	public City(int id, String name) {
		setId(id);
		setName(name);
		getCitySceneFromDB();
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private void getCitySceneFromDB() {
		ArrayList<Scene> scenes = Sceh.getCitySceneByCityId(this.id);
		this.scenes = scenes;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Scene> getCityScene(){
		return scenes;
	}
	
	public JSONObject getCityData() {
		JSONObject jso = new JSONObject();
        jso.put("id", getId());
        jso.put("name", getName());
        return jso;
	}
	
	public JSONArray getCitySceneData() {
        JSONArray result = new JSONArray();

        for(int i=0 ; i < this.scenes.size() ; i++) {
            result.put(this.scenes.get(i).getSceneData());
        }

        return result;
	}
	
	public JSONObject getCityAllInfo() {
        JSONObject jso = new JSONObject();
        jso.put("city_info", getCityData());
        jso.put("scene_info", getCitySceneData());

        return jso;
    }
}