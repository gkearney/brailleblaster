package org.brailleblaster.perspectives.imageDescriber.UIComponents;

import org.brailleblaster.perspectives.Controller;
import org.brailleblaster.perspectives.imageDescriber.ImageDescriberController;
import org.brailleblaster.settings.ConfigFileDialog;
import org.brailleblaster.wordprocessor.BBMenu;
import org.brailleblaster.wordprocessor.WPManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ImageDescriberMenu extends BBMenu{
	ImageDescriberController currentController;
	
	//file menu
	MenuItem openItem, saveItem, saveAsItem, closeItem;

	//editMenu
	Menu editMenu;
	MenuItem editItem, prevItem, nextItem, applyItem, undoAllItem, applyToAllItem, clearAllItem;
			
	public ImageDescriberMenu(final WPManager wp, ImageDescriberController idc) {
		super(wp);
		viewMenu.getItem(0).getMenu().getItem(0).setSelection(false);
		viewMenu.getItem(0).getMenu().getItem(1).setSelection(true);
		selectedPerspective = viewMenu.getItem(0).getMenu().getItem(1);
		currentController = idc;
		
		openItem = new MenuItem(fileMenu, SWT.PUSH, 0);
		openItem.setText(lh.localValue("&Open") + "\t" + lh.localValue("Ctrl + O"));
		openItem.setAccelerator(SWT.MOD1 + 'O');
		openItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = wp.getFolder().getSelectionIndex();
				if(index == -1){
					wp.addDocumentManager(null);
					currentController = (ImageDescriberController) wp.getList().getFirst(); 
					currentController.fileOpenDialog();
					if(currentController.getWorkingPath() == null){
						currentController.close();
						wp.removeController(currentController);
						currentController = null;
					}
				}
				else {
					currentController.fileOpenDialog();
				}
			}		
		});
		
		saveItem = new MenuItem(fileMenu, SWT.PUSH, 2);
		saveItem.setText(lh.localValue("&Save") + "\t" + lh.localValue("Ctrl + S"));
		saveItem.setAccelerator(SWT.MOD1 + 'S');
		saveItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(wp.getFolder().getItemCount() != 0)
					currentController.save();
			}
		});
		
		saveAsItem = new MenuItem(fileMenu, SWT.PUSH, 3);
		saveAsItem.setText(lh.localValue("Save&As") + "\t" + lh.localValue("F12"));
		saveAsItem.setAccelerator(SWT.F12);
		saveAsItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(wp.getFolder().getItemCount() != 0)
					currentController.saveAs();
			}	
		});
		
		closeItem = new MenuItem(fileMenu, SWT.PUSH, 5);
		closeItem.setText(lh.localValue("&Close") + "\t" + lh.localValue("Ctrl + W"));
		closeItem.setAccelerator(SWT.MOD1 + 'W');
		closeItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int count = wp.getFolder().getItemCount();				
				Controller temp = currentController;
				
				if(count > 0)
					currentController.close();

				wp.removeController(temp);
	
				if(wp.getList().size() == 0)
					setCurrent(null);	
			}
		});
		
		editItem = new MenuItem(menuBar, SWT.CASCADE, 1);
		editItem.setText(lh.localValue("&Edit"));
		
		editMenu = new Menu(wp.getShell(), SWT.DROP_DOWN);
		prevItem = new MenuItem(editMenu, SWT.PUSH);
		prevItem.setText("Previous");
		prevItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				currentController.setImageToPrevious();
			} // widgetSelected()
		});
		
		nextItem = new MenuItem(editMenu, SWT.PUSH);
		nextItem.setText(lh.localValue("&Next"));
		nextItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentController.setImageToNext();
			} // widgetSelected()

		}); // nextBtn.addSelectionListener...)
		
		applyItem = new MenuItem(editMenu, SWT.PUSH);
		applyItem.setText(lh.localValue("&Apply"));
		applyItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentController.apply();
				// Set image's description.
				//imgDesc.setCurElmProd(imgDescTextBox.getText(), null, null, null);
				//idd.setDocumentEdited(true);
			} // widgetSelected()
		}); // applyBtn.addSelectionListener...
		
		undoAllItem = new MenuItem(editMenu, SWT.PUSH);
		undoAllItem.setText(lh.localValue("&Undo All"));
		undoAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentController.cancel();
			} // widgetSelected()

		}); // undoAllItem.addSelectionListener...
		
		applyToAllItem = new MenuItem(editMenu, SWT.PUSH);
		applyToAllItem.setText(lh.localValue("A&pply to All"));
		applyToAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentController.applyToAll();
			} // widgetSelected()
			
		}); // applyToAll.addSelectionListener...
		
		clearAllItem = new MenuItem(editMenu, SWT.PUSH);
		clearAllItem.setText(lh.localValue("&Clear All"));
		clearAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentController.clearAll();
			} // widgetSelected()

		}); // clearAllBtn.addSelectionListener
		

		
		/////////////////
		// BRAILLE!
		MenuItem brailleItem = new MenuItem(menuBar, SWT.CASCADE, 4);
		brailleItem.setText(lh.localValue("&Braille"));
		
		Menu brailleMenu = new Menu(wp.getShell(), SWT.DROP_DOWN);
		
		MenuItem transTemplateItem = new MenuItem(brailleMenu, SWT.PUSH);
		transTemplateItem.setText(lh.localValue("&Translation Templates"));
		transTemplateItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new ConfigFileDialog(wp.getShell(), SWT.NONE, wordProc);
			}
		});
		brailleItem.setMenu(brailleMenu);
		
		editItem.setMenu(editMenu);
	}

	@Override
	public void setCurrent(Controller controller) {
		this.currentController = (ImageDescriberController)controller;
	}

	@Override
	public Controller getCurrent() {
		return currentController;
	}
}