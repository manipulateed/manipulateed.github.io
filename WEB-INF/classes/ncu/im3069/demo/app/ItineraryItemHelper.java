package ncu.im3069.demo.app;

import java.sql.*;
import java.util.*;
import java.util.Date;

import org.json.*;

import ncu.im3069.demo.util.DBMgr;

//TODO: Auto-generated Javadoc
/**
* <p>
* The Class ItineraryItemHelper<br>
* ItineraryItemHelper類別（class）主要管理所有與ItineraryItem相關與資料庫之方法（method）
* </p>
* 
* @author IPLab
* @version 1.0.0
* @since 1.0.0
*/

public class ItineraryItemHelper {
	/** 靜態變數，儲存MemberHelper物件 */
    private static ItineraryItemHelper itih;
    
    /** 儲存JDBC資料庫連線 */
    private Connection conn = null;
    
    /** 儲存JDBC預準備之SQL指令 */
    private PreparedStatement pres = null;
    
    /**
     * 實例化（Instantiates）一個新的（new） ItineraryItemHelper物件<br>
     * 採用Singleton不需要透過new
     */
    private ItineraryItemHelper() {
        
    }
    
    /**
     * 靜態方法<br>
     * 實作Singleton（單例模式），僅允許建立一個ItineraryItemHelper物件
     *
     * @return the helper 回傳ItineraryItemHelper物件
     */
    public static ItineraryItemHelper getHelper() {
        /** Singleton檢查是否已經有MemberHelper物件，若無則new一個，若有則直接回傳 */
        if(itih == null) itih = new ItineraryItemHelper();
        
        return itih;
    }
    
