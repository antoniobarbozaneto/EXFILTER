package com.lipogramsw.exfilter;
/*
 Copyright 2021 Arnaldo Tramontano Filho

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
---
This code also contains or uses software under Apache License v2.0.
			DO NOT REMOVE COPYRIGHT ATTRIBUTIONS.
FOR CONTACT: Arnaldo Tramontano Filho (arnaldo.tramontano@gmail.com)
 */
 

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.lipogramsw.filemanager.ZipHandler;
import com.lipogramsw.sendmail.SendMail;


public class Exfilter
{
  static Options options;
  static CommandLine cmd = null;

  
  public static void main(String[] args) throws Exception 
  {
    DefaultParser defaultParser = new DefaultParser();    
    initOptions();

    
    
    try 
    {
      cmd = defaultParser.parse(options, args);
    }
    catch (Exception e) 
    {
      showHelp(e, options);
      System.exit(4);
    }
    
    // DO NOT REMOVE THIS CODE
    if (cmd.hasOption(ExConst.PARAM_INFORMATION))
    {
    	showInfo();
        System.exit(0);
    } 
    else
    {
        LogHandler.getInstance().writeLog("Use option --info for licensing and copyright information.");
    }
    
    if (cmd.hasOption(ExConst.PARAM_LOGID))
    {
    	LogHandler.getInstance().setLogID(cmd.getOptionValue(ExConst.PARAM_LOGID));
    }

    if (cmd.hasOption(ExConst.PARAM_TIMESTAMP))
    {
    	LogHandler.getInstance().setTimeStamp(true);
    }
    else
    {
    	LogHandler.getInstance().setTimeStamp(false);
    }
    
    if (cmd.hasOption(ExConst.PARAM_OPERATION))
    {
    	if (cmd.getOptionValue(ExConst.PARAM_OPERATION, null) == null )
    	{
    		showHelp(new Exception("ERROR: No operation provided!"), options);
    	    System.exit(4);
    	}
    	
    	// Create a ZIP FILE NAME to be used when no name is provided. The idea is that if no name is
    	// provided, the system can move on with a self created name.
    	String zipFileName = "ZIPFILE_" + (new SimpleDateFormat("yyyy-MM-dd_HHmmss")).format(Long.valueOf(System.currentTimeMillis())) + ".zip";
    	zipFileName = cmd.getOptionValue(ExConst.PARAM_ZIP_OUTPUT, zipFileName);
    	
    	// ZIP FILES
    	if (cmd.getOptionValue(ExConst.PARAM_OPERATION, "").equalsIgnoreCase(ExConst.PARAM_OPERATION_ZIP) ||
    			cmd.getOptionValue(ExConst.PARAM_OPERATION, "").equalsIgnoreCase(ExConst.PARAM_OPERATION_ZIPSEND))
    	{
    		LogHandler.getInstance().writeLog("Starting ZIP operation, looking for files...");

    		// If compressing a single/list of files
    		if (cmd.hasOption(ExConst.PARAM_ZIP_FILE))
    		{
    			LogHandler.getInstance().writeLog("ZIP will compress specific files.");
    			
	    		// Get a list of filename parameters
	    		String[] fileNames = cmd.getOptionValues(ExConst.PARAM_ZIP_FILE);
	    		// Start the ZIP Handler and process.
	    		ZipHandler zipHandler = new ZipHandler();
	    		zipHandler.zipFile(fileNames,  zipFileName);
    		}
    		
    		// If compressing a whole folder
    		if (cmd.hasOption(ExConst.PARAM_ZIP_FOLDER))
    		{
    			
    			if (cmd.hasOption(ExConst.PARAM_ZIP_REGEX))
    			{
        			LogHandler.getInstance().writeLog("ZIP will compress all files in '" + cmd.getOptionValue(ExConst.PARAM_ZIP_FOLDER, "") + "' that matches RegEX '" + cmd.getOptionValue(ExConst.PARAM_ZIP_REGEX, "(.*)") + "'");

    				// Start the ZIP Handler and process.
    	    		ZipHandler zipHandler = new ZipHandler();
    	    		zipHandler.zipFolderRegex(cmd.getOptionValue(ExConst.PARAM_ZIP_FOLDER, ""), cmd.getOptionValue(ExConst.PARAM_ZIP_REGEX, "(.*)"), zipFileName);
    			}
    			else
    			{
        			LogHandler.getInstance().writeLog("ZIP will compress all files in '" + cmd.getOptionValue(ExConst.PARAM_ZIP_FOLDER, "") + "'");

    	    		// Start the ZIP Handler and process.
    	    		ZipHandler zipHandler = new ZipHandler();
    	    		zipHandler.zipFolder(cmd.getOptionValue(ExConst.PARAM_ZIP_FOLDER, ""), zipFileName);
    			}
    		}
    		
    		
    		
    	}
    	
    	
    	
    	// SEND MESSAGE VIA EMAIL
    	if (cmd.getOptionValue(ExConst.PARAM_OPERATION, "").equalsIgnoreCase(ExConst.PARAM_OPERATION_SEND) ||
    			cmd.getOptionValue(ExConst.PARAM_OPERATION, "").equalsIgnoreCase(ExConst.PARAM_OPERATION_ZIPSEND))
    	{
    		LogHandler.getInstance().writeLog("Starting SEND operation, looking for channel...");
    		
    		if (cmd.hasOption(ExConst.PARAM_CHANNEL))
    		{
    	    	if (cmd.getOptionValue(ExConst.PARAM_CHANNEL, null) == null )
    	    	{
    	    		showHelp(new Exception("ERROR: No send channel provided!"), options);
    	    	    System.exit(4);
    	    	}
    			
    	    	if (cmd.getOptionValue(ExConst.PARAM_CHANNEL, "").equalsIgnoreCase(ExConst.PARAM_CHANNEL_MAIL))
    	    	{
    	    		LogHandler.getInstance().writeLog("Channel is 'MAIL', setting parameters...");
    	    		
    	    		SendMail newMail = new SendMail();
    	    		
    	    		// Show SMTP debug messages 
    	    		if (cmd.hasOption(ExConst.PARAM_MAIL_SMTP_DEBUG))
    	    		{
    	    			newMail.setSmtpDebug(true);
    	    		}
    	    		
    	    		newMail.setServer(cmd.getOptionValue(ExConst.PARAM_MAIL_SERVER, null));
    	    		newMail.setPort(cmd.getOptionValue(ExConst.PARAM_MAIL_PORT, "25"));
    	    		newMail.setUser(cmd.getOptionValue(ExConst.PARAM_USER, null));
    	    		newMail.setPasswd(cmd.getOptionValue(ExConst.PARAM_PASS, null));
    	    		newMail.setMailFrom(cmd.getOptionValue(ExConst.PARAM_MAIL_FROM, null));
    	    		newMail.setMailTo(cmd.getOptionValues(ExConst.PARAM_MAIL_TO));
    	    		newMail.setSubject(cmd.getOptionValue(ExConst.PARAM_MAIL_SUBJECT, ExConst.DEFAULT_SUBJECT));
    	    		newMail.setBody(cmd.getOptionValue(ExConst.PARAM_MAIL_BODY, ExConst.DEFAULT_BODY));
    	    		
    	    		// Should use STARTTLS for auth?
    	    		if (cmd.hasOption(ExConst.PARAM_MAIL_STARTTLS))
    	    		{
    	    			newMail.setStartTLS(true);
    	    		}
    	    		
    	    		// Should use SSL for auth?
    	    		if (cmd.hasOption(ExConst.PARAM_MAIL_SSL))
    	    		{
    	    			newMail.setSSL(true);
    	    		}
    	    		
    	    		// Is there any body file to use?
    	    		if (cmd.hasOption(ExConst.PARAM_MAIL_BODYFILE)) 
    	    		{
    	    			LogHandler.getInstance().writeLog("Will use message body file '" + cmd.getOptionValue(ExConst.PARAM_MAIL_BODYFILE, "") + "'");
    	    			
    	    			// TODO: Check if file exists
    	    			
    	    			try 
    	    			{
    	    				// Read file in UTF-8 and set the message body to its contents
    	    				byte[] encoded = Files.readAllBytes(Paths.get(cmd.getOptionValue(ExConst.PARAM_MAIL_BODYFILE, "")));
    	    				newMail.setBody(new String(encoded, StandardCharsets.UTF_8));
    	    			}
    	    			catch(Exception e)
    	    			{
    	    				// Error while reading file.
    	    				LogHandler.getInstance().writeLog("ERROR: unable to read message body file");
            	    		System.exit(4);
    	    			}
    	    		}
    	    		
    	    		// Is there any file to be attached?
    	    		if (cmd.hasOption(ExConst.PARAM_MAIL_ATTACH)) 
    	    		{
    	    			LogHandler.getInstance().writeLog("Will attach specific files.");

	    	    		// Get a list of filename parameters to be attached
	    	    		String[] fileAttachNames = cmd.getOptionValues(ExConst.PARAM_MAIL_ATTACH);
	    	    		// Set the filenames
    	    			newMail.setFileAttach(fileAttachNames);
    	    		}
    	    		
    	    		// Must attach the newly zip file (created by ZIPSEND)?
    	    		if (cmd.hasOption(ExConst.PARAM_MAIL_ATTACHZIP) && cmd.getOptionValue(ExConst.PARAM_OPERATION, "").equalsIgnoreCase(ExConst.PARAM_OPERATION_ZIPSEND)) 
    	    		{
    	    			LogHandler.getInstance().writeLog("Will automaticallly attach ZIP file '" + zipFileName + "'");
    	    			String[] fileAttachNames = {zipFileName};
    	    			
    	    			newMail.setFileAttach(fileAttachNames);
    	    		}
    	    		
    	    		
    	    		// Sends the message:
    	    		if (newMail.send())
    	    		{
    	    			// Success 
        	    		LogHandler.getInstance().writeLog("Message successfully sent.");
    	    		}
    	    		else
    	    		{
    	    			// If anything fails, log and leave with errorlevel 4. 
        	    		LogHandler.getInstance().writeLog("ERROR: message was NOT sent.");
        	    		System.exit(4);
    	    		}
    	    		
    	    		// What to do next?
    	    		if (cmd.hasOption(ExConst.PARAM_MAIL_AFTER))
    	    		{
    	    			LogHandler.getInstance().writeLog("WARNING: Next action parameters are unused in this version.");
    	    			
    	    			/*
    	    			if (cmd.getOptionValue(ExConst.PARAM_MAIL_AFTER, "").equalsIgnoreCase(ExConst.PARAM_MAIL_AFTER_DELETE))
    	    			{
    	    				// Option is to delete the attached file.
    	    				LogHandler.getInstance().writeLog("Next action: delete attached file.");
    	    				
    	    				// TODO: Delete file.
    	    				
    	    				System.exit(0);
    	    			}
    	    			*/
    	    		}
    	    		System.exit(0);
    	    	}
    			
    	    	if (cmd.getOptionValue(ExConst.PARAM_CHANNEL, "").equalsIgnoreCase(ExConst.PARAM_CHANNEL_POST))
    	    	{
    	    		LogHandler.getInstance().writeLog("- Channel is 'HTTP POST', checking parameters...");


    	    		LogHandler.getInstance().writeLog("Finished with status 0 (success).");
    	    		System.exit(0);
    	    	}
    		}
    		else
    		{
    			showHelp(new Exception("ERROR: No send channel provided!"), options);
	    	    System.exit(4);
    		}
    		LogHandler.getInstance().writeLog("Finished with status 4 (error).");
    		System.exit(4);
    	}

    }
   
  }
  
