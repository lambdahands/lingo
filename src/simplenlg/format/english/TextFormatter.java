/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is "Simplenlg".
 *
 * The Initial Developer of the Original Code is Ehud Reiter, Albert Gatt and Dave Westwater.
 * Portions created by Ehud Reiter, Albert Gatt and Dave Westwater are Copyright (C) 2010-11 The University of Aberdeen. All Rights Reserved.
 *
 * Contributor(s): Ehud Reiter, Albert Gatt, Dave Wewstwater, Roman Kutlak, Margaret Mitchell.
 */
package simplenlg.format.english;

import java.util.ArrayList;
import java.util.List;

import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentCategory;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.ElementCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGModule;
import simplenlg.framework.StringElement;

/**
 * <p>
 * This processing module adds some simple plain text formatting to the
 * SimpleNLG output. This includes the following:
 * <ul>
 * <li>Adding the document title to the beginning of the text.</li>
 * <li>Adding section titles in the relevant places.</li>
 * <li>Adding appropriate new line breaks for ease-of-reading.</li>
 * <li>Indenting list items with ' * '.</li>
 * </ul>
 * </p>
 * 
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 * 
 */
public class TextFormatter extends NLGModule {

	@Override
	public void initialise() {
		// Do nothing
	}

	@Override
	public NLGElement realise(NLGElement element) {
		NLGElement realisedComponent = null;
		StringBuffer realisation = new StringBuffer();
		
		if (element != null) {
			ElementCategory category = element.getCategory();
			List<NLGElement> components = element.getChildren();

			//NB: The order of the if-statements below is important!
			
			// check if this is a canned text first
			if (element instanceof StringElement) {
				realisation.append(element.getRealisation());

			} else if (category instanceof DocumentCategory) {
				// && element instanceof DocumentElement
				String title = element instanceof DocumentElement ? ((DocumentElement) element)
						.getTitle()
						: null;
				// String title = ((DocumentElement) element).getTitle();

				switch ((DocumentCategory) category) {

				case DOCUMENT:
				case SECTION:
				case LIST:
					if (title != null) {
						realisation.append(title).append('\n');
					}
					for (NLGElement eachComponent : components) {
						realisedComponent = realise(eachComponent);
						if (realisedComponent != null) {
							realisation.append(realisedComponent
									.getRealisation());
						}
					}
					break;

				case PARAGRAPH:
					if (null != components && 0 < components.size()) {
						realisedComponent = realise(components.get(0));
						if (realisedComponent != null) {
							realisation.append(realisedComponent
									.getRealisation());
						}
						for (int i = 1; i < components.size(); i++) {
							if (realisedComponent != null) {
								realisation.append(' ');
							}
							realisedComponent = realise(components.get(i));
							if (realisedComponent != null) {
								realisation.append(realisedComponent
										.getRealisation());
							}
						}
					}
					realisation.append("\n\n");
					break;

				case SENTENCE:
					realisation.append(element.getRealisation());
					break;

				case LIST_ITEM:
					// cch fix
					//realisation.append(" * ").append(element.getRealisation()); //$NON-NLS-1$
					realisation.append(" * "); //$NON-NLS-1$

					for (NLGElement eachComponent : components) {
						realisedComponent = realise(eachComponent);
						
						if (realisedComponent != null) {
							realisation.append(realisedComponent
									.getRealisation());	
							
							if(components.indexOf(eachComponent) < components.size()-1) {
								realisation.append(' ');
							}
						}
					}
					//finally, append newline
					realisation.append("\n");
					break;
				}

				// also need to check if element is a listelement (items can
				// have embedded lists post-orthography) or a coordinate
			} else if (element instanceof ListElement || element instanceof CoordinatedPhraseElement) {
				for (NLGElement eachComponent : components) {
					realisedComponent = realise(eachComponent);
					if (realisedComponent != null) {
						realisation.append(realisedComponent.getRealisation()).append(' ');
					}
				}				
			} 
		}
		
		return new StringElement(realisation.toString());
	}

	@Override
	public List<NLGElement> realise(List<NLGElement> elements) {
		List<NLGElement> realisedList = new ArrayList<NLGElement>();

		if (elements != null) {
			for (NLGElement eachElement : elements) {
				realisedList.add(realise(eachElement));
			}
		}
		return realisedList;
	}
}
