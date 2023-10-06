package org.x2vc.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.x2vc.analysis.IAnalyzerRule;
import org.x2vc.analysis.IDocumentAnalyzer;
import org.x2vc.process.commands.IProcessDirectorFactory;
import org.x2vc.process.commands.IProcessDirectorManager;
import org.x2vc.process.tasks.*;
import org.x2vc.processor.IHTMLDocumentFactory;
import org.x2vc.processor.IXSLTProcessor;
import org.x2vc.report.IProcessingMessageCollector;
import org.x2vc.report.IReportWriter;
import org.x2vc.report.IVulnerabilityCandidateCollector;
import org.x2vc.schema.IInitialSchemaGenerator;
import org.x2vc.schema.ISchemaManager;
import org.x2vc.schema.evolution.ISchemaModificationProcessor;
import org.x2vc.schema.evolution.ISchemaModifierCollector;
import org.x2vc.schema.evolution.IValueTraceAnalyzer;
import org.x2vc.stylesheet.INamespaceExtractor;
import org.x2vc.stylesheet.IStylesheetManager;
import org.x2vc.stylesheet.IStylesheetPreprocessor;
import org.x2vc.stylesheet.structure.IStylesheetStructureExtractor;
import org.x2vc.utilities.IDebugObjectWriter;
import org.x2vc.xml.document.IDocumentGenerator;
import org.x2vc.xml.request.ICompletedRequestRegistry;
import org.x2vc.xml.request.IDocumentRequest;
import org.x2vc.xml.request.IRequestGenerator;
import org.x2vc.xml.value.IPrefixSelector;
import org.x2vc.xml.value.IValueGeneratorFactory;

import com.github.racc.tscg.TypesafeConfigModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@ExtendWith(MockitoExtension.class)
class CheckerModuleTest {

	// see also https://stackoverflow.com/questions/26710191/how-to-test-implementations-of-guice-abstractmodule

	private Config config;

	@Inject
	private Provider<Set<IAnalyzerRule>> ruleProvider;
	@Inject
	private Provider<IDocumentAnalyzer> documentAnalyzerProvider;
	@Inject
	private Provider<IWorkerProcessManager> workerProcessManagerProvider;
	@Inject
	private Provider<IProcessDirectorManager> processDirectorManagerProvider;
	@Inject
	private Provider<IProcessDirectorFactory> processDirectorFactoryProvider;
	@Inject
	private Provider<IDebugObjectWriter> debugObjectWriterProvider;
	@Inject
	private Provider<IInitializationTaskFactory> initializationTaskFactoryProvider;
	@Inject
	private Provider<IInitialVulnerabilityCheckTaskFactory> initialVulnerabilityCheckTaskFactoryProvider;
	@Inject
	private Provider<IFollowUpVulnerabilityCheckTaskFactory> followUpVulnerabilityCheckTaskFactoryProvider;
	@Inject
	private Provider<IReportGeneratorTaskFactory> reportGeneratorTaskFactoryProvider;
	@Inject
	private Provider<ISchemaEvolutionTaskFactory> schemaEvolutionTaskFactoryProvider;
	@Inject
	private Provider<ISchemaExplorationTaskFactory> schemaExplorationTaskFactoryProvider;
	@Inject
	private Provider<IHTMLDocumentFactory> htmlDocumentFactoryProvider;
	@Inject
	private Provider<IXSLTProcessor> xsltProcessorProvider;
	@Inject
	private Provider<IProcessingMessageCollector> processingMessageCollectorProvider;
	@Inject
	private Provider<IReportWriter> reportWriterProvider;
	@Inject
	private Provider<IVulnerabilityCandidateCollector> vulnerabilityCandidateCollectorProvider;
	@Inject
	private Provider<IValueTraceAnalyzer> valueTraceAnalyzerProvider;
	@Inject
	private Provider<ISchemaModificationProcessor> schemaModificationProcessorProvider;
	@Inject
	private Provider<ISchemaModifierCollector> schemaModifierCollectorProvider;
	@Inject
	private Provider<ISchemaManager> schemaManagerProvider;
	@Inject
	private Provider<IInitialSchemaGenerator> initialSchemaGeneratorProvider;
	@Inject
	private Provider<IStylesheetManager> stylesheetManagerProvider;
	@Inject
	private Provider<IStylesheetPreprocessor> stylesheetPreprocessorProvider;
	@Inject
	private Provider<INamespaceExtractor> namespaceExtractorProvider;
	@Inject
	private Provider<IStylesheetStructureExtractor> stylesheetStructureExtractorProvider;
	@Inject
	private Provider<IDocumentGenerator> documentGeneratorProvider;
	@Inject
	private Provider<IRequestGenerator> requestGeneratorProvider;
	@Inject
	private Provider<ICompletedRequestRegistry> completedRequestRegistryProvider;
	@Inject
	private Provider<IPrefixSelector> prefixSelectorProvider;
	@Inject
	private Provider<IValueGeneratorFactory> valueGeneratorFactory;

	@BeforeEach
	void setUp() throws Exception {
		this.config = ConfigFactory.load();
		Guice.createInjector(new CheckerModule(this.config),
				TypesafeConfigModule.fromConfigWithPackage(this.config, "org.x2vc"))
			.injectMembers(this);
	}

