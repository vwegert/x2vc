package org.x2vc.xml.document;

/*-
 * #%L
 * x2vc - XSLT XSS Vulnerability Checker
 * %%
 * Copyright (C) 2023 x2vc authors and contributors
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */

import org.x2vc.xml.request.IDocumentRequest;

/**
 * This component processes an XML document request and produces an XML document container that contains a document and
 * a corresponding descriptor. The results are generated by executing the rules.
 */
public interface IDocumentGenerator {

	/**
	 * The namespace used for the trace information generated into the document.
	 */
	static final String TRACE_ELEMENT_NAMESPACE = "https://github.com/x2vc/xmlTrace";

	/**
	 * The local name of the attribute added to identify each element.
	 */
	static final String TRACE_ATTRIBUTE_ELEMENT_ID = "elementID";

	/**
	 * Generates a document from the request.
	 *
	 * @param request
	 * @return the generated document
	 */
	IXMLDocumentContainer generateDocument(IDocumentRequest request);

}