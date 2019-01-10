package com.util.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//自定义dao继承BaseDao
public class BaseDao {
	static Properties p  ;
	static {
		try {
			p = new Properties();
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("db-config.properties") ;
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//增删改
	private void executeUpdate(String sql,Object...params)  {
		try {
			//1 引入jar
			//2 加载驱动
			Class.forName(p.getProperty("driver")) ;
			//3 创建连接
			Connection conn = DriverManager.getConnection(
					p.getProperty("url"), 
					p.getProperty("username"), 
					p.getProperty("password")
			);
			//4 创建命令集
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++) {
				stmt.setObject(i+1,params[i]);
			}
			//5执行sql
			stmt.executeUpdate() ;
			stmt.close();
			conn.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//Car.class
	//定义executeQuery负责查询操作，查询时需要告诉具体的sql操作，告诉具体的参数
	//告诉每条记录需要组成的对象类型
	//该方法会将查询结果组成一个一个的指定对象组装到List集合中
	//暂时使用泛型T表示要创建的对象。
	private <T> List<T> executeQuery(String sql,Class<T> template,Object...params) {
		List<T> ts = new ArrayList<T>();
		try {
			//1 引入驱动jar
			//2加载驱动
			Class.forName(p.getProperty("driver"));
			//3创建连接
			Connection conn = DriverManager.getConnection(
					p.getProperty("url"),
					p.getProperty("username"),
					p.getProperty("password")
			);
			//4 创建命令集
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++) {
				stmt.setObject(i+1, params[i]);
			}
			//5执行sql
			ResultSet rs = stmt.executeQuery();
			//5.5 操作结果集(结果集中的数据取出组成我们需要的类型,List,User等)
			
			while(rs.next()) {
				T obj = null ;
				if(template == long.class || template == Long.class ||template == int.class || template == Integer.class || template == String.class) {
					//查询的就是单一类型的值，不需要组成复杂的对象。
					obj = (T) rs.getObject(1);
				}else {
					//template.getDeclaredFields()
					obj = template.newInstance(); // User user = new User();
					Method[] ms = template.getMethods();
					/*根据set方法，找到对应的属性。根据属性在结果集中找到属性值。为对象的属性赋值。*/
					for(Method m : ms){
						String mname = m.getName() ;
						if(mname.startsWith("set")) {
							//set方法。  setUname() --> uname , setUpass() --> upass
							String key = mname.substring(3);//去掉set
							key = key.substring(0,1).toLowerCase() + key.substring(1); // uname
							Object value = rs.getObject(key); //rs.getObject("uname");
							m.invoke(obj, value); // user.setUname(value);
						}
					}
				}
				
				ts.add(obj);
			}
			
			//select * from t_user ; --> List<User>
			/*
			 * while(rs.next){
			 * 	User user = new User();
			 * 	int uno = rs.getInt("uno");
			 * 	String uname = rs.getString("uname");
			 * 	String upass = rs.getString("upass");
			 * 	String truename = rs.getString("truename");
			 * 
			 * 	user.setUno(uno);
			 * 	user.setUname(uname);
			 * 	user.setUpass(upass);
			 * 	user.setTruename(truename);
			 * 
			 * 	users.add(user);
			 * }
			 * 
			 * 
			 */
			
			//6各种关闭
			rs.close();
			stmt.close();
			conn.close();
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ts ;
	}
	
	public void insert(String sql,Object...params){
		if(sql.trim().substring(0, 6).equalsIgnoreCase("insert")) {
			//是一个insert语句
			this.executeUpdate(sql, params);
		}else {
			throw  new SqlFormatException("not a insert sql : ["+sql+"]");
		}
	}
	public void update(String sql,Object...params){
		if(sql.trim().substring(0, 6).equalsIgnoreCase("update")) {
			//是一个insert语句
			this.executeUpdate(sql, params);
		}else {
			throw  new SqlFormatException("not a update sql : ["+sql+"]");
		}
	}
	public void delete(String sql,Object...params){
		if(sql.trim().substring(0, 6).equalsIgnoreCase("delete")) {
			//是一个insert语句
			this.executeUpdate(sql, params);
		}else {
			throw  new SqlFormatException("not a delete sql : ["+sql+"]");
		}
	}
	public <T> List<T> select(String sql,Class<T> template , Object...params){
		if(sql.trim().substring(0, 6).equalsIgnoreCase("select")) {
			//是一个insert语句
			return this.executeQuery(sql, template,params);
		}else {
			throw  new SqlFormatException("not a select sql : ["+sql+"]");
		}
	}
	
	
	
	
}
