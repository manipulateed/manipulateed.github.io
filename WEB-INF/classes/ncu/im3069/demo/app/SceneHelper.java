package ncu.im3069.demo.app;

import java.sql.*;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.json.*;

import ncu.im3069.demo.util.DBMgr;

public class SceneHelper {

	private SceneHelper() {
		// TODO Auto-generated constructor stub
	}
	
	/** 靜態變數，儲存MemberHelper物件 */
    private static SceneHelper Sceh;
    
    /** 儲存JDBC資料庫連線 */
    private Connection conn = null;
    
    /** 儲存JDBC預準備之SQL指令 */
    private PreparedStatement pres = null;
    
    /**
     * 靜態方法<br>
     * 實作Singleton（單例模式），僅允許建立一個SceneHelper物件
     *
     * @return the helper 回傳SceneHelper物件
     */
    public static SceneHelper getHelper() {
        /** Singleton檢查是否已經有SceneHelper物件，若無則new一個，若有則直接回傳 */
        if(Sceh == null) Sceh = new SceneHelper();
        
        return Sceh;
    }
    
    /**
     * 透過編號（ID）刪除
     *
     * @param id 編號
     * @return the JSONObject 回傳SQL執行結果
     */
    public JSONObject deleteByID(int id) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            
            /** SQL指令 */