  private static void initOptions() 
  {
    options = new Options();
    options.addOption(null, ExConst.PARAM_INFORMATION, false, "Show information about this program");

    options.addRequiredOption(null, ExConst.PARAM_OPERATION, true, "Operation type ('zip', 'send', 'zipsend')");
    options.addOption(null, ExConst.PARAM_CHANNEL, true, "Send channel (only if OPERATION is SEND; 'email' on this version)");

    // LOGIN and PASSWORD for both EMAIL or POST
    options.addOption(null, ExConst.PARAM_USER, true, "User name for authentication");
    options.addOption(null, ExConst.PARAM_PASS, true, "User password for authentication");
    

    // MAIL channel options
    options.addOption(null, ExConst.PARAM_MAIL_SERVER, true, "SMTP server address");
    options.addOption(null, ExConst.PARAM_MAIL_PORT, true, "SMTP server port (dafault is 25)");
    options.addOption(null, ExConst.PARAM_MAIL_STARTTLS, false, "SMTP server use STARTTLS (default is FALSE");
    options.addOption(null, ExConst.PARAM_MAIL_SSL, false, "SMTP server use SSL (default is FALSE)");
    options.addOption(null, ExConst.PARAM_MAIL_FROM, true, "'FROM' address");
    options.addOption(null, ExConst.PARAM_MAIL_TO, true, "'TO' address (may use multiple parameters)");
    options.addOption(null, ExConst.PARAM_MAIL_SUBJECT, true, "Message subject");
    options.addOption(null, ExConst.PARAM_MAIL_BODY, true, "Message body (inline, quote bounded)");
    options.addOption(null, ExConst.PARAM_MAIL_BODYFILE, true, "Message body (text file, UTF-8 encoded)");
    options.addOption(null, ExConst.PARAM_MAIL_ATTACH, true, "File to be attached, if any (multiple allowed)");
    options.addOption(null, ExConst.PARAM_MAIL_ATTACHZIP, false, "Attach the newly created ZIP (only when 'ZIPSEND')");
    options.addOption(null, ExConst.PARAM_MAIL_SMTP_DEBUG, false, "Show SMTP debug messages");
    //options.addOption(null, ExConst.PARAM_MAIL_AFTER, true, "What to do with attached file after ('delete')");

    // ZIP handling options
    options.addOption(null, ExConst.PARAM_ZIP_FILE, true, "Input file to be compressed (multiple allowed)");
    options.addOption(null, ExConst.PARAM_ZIP_FOLDER, true, "Input folder to be compressed (with subfolders)");
    options.addOption(null, ExConst.PARAM_ZIP_REGEX, true, "Regular Expression to match filenames (needs ZFOLDER)");
    options.addOption(null, ExConst.PARAM_ZIP_OUTPUT, true, "Output file (zip compressed)");
    
    // Logging functionalities
    options.addOption(null, ExConst.PARAM_LOGID, true, "General use string for log identification");
    options.addOption(null, ExConst.PARAM_TIMESTAMP, false, "Use timestamp in log messages");
    
  }

  private static void showHelp(Exception e, Options o) 
  {
    HelpFormatter formatter = new HelpFormatter();
    System.out.println("Java Exfilter Tools\n");
    System.out.println(e.getMessage());
    formatter.printHelp("exfilter", "Exfilters and manipulates integration files. ", o, "arnaldo.tramontano@gmail.com", true);
  }
 
  // DO NOT MAKE CHANGES IN CODE BELOW
  private static void showInfo()
  {
	  System.out.println("Java Exfilter Tools");
	  System.out.println("");
	  System.out.println("Copyright 2021 Arnaldo Tramontano Filho");
      System.out.println("");
      System.out.println("Licensed under the Apache License, Version 2.0 (the \"License\");");
      System.out.println("you may not use this file except in compliance with the License.");
      System.out.println("You may obtain a copy of the License at");
      System.out.println("");
      System.out.println("    http://www.apache.org/licenses/LICENSE-2.0");
      System.out.println("");
      System.out.println("Unless required by applicable law or agreed to in writing, software");
      System.out.println("distributed under the License is distributed on an \"AS IS\" BASIS,");
      System.out.println("WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
      System.out.println("See the License for the specific language governing permissions and");
      System.out.println("limitations under the License.");
      System.out.println("");
  }
  
}
