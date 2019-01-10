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

//�Զ���dao�̳�BaseDao
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
	//��ɾ��
	private void executeUpdate(String sql,Object...params)  {
		try {
			//1 ����jar
			//2 ��������
			Class.forName(p.getProperty("driver")) ;
			//3 ��������
			Connection conn = DriverManager.getConnection(
					p.getProperty("url"), 
					p.getProperty("username"), 
					p.getProperty("password")
			);
			//4 �������
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++) {
				stmt.setObject(i+1,params[i]);
			}
			//5ִ��sql
			stmt.executeUpdate() ;
			stmt.close();
			conn.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//Car.class
	//����executeQuery�����ѯ��������ѯʱ��Ҫ���߾����sql���������߾���Ĳ���
	//����ÿ����¼��Ҫ��ɵĶ�������
	//�÷����Ὣ��ѯ������һ��һ����ָ��������װ��List������
	//��ʱʹ�÷���T��ʾҪ�����Ķ���
	private <T> List<T> executeQuery(String sql,Class<T> template,Object...params) {
		List<T> ts = new ArrayList<T>();
		try {
			//1 ��������jar
			//2��������
			Class.forName(p.getProperty("driver"));
			//3��������
			Connection conn = DriverManager.getConnection(
					p.getProperty("url"),
					p.getProperty("username"),
					p.getProperty("password")
			);
			//4 �������
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++) {
				stmt.setObject(i+1, params[i]);
			}
			//5ִ��sql
			ResultSet rs = stmt.executeQuery();
			//5.5 ���������(������е�����ȡ�����������Ҫ������,List,User��)
			
			while(rs.next()) {
				T obj = null ;
				if(template == long.class || template == Long.class ||template == int.class || template == Integer.class || template == String.class) {
					//��ѯ�ľ��ǵ�һ���͵�ֵ������Ҫ��ɸ��ӵĶ���
					obj = (T) rs.getObject(1);
				}else {
					//template.getDeclaredFields()
					obj = template.newInstance(); // User user = new User();
					Method[] ms = template.getMethods();
					/*����set�������ҵ���Ӧ�����ԡ����������ڽ�������ҵ�����ֵ��Ϊ��������Ը�ֵ��*/
					for(Method m : ms){
						String mname = m.getName() ;
						if(mname.startsWith("set")) {
							//set������  setUname() --> uname , setUpass() --> upass
							String key = mname.substring(3);//ȥ��set
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
			
			//6���ֹر�
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
			//��һ��insert���
			this.executeUpdate(sql, params);
		}else {
			throw  new SqlFormatException("not a insert sql : ["+sql+"]");
		}
	}
	public void update(String sql,Object...params){
		if(sql.trim().substring(0, 6).equalsIgnoreCase("update")) {
			//��һ��insert���
			this.executeUpdate(sql, params);
		}else {
			throw  new SqlFormatException("not a update sql : ["+sql+"]");
		}
	}
	public void delete(String sql,Object...params){
		if(sql.trim().substring(0, 6).equalsIgnoreCase("delete")) {
			//��һ��insert���
			this.executeUpdate(sql, params);
		}else {
			throw  new SqlFormatException("not a delete sql : ["+sql+"]");
		}
	}
	public <T> List<T> select(String sql,Class<T> template , Object...params){
		if(sql.trim().substring(0, 6).equalsIgnoreCase("select")) {
			//��һ��insert���
			return this.executeQuery(sql, template,params);
		}else {
			throw  new SqlFormatException("not a select sql : ["+sql+"]");
		}
	}
	
	
	
	
}