            String sql = "DELETE FROM `tripy`.`tbl_scene_info` WHERE `Scene_Id` = ? LIMIT 1";

            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, id);
            /** 執行刪除之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }

        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);

        return response;
    }
    
    /**
     * 取回所有資料
     *
     * @return the JSONObject 回傳SQL執行結果與自資料庫取回之所有資料
     */
    public JSONObject getAll() {
        Scene s = null;
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */

            String sql = "SELECT * FROM `tripy`.`tbl_scene_info`";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int scene_id = rs.getInt("Scene_Id");
                String name = rs.getString("Scene_Name");
                String address = rs.getString("Scene_Address");
                String detail = rs.getString("Scene_Detail");
                String phone = rs.getString("Scene_Phone");
                String opentime = rs.getString("Scene_OpenTime");
                String image = rs.getString("Scene_Image");
                ArrayList<String> images = new ArrayList<String>();
                if(image != null) {
                    String[] images_ = image.split(","); 
                    for (int i = 0; i < images_.length; i++) {
                    	images.add(images_[i]);
                    }
                }
                
                /** 將每一筆資料產生物件 */
                s = new Scene(scene_id, name, address, detail, opentime, phone, images);
                /** 取出該資料並封裝至 JSONsonArray 內 */
                jsa.put(s.getSceneAllInfo());
            }

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間、影響行數與所有資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }
    
    /**
     * 取回該城市所有景點資料
     *
     * @return the ArrayList 回傳SQL執行結果與自資料庫取回之所有資料
     */
    public ArrayList<Scene> getCitySceneByCityId(int id) {
    	ArrayList<Scene> result = new ArrayList<Scene>();
    	Scene scene = null;
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */

            String sql = "SELECT * FROM `tripy`.`tbl_scene_info` where `Scene_City` = ? ";         
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);         
            pres.setInt(1, id);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
                
            	/** 將 ResultSet 之資料取出 */
                int scene_id = rs.getInt("Scene_Id");
                String name = rs.getString("Scene_Name");
                String address = rs.getString("Scene_Address");
                String detail = rs.getString("Scene_Detail");
                String phone = rs.getString("Scene_Phone");
                String opentime = rs.getString("Scene_OpenTime");
                String image = rs.getString("Scene_Image");
                ArrayList<String> images = new ArrayList<String>();
                if(image != null) {
                    String[] images_ = image.split(","); 
                    for (int i = 0; i < images_.length; i++) {
                    	images.add(images_[i]);
                    }
                }
                /** 將每一筆資料產生新物件 */
                scene = new Scene(scene_id, name, address, detail, opentime, phone, images);
                /** 取出該資料並封裝至 JSONsonArray 內 */
                result.add(scene);
            }

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }

        return result;
    }
    
    public JSONObject getById(int id) {
        Scene s = null;
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `tripy`.`tbl_scene_info` WHERE `Scene_Id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, id);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            /** 正確來說資料庫只會有一筆該編號之資料，因此其實可以不用使用 while 迴圈 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int scene_id = rs.getInt("Scene_Id");
                String name = rs.getString("Scene_Name");
                String address = rs.getString("Scene_Address");
                String detail = rs.getString("Scene_Detail");
                String phone = rs.getString("Scene_Phone");
                String opentime = rs.getString("Scene_OpenTime");
                String image = rs.getString("Scene_Image");
                ArrayList<String> images = new ArrayList<String>();
                if(image != null) {
                    String[] images_ = image.split(","); 
                    for (int i = 0; i < images_.length; i++) {
                    	images.add(images_[i]);
                    }
                }
                /** 將每一筆資料產生新物件 */
                s = new Scene(scene_id, name, address, detail, opentime, phone, images);
                /** 取出該資料並封裝至 JSONsonArray 內 */
                jsa.put(s.getSceneAllInfo());
            }
            
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間、影響行數與所有資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }
    

    public JSONObject getByKeyWord(String keyword) {
        Scene s = null;
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `tripy`.`tbl_scene_info` WHERE `Scene_Name` LIKE ?";
         
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, "%"+keyword+"%");
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */

            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int scene_id = rs.getInt("Scene_Id");
                String name = rs.getString("Scene_Name");
                String address = rs.getString("Scene_Address");
                String detail = rs.getString("Scene_Detail");
                String phone = rs.getString("Scene_Phone");
                String opentime = rs.getString("Scene_OpenTime");
                String image = rs.getString("Scene_Image");
                int city_id = rs.getInt("Scene_City");
                ArrayList<String> images = new ArrayList<String>();
                if(image != null) {
                    String[] images_ = image.split(","); 
                    for (int i = 0; i < images_.length; i++) {
                    	images.add(images_[i]);
                    }
                }
                
                /** 將每一筆資料產生新物件 */
                s = new Scene(scene_id, name, address, detail, opentime, phone, images);
                JSONObject search = new JSONObject();
                search.put("scene",s.getSceneAllInfo());
                search.put("city_id",city_id);
                /** 取出該資料並封裝至 JSONsonArray 內 */
                jsa.put(search);
            }
            
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間、影響行數與所有資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }
    
    
    /**
     * 檢查該景點是否重複有重複名稱
     *
     * @param s Scene物件
     * @return boolean 若重複回傳False，若不存在則回傳True
     */
    public boolean checkDuplicate(Scene s){
        /** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
        int row = -1;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT count(*) FROM `tripy`.`tbl_scene_info` WHERE `Scene_Name` = ?";
            
            /** 取得所需之參數 */
            String name = s.getName();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, name);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 讓指標移往最後一列，取得目前有幾行在資料庫內 */
            rs.next();
            row = rs.getInt("count(*)");
            System.out.print(row);

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        

        return (row == 0) ? false : true;
    }
    
    /**
     * 建立該景點至資料庫
     *
     * @param s Scene物件
     * @return the JSON object 回傳SQL指令執行之結果
     */
    public JSONObject createByCityId(int city_id, Scene s) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "INSERT INTO `tripy`.`tbl_scene_info`(`Scene_Name`,`Scene_Address`,`Scene_Detail`,`Scene_City`,`Scene_Phone`,`Scene_OpenTime`)"+
            		"VALUES(?, ?, ?, ?, ?, ?)";
            
            /** 取得所需之參數 */
            String name = s.getName();
            String address = s.getAddress();
            String detail = s.getDetail();
            String phone = s.getPhone();
            String opentime = s.getOpentime();
//            String images = s.getImages().stream().map(String::valueOf).collect(Collectors.joining(","));
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, name);
            pres.setString(2, address);
            pres.setString(3, detail);
            pres.setInt(4, city_id);
            pres.setString(5, phone);
            pres.setString(6, opentime);
 //           pres.setString(7, images);
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();
            
            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(pres, conn);
        }

        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);

        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("time", duration);
        response.put("row", row);

        return response;
    }
    
    /**
     * 更新資料
     *
     * @param m 一物件
     * @return the JSONObject 回傳SQL指令執行結果與執行之資料
     */
    public JSONObject update(Scene s) {
        /** 紀錄回傳之資料 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "Update `tripy`.`tbl_scene_info` SET `Scene_Name` = ? ,`Scene_Address` = ? ,`Scene_Detail` = ?, `Scene_Phone` = ? ,`Scene_OpenTime` = ? , `Scene_Image` = ? WHERE `Scene_Id` = ?";
            /** 取得所需之參數 */
            String name = s.getName();
            String address = s.getAddress();
            String detail = s.getDetail();
            String phone = s.getPhone();
            String opentime = s.getOpentime();
            String images = s.getImages().stream().map(String::valueOf).collect(Collectors.joining(","));
            int id = s.getId();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, name);
            pres.setString(2, address);
            pres.setString(3, detail);
            pres.setString(4, phone);
            pres.setString(5, opentime);
            pres.setString(6, images);
            pres.setInt(7, id);
            
            
            /** 執行更新之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(pres, conn);
        }
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }
}
