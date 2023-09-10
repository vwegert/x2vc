package org.x2vc.analysis.rules;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.x2vc.report.IVulnerabilityCandidate;
import org.x2vc.report.VulnerabilityCandidate;
import org.x2vc.schema.ISchemaManager;
import org.x2vc.schema.structure.IXMLSchema;
import org.x2vc.xml.document.IDocumentModifier;
import org.x2vc.xml.document.IXMLDocumentContainer;
import org.x2vc.xml.value.IValueDescriptor;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Rule E.2: Check the text content of every element that contains the prefix
 * used to generate the values whether it is possible to inject arbitrary code.
 * This checks for xsl:copy and xsl:copy-of vulnerabilities.
 */
public class ElementCopyCheckRule extends AbstractTextRule {

	/**
	 * @see #getRuleID()
	 */
	public static final String RULE_ID = "E.2";

	private static final Logger logger = LogManager.getLogger();

	private ISchemaManager schemaManager;

	/**
	 * @param schemaManager
	 */
	@Inject
	ElementCopyCheckRule(ISchemaManager schemaManager) {
		super();
		this.schemaManager = schemaManager;
	}

	@Override
	public String getRuleID() {
		return RULE_ID;
	}

	@Override
	protected boolean isApplicableTo(TextNode textNode, IXMLDocumentContainer xmlContainer) {
		logger.traceEntry();
		// this rule is applicable for text nodes that contain the prefix only
		final String prefix = xmlContainer.getDocumentDescriptor().getValuePrefix();
		final boolean result = textNode.text().contains(prefix);
		return logger.traceExit(result);
	}

	@Override
	protected void performCheckOn(TextNode textNode, IXMLDocumentContainer xmlContainer,
			Consumer<IDocumentModifier> collector) {
		logger.traceEntry();

		final String textContent = textNode.text();
		final Optional<ImmutableSet<IValueDescriptor>> valueDescriptors = xmlContainer.getDocumentDescriptor()
			.getValueDescriptors(textContent);
		if (valueDescriptors.isPresent()) {
			// we can't address the text node directly - we need to emit the path to the
			// parent node
			final String parentElementPath = getPathToNode(textNode.parentNode());
			logger.debug("checking text of element {}", parentElementPath);
			final IXMLSchema schema = this.schemaManager.getSchema(xmlContainer.getStylesheeURI());
			for (final IValueDescriptor valueDescriptor : valueDescriptors.get()) {
				final String currentValue = valueDescriptor.getValue();
				// try to replace the entire element with script element
				final UUID schemaElementID = valueDescriptor.getSchemaElementID();
				logger.debug("attempt to replace \"{}\" with \"<script></script>\" for schema element {}", currentValue,
						schemaElementID);
				final AnalyzerRulePayload payload = AnalyzerRulePayload.builder()
					.withSchemaElementID(schemaElementID)
					.withElementSelector(parentElementPath)
					.withInjectedValue("script") // this is the sub-element we'll be looking for
					.build();
				// this is what we try to inject ---------------------------vvvvvvvvvvvvvvvvv
				requestModification(schema, valueDescriptor, currentValue, "<script>alert('XSS!')</script>", payload,
						collector);
			}
		}
		logger.traceExit();
	}

	@Override
	protected void verifyNode(UUID taskID, Node node, IXMLDocumentContainer xmlContainer,
			Optional<String> injectedValue, Optional<UUID> schemaElementID, Optional<String> elementSelector,
			Consumer<IVulnerabilityCandidate> collector) {
		logger.traceEntry();
		checkArgument(injectedValue.isPresent());
		checkArgument(schemaElementID.isPresent());
		checkArgument(elementSelector.isPresent());

		// As per the "can't address the text node directly" comment above, the node to
		// be examined here will actually be an Element node. We have to examine its
		// contents - which is fine, because if we succeeded to inject a new Element, it
		// wouldn't have turned up as part of the text node anyway.
		if (node instanceof final Element element) {
			final String parentPath = getPathToNode(element);
			final String injectedTag = injectedValue.get();
			logger.debug("follow-up check on {} to check for injection of \"{}\" tag", parentPath, injectedTag);

			final Elements possiblyInjectedElements = element.getElementsByTag(injectedTag);
			possiblyInjectedElements.forEach(injectedElement -> {
				logger.debug("tag \"{}\" injected from input data, follow-up check positive",
						injectedTag);

				final String injectedPath = getPathToNode(injectedElement.parentNode());

				// TODO Report Output: provide better input sample (formatting, highlighting?)
				final String inputSample = xmlContainer.getDocument();

				// the output sample can be derived from the node
				final String outputSample = element.toString();

				collector.accept(new VulnerabilityCandidate(RULE_ID, taskID, schemaElementID.get(),
						injectedPath, inputSample, outputSample));
			});
		} else {
			logger.warn("follow-up check called for non-element node");
		}
		logger.traceExit();
	}

}