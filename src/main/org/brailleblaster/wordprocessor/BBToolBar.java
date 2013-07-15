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
 * this program; see the file LICENSE.
 * If not, see
 * http://www.apache.org/licenses/
 *
 * Maintained by John J. Boyer john.boyer@abilitiessoft.com
 */

package org.brailleblaster.wordprocessor;

import org.brailleblaster.BBIni;
import org.brailleblaster.localization.LocaleHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class BBToolBar {

	private ToolBar toolBar;
	private Button checkBrailleItem;
	// FO
	public BBToolBar(Shell shell, final WPManager wp) {
		String sep = BBIni.getFileSep();
		LocaleHandler lh = new LocaleHandler();
		toolBar = new ToolBar(shell, SWT.HORIZONTAL);
		FormData location = new FormData();
		location.left = new FormAttachment(0);
		location.right = new FormAttachment(40);
		location.top = new FormAttachment(4);
		toolBar.setLayoutData(location);
		
		// Path to dist folder.
		String distPath = BBIni.getProgramDataPath().substring(0, BBIni.getProgramDataPath().lastIndexOf(sep));
		distPath += sep + "programData";
		
		// FO
		String tlabel;
		ToolItem openItem = new ToolItem(toolBar, SWT.PUSH);
		tlabel = lh.localValue("&Open");
		openItem.setText(tlabel.replace("&", ""));
		openItem.setImage(new Image(null, distPath + sep + "images" + sep + "open.png"));
		openItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (BBIni.debugging()) {
					// dm.setReturn (WP.OpenDocumentGetFile);
				} 
				else {
					int index= wp.getFolder().getSelectionIndex();
					if(index == -1){
						wp.addDocumentManager(null);
						wp.getList().getFirst().fileOpenDialog();
					}
					else {
						wp.getList().get(index).fileOpenDialog();
					}
				}
			}
		});

		ToolItem saveItem = new ToolItem(toolBar, SWT.PUSH);
		// FO
		tlabel = lh.localValue("&Save");
		saveItem.setText(tlabel.replace("&", ""));
		saveItem.setImage(new Image(null, distPath  + sep + "images" + sep + "save.png"));
		saveItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (BBIni.debugging()) {
					// dm.setReturn (WP.OpenDocumentGetFile);
				} 
				else {
					int index= wp.getFolder().getSelectionIndex();
					if(index == -1){
						wp.addDocumentManager(null);
						wp.getList().getFirst().fileSave();
					}
					else {
						wp.getList().get(index).fileSave();
					}
				}
			}
		});
		
		ToolItem saveAsItem = new ToolItem(toolBar, SWT.PUSH);
		saveAsItem.setText("Save As");
		saveAsItem.setImage(new Image(null, distPath  + sep + "images" + sep + "saveAs.png"));
		saveAsItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (BBIni.debugging()) {
					// dm.setReturn (WP.OpenDocumentGetFile);
				} 
				else {
					int index= wp.getFolder().getSelectionIndex();
					if(index == -1){
						wp.addDocumentManager(null);
						wp.getList().getFirst().saveAs();
					}
					else {
						wp.getList().get(index).saveAs();
					}
				}
			}
		});

		ToolItem translateItem = new ToolItem(toolBar, SWT.PUSH);
		// FO
		tlabel = lh.localValue("&Translate");
		translateItem.setText(tlabel.replace("&", ""));
		translateItem.setImage(new Image(null, distPath  + sep + "images" + sep + "translate.png"));
		// FO
		translateItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// dm.translateView(true);
			}
		});

		ToolItem embossNow = new ToolItem(toolBar, SWT.PUSH);
		// FO
		tlabel = lh.localValue("Emboss&Now!");
		embossNow.setText(tlabel.replace("&", ""));
		embossNow.setImage(new Image(null, distPath  + sep + "images" + sep + "emboss.png"));
		embossNow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index= wp.getFolder().getSelectionIndex();
				if(index != -1){
					wp.getList().get(index).fileEmbossNow();
				}
			}
		});

		/**
		 * ToolItem embossWithInk = new ToolItem (toolBar, SWT.PUSH); tlabel =
		 * lh.localValue ("EmbossInkN&ow"); embossWithInk.setText
		 * (tlabel.replace ("&", "")); embossWithInk.setEnabled(false);
		 * embossWithInk.addSelectionListener (new SelectionAdapter() { public
		 * void widgetSelected (SelectionEvent e) { dm.placeholder(); } });
		 **/

		ToolItem daisyPrint = new ToolItem(toolBar, SWT.PUSH);
		tlabel = lh.localValue("&Print");
		daisyPrint.setText(tlabel.replace("&", ""));
		daisyPrint.setImage(new Image(null, distPath  + sep + "images" + sep + "print.png"));
		daisyPrint.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// dm.daisyPrint();
			}
		});

		toolBar.pack();

		FormData bloc = new FormData();
		bloc.left = new FormAttachment(40);
		bloc.right = new FormAttachment(47);
		bloc.top = new FormAttachment(5);
		checkBrailleItem = new Button(shell, SWT.CHECK);
		checkBrailleItem.setLayoutData(bloc);
		checkBrailleItem.setText(lh.localValue("viewBraille"));
		checkBrailleItem.setEnabled(false);
		checkBrailleItem.pack();
		checkBrailleItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index= wp.getFolder().getSelectionIndex();
				if(index != -1){
					wp.getList().get(index).toggleBrailleFont();
				}
			}
		});
	}
	
	public void toggleCheckBox(boolean enabled, boolean state){
		this.checkBrailleItem.setEnabled(enabled);
		this.checkBrailleItem.setSelection(state);
	}
}