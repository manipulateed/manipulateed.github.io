package ncu.im3069.demo.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
public class District {
	
	private int id;
	
	private String name;
	
	private ArrayList<City> cities = new ArrayList<City>();
	
	private CityHelper Cityh = CityHelper.getHelper();
	
	public District(int id, String name) {
		// TODO Auto-generated constructor stub
		setId(id);
		setName(name);
		getDistrictCityFromDB();
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private void getDistrictCityFromDB() {
		ArrayList<City> cities = Cityh.getDistrictCityByDistrictId(this.id);
		this.cities = cities;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<City> getDistrictCity(){
		return cities;
	}
	
	public JSONObject getDistrictData() {
		JSONObject jso = new JSONObject();
        jso.put("id", getId());
        jso.put("first_name", getName());
        return jso;
	}
	
	public JSONArray getDistrictCityData() {
        JSONArray result = new JSONArray();

        for(int i=0 ; i < this.cities.size() ; i++) {
            result.put(this.cities.get(i).getCityData());
        }

        return result;
	}
	
	public JSONObject getDistrictAllInfo() {
        JSONObject jso = new JSONObject();
        jso.put("order_info", getDistrictData());
        jso.put("product_info", getDistrictCityData());

        return jso;
    }

}
