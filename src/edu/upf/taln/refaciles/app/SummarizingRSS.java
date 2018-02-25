/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.refaciles.app;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import gate.*;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.OffsetComparator;
import gate.util.persistence.PersistenceManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import edu.upf.taln.refaciles.services.HttpCallServices;
import static edu.upf.taln.refaciles.utils.CopyURLToFile.saveFileFromUrlWithJavaIO;

/**
 *
 * @author UPF
 */
public class SummarizingRSS {
    
    
    
  
    public static String gappToTest="MySUMMA.gapp";
    public static String splitGapp="SplitAndTok.gapp";
    // this is the controller to load the GAPP
    public static CorpusController application;
    public static CorpusController splitting;
   
    
    public static String getHead() {
        String head="";
        head="<!DOCTYPE html>\n" +
"<html>\n" +
"<head><meta charset=\"ISO-8859-1\"><link rel=\"stylesheet\" href=\"style.css\" />\n" +
"    \n" +
"     <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" +
"  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script> \n" +
"   <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">  \n" +
"  <title>ReFácilEs</title></head>\n" +
"\n" +
"<body>\n" +
"\n" +
"    <h1><img src=\"facil_lectura.png\" width=60  height=60 alt=\"Fácil Lectura\"/> ReFácilEs"  +  
                "<img src=\"elpais.png\" width=60  height=60 alt=\"Noticias de El País\"/> </h1>"+
                "<h2>Resúmenes en lectura fácil en español</h2>\n" +
"";
        return head;
    }
    
    public static String getFoot() {
      String foot= "<p>ReFácilEs es posible gracias a:</p> <footer> "+
        
    "<img src=\"taln.jpg\" width=90  height=40 alt=\"TALN research group\"/>" +
    "<img src=\"summa.png\" width=70  height=40 alt=\"SUMMA summarization toolkit\"/>" +
    "<img src=\"simplext.png\" width=70  height=40 alt=\"The Simplext Project\"/>" +
    "<img src=\"able.png\" width=70  height=40 alt=\"The Able to Include Project\"/></footer>" +
   "</body>";
       
        return foot;
    }
    
     // creates "collapsable" section in web page
     // the section contains an image, the headline, and the sentences
     // audio files are also integrated
     public static String createSection1(String linkImage,
            String headline, String readHead,
            ArrayList<String> sentences, ArrayList<String> readSents, String refID,
            String link) {
            String corrected;
            String section="<article> \n" +
"            <img src=\"" + linkImage + "\" alt=\"\" width=\"60\" height=\"60\" />\n" +
"            <a href=\"#demo" + refID + "\" class=\"xxx\" data-toggle=\"collapse\">";
            // add headline
            corrected=headline;
            if(StringUtils.countMatches("?", corrected)>=2) {
                System.out.println("MATCH");
                corrected=corrected.replaceAll("\\?", "\"");
            } 
            section=section+corrected;
            section=section+"</a> <video src=\" "+ readHead + "\" width=32  height=24 controls poster=\"sound.jpg \">\n" +
"Lo sentimos. Este sonido no puede ser reproducido en tu navegador.<br>\n" +
"      </video> ";
            section=section+"<div id=\"demo" + refID +"\" class=\"collapse\">\n" +
"      <p>";
            
            
            for(int i=0;i<sentences.size(); i++) {
                    corrected=sentences.get(i);
                    if(StringUtils.countMatches("?", corrected)>=2) {
                        corrected=corrected.replaceAll("\\?", "\"");
                    } 
                    section=section+corrected;
                    section=section+"</a> <video src=\""+ readSents.get(i)+ "\" width=32  height=24 controls poster=\"sound.jpg\"  \">\n" +
        "Lo sentimos. Este sonido no puede ser reproducido en tu navegador.<br>\n" +
        "      </video> ";
                    section=section + "</p>";
                    section=section + "<p>";
           
            }
            section = section + "<a target=\"_blank\" href=\""+link+"\"> Noticia completa </a>";
            section = section + "</article>";
            return section;
    }
    