	@Test
	void testAnalyzerRules() {
		final Set<IAnalyzerRule> rules = this.ruleProvider.get();
		assertNotNull(rules);
		assertFalse(rules.isEmpty());
		// check whether all rules are present
		final List<String> ruleClassNames = rules.stream()
			.map(rule -> rule.getClass().getSimpleName())
			.sorted()
			.toList();
		assertEquals(List.of(
				"CSSAttributeCheckRule",
				"CSSBlockCheckRule",
				"CSSURLCheckRule",
				"DirectAttributeCheckRule",
				"DirectElementCheckRule",
				"DisabledOutputEscapingCheckRule",
				"ElementCopyCheckRule",
				"GeneralURLCheckRule",
				"JavascriptBlockCheckRule",
				"JavascriptHandlerCheckRule",
				"JavascriptURLCheckRule"), ruleClassNames);
	}

	@Test
	void testDocumentAnalyzer() {
		assertNotNull(this.documentAnalyzerProvider.get());
	}

	@Test
	void testWorkerProcessManager() {
		assertNotNull(this.workerProcessManagerProvider.get());
	}

	@Test
	void testProcessDirectorManager() {
		assertNotNull(this.processDirectorManagerProvider.get());
	}

	@Test
	void testProcessDirectorFactory() {
		final IProcessDirectorFactory factory = this.processDirectorFactoryProvider.get();
		assertNotNull(factory);
		assertNotNull(factory.create(mock(File.class), mock(ProcessingMode.class)));
	}

	@Test
	void testDebugObjectWriter() {
		assertNotNull(this.debugObjectWriterProvider.get());
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInitializationTaskFactory() {
		final IInitializationTaskFactory factory = this.initializationTaskFactoryProvider.get();
		assertNotNull(factory);
		assertNotNull(factory.create(mock(File.class), mock(ProcessingMode.class), mock(Consumer.class)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testInitialVulnerabilityCheckTaskFactory() {
		final IInitialVulnerabilityCheckTaskFactory factory = this.initialVulnerabilityCheckTaskFactoryProvider.get();
		assertNotNull(factory);
		assertNotNull(factory.create(mock(File.class), mock(Consumer.class), mock(BiConsumer.class)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testFollowUpVulnerabilityCheckTaskFactory() {
		final IFollowUpVulnerabilityCheckTaskFactory factory = this.followUpVulnerabilityCheckTaskFactoryProvider.get();
		assertNotNull(factory);
		assertNotNull(factory.create(mock(File.class), mock(IDocumentRequest.class), mock(Consumer.class),
				mock(BiConsumer.class)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testReportGeneratorTaskFactory() {
		final IReportGeneratorTaskFactory factory = this.reportGeneratorTaskFactoryProvider.get();
		assertNotNull(factory);
		assertNotNull(factory.create(mock(File.class), mock(Consumer.class)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testSchemaEvolutionTaskFactory() {
		final ISchemaEvolutionTaskFactory factory = this.schemaEvolutionTaskFactoryProvider.get();
		assertNotNull(factory);
		assertNotNull(factory.create(mock(File.class), mock(ISchemaModifierCollector.class), mock(Consumer.class)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testSchemaExplorationTaskFactory() {
		final ISchemaExplorationTaskFactory factory = this.schemaExplorationTaskFactoryProvider.get();
		assertNotNull(factory);
		assertNotNull(factory.create(mock(File.class), mock(Consumer.class), mock(BiConsumer.class)));
	}

	@Test
	void testHTMLDocumentFactory() {
		assertNotNull(this.htmlDocumentFactoryProvider.get());
	}

	@Test
	void testXSLTProcessor() {
		assertNotNull(this.xsltProcessorProvider.get());
	}

	@Test
	void testProcessingMessageCollector() {
		assertNotNull(this.processingMessageCollectorProvider.get());
	}

	@Test
	void testReportWriter() {
		assertNotNull(this.reportWriterProvider.get());
	}

	@Test
	void testVulnerabilityCandidateCollector() {
		assertNotNull(this.vulnerabilityCandidateCollectorProvider.get());
	}

	@Test
	void testValueTraceAnalyzer() {
		assertNotNull(this.valueTraceAnalyzerProvider.get());
	}

	@Test
	void testSchemaModificationProcessor() {
		assertNotNull(this.schemaModificationProcessorProvider.get());
	}

	@Test
	void testSchemaModifierCollector() {
		assertNotNull(this.schemaModifierCollectorProvider.get());
	}

	@Test
	void testSchemaManager() {
		assertNotNull(this.schemaManagerProvider.get());
	}

	@Test
	void testInitialSchemaGenerator() {
		assertNotNull(this.initialSchemaGeneratorProvider.get());
	}

	@Test
	void testStylesheetManager() {
		assertNotNull(this.stylesheetManagerProvider.get());
	}

	@Test
	void testStylesheetPreprocessor() {
		assertNotNull(this.stylesheetPreprocessorProvider.get());
	}

	@Test
	void testNamespaceExtractor() {
		assertNotNull(this.namespaceExtractorProvider.get());
	}

	@Test
	void testStylesheetStructureExtractor() {
		assertNotNull(this.stylesheetStructureExtractorProvider.get());
	}

	@Test
	void testDocumentGenerator() {
		assertNotNull(this.documentGeneratorProvider.get());
	}

	@Test
	void testRequestGenerator() {
		assertNotNull(this.requestGeneratorProvider.get());
	}

	@Test
	void testCompletedRequestRegistry() {
		assertNotNull(this.completedRequestRegistryProvider.get());
	}

	@Test
	void testPrefixSelector() {
		assertNotNull(this.prefixSelectorProvider.get());
	}

	@Test
	void testValueGeneratorFactory() {
		final IValueGeneratorFactory factory = this.valueGeneratorFactory.get();
		assertNotNull(factory);
		assertNotNull(factory.createValueGenerator(mock(IDocumentRequest.class)));
	}

}