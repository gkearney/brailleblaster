/* BrailleBlaster Braille Transcription Application
  *
  * Copyright (C) 2010, 2012
  * ViewPlus Technologies, Inc. www.viewplus.com
  * and
  * Abilitiessoft, Inc. www.abilitiessoft.com
  * All rights reserved
  *
  * This file may contain code borrowed from files produced by various 
  * Java development teams. These are gratefully acknoledged.
  *
  * This file is free software; you can redistribute it and/or modify it
  * under the terms of the Apache 2.0 License, as given at
  * http://www.apache.org/licenses/
  *
  * This file is distributed in the hope that it will be useful, but
  * WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
  * See the Apache 2.0 License for more details.
  *
  * You should have received a copy of the Apache 2.0 License along with 
  * this program; see the file LICENSE.txt
  * If not, see
  * http://www.apache.org/licenses/
  *
  * Maintained by John J. Boyer john.boyer@abilitiessoft.com
*/

package org.brailleblaster.document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;

import org.brailleblaster.BBIni;
import org.brailleblaster.perspectives.braille.Manager;
import org.brailleblaster.util.CheckLiblouisutdmlLog;
import org.brailleblaster.util.FileUtils;
import org.brailleblaster.util.Notify;
import org.liblouis.liblouisutdml;


public class BBDocument {
	protected Manager dm;
	protected Document doc;
	private static String fileSep = BBIni.getFileSep();
	protected liblouisutdml lutdml = liblouisutdml.getInstance();
	protected FileUtils fu = new FileUtils();
	protected static Logger logger = BBIni.getLogger();
	private ArrayList<String>missingSemanticsList;
	private String systemId;
	private String publicId;
	protected SemanticFileHandler semHandler;
	
	public BBDocument(Manager dm){		
		this.dm = dm;
		this.missingSemanticsList = new ArrayList<String>();
		this.semHandler = new SemanticFileHandler(dm.getCurrentConfig());
	}
	
	public BBDocument(Manager dm, Document doc){
		this.dm = dm;
		this.doc = doc;
		this.missingSemanticsList = new ArrayList<String>();
		this.semHandler = new SemanticFileHandler(dm.getCurrentConfig());
	}
	
	public boolean createNewDocument(){
		return startNewDocument();
	}
	