    public static String createSection(String linkImage,
            
            String headline, String readHead,
            String[] sentences, String[] readSents, String refID) {
            String section="<article> \n" +
"            <img src=\"" + linkImage + "\" alt=\"\" width=\"60\" height=\"60\" />\n" +
"            <a href=\"#demo" + refID + "\" class=\"xxx\" data-toggle=\"collapse\">";
            // add headline
            section=section+headline;
            section=section+"</a> <video src=\" "+ readHead + "\" width=32  height=24 controls poster=\"sound.jpg \">\n" +
"Lo sentimos. Este sonido no puede ser reproducido en tu navegador.<br>\n" +
"      </video> ";
            section=section+"<div id=\"demo" + refID +"\" class=\"collapse\">\n" +
"      <p>";
            section=section+sentences[0];
            section=section+"</a> <video src=\""+ readSents[0]+ "\" width=32  height=24 controls poster=\"sound.jpg\"  \">\n" +
"Lo sentimos. Este sonido no puede ser reproducido en tu navegador.<br>\n" +
"      </video> ";
            section=section + "</p>";
            section=section + "<p>";
            section=section + sentences[1];
            section=section+"</a> <video src=\""+readSents[1]+"\" width=32  height=24 controls poster=\"sound.jpg\"  \">\n" +
"Lo sentimos. Este sonido no puede ser reproducido en tu navegador.<br>\n" +
"      </video> ";
            section=section + "</p>";
            section = section + "</article>";
            return section;
    }
    
    
    
