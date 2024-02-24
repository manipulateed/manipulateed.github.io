package ncu.im3069.demo.app;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ncu.im3069.demo.util.DBMgr;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * The Class CollectionItemHelper<br>
 * CollectionItemHelper類別（class）主要管理所有與Collection相關與資料庫之方法（method）
 * </p>
 * 
 * @author IPLab
 * @version 1.0.0
 * @since 1.0.0
 */
public class CollectionItemHelper {
	
	/** 靜態變數，儲存CollectionItemHelper物件 */
	private static CollectionItemHelper collih;
	
	/** 儲存JDBC資料庫連線 */
	private Connection conn = null;
	
	 /** 儲存JDBC預準備之SQL指令 */
	private PreparedStatement pres = null;
	
	/**
     * 實例化（Instantiates）一個新的（new）CollectionItemHelper物件<br>
     * 採用Singleton不需要透過new
     */
	private CollectionItemHelper() {
		
	}
	
	/**
     * 靜態方法<br>
     * 實作Singleton（單例模式），僅允許建立一個CollectionItemHelper物件
     *
     * @return the helper 回傳CollectionItemHelper物件
     */
	 public static CollectionItemHelper getHelper(){
		 /** Singleton檢查是否已經有MemberHelper物件，若無則new一個，若有則直接回傳 */
		 if(collih == null) {
			 collih = new CollectionItemHelper();
		 }
		 
		 return collih;
	 }
	 
	 /**
	     * 透過收藏景點編號(collectionItem_id)(ID)刪除收藏景點
	     *
	     * @param collectionItem_id (ID) 收藏景點編號
	     * @return the JSONObject 回傳SQL執行結果
	     */
	public JSONObject deleteCollectionItemByID(int id) {
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
			String sql = "DELETE FROM `tripy`.`tbl_Collection` WHERE `Collection_Id` = ? LIMIT 1";
			
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
     * 建立收藏景點至資料庫
     *
     * @param collectionList_id 收藏清單之id 
     * @param col 收藏景點之collectionItem物件
     * @return the JSON object 回傳SQL指令執行之結果
     */
    public JSONObject createCollectionItemByCollectionListID(int collectionList_id, CollectionItem col) {
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
            String sql = "INSERT INTO `tripy`.`tbl_Collection`(`Collection_CL`, `Scene`) VALUES(?, ?)";
  
            /** 取得所需之參數 */
            int scene_id = col.getScene().getId(); //先在col中取得scene物件，再從scene去取得getid()
            
            /** 將參數回填至SQL指令當中 */
            //在SQL當中只有
            pres = conn.prepareStatement(sql);
            pres.setInt(1, collectionList_id);
            pres.setInt(2, scene_id);
            
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
     * 利用collectionList_Id比對資料庫 collectionItem 中的清單id去取得該收藏清單之所有景點資料
     *
     * @return the ArrayList 回傳SQL執行結果與自資料庫取回之所有資料
     */
    public ArrayList<CollectionItem> getAllByCollectionListId(String collectionList_id) {
    	/** 使用ArrayList取得該收藏清單之所有收藏景點*/
    	ArrayList<CollectionItem> result = new ArrayList<CollectionItem>();
    	/** 新建一個CollecitonItem物件之 coli 變數，用於紀錄每一個收藏景點 */
    	CollectionItem coli = null;
    	/** 記錄實際執行之SQL指令 */
    	String execute_sql = "";
 
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
    	
    	try {
    		 /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `tripy`.`tbl_collection` WHERE `tbl_collection`.`Collection_CL` = ?";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, Integer.parseInt(collectionList_id));
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            execute_sql = pres.toString();
            System.out.println(execute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {

                
                /** 將 ResultSet 之資料取出 */
                int collectionItem_id = rs.getInt("Collection_Id");
                //Scene Scene = rs.getObject("scene", Scene.class);
                int scene_id = rs.getInt("Scene");
                
                /** 將每一筆收藏景點資料產生一名新CollectionItem物件 */
                coli = new CollectionItem(collectionItem_id, scene_id);
                /** 取出收藏景點之資料並封裝至 JSONsonArray 內 */
                result.add(coli);
            }
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
    	return result;
    }
    
    public JSONObject getAllByCollectionListID(String collectionList_id) {
        CollectionItem coll = null;
        JSONArray jsa = new JSONArray();
        String exexcute_sql = "";     
        long start_time = System.nanoTime();
        int row = 0;
        ResultSet rs = null;
        try {
            conn = DBMgr.getConnection();
            String sql = "SELECT * FROM `tripy`.`tbl_Collection` WHERE `tbl_Collection`.`Collection_CL` = ?";
            pres = conn.prepareStatement(sql);
            pres.setInt(1, Integer.parseInt(collectionList_id));
            rs = pres.executeQuery();
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            while(rs.next()) {
                row += 1;
                int collectionItem_id = rs.getInt("Collection_Id");
                //Scene Scene = rs.getObject("scene", Scene.class);
                int scene_id = rs.getInt("Scene");              
                /** 將每一筆收藏景點資料產生一名新CollectionItem物件 */
                coll = new CollectionItem(collectionItem_id, scene_id);
                jsa.put(coll.getCollectionData());
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
    
    public JSONObject getByCollectionID(String id) {
        /** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之會員資料 */
        CollectionItem colli = null;
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
            String sql = "SELECT * FROM `tripy`.`tbl_Collection` WHERE `tbl_Collection`.`Collection_Id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, Integer.parseInt(id));
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            /** 正確來說資料庫只會有一筆該會員編號之資料，因此其實可以不用使用 while 迴圈 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int collectionItem_id = rs.getInt("Collection_Id");
                //Scene Scene = rs.getObject("scene", Scene.class);
                int scene_id = rs.getInt("Scene");
                
                /** 將每一筆收藏景點資料產生一名新CollectionItem物件 */
                colli = new CollectionItem(collectionItem_id, scene_id);
                /** 取出收藏景點之資料並封裝至 JSONsonArray 內 */
                jsa.put(colli.getCollectionData());
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
     * 檢查該名會員之收藏清單中的收藏景點是否重複
     *
     * @param coli 收藏景點物件
     * @return boolean 若重複收藏該景點回傳False，若收藏景點不存在則回傳True
     */
    public boolean checkDuplicate(CollectionItem coli, int collectionList_id) {
    	/** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
        int row = -1;
        ResultSet rs = null;       
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT count(*) FROM `tripy`.`tbl_Collection` WHERE `Scene` = ? AND `Collection_CL` = ?";
            int scene_id = coli.getScene().getId();
            pres = conn.prepareStatement(sql);
            pres.setInt(1, scene_id);
            pres.setInt(2, collectionList_id);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();
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
        
        /** 
         * 判斷是否已經有一筆該收藏景點之資料
         * 若無一筆則回傳False，否則回傳True 
         */
        return (row == 0) ? false : true;
    } 
}
