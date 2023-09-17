package org.x2vc.stylesheet;

import java.net.URI;

import org.x2vc.stylesheet.coverage.IStylesheetCoverage;
import org.x2vc.stylesheet.structure.IStylesheetStructure;

import com.google.common.collect.Multimap;

/**
 * This object is a result of the stylesheet preparation process and provides
 * access to the precompiled extended stylesheet and the structure information.
 * It can also be used to create a new coverage statistics object.
 *
 * This object can be serialized and deserialized to create a local copy.
 */
public interface IStylesheetInformation {

	/**
	 * @return the URI of the stylesheet (either a local file URI or a temporary,
	 *         in-memory ID issued by {@link IStylesheetManager}).
	 */
	URI getURI();

	/**
	 * @return the original (unprepared) stylesheet
	 */
	String getOriginalStylesheet();

	/**
	 * @return the prepared stylesheet
	 */
	String getPreparedStylesheet();

	/**
	 * Returns the list of the namespace prefixes used and - if possible - the
	 * namespace URIs they are associated with. Since a namespace alias may be
	 * associated with multiple different URIs in different places, this has to be a
	 * multimap.
	 *
	 * @return a map assigning namespace prefixes to the URIs they are associated
	 *         with
	 * @see INamespaceExtractor
	 */
	Multimap<String, URI> getNamespacePrefixes();

	/**
	 * Determines the namespace prefix to use for the trace elements. This prefix is
	 * guaranteed not to collide with any of the other namespace prefixes in the
	 * document.
	 *
	 * @return the namespace prefix to use for the trace elements
	 */
	String getTraceNamespacePrefix();

	/**
	 * @return the structure information corresponding to the stylesheet
	 */
	IStylesheetStructure getStructure();

	/**
	 * @return a new empty coverage statistics object related to this stylesheet
	 */
	IStylesheetCoverage createCoverageStatistics();

}