    // Takes a "simplified" text and extracts each sentence 
    public static ArrayList<String> getSimpleSentences(String simpleText)  {
        Document simpleDoc;
        Corpus simpleCor;
        ArrayList<String> simpSentList=new ArrayList();  
        AnnotationSet sentences;
        ArrayList<Annotation> sentList;
        String sentContent;
        Annotation sentence;
        Long start, end;
        String dc;
        try {
            simpleCor=Factory.newCorpus("");
            simpleDoc=Factory.newDocument(simpleText);
            dc=simpleDoc.getContent().toString();
            simpleCor.add(simpleDoc);
            splitting.setCorpus(simpleCor);
            splitting.execute();
            sentences=simpleDoc.getAnnotations().get("Sentence");
            sentList=new ArrayList(sentences);
            Collections.sort(sentList, new OffsetComparator());
            for(int s=0;s<sentList.size();s++) {
                sentence=sentList.get(s);
                start=sentence.getStartNode().getOffset();
                end  =sentence.getEndNode().getOffset();
                simpSentList.add(dc.substring(start.intValue(), end.intValue()));
                
            }
            
        } catch (ResourceInstantiationException ex) {
            Logger.getLogger(SummarizingRSS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(SummarizingRSS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        return simpSentList;
    }
   
    // how many items in the page
    public static int TOP=5;
    // how many sentences in summatry
    public static int SUM=2;
    // Generate the web site
   
    public static void main(String[] args) {
        try {
         

           
            
            URL url;
            String rssSite;
            XmlReader reader = null;
            Document doc;
            Corpus corpus;
            
            
            HttpCallServices http = new HttpCallServices();
            
            
            
            OutputStreamWriter osw;
       
            File fout=new File("."+File.separator+"output"+File.separator+"ElPais_ReFacilEs"+File.separator+"refaciles_el_pais_9.html");
            File fout1=new File("."+File.separator+"output"+File.separator+"ElPais_Standard"+File.separator+"refaciles_el_pais_9.html");
            File dirFacil=new File("."+File.separator+"output"+File.separator+"ElPais_ReFacilEs");
            File dirStandard=new File("."+File.separator+"output"+File.separator+"ElPais_Standard");
            
            // clean mp4 files 
            
            File[] flist=dirFacil.listFiles();
            for(int f=0;f<flist.length;f++) {
                if(flist[f].getName().endsWith("mp4")) {
                    System.out.println(flist[f].getName());
                    flist[f].delete();
                }
                
            }
            
            File[] flist1=dirStandard.listFiles();
            for(int f=0;f<flist1.length;f++) {
                
                if(flist1[f].getName().endsWith("mp4")) {
                    System.out.println(flist1[f].getName());
                    flist1[f].delete();
                }
                
            }
            
            OutputStreamWriter osw1;
       
         
           
            String header=getHead();
            String footer=getFoot();
            
            FileOutputStream writer=new FileOutputStream(fout);
            osw=new OutputStreamWriter(writer,"ISO-8859-1");
      
            osw.append(header+"\n");
            osw.flush();
            
            FileOutputStream writer1=new FileOutputStream(fout1);
            osw1=new OutputStreamWriter(writer1,"ISO-8859-1");
      
            osw1.append(header+"\n");
            osw1.flush();
            
            String title;
            String link;
            
            JSONObject simpleObject;
            
            try {
                Gate.init();
                
                // load the GAPP
                application =
                        (CorpusController)
                        PersistenceManager.loadObjectFromFile(new
                             File("."+File.separator+"gapps"+File.separator+gappToTest));
                 splitting =
                        (CorpusController)
                        PersistenceManager.loadObjectFromFile(new
                             File("."+File.separator+"gapps"+File.separator+splitGapp));
                
               
                // News provides to target: the latest news from ElPais
                rssSite="http://ep00.epimg.net/rss/tags/ultimas_noticias.xml";
            
           
                
                url  = new URL(rssSite);
                reader = new XmlReader(url);
                SyndFeed feed = new SyndFeedInput().build(reader);
                System.out.println("Feed Title: "+ feed.getTitle());
                //  The corpus to store the document
                corpus=Factory.newCorpus("");
                int count=0;
                Iterator i=feed.getEntries().iterator();
                Document cleanDocument;
                 Iterator iteEnclosures;
                SyndEnclosure enclosure;
                boolean found;
                String linkImage;
                String headline;
                String response;
                String headSound;
                JSONObject jsonSpeech; 
                String headlineFile;
                
                String sentSound;
                String sentlineFile;
                String original, simplified;
                ArrayList<String> simpSentList;
                ArrayList<String> allSimpSentList;
                ArrayList<String> speechFileList;
                ArrayList<String> allSpeechFileList;
                
                ArrayList<String> allSentList;
                ArrayList<String> allSpeechFullFileList;
                // get up to TOP news items which satisfy certain conditions
                while(i.hasNext() && count<TOP) {        
                   
                    SyndEntry entry = (SyndEntry) i.next();
                    title=entry.getTitle();
                    
                    SyndContent c;
                    
                    link=entry.getLink();
                    
                    
                    // extract image for this news
                    // the code is customized to ElPais
                    
                    iteEnclosures=entry.getEnclosures().iterator();
                    found=false;
                    linkImage="elpais.png";
                    while(iteEnclosures.hasNext() & !found) {
                        enclosure=(SyndEnclosure)iteEnclosures.next();
                        if(enclosure.getType().contains("image")) {
                            linkImage=enclosure.getUrl();
                            found=true;
                        }
            
                     }
                    
                    System.out.println(title);
                    System.out.println(link);
                    doc=Factory.newDocument(new URL(link),"UTF-8");
                    // some titles may contain characters which are wrongly displayed 
                    // replacing them wont affect the content
                    if(title.contains("‘")) {
                        title=title.replaceAll("‘", "\"");
                    }
                     if(title.contains("’")) {
                        title=title.replaceAll("’", "\"");
                    }
                    headline=title;
                    
                    response=http.sendGetSpeech(headline, "es");
                  
                    jsonSpeech=new JSONObject(response); 
                    headSound=(String)jsonSpeech.get("audioSpeech");
                    headlineFile="headLine"+count+".mp4";
                    saveFileFromUrlWithJavaIO(dirFacil.toString()+File.separator+headlineFile,
                headSound);
                    saveFileFromUrlWithJavaIO(dirStandard.getCanonicalPath()+File.separator+headlineFile,
                headSound);
         
                    headSound=headlineFile;
                    
                    
                    
                  
                    
                    
                    
                    
                    cleanDocument=Factory.newDocument(getParagraphs(doc));

                    corpus.add(cleanDocument);
                    application.setCorpus(corpus);
                    application.execute();
                    System.out.println("*** SUMMARY ***");
                    System.out.println(getSummary(cleanDocument));
                    System.out.println("***************");
                    
                    String[] sentences=new String[SUM];
                    
                    String[] summary=getSummarySentences(cleanDocument);
                    for(int s=0;s<SUM; s++) {
                        sentences[s]="";
                   
                    }
                    if(summary.length>=SUM){
                        allSimpSentList=new ArrayList();
                        allSpeechFileList=new ArrayList();
                        for(int sum=0;sum<SUM;sum++) {
                            sentences[sum]=http.sendGet(summary[sum],"es");

                            sentences[sum]=sentences[sum].replace("callback(", "");
                            sentences[sum]=sentences[sum].substring(0, sentences[sum].length()-1);

                            simpleObject=new JSONObject(sentences[sum]); 
                            sentences[sum]=(String)simpleObject.get("simplifiedText");

                               simpSentList=getSimpleSentences(sentences[sum]);
                               allSimpSentList.addAll(simpSentList);
                               speechFileList=new ArrayList();
                               
                               for(int s=0;s<simpSentList.size();s++) {
                                    response=http.sendGetSpeech(simpSentList.get(s), "es");

                                    jsonSpeech=new JSONObject(response); 
                                    sentSound=(String)jsonSpeech.get("audioSpeech");
                                    sentlineFile="sentence_"+count+"_"+sum+"_"+s+".mp4";
                                    speechFileList.add(sentlineFile);
                                    try {
                                    saveFileFromUrlWithJavaIO(dirFacil.toString()+File.separator+sentlineFile,
                                sentSound);
                                    }  catch(IOException e) {
                                        e.printStackTrace();
                                        continue;
                                    }
                                    
                               }
                               allSpeechFileList.addAll(speechFileList);
                              
                        }
                        osw.write(createSection1(linkImage,headline,headSound,allSimpSentList,allSpeechFileList,count+"",link));



                        osw.flush();
                        
                        allSentList=new ArrayList();
                        for(int s=0;s<SUM; s++) {
                             allSentList.add(summary[s]);
                        }
                      
                        allSpeechFullFileList=new ArrayList();
                        for(int s=0;s<allSentList.size();s++) {
                                    response=http.sendGetSpeech(allSentList.get(s), "es");

                                    jsonSpeech=new JSONObject(response); 
                                    sentSound=(String)jsonSpeech.get("audioSpeech");
                                    sentlineFile="sentence_"+count+"_"+s+".mp4";
                                    allSpeechFullFileList.add(sentlineFile);
                                    try {
                                    saveFileFromUrlWithJavaIO(dirStandard.toString()+File.separator+sentlineFile,
                                sentSound);
                                    } catch(IOException e) {
                                        e.printStackTrace();
                                        continue;
                                    }
                                    
                        }
                        
                        
                        
                        osw1.write(createSection1(linkImage,headline,headSound,allSentList,allSpeechFullFileList,count+"",link));



                        osw1.flush();
                        
                        
                        count++;
                        
                    }
                    
                    Factory.deleteResource(doc);
                    
                    Factory.deleteResource(cleanDocument);
                }
                
                osw.write(footer);
                osw.flush();
                osw.close();
                
                osw1.write(footer);
                osw1.flush();
                osw1.close();
                
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (FeedException ex) {
               ex.printStackTrace();
            } catch (ExecutionException ex) {
               ex.printStackTrace();
            } catch (PersistenceException ex) {
                  ex.printStackTrace();
            } catch (IOException ex) {
                  ex.printStackTrace();
            } catch (ResourceInstantiationException ex) {
                  ex.printStackTrace();
            } catch(GateException ge) {
                  ge.printStackTrace();
            } catch (Exception ex) {
                  ex.printStackTrace();
               
            }
            
        } catch (IOException ex) {
            Logger.getLogger(SummarizingRSS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    // get paragraphs from document 
    public static String getParagraphs(Document doc) {
        String text="";
        String line;
        AnnotationSet paras;
        paras = doc.getAnnotations("Original markups").get("p");
        String dc=doc.getContent().toString();
        Long start, end;
        ArrayList<Annotation> list=new ArrayList(paras);
        Collections.sort(list, new OffsetComparator());
        Annotation para;
        for(int i=1;i<list.size();i++) {
            para=list.get(i);
            start=para.getStartNode().getOffset();
            end=para.getEndNode().getOffset();
            line=dc.substring(start.intValue(), end.intValue());
            if(line.endsWith(".")) {
            text=text+"\n"+line;
            }
            
        }
        if(text.contains("‘")) {
                        text=text.replaceAll("‘", "\"");
                    }
         if(text.contains("‘")) {
                        text=text.replaceAll("‘", "\"");
                    }
        if(text.contains("—")) {
            text=text.replaceAll("—", "-");
        }
        if(text.contains("“")) {
                        text=text.replaceAll("“", "\"");
                    }
        if(text.contains("”")) {
                        text=text.replaceAll("”", "\"");
                    }
        
        return text;
        
    }
    // gets the summary sentences generated by the SUMMA gapp
    public static String[] getSummarySentences(Document doc) {
        String[] summary;
        String dc=doc.getContent().toString();
        AnnotationSet sentences=doc.getAnnotations("EXTRACT").get("Sentence");
        summary=new String[sentences.size()];
        // sort the annotations
        Annotation sentence;
        Long start, end;
        ArrayList<Annotation> sentList=new ArrayList(sentences);
        Collections.sort(sentList,new OffsetComparator());
        for(int s=0;s<sentList.size();s++) {
            sentence=sentList.get(s);
            start=sentence.getStartNode().getOffset();
            end  =sentence.getEndNode().getOffset();
            summary[s]=dc.substring(start.intValue(), end.intValue());
        }
        
        return summary;
    }
    // gets the summary (as a string) generated by the SUMMA gapp
    public static String getSummary(Document doc) {
        String summary="";
        String dc=doc.getContent().toString();
        AnnotationSet sentences=doc.getAnnotations("EXTRACT").get("Sentence");
        // sort the annotations
        Annotation sentence;
        Long start, end;
        ArrayList<Annotation> sentList=new ArrayList(sentences);
        Collections.sort(sentList,new OffsetComparator());
        for(int s=0;s<sentList.size();s++) {
            sentence=sentList.get(s);
            start=sentence.getStartNode().getOffset();
            end  =sentence.getEndNode().getOffset();
            summary=summary+dc.substring(start.intValue(), end.intValue())+"\n";
        }
        
        return summary;
    } 
            
    
}