	private boolean startNewDocument(){
		String template = BBIni.getProgramDataPath() + BBIni.getFileSep() + "xmlTemplates" + BBIni.getFileSep() + "document.xml";
		String tempFile = BBIni.getTempFilesPath() + BBIni.getFileSep() + "newDoc.xml";
		
		fu.copyFile(template, tempFile);
		
		Builder builder = new Builder();
		try {
			doc = builder.build(new File(tempFile));
			return true;
		} 
		catch (ParsingException e) {
			e.printStackTrace();
			new Notify("Framework is malformed");
			return false;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean startDocument (InputStream inputStream, String configFile, String configSettings) throws Exception {
		String fileName = "xxx";
		return buildDOM(fileName);
	}
	
	public boolean startDocument (String completePath, String configFile, String configSettings) throws Exception {
			return setupFromFile (completePath, configFile, configSettings);
	}
	
	private boolean setupFromFile (String completePath, String configFile, String configSettings) throws Exception {
		String configFileWithPath = "temp";
		String configWithUTD;
		
		// Use the default; we don't have a local version.
		configFileWithPath = fu.findInProgramData ("liblouisutdml" + BBIni.getFileSep() + "lbu_files" + BBIni.getFileSep() + configFile);
		
		if (configSettings == null) {
			configWithUTD = "formatFor utd\n mode notUC\n printPages no\n";
		} 
		else {
			configWithUTD = configSettings + "formatFor utd\n mode notUC\n printPages no\n";
		}
		
		if(dm.getWorkingPath() != null){
			configWithUTD += semHandler.getSemanticsConfigSetting(completePath);			
		}
		else {
			configFileWithPath = fu.findInProgramData ("liblouisutdml" + BBIni.getFileSep() + "lbu_files" + BBIni.getFileSep() + "nimas.cfg");
		}
		
		String outFile = BBIni.getTempFilesPath() + fileSep + "outFile.utd";
		String logFile = BBIni.getLogFilesPath() + fileSep + "liblouisutdml.log";
		int extPos = completePath.lastIndexOf (".") + 1;
		String ext = completePath.substring (extPos);
		if (ext.equalsIgnoreCase ("xml") || ext.equals("xhtml") || ext.equals("html") || ext.equals("htm")) {
			String tempPath = BBIni.getTempFilesPath() + completePath.substring(completePath.lastIndexOf(BBIni.getFileSep()), completePath.lastIndexOf(".")) + "_temp.xml";
			if(normalizeFile(completePath, tempPath) && lutdml.translateFile (configFileWithPath, tempPath, outFile, logFile, configWithUTD, 0)){
				deleteFile(tempPath);
				return buildDOM(outFile);
			}
			else 
				return false;
		} 
		else if (ext.equalsIgnoreCase ("txt")) {
			if(lutdml.translateTextFile (configFileWithPath, completePath, outFile, logFile, configWithUTD, 0))
				return buildDOM(outFile);
		} 
		else if (ext.equalsIgnoreCase ("brf")) {
			if(lutdml.backTranslateFile (configFileWithPath, completePath, outFile, logFile, configWithUTD, 0))
				return buildDOM(outFile);
		} 
		else if (ext.equalsIgnoreCase ("utd")) {
			String tempPath = BBIni.getTempFilesPath() + completePath.substring(completePath.lastIndexOf(BBIni.getFileSep()), completePath.lastIndexOf(".")) + "_temp.utd";
			normalizeUTD(completePath, tempPath);
			return buildDOM(tempPath);
		} 
		else {
			throw new IllegalArgumentException (completePath + " not .xml, .txt, or .brf");
		}
		new CheckLiblouisutdmlLog().displayLog();
		
		
		return false;
	}
	
	private boolean buildDOM(String fileName) throws Exception{
		if (this.doc != null) {
			  throw new Exception ("Attempt to reuse instance");
		}
		
		File file = new File (fileName);
		Builder parser = new Builder();
		try {
			this.doc = parser.build (file);
			removeBraillePageNumber();
			return true;
		} 
		catch(ConnectException e){
			new Notify("Brailleblaster failed to access necessary materials from online.  Please check your internet connection and try again.");
			e.printStackTrace();
			logger.log(Level.SEVERE, "Connections Error", e);
			return false;
		}
		catch(UnknownHostException e){
			new Notify("Brailleblaster failed to access necessary materials from online.  Please check your internet connection and try again.");
			e.printStackTrace();
			logger.log(Level.SEVERE, "Unknown Host Error", e);
			return false;
		}
		catch (ParsingException e) {
			new Notify("Problem processing " + fileName + " See stack trace.");
			new CheckLiblouisutdmlLog().displayLog();
			e.printStackTrace();
			logger.log(Level.SEVERE, "Parse Error", e);
			return false;
		} 
		catch (IOException e) {
			new Notify ("IO error occurred while parsing " + fileName + " See stack trace.");
			e.printStackTrace();
			logger.log(Level.SEVERE, "IO Error", e);
			return false;
		}
	}
	
	private boolean normalizeFile(String originalFilePath, String tempFilePath){
		Normalizer n = new Normalizer(this, originalFilePath);
		return n.createNewNormalizedFile(tempFilePath);
	}
	
	private boolean normalizeUTD(String originalFilePath, String tempFilePath){
		Normalizer n = new Normalizer(this, originalFilePath);
		return n.createNewUTDFile(tempFilePath);
	}
	
	public Element makeElement(String name, String attribute, String value){
		Element e = new Element(name);
		addNamespace(e);
		if(attribute != null){
			e.addAttribute(new Attribute(attribute, value));
		}
		return e;
	}
	
	protected void addNamespace(Element e){
		e.setNamespaceURI(doc.getRootElement().getNamespaceURI());
		
		Elements els = e.getChildElements();
		for(int i = 0; i < els.size(); i++){
			addNamespace(els.get(i));
		}
	}
	
	public Element getRootElement(){
		return doc.getRootElement();
	}
	
	public Document getNewXML(){
		Document d = new Document(this.doc);
		setOriginalDocType(d);
		removeAllBraille(d.getRootElement());
		return d;
	}
	
	private void removeAllBraille(Element e){
		Elements els = e.getChildElements();
		
		if(e instanceof Element && e.getLocalName().equals("meta")){
			if(checkAttribute(e, "name") && e.getAttributeValue("name").equals("utd"))
				e.getParent().removeChild(e);
			else {
				Attribute attr = e.getAttribute("semantics");
				e.removeAttribute(attr);
			}
				
		}
		else if(e instanceof Element && e.getAttribute("semantics") != null){
			Attribute attr = e.getAttribute("semantics");
			e.removeAttribute(attr);
		}
		
		for(int i = 0; i < els.size(); i++){
			if(els.get(i).getLocalName().equals("brl")){
				e.removeChild(els.get(i));
			}
			else {
				removeAllBraille(els.get(i));
			}
		}
	}
	
	public Document getDOM(){
		return doc;
	}
	
	private void removeBraillePageNumber(){
		Elements e = this.doc.getRootElement().getChildElements();
		
		for(int i = 0; i < e.size(); i++){
			if(checkAttribute(e.get(i), "semantics") && e.get(i).getAttributeValue("semantics").equals("style,document")){
				Elements els = e.get(i).getChildElements();
				for(int j = 0; j < els.size(); j++){
					if(els.get(j).getLocalName().equals("brl")){
						int index = e.get(i).indexOf(els.get(j));
						e.get(i).removeChild(index);
					}
				}
			}
		}
	}
	
	public boolean createBrlFile(Manager dm, String filePath){		
		Document temp = getNewXML();
		String inFile = createTempFile(temp);
		String config = fu.findInProgramData ("liblouisutdml" + BBIni.getFileSep() + "lbu_files" + BBIni.getFileSep() + BBIni.getDefaultConfigFile());
		String logFile = BBIni.getTempFilesPath() + fileSep + "liblouisutdml.log";
		String semFile = "";
		
		if(inFile.equals(""))
			return false;
		
		String fileName;
		if(dm.getWorkingPath() == null)
			fileName = "outFile.utd";
		else
			fileName = dm.getWorkingPath();
		
		if(fu.exists(BBIni.getTempFilesPath() + BBIni.getFileSep() + fu.getFileName(fileName) + ".sem")){
			semFile = "semanticFiles "+ semHandler.getDefaultSemanticsFiles() + "," + BBIni.getTempFilesPath() + BBIni.getFileSep() + fu.getFileName(fileName) + ".sem" + "\n";
		}
		
		boolean result = lutdml.translateFile (config, inFile, filePath, logFile, semFile + "formatFor brf\n", 0);
		deleteTempFile(inFile);
		return result;
	}
	
	private String createTempFile(Document newDoc){
		String filePath = BBIni.getTempFilesPath() + BBIni.getFileSep() + "tempXML.xml";
		if(fu.createXMLFile(newDoc, filePath))
			return filePath;
		else	    
			return "";
	}
	
	private void deleteTempFile(String filePath){
		File f = new File(filePath);
		f.delete();
	}
	
	public boolean checkAttributeValue(Element e, String attribute, String value){
		try {
			if(e.getAttributeValue(attribute).equals(value))
					return true;	
			else
				return false;
		}
		catch(Exception ex){
			logger.log(Level.SEVERE, "Exception", ex);
			return false;
		}
	}
	
	public boolean checkAttribute(Element e, String attribute){
			if(e.getAttribute(attribute) != null)
				return true;
			else
				return false;
	}
	
	public void checkSemantics(Element e){
		if(e.getAttributeValue("semantics") == null){
			//Notify errorMessage = new Notify("No semantic attribute exists for element \"" + e.getLocalName() + "\". Please consider editing the configuration files.");
			Attribute attr = new Attribute("semantics", "style,para");
			e.addAttribute(attr);
			if(!e.getLocalName().equals("meta") && !this.missingSemanticsList.contains(e.getLocalName()))
				this.missingSemanticsList.add(e.getLocalName());
		}
	}
	
	public void notifyUser(){
		if(this.missingSemanticsList.size() > 0){
			String text = "No semantic attribute exists for the following element(s): \n";
			for(int i = 0; i < this.missingSemanticsList.size(); i++){
				text += this.missingSemanticsList.get(i) + "\n";
			}
			text += "Please check your document and consider editing the configuration files.";
			new Notify(text);
		}
	}
	
	public String getOutfile(){
		return BBIni.getTempFilesPath() + fileSep + "outFile.utd";
	}
	
	public void resetBBDocument(String config){
		deleteDOM();
		semHandler.resetSemanticHandler(config);
	}
	
	public void deleteDOM(){
		this.doc = null;
		System.gc();
	}
	
	private void deleteFile(String path){
		File f = new File(path);
		f.delete();
	}
	
	public void setPublicId(String id){
		publicId = id;
	}
	
	public void setSystemId(String id){
		systemId = id;
	}
	
	public void setOriginalDocType(Document d) {
		if(this.doc.getDocType() == null)
			d.setDocType(new DocType(this.getRootElement().getLocalName(), publicId, systemId));
	}
	
	public SemanticFileHandler getSemanticFileHandler(){
		return semHandler;
	}
	
	public Nodes query(String query){
		XPathContext context = XPathContext.makeNamespaceContext(doc.getRootElement());
		return doc.query(query, context);
	}
}
