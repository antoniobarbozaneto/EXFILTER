package com.lipogramsw.exfilter;

public class ExConst {

	public static String PARAM_INFORMATION = "info";				// DO NOT REMOVE
	
	public static String PARAM_OPERATION = "operation";
	public static String PARAM_OPERATION_ZIP = "zip";				// Zip files
	public static String PARAM_OPERATION_SEND = "send";				// Send using 'CHANNEL'
	public static String PARAM_OPERATION_ZIPSEND = "zipsend";		// Zip filed and send using 'CHANNEL'
	

	public static String PARAM_CHANNEL = "channel";		
	public static String PARAM_CHANNEL_MAIL = "email";				// Send email message
	public static String PARAM_CHANNEL_POST = "post";				// Post to a webservice (unused)
	public static String PARAM_CHANNEL_FTP = "ftp";					// FTP send (unused)
	
	public static String PARAM_MAIL_SERVER = "mserver";
	public static String PARAM_MAIL_PORT = "mport";
	public static String PARAM_MAIL_STARTTLS = "mstarttls";			// Use STARTTLS
	public static String PARAM_MAIL_SSL = "mssl";					// Use SSL
	
	public static String PARAM_MAIL_FROM = "mfrom";
	public static String PARAM_MAIL_TO = "mto";
	public static String PARAM_MAIL_SUBJECT = "msubject";
	public static String PARAM_MAIL_BODY = "mbody";
	public static String PARAM_MAIL_BODYFILE = "mbodyfile";
	public static String PARAM_MAIL_ATTACH = "mattach";
	public static String PARAM_MAIL_ATTACHZIP = "mattachzip";		// Flag to attach the ZIP created before (only with ZIPSEND)
	public static String PARAM_MAIL_AFTER = "mafter";				// What to do after send message (unused)
	public static String PARAM_MAIL_SMTP_DEBUG = "mdebug";			// Option for MAIL SMTP DEBUG
	//public static String PARAM_MAIL_AFTER_DELETE = "delete";		// Option for MAIL AFTER
	
	public static String PARAM_USER = "user";
	public static String PARAM_PASS = "pass";
	
	
	public static String PARAM_ZIP_FILE = "zfile";					// Single input file 
	public static String PARAM_ZIP_FOLDER = "zfolder";				// Whole folder
	public static String PARAM_ZIP_REGEX = "zregex";				// File pattern (RegEx)
	
	public static String PARAM_ZIP_OUTPUT = "zoutput";				// ZIP file output
	
	public static String PARAM_ZIP_AFTER = "zafter";				// What to do after zip files (unused)
	public static String PARAM_ZIP_AFTER_DELETE = "delete";			// After ZIP: delete originals
	public static String PARAM_ZIP_AFTER_MOVE = "move"; 			// After ZIP: move originals
	
	public static String PARAM_LOGID = "log-id";
	public static String PARAM_TIMESTAMP = "log-timestamp";
	
	
	public static String DEFAULT_SUBJECT = "OSGT System Message";
	public static String DEFAULT_BODY = "[Automatic Generated Message]";
	
	


	

	
	
	
	
}
