package com.milesbone.queue.exception;

public class QueueOperateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8261453089086010792L;
	
	private String sysName;
	private int errTypeCode;
	private int errInfoCode;

	public QueueOperateException() {
		super();
	}

	public QueueOperateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public QueueOperateException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueueOperateException(String message) {
		super(message);
	}

	public QueueOperateException(Throwable cause) {
		super(cause);
	}

	
	
	public QueueOperateException(String sysName, int errTypeCode, int errInfoCode) {
		super();
		this.sysName = sysName;
		this.errTypeCode = errTypeCode;
		this.errInfoCode = errInfoCode;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public int getErrTypeCode() {
		return errTypeCode;
	}

	public void setErrTypeCode(int errTypeCode) {
		this.errTypeCode = errTypeCode;
	}

	public int getErrInfoCode() {
		return errInfoCode;
	}

	public void setErrInfoCode(int errInfoCode) {
		this.errInfoCode = errInfoCode;
	}

	
	
}
