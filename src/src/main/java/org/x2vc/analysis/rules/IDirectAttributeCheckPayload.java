package org.x2vc.analysis.rules;

import java.util.UUID;

import org.x2vc.schema.structure.IXMLSchemaObject;
import org.x2vc.xml.document.IModifierPayload;

/**
 * An {@link IModifierPayload} produced by the {@link DirectAttributeCheckRule}.
 */
public interface IDirectAttributeCheckPayload extends IAnalyzerRulePayload {

	/**
	 * @return the injectedAttribute
	 */
	String getInjectedAttribute();

	/**
	 * @return the elementSelector
	 */
	String getElementSelector();

	/**
	 * @return the ID of the schema element {@link IXMLSchemaObject} that describes
	 *         the value
	 */
	UUID getSchemaElementID();

}
