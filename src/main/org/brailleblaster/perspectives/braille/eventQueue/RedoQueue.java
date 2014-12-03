package org.brailleblaster.perspectives.braille.eventQueue;

import org.brailleblaster.perspectives.braille.Manager;
import org.brailleblaster.perspectives.braille.document.BrailleDocument;
import org.brailleblaster.perspectives.braille.mapping.maps.MapList;
import org.brailleblaster.perspectives.braille.stylers.HideActionHandler;
import org.brailleblaster.perspectives.braille.viewInitializer.ViewInitializer;

public class RedoQueue extends EventQueue{

	private static final long serialVersionUID = 3665037341723873085L;

	public RedoQueue(){
		super();
	}
	
	@Override
	protected void handleEvent(EventFrame f, ViewInitializer vi, BrailleDocument doc, MapList list, Manager manager) {
		for(int i = 0; i < f.size(); i++){
			Event event = f.get(i);
			switch(event.eventType){
				case Update:
					break;
				case Insert:
					break;
				case Delete:
					break;
				case Hide:
					HideActionHandler h = new HideActionHandler(manager, list, vi);
					h.hideText(event);
					break;
				default:
					break;
			}
		}
	}

}
