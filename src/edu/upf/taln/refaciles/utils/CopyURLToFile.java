/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.refaciles.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author UPF
 */
public class CopyURLToFile {
    
    
    
    public static void main(String[] args) {

 

try {
    
    
 saveFileFromUrlWithJavaIO("C:\\work\\programs\\AIW2017P2\\output\\ElPais_ReFacilEs\\test.mp4",
         "http://able2include.teamnet.ro:8090//AudioFiles//khek8d2qgqrmic8s.mp4");
 
 saveFileFromUrlWithJavaIO("C:\\work\\programs\\AIW2017P2\\src\\utils\\test.mp4",
         "http://able2include.teamnet.ro:8090/AudioFiles/all4vfd6e8f9fq1f.mp4");
         
} catch(Exception e) {
    e.printStackTrace();
}
}

// Using Java IO
 public static void saveFileFromUrlWithJavaIO(String fileName, String fileUrl)
 throws MalformedURLException, IOException {
 BufferedInputStream in = null;
 FileOutputStream fout = null;
 try {
 in = new BufferedInputStream(new URL(fileUrl).openStream());
 fout = new FileOutputStream(fileName);

byte data[] = new byte[1024];
 int count;
 while ((count = in.read(data, 0, 1024)) != -1) {
 fout.write(data, 0, count);
 }
 } finally {
 if (in != null)
 in.close();
 if (fout != null)
 fout.close();
 }
 }

// Using Commons IO library
 // Available at http://commons.apache.org/io/download_io.cgi
 public static void saveFileFromUrlWithCommonsIO(String fileName,
 String fileUrl) throws MalformedURLException, IOException {
 FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
 }

}

    
