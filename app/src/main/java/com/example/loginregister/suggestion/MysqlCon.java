package com.example.loginregister.suggestion;

import android.util.Log;

import com.example.loginregister.datasets.DietStatus;
import com.example.loginregister.datasets.FoodInfo;
import com.example.loginregister.datasets.UserInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlCon {
    String mysql_ip = "10.0.2.2";
    int mysql_port = 3306;
    String db_name = "loginregister";
    String url = "jdbc:mysql://" + mysql_ip + ":" + mysql_port + "/" + db_name;
    String db_user = "root";
    String db_password = "";

    public void run() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v("DB", "加載驅動成功");
        } catch(ClassNotFoundException e) {
            Log.e("DB", "加載驅動失敗");
            return;
        }

        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            Log.v("DB", "遠端連接成功");
        } catch(SQLException e) {
            Log.e("DB", "遠端連線失敗");
            Log.e("DB", e.toString());
        }
    }

    public UserInfo getUserData(String uname) {
        UserInfo userInfo = new UserInfo();
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "SELECT * FROM users WHERE `username` = \"" + uname + "\"";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            rs.next();

            userInfo.user_id = rs.getInt("id");
            userInfo.fullname = rs.getString("fullname");
            userInfo.username = rs.getString("username");
            userInfo.password = rs.getString("password");
            userInfo.email = rs.getString("email");
            userInfo.height = rs.getInt("height");
            userInfo.weight = rs.getInt("weight");
            userInfo.birthday = rs.getDate("birthday");
            userInfo.gender = rs.getString("gender");

            st.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public List<FoodInfo> getFoodData(int calories) {
        List<FoodInfo> foodInfo = new ArrayList<>();
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "SELECT * FROM food_seven WHERE `熱量` <= " + calories;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                FoodInfo result = new FoodInfo();
                result.food_id = rs.getInt("food_id");
                result.title = rs.getString("title");
                result.categories = rs.getString("categories");
                result.tags = rs.getString("tags");
                result.weight = rs.getDouble("重量(g)");
                result.calories = rs.getDouble("熱量");
                result.protein = rs.getDouble("蛋白質(g)");
                result.carbs = rs.getDouble("碳水化合物(g)");
                result.fat = rs.getDouble("脂肪(g)");
                result.sugar = rs.getDouble("糖");
                result.sodium = rs.getDouble("鈉");
                result.image = rs.getString("image");
                foodInfo.add(result);
            }
            st.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return foodInfo;
    }
}
