package com.util.jdbc;

/**
 * 当执行的sql方法与sql操作不匹配时抛出该异常
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
