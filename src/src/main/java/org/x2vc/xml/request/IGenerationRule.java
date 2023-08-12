package org.x2vc.xml.request;

import java.io.Serializable;
import java.util.UUID;

/**
 * This component represents a single rule to add information to an XML
 * document. The following types of rule are available:
 * <ul>
 * <li>add an element: {@link IAddElementRule}</li>
 * <li>set attribute of element to value: {@link ISetAttributeRule}</li>
 * <li>add text content to an element: {@link IAddDataContentRule}</li>
 * <li>add raw content (text containing tags) to an element:
 * {@link IAddRawContentRule}</li>
 * </ul>
 */
public interface IGenerationRule extends Serializable {

	/**
	 * @return the ID of the generation rule
	 */
	UUID getID();

}