    /**
     * 透過行程編號（ID）刪除行程
     *
     * @param id 行程編號
     * @return the JSONObject 回傳SQL執行結果
     */
    public JSONObject deleteItineraryItemByID(int id) {
    	/** 記錄實際執行之SQL指令 */
		String execute_sql = "";
		
		/** 紀錄程式開始執行時間 */
		long start_time = System.nanoTime();
		
		/** 紀錄SQL總行數 */
		int row = 0;
		
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;
		
		try {
			/** 取得資料庫連線 */
			conn = DBMgr.getConnection();
			
			/** SQL指令 */
			String sql = "DELETE FROM `tripy`.`tbl_Itinerary` WHERE `Itinerary_Id` = ? LIMIT 1";
			
			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setInt(1, id);
			/** 執行刪除之SQL指令並記錄影響之行數 */
			row = pres.executeUpdate();
			
			/** 紀錄真實執行的SQL指令，並印出 */
			execute_sql = pres.toString();
			System.out.println(execute_sql);		
			
		} catch(SQLException e) {
			/** 印出JDBC SQL指令錯誤 */
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch(Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放出所有資料庫相關之資源 */
			DBMgr.close(rs, pres, conn);
		}
		
		/** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", execute_sql);
        response.put("row", row);
        response.put("time", duration);
		
		return response;
    }
    
    /**
     * 建立行程景點至資料庫
     *
     * @param itineraryList_id 行程清單之id 
     * @param iti 行程景點之itineraryItem物件
     * @return the JSON object 回傳SQL指令執行之結果
     */
    public JSONObject createItineraryItemByItineraryListID(int itineraryList_id, ItineraryItem iti) {
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
            String sql = "INSERT INTO `tripy`.`tbl_Itinerary`(`Itinerary_IL`, `Scene`, `Itinerary_Day`, `Itinerary_Day_Order`)  VALUES(?, ?, ?, ?)";
            //???????----- 是不是只需要scene的就好了
  
            /** 取得所需之參數 */
            int scene_id = iti.getScene().getId();
            java.util.Date utilDate = iti.getDate(); // Assuming iti is an object of type java.util.Date
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            // pres.setDate(3, sqlDate);

            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, itineraryList_id);
            pres.setInt(2, scene_id);
            pres.setDate(3, sqlDate);
            pres.setInt(4, iti.getDate_order());
          
            
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
     * 取回行程清單中的所有行程
     *
     * @return the JSONObject 回傳SQL執行結果與自資料庫取回之所有資料
     */
    public JSONObject getAllByItineraryListID(int itineraryList_id){
    	/** 新建一個 collectionItem 物件之 colli 變數，用於紀錄每一位查詢回收藏清單之所有收藏景點資料 */
        ItineraryItem iti = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `tripy`.`tbl_Itinerary` WHERE `Itinerary_IL` = ?";
            pres = conn.prepareStatement(sql);
            pres.setInt(1, itineraryList_id);
            rs = pres.executeQuery();
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            while(rs.next()) {
                row += 1;
                int itinerary_Id = rs.getInt("Itinerary_Id");
                int scene_id = rs.getInt("Scene");
                java.sql.Date  date = rs.getDate("Itinerary_Day");
                int order = rs.getInt("Itinerary_Day_Order");
                iti = new ItineraryItem(itinerary_Id, scene_id, date, order);
                jsa.put(iti.getItineraryItemData());
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
        
        /** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;   
    }
    /**
     * 取回行程清單中的所有行程
     *
     * @return the JSONObject 回傳SQL執行結果與自資料庫取回之所有資料
     */
    public JSONObject getAllByItineraryListID(Date d, int itineraryList_id){
    	/** 新建一個 collectionItem 物件之 colli 變數，用於紀錄每一位查詢回收藏清單之所有收藏景點資料 */
        ItineraryItem iti = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
    	java.sql.Date sqlDate =  new java.sql.Date(d.getTime());
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `tripy`.`tbl_Itinerary` WHERE `Itinerary_IL` = ? AND `Itinerary_Day` = ?";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, itineraryList_id);
            pres.setDate(2, sqlDate);
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
                int itinerary_Id = rs.getInt("Itinerary_Id");
                int scene_id = rs.getInt("Scene");
                java.sql.Date  date = rs.getDate("Itinerary_Day");
                

                int order = rs.getInt("Itinerary_Day_Order");
                
                /** 將每一筆收藏景點資料產生一名新collectionItem物件 */
                iti = new ItineraryItem(itinerary_Id, scene_id, date, order);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(iti.getItineraryItemData());
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
        
        /** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;   
    }
    
    public ArrayList<ItineraryItem> getAllByItineraryListID_(int itineraryList_id){
    	/** 新建一個 collectionItem 物件之 colli 變數，用於紀錄每一位查詢回收藏清單之所有收藏景點資料 */
        ItineraryItem iti = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
        ArrayList<ItineraryItem> data = new ArrayList<ItineraryItem>();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
   
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
    	
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `tripy`.`tbl_Itinerary` WHERE `Itinerary_IL` = ?";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, itineraryList_id);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
              
            	/** 將 ResultSet 之資料取出 */
                int itinerary_Id = rs.getInt("Itinerary_Id");
                int scene_id = rs.getInt("Scene");
                java.sql.Date date = rs.getDate("Itinerary_Day");
                int order = rs.getInt("Itinerary_Day_Order");
                
                /** 將每一筆收藏景點資料產生一名新collectionItem物件 */
                iti = new ItineraryItem(itinerary_Id, scene_id, date, order);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                data.add(iti);
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
        return data;   
    }
    
    

    /**
     * 透過行程景點編號（itineraryItem_id）取得行程景點資料
     *
     * @param id 行程景點編號
     * @return the JSON object 回傳SQL執行結果與該行程景點編號之景點資料
     */
    public JSONObject getByItineraryItemID(int id) {
    	/** 新建一個 Scene 物件之 m 變數，用於紀錄查詢之收藏景點資料 */
        ItineraryItem iti = null;
        /** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
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
            String sql = "SELECT * FROM `tripy`.`tbl_Itinerary` WHERE `Itinerary_Id` = ? LIMIT 1";
            
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
                int itinerary_Id = rs.getInt("Itinerary_Id");
                int scene_id = rs.getInt("Scene");
                java.sql.Date date = rs.getDate("Itinerary_Day");
                int order = rs.getInt("Itinerary_Day_Order");
                
                /** 將每一筆收藏景點資料產生一名新collectionItem物件 */
                iti = new ItineraryItem(itinerary_Id, scene_id, date, order);
                /** 取出該名會員之資料並封裝至 JSONsonArray 內 */
                jsa.put(iti.getItineraryItemData());
            }
            
        }catch (SQLException e) {
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
        
        /** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("time", duration);
        response.put("data", jsa);

        return response;
    }

    /**
     * 更新行程之行程順序
     *
     * @param m 一名行程之Itinerary物件
     * @param date_order 順序之int
     */
    public void updateDateOrder(ItineraryItem iti, int date_order) {
    	/** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
    
        try {
        	/** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "Update `tripy`.`tbl_Itinerary` SET `Itinerary_Day_Order` = ? WHERE `Itinerary_Id` = ?";
            /** 取得會員編號 */
            int id = iti.getId();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, date_order);
            pres.setInt(2, id);
            /** 執行更新之SQL指令 */
            pres.executeUpdate();
        	
            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
        }  catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(pres, conn);
        }
    }
    
    /**
     * 更新行程之行程資料
     *
     * @param iti 一行程之ItineraryItem物件
     * @return the JSONObject 回傳SQL指令執行結果與執行之資料
     */
    public JSONObject update(ItineraryItem iti) {
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
            //String sql = "Update `tripy`.`tbl_Itinerary` SET `Scene` = ?, `Itinerary_Day` = ?, `Itinerary_Day_Order` = ? , WHERE `Itinerary_Id` = ?";
            String sql = "UPDATE `tripy`.`tbl_Itinerary` SET `Itinerary_Day_Order` = ? WHERE `Itinerary_Id` = ?";

            /** 取得所需之參數 */
            //    int sc_id = iti.getScene().getId();
            //    Date day = iti.getDate();   
            //java.sql.Date sqlDate = new java.sql.Date(day.getTime());

            int day_order = iti.getDate_order();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, day_order);
            pres.setInt(2, iti.getId());
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