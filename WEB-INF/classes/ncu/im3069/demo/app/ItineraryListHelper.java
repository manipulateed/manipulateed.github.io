package ncu.im3069.demo.app;
import java.sql.*;
import java.util.*;
import java.util.Date;

import org.json.*;

import ncu.im3069.demo.util.DBMgr;

public class ItineraryListHelper {
    
    private static ItineraryListHelper ilh;
    private Connection conn = null;
    private PreparedStatement pres = null;
    
    //建構式
    private ItineraryListHelper() {
    }
    
    public static ItineraryListHelper getHelper() {
        if(ilh == null) ilh = new ItineraryListHelper();
        
        return ilh;
    }   
    
    /**
     * 檢查該用戶之行程清單是否重複新增
     */
    public boolean checkDuplicate(ItineraryList il, int user_Id){
        /** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
        int row = -1;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 poi nter 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT count(*) FROM `tripy`.`tbl_Itinerary_List` INNER JOIN"
            		+ " `tripy`.`tbl_IL_User` ON `tbl_Itinerary_List`.`IL_Id` = `tbl_IL_User`.`IL_User_ILId`"
            		+ "WHERE `IL_Name` = ? AND `IL_User_UserId` = ?";
            
            /** 取得所需之參數 */
            String name = il.getName();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, name);
            pres.setInt(2,user_Id);
            
            /** 執行SQL指令並記錄其回傳之資料 */
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
        
        /** 
         * 判斷該用戶是否已經有一筆該行程清單之資料
         * 若無一筆則回傳False，否則回傳True 
         */
        return (row == 0) ? false : true;
    }

    
    /**
     * 新增行程清單至資料庫
     * @param <user_Id>
     * @return the JSON object 回傳SQL指令執行之結果
     */
    
    public JSONObject createItineraryListByUserId(ItineraryList il, int user_Id) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄程式開始執行時間 */
        long start_time = System.nanoTime();
        /** 紀錄SQL總行數 */
        int row = 0;
        
        int Itinerary_id = -1;        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "INSERT INTO `tripy`.`tbl_Itinerary_List`(`IL_Name`, `IL_Start`, `IL_End`)"
                    + " VALUES(?, ?, ?)";
            
            //加入關聯表格(tbl_IL_User)
            String sql3 = "INSERT INTO `tripy`.`tbl_IL_User`(IL_User_ILId, IL_User_UserId)"+ " VALUES(?,?)  ";
                     

            
            /** 取得所需之參數 */
            String name = il.getName();
            Date start = il.getStart();        
            Date end = il.getEnd();

            // 使用 java.sql.Date 的函數進行轉換
            java.sql.Date sqlStartDate = new java.sql.Date(start.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(end.getTime());
            
            /** 將參數回填至SQL指令當中 */
            //sql
            pres = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pres.setString(1, name);
            pres.setDate(2, sqlStartDate);
            pres.setDate(3, sqlEndDate);
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            int affectedRows = pres.executeUpdate();
            
            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            /** 取得插入 ItineraryList 後的自動生成的 Itinerary_Id */
            

            if (affectedRows > 0) {
                ResultSet generatedKeys = pres.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Itinerary_id = generatedKeys.getInt(1);
                }
            }
            il.setId(Itinerary_id);
            /** 取得插入 ItineraryList 後的自動生成的 Itinerary_Id */
           // ResultSet generatedKeys = pres.getGeneratedKeys();
               
            pres = conn.prepareStatement(sql3);
            
            pres.setInt(1, Itinerary_id);
            pres.setInt(2, user_Id);            
            /** 執行新增之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();
            exexcute_sql += pres.toString();
            // 執行將 il.getId() 和 collaborator.getId() 插入 tbl_IL_User 的 SQL 語句
            for (Member_ collaborator : il.getCollaborator()) {
            	addCollaboratorToItineraryList(il, collaborator.getId());
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
        
        /** 紀錄程式結束執行時間 */
        long end_time = System.nanoTime();
        /** 紀錄程式執行時間 */
        long duration = (end_time - start_time);

        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("time", duration);
        response.put("row", row);
        response.put("ItineraryList_id", Itinerary_id);

        return response;
    }
    
    
    
    /**
     * 新增協作者至資料庫
     * @param <user_Id>
     * @return the JSON object 回傳SQL指令執行之結果
     */
    
    public JSONObject addCollaboratorToItineraryList(ItineraryList il, int c_user_Id) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "INSERT INTO `tripy`.`tbl_IL_User`(IL_User_ILId, IL_User_UserId) VALUES(?,?)  ";
        
                              
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, il.getId());
            pres.setInt(2, c_user_Id);
            
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            pres.executeUpdate();
            
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
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);

        return response;
    }
    
    public ArrayList<Member_> getCollaboratorsByItineraryListId(int itineraryListId) {
        ArrayList<Member_> collaborators = new ArrayList<>();

        Connection conn = null;
        PreparedStatement prep = null;
        ResultSet rs = null;

        try {
            conn = DBMgr.getConnection();
            String sql = "SELECT * FROM  `tripy`.tbl_User AS u " +
                         "INNER JOIN tbl_IL_User AS ilu ON u.user_Id = ilu.IL_User_UserId " +
                         "INNER JOIN `Tripy`.`tbl_Sex` ON u.`User_Sex_Id` = `tbl_Sex`.`Sex_Id`"+
                         "WHERE ilu.IL_User_ILId = ?";
            
            pres = conn.prepareStatement(sql);
            pres.setInt(1, itineraryListId);
            rs = pres.executeQuery();

            while (rs.next()) {
                int user_Id = rs.getInt("User_Id");
                String user_Name = rs.getString("User_Name");
                String user_Email = rs.getString("User_email");
                String user_Password = rs.getString("User_Password");
                String user_Sex = rs.getString("Gender");
                String user_IdCard = rs.getString("User_IdCard");
                             
                Member_ collaborator = new Member_(user_Id, user_Name,user_Email,user_Password,user_Sex,user_IdCard);
                collaborators.add(collaborator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBMgr.close(rs, prep, conn);
        }

        return collaborators;
    }
    /**
     * 刪除協作者至資料庫
     * @param <user_Id>
     * @return the JSON object 回傳SQL指令執行之結果
     */
    
    public JSONObject deleteCollaboratorToItineraryList(ItineraryList il, int c_user_Id) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "DELETE FROM `tripy`.`tbl_IL_User` WHERE IL_User_ILId = ? AND IL_User_UserId = ?  ";
            
            /** 取得所需之參數 */

                     
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, il.getId());
            pres.setInt(2, c_user_Id);
            
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            pres.executeUpdate();
            
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
        
        /** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);

        return response;
    }
    
    
    
    /**
     * 刪除行程清單(透過行程清單Id)
     *
     * @param id 行程清單編號
     * @return the JSONObject 回傳SQL執行結果
     */
    public JSONObject deleteItineraryList(int ItineraryionList_Id) {
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
            String sql = "DELETE FROM `tripy`.`tbl_Itinerary_List` WHERE `IL_Id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, ItineraryionList_Id);
                     
            
            /** 執行刪除之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();
            
            String sql2 = "DELETE FROM `tripy`.`tbl_il_user` WHERE `IL_User_ILId` = ?";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql2);
            pres.setInt(1, ItineraryionList_Id);
                     
            
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
     * 編輯行程清單資料
     *
     * @param il 行程清單物件
     * @return the JSONObject 回傳SQL指令執行結果與執行之資料
     */
    public JSONObject editItineraryList(ItineraryList il) {
        /** 紀錄回傳之資料 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄SQL總行數 */
        int row = 0;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "Update `tripy`.`tbl_Itinerary_List` SET `IL_Name` = ? WHERE `IL_Id` = ?";
            /** 取得所需之參數 */
            String name = il.getName();
            int id = il.getId();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, name); 
            pres.setInt(2, id);
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
        
        /** 將SQL指令與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("data", jsa);

        return response;
    }    
    
    /**
     * get所有行程清單資料
     *
     * @return the JSONObject 回傳SQL指令執行結果與執行之資料
     */
    public JSONObject getAll(int user_id) {
        ItineraryList il = null;
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT il.* FROM `tripy`.`tbl_Itinerary_List` AS il "
                    + "INNER JOIN `tripy`.`tbl_IL_User` AS ilu ON il.IL_Id = ilu.IL_User_ILId "
                    + "WHERE ilu.IL_User_UserId = ?";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, user_id);

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
                int id = rs.getInt("IL_Id");
                String name = rs.getString("IL_Name");
                Date start = rs.getDate("IL_Start");
                Date end = rs.getDate("IL_End");
                            
                /** 將每一筆資料產生一個新ItineraryList物件 */
                il = new ItineraryList(id, name,start,end);
                /** 取出該項商品之資料並封裝至 JSONsonArray 內 */
                jsa.put(il.getItineraryListData());
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
        
     
        /** 將SQL指令、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("data", jsa);

        return response;
    }
    
    
    /**
     * get行程清單資訊 by 行程清單編號（ID）
     *
     * @param id 行程清單編號
     * @return the JSON object 回傳SQL執行結果與該會員編號之會員資料
     */
    public JSONObject getById(String ItineraryList_Id) {
        JSONObject data = new JSONObject();
        ItineraryList il = null;
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `tripy`.`tbl_Itinerary_List` WHERE `IL_Id` = ?";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, Integer.parseInt(ItineraryList_Id));
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
                int id = rs.getInt("IL_Id");
                String name = rs.getString("IL_Name");
                Date start = rs.getDate("IL_Start");
                Date end = rs.getDate("IL_End");                  
                
                /** 將每一筆資料產生一個新ItineraryList物件 */
                il = new ItineraryList(id, name,start,end);
                /** 取出該項商品之資料並封裝至 JSONsonArray 內 */
                //jsa.put(il.getItineraryListData());
                data = il.getItineraryListAllInfo();
                   
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
        
        /** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
        
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("data", data);

        return response;
    }
    
    /**
     * get所有行程清單資料
     *
     * @return the JSONObject 回傳SQL指令執行結果與執行之資料
     */
    public ArrayList<ItineraryList> getItineraryListByUserId(int user_id) {
        ItineraryList il = null;
        ArrayList<ItineraryList> list = new ArrayList<ItineraryList>();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";

        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT il.* FROM `tripy`.`tbl_Itinerary_List` AS il "
                    + "INNER JOIN `tripy`.`tbl_IL_User` AS ilu ON il.IL_Id = ilu.IL_User_ILId "
                    + "WHERE ilu.IL_User_UserId = ?";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, user_id);

            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
   
                
                /** 將 ResultSet 之資料取出 */
                int id = rs.getInt("IL_Id");
                String name = rs.getString("IL_Name");
                Date start = rs.getDate("IL_Start");
                Date end = rs.getDate("IL_End");
                            
                /** 將每一筆資料產生一個新ItineraryList物件 */
                il = new ItineraryList(id, name,start,end);
                /** 取出該項商品之資料並封裝至 JSONsonArray 內 */
                list.add(il);
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
        
        return list;
    }
    
    
}
