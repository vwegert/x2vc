package org.x2vc.utilities;

import java.util.UUID;

import org.x2vc.processor.IHTMLDocumentContainer;
import org.x2vc.xml.document.IXMLDocumentContainer;
import org.x2vc.xml.request.IDocumentRequest;

/**
 * This component is used to write certain objects to the file system for
 * debugging purposes.
 */
public interface IDebugObjectWriter {

	/**
	 * Writes an {@link IDocumentRequest} to a file.
	 *
	 * @param taskID  a common task ID to keep all the output files belonging to a
	 *                single task together
	 * @param request
	 */
	void writeRequest(UUID taskID, IDocumentRequest request);

	/**
	 * Writes an XML document kept in an {@link IXMLDocumentContainer} to a file.
	 *
	 * @param taskID      a common task ID to keep all the output files belonging to
	 *                    a single task together
	 * @param xmlDocument
	 */
	void writeXMLDocument(UUID taskID, IXMLDocumentContainer xmlDocument);

	/**
	 * Writes a HTML document kept in an {@link IHTMLDocumentContainer} to a file.
	 *
	 * @param taskID       a common task ID to keep all the output files belonging
	 *                     to a single task together
	 * @param htmlDocument
	 */
	void writeHTMLDocument(UUID taskID, IHTMLDocumentContainer htmlDocument);

}