package org.x2vc.xml.request;

import org.x2vc.xml.value.IValueGenerator;

/**
 * Controls the way MIXED content is generated by the {@link IValueGenerator}.
 */
public enum MixedContentGenerationMode {

	/**
	 * Generate the full range of mixed content, including various HTML tags. This mode is used for XSS checks.
	 */
	FULL,

	/**
	 * Only generate text contents and omit the HTML tags. This mode is used during the schema evolution to avoid
	 * unnecessary schema elements from being generated.
	 */
	RESTRICTED

}
