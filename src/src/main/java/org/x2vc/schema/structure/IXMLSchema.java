package org.x2vc.schema.structure;

import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * A working copy of the XML Schema. This contains the stylesheet parameters as well as an simplified version of a
 * standard W3C XML Schema that holds additional information:
 * <ul>
 * <li>origin of certain information (i. e. whether the value can be influenced by user input)</li>
 * <li>size and content restrictions</li>
 * </ul>
 *
 * This component is immutable and carries a version number. A new version of the schema can be obtained by applying a
 * schema change operation to the schema; this is done by the schema manager to keep version numbers unique and
 * consistent.
 *
 * The XML schema can be stored for later retrieval. Once a new version is created, it is stored automatically.
 *
 */
public interface IXMLSchema {

	/**
	 * @return the URI of the stylesheet for which this schema describes input data
	 */
	URI getStylesheetURI();

	/**
	 * @return the version of the schema
	 */
	int getVersion();

	/**
	 * @return the URI of the schema itself (containing the URI)
	 */
	URI getURI();

	/**
	 * @return the element types that comprise the schema.
	 */
	Collection<IElementType> getElementTypes();

	/**
	 * @return the possible root element references
	 */
	Collection<IElementReference> getRootElements();

	/**
	 * @param id the ID of a schema object
	 * @return the object with the ID
	 * @throws IllegalArgumentException if the object is not part of the schema
	 */
	ISchemaObject getObjectByID(UUID id) throws IllegalArgumentException;

	/**
	 * @param <T>
	 * @param id            the ID of a schema object
	 * @param requestedType the requested type
	 * @return the object with the ID, cast to the requested type
	 * @throws IllegalArgumentException if the object is not part of the schema or the object is not of the requested
	 *                                  type
	 */
	<T extends ISchemaObject> T getObjectByID(UUID id, Class<T> requestedType) throws IllegalArgumentException;

	/**
	 * @param id the ID of a schema object
	 * @return a set of all the paths the object can appear in a document
	 * @throws IllegalArgumentException if the object is not part of the schema
	 */
	Set<String> getObjectPaths(UUID id) throws IllegalArgumentException;

	/**
	 * @param elementType
	 * @return all element references contained in the schema that use to the element type
	 */
	Set<IElementReference> getReferencesUsing(IElementType elementType);

}
