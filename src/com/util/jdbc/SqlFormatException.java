package com.util.jdbc;

/**
 * ��ִ�е�sql������sql������ƥ��ʱ�׳����쳣
 * delete("insert....")
 * @author Administrator
 *
 */
public class SqlFormatException extends RuntimeException {
	public SqlFormatException() {}
	
	public SqlFormatException(String msg) {
		super(msg);
	}
}
