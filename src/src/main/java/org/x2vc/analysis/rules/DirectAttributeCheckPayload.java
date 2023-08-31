package org.x2vc.analysis.rules;

import java.util.UUID;

/**
 * Standard implementation of {@link IDirectAttributeCheckPayload}.
 */
class DirectAttributeCheckPayload implements IDirectAttributeCheckPayload {

	private static final long serialVersionUID = -176115350310411970L;

	private UUID schemaElementID;
	private String elementSelector;
	private String injectedAttribute;
	private String injectedValue;

	/**
	 * @param schemaElementID
	 * @param elementSelector
	 * @param injectedAttribute
	 * @param injectedValue
	 */
	public DirectAttributeCheckPayload(UUID schemaElementID, String elementSelector, String injectedAttribute,
			String injectedValue) {
		super();
		this.schemaElementID = schemaElementID;
		this.elementSelector = elementSelector;
		this.injectedAttribute = injectedAttribute;
		this.injectedValue = injectedValue;
	}

	/**
	 * @param schemaElementID
	 * @param elementSelector
	 * @param injectedAttribute
	 */
	public DirectAttributeCheckPayload(UUID schemaElementID, String elementSelector, String injectedAttribute) {
		super();
		this.schemaElementID = schemaElementID;
		this.elementSelector = elementSelector;
		this.injectedAttribute = injectedAttribute;
	}

	@Override
	public UUID getSchemaElementID() {
		return this.schemaElementID;
	}

	@Override
	public String getElementSelector() {
		return this.elementSelector;
	}

	@Override
	public String getInjectedAttribute() {
		return this.injectedAttribute;
	}

	@Override
	public String getInjectedValue() {
		return this.injectedValue;
	}

}