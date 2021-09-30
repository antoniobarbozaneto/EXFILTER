package com.lipogramsw.filemanager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.lipogramsw.exfilter.LogHandler;

public class ZipHandler {
	
	
	
	public void zipFile(String fileName, String zipFileName)
	{
		try
		{
			// Creates the ZIP file
			LogHandler.getInstance().writeLog("Creating ZIP file '" + zipFileName + "'...");
			final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
			
			// Compress a single file into ZIP
			LogHandler.getInstance().writeLog("Compressing file '" + fileName + "'...");
			outputStream.putNextEntry(new ZipEntry(Paths.get(fileName).getFileName().toString()));
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            outputStream.write(bytes, 0, bytes.length);
            outputStream.closeEntry();
            outputStream.close();
            LogHandler.getInstance().writeLog("File successfully created.");
		}
		catch (Exception e)
		{
			LogHandler.getInstance().writeLog("ERROR: unable to compress files.");
			System.exit(4);
		}
	}
	
	// Compress several files defined in array
	public void zipFile(String[] fileNames, String zipFileName)
	{
		try
		{
			// Creates the ZIP file
			LogHandler.getInstance().writeLog("Creating ZIP file '" + zipFileName + "'...");
			final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
			
			// Compress each file into ZIP
			for (String fileName : fileNames)
			{
				LogHandler.getInstance().writeLog("Compressing file '" + fileName + "'...");
				outputStream.putNextEntry(new ZipEntry(Paths.get(fileName).getFileName().toString()));
	            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
	            outputStream.write(bytes, 0, bytes.length);
	            outputStream.closeEntry();
			}
            outputStream.close();
            LogHandler.getInstance().writeLog("Done!");
		}
		catch (IOException e)
		{
			LogHandler.getInstance().writeLog("ERROR: unable to compress files.");
			System.exit(4);
		}
	}
	
	// Compress a whole folder into a single ZIP file
	public void zipFolder(String dirPath, String zipFileName) 
	{
        final Path sourceDir = Paths.get(dirPath);
        try 
        {
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
            LogHandler.getInstance().writeLog("Creating ZIP file '" + zipFileName + "'...");
            
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    try {
                        Path targetFile = sourceDir.relativize(file);
                        LogHandler.getInstance().writeLog("Compressing '" + targetFile.toString() + "'...");
                        
                        outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
                        byte[] bytes = Files.readAllBytes(file);
                        outputStream.write(bytes, 0, bytes.length);
                        outputStream.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            outputStream.close();
            LogHandler.getInstance().writeLog("File successfully created.");
        } catch (IOException e) {
        	LogHandler.getInstance().writeLog("ERROR: unable to compress files.");
			System.exit(4);
        }
    }
	
	// Compress files from a folder into a single ZIP file, according to REGEX to 
	// select matching file names
	public void zipFolderRegex(String dirPath, String regEx, String zipFileName) 
	{
        final Path sourceDir = Paths.get(dirPath);
        try 
        {
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
            LogHandler.getInstance().writeLog("Creating ZIP file '" + zipFileName + "'...");
            
            // Will walk only in this folder, ignoring subfolders
            Files.walkFileTree(sourceDir, new HashSet<>(), 1, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                	
                	if (file.getFileName().toString().matches(regEx))
                	{
	                    try 
	                    {
	                        Path targetFile = sourceDir.relativize(file);
	                        LogHandler.getInstance().writeLog("Compressing '" + targetFile.toString() + "'...");
	                        
	                        outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
	                        byte[] bytes = Files.readAllBytes(file);
	                        outputStream.write(bytes, 0, bytes.length);
	                        outputStream.closeEntry();
	                    } 
	                    catch (IOException e) 
	                    {
	                        e.printStackTrace();
	                    }
                	}
                    return FileVisitResult.CONTINUE;
                }
            });
            outputStream.close();
            LogHandler.getInstance().writeLog("File successfully created.");
        } catch (IOException e) {
        	LogHandler.getInstance().writeLog("ERROR: unable to compress files.");
			System.exit(4);
        }
    }

}
