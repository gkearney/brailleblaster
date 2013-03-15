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

package org.brailleblaster.views;

import java.util.LinkedList;

import nu.xom.Node;

import org.brailleblaster.abstractClasses.AbstractContent;
import org.brailleblaster.abstractClasses.AbstractView;
import org.brailleblaster.mapping.TextMapElement;
import org.brailleblaster.wordprocessor.BBEvent;
import org.brailleblaster.wordprocessor.DocumentManager;
import org.brailleblaster.wordprocessor.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Group;


public class TextView extends AbstractView {
	public int total;
	private int oldCursorPosition;
	private int deletionChar;
	public TextView (Group documentWindow) {
		super (documentWindow, 16, 57, 0, 100);
		this.total = 0;
	}

/* This is a derivative work from 
 * org.eclipse.swt.custom.DefaultContent.java 
*/

	public void initializeView(){
	
	}

	public void initializeListeners(final DocumentManager dm){
		view.addTraverseListener(new TraverseListener(){
			@Override
			public void keyTraversed(TraverseEvent e) {
				if(e.keyCode == SWT.TAB && e.stateMask == SWT.CTRL){
					Message message = new Message(BBEvent.CHANGE_FOCUS);
					message.put("offset", view.getCaretOffset());
					message.put("sender", "text");
					dm.dispatch(message);
				}
			}
		});
		
		view.addVerifyKeyListener(new VerifyKeyListener(){
			@Override
			public void verifyKey(VerifyEvent e) {
				oldCursorPosition = view.getCaretOffset();
				if(e.keyCode == SWT.DEL){
					deletionChar = SWT.DEL;
				}
				else if(e.keyCode == SWT.BS){
					deletionChar = SWT.BS;
				}
			}		
		});

		view.addExtendedModifyListener(new ExtendedModifyListener(){
			@Override
			public void modifyText(ExtendedModifyEvent e) {
				if(dm.document.getDOM() != null){
					if(e.length > 0){
						Message message = new Message(BBEvent.TEXT_INSERTION);
						message.put("offset", view.getCaretOffset() - e.length);
						message.put("length", e.length);
						dm.dispatch(message);
					}
					else {
						int offset = 0;
						if(view.getCaretOffset() - oldCursorPosition == 0){
							offset = -1;
						}
						else{
							offset = view.getCaretOffset() - oldCursorPosition;
						}
						Message message = new Message(BBEvent.TEXT_DELETION);
						message.put("offset", view.getCaretOffset() + 1);
						message.put("length", offset);
						message.put("deletionType", deletionChar);
						dm.dispatch(message);
					}
				}			
			}
		});	
	}
	
	public void setCursor(int offset){
		view.setFocus();
		view.setCaretOffset(offset);
	}

	public void setText(Node n, LinkedList<TextMapElement>list){
		view.append(n.getValue() + "\n");
		list.add(new TextMapElement(this.total, n));
		this.total += n.getValue().length() + 1;
	}

	public String getString(int start, int length){
		return view.getTextRange(start, length);
	}

	private class TextContent extends AbstractContent {
	}

}