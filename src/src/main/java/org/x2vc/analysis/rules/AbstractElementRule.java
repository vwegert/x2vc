package org.x2vc.analysis.rules;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.x2vc.analysis.IAnalyzerRule;
import org.x2vc.xml.document.IDocumentModifier;
import org.x2vc.xml.document.IXMLDocumentDescriptor;

/**
 * An {@link IAnalyzerRule} that performs a check on an element level.
 */
public abstract class AbstractElementRule extends AbstractRule implements IAnalyzerRule {

	private static final Logger logger = LogManager.getLogger();

	@Override
	public final void checkNode(Node node, IXMLDocumentDescriptor descriptor, Consumer<IDocumentModifier> collector) {
		logger.traceEntry();
		if (node instanceof final Element element && (isApplicableTo(element, descriptor))) {
			performCheckOn(element, descriptor, collector);
		}
		logger.traceExit();
	}

	/**
	 * Determines whether the entire rule can be applied to the element at all.
	 *
	 * @param element    the element to check
	 * @param descriptor the input document descriptor
	 * @return <code>true</code> if the rule should be checked further
	 */
	protected abstract boolean isApplicableTo(Element element, IXMLDocumentDescriptor descriptor);

	/**
	 * Performs the actual check on the element in question.
	 *
	 * @param element    the element to check
	 * @param descriptor the input document descriptor
	 * @param collector  a sink to send any resulting modification requests to
	 */
	protected abstract void performCheckOn(Element element, IXMLDocumentDescriptor descriptor,
			Consumer<IDocumentModifier> collector);

}
