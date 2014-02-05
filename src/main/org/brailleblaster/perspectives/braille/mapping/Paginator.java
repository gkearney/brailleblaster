package org.brailleblaster.perspectives.braille.mapping;

import java.util.LinkedList;

import org.brailleblaster.perspectives.braille.messages.Message;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

public class Paginator {
	private LinkedList<PageMapElement>list;
	
	public Paginator(){
		list = new LinkedList<PageMapElement>();
	}
	
	public PageMapElement getPageMapElement(int index){
		return list.get(index);
	}
	
	public Node findTextNode(Element e){
		int count = e.getChildCount();
		for(int i = 0; i < count; i++){
			if(e.getChild(i) instanceof Element && (((Element)e.getChild(i)).getLocalName().equals("span") || ((Element)e.getChild(i)).getLocalName().equals("brl"))){
				return findTextNode((Element)e.getChild(i));
			}
			else if(e.getChild(i) instanceof Text && ((Element)e.getChild(i).getParent()).getLocalName().equals("span")){
				return e.getChild(i);
			}
		}
		
		return null;
	}
	
	public Node findBrailleNode(Element e){
		int count = e.getChildCount();
		for(int i = 0; i < count; i++){
			if(e.getChild(i) instanceof Element && (((Element)e.getChild(i)).getLocalName().equals("span") || ((Element)e.getChild(i)).getLocalName().equals("brl"))){
				return findBrailleNode((Element)e.getChild(i));
			}
			else if(e.getChild(i) instanceof Text && ((Element)e.getChild(i).getParent()).getLocalName().equals("brl")){
				return e.getChild(i);
			}
		}
		
		return null;
	}
	
	public PageMapElement getLast(){
		return list.getLast();
	}
	
	public void add(PageMapElement p){
		list.add(p);
	}
	
	public boolean inPrintPageRange(int offset){		
		if(list.size() > 0 && searchList(offset, 0, list.size() - 1) != -1)
			return true;
		else
			return false;
	}
	
	public boolean inBraillePageRange(int offset){		
		if(list.size() > 0 && searchBraille(offset, 0, list.size() - 1) != -1)
			return true;
		else
			return false;
	}
	
	public PageMapElement findPage(int offset){
		int index= searchList(offset, 0, list.size());
		if(index != -1)
			return list.get(index);
		else
			return null;
	}
	
	public int findCurrentPrintPage(int offset){
		if(list.size() == 0)
			return -1;
		else
			return findRange(offset);
	}
	
	private int findRange(int offset){
		for(int i = 0; i < list.size(); i++){
			if(i == 0 && offset < list.get(i).start)
				return i;
			else if(offset < list.get(i).start && offset > list.get(i - 1).end)
				return i;
			else if(i == list.size() - 1 && offset > list.get(i).end)
				return i;
		}
		
		return -1;
	}
	
	private int searchList(int offset, int low, int high){
		if(low > high)
			return -1;
		
		int mid = low + (high - low) / 2;
		PageMapElement current = list.get(mid);
		if(offset >= current.start && offset <= current.end)
			return mid;
		else if(offset < current.start)
			return searchList(offset, low, mid - 1);
		else 
			return searchList(offset, mid + 1, high);
	}
	
	public PageMapElement findBraillePage(int offset){
		int index= searchBraille(offset, 0, list.size());
		if(index != -1)
			return list.get(index);
		else
			return null;
	}
	
	private int searchBraille(int offset, int low, int high){
		if(low > high)
			return -1;
		
		int mid = low + (high - low) / 2;
		PageMapElement current = list.get(mid);
		if(offset >= current.brailleStart && offset <= current.brailleEnd)
			return mid;
		else if(offset < current.brailleStart)
			return searchBraille(offset, low, mid - 1);
		else 
			return searchBraille(offset, mid + 1, high);
	}
	
	/*
	private int findNext(int offset, int low, int high){
		if(low > high)
			return -1;
		
		int mid = low + (high - low) / 2;
		PageMapElement current = list.get(mid);
		if(offset < current.start){
			if(mid > 0 && list.get(mid - 1).end < offset)
				return mid;
			else
				return findNext(offset, low, mid - 1);
		}
		else 
			return findNext(offset, mid + 1, high);
	}
	*/
	public void updateOffsets(TextMapElement t, Message message){
		int textOffset = (Integer)message.getValue("length");
		int brailleOffset;
		if(message.contains("newBrailleLength"))
			brailleOffset = (Integer)message.getValue("newBrailleLength") - (Integer)message.getValue("brailleLength");
		else
			brailleOffset =  (Integer)message.getValue("length");
		
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).start + textOffset > t.end){
				list.get(i).start += textOffset;
				list.get(i).end += textOffset;
					
				list.get(i).brailleStart += brailleOffset;
				list.get(i).brailleEnd += brailleOffset;
			}
		}	
	}
	
	public void updateOffsets(TextMapElement t, int textOffset, int brailleOffset){
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).start + textOffset > t.end){
				list.get(i).start += textOffset;
				list.get(i).end += textOffset;
			
				list.get(i).brailleStart += brailleOffset;
				list.get(i).brailleEnd += brailleOffset;
			}
		}
	}
}
