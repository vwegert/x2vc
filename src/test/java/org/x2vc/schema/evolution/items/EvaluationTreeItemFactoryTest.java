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
package org.x2vc.schema.evolution.items;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.x2vc.schema.evolution.IModifierCreationCoordinator;
import org.x2vc.schema.structure.IXMLSchema;

import net.sf.saxon.expr.*;
import net.sf.saxon.functions.SystemFunction;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.NamespaceUri;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.NodeTest;

@ExtendWith(MockitoExtension.class)
class EvaluationTreeItemFactoryTest {

	@Mock
	private IXMLSchema schema;

	@Mock
	private IModifierCreationCoordinator coordinator;

	private EvaluationTreeItemFactory factory;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.factory = new EvaluationTreeItemFactory(this.schema, this.coordinator);
	}

	@ParameterizedTest
	@CsvSource({
			"net.sf.saxon.expr.ForExpression, AssignationItem",
			"net.sf.saxon.expr.flwor.OuterForExpression, AssignationItem",
			"net.sf.saxon.expr.LetExpression, AssignationItem",
			"net.sf.saxon.expr.EagerLetExpression, AssignationItem",
			"net.sf.saxon.expr.QuantifiedExpression, AssignationItem",
			"net.sf.saxon.expr.AttributeGetter, AttributeGetterItem",
			"net.sf.saxon.expr.AxisExpression, AxisExpressionItem",
			"net.sf.saxon.expr.ArithmeticExpression, IndependentBinaryExpressionItem",
			"net.sf.saxon.expr.compat.ArithmeticExpression10, IndependentBinaryExpressionItem",
			"net.sf.saxon.expr.AndExpression, IndependentBinaryExpressionItem",
			"net.sf.saxon.expr.OrExpression, IndependentBinaryExpressionItem",
			// net.sf.saxon.expr.FilterExpression - special case!
			"net.sf.saxon.expr.GeneralComparison20, IndependentBinaryExpressionItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.IdentityComparison
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.LookupExpression
			"net.sf.saxon.expr.SlashExpression, SlashExpressionItem",
			"net.sf.saxon.expr.SimpleStepExpression, SlashExpressionItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.SwitchCaseComparison
			"net.sf.saxon.expr.ValueComparison, IndependentBinaryExpressionItem",
			"net.sf.saxon.expr.VennExpression, IndependentBinaryExpressionItem",
			"net.sf.saxon.expr.SingletonIntersectExpression, IndependentBinaryExpressionItem",
			"net.sf.saxon.expr.compat.GeneralComparison10, IndependentBinaryExpressionItem",
			"net.sf.saxon.expr.ContextItemExpression, NoOperationItem",
			"net.sf.saxon.expr.CurrentItemExpression, NoOperationItem",
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.DynamicFunctionCall
			"net.sf.saxon.expr.ErrorExpression, NoOperationItem",
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.FunctionCall (abstract)
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.StaticFunctionCall
			"net.sf.saxon.expr.SystemFunctionCall, SystemFunctionCallItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.UserFunctionCall
			"net.sf.saxon.functions.IntegratedFunctionCall, IntegratedFunctionCallItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.xpath.XPathFunctionCall
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.IntegerRangeTest
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.IsLastExpression
			"net.sf.saxon.expr.Literal, NoOperationItem",
			"net.sf.saxon.expr.StringLiteral, NoOperationItem",
			"net.sf.saxon.functions.hof.FunctionLiteral, NoOperationItem",
			"net.sf.saxon.expr.NumberSequenceFormatter, NumberSequenceFormatterItem",
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.PseudoExpression (abstract)
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.DefaultedArgumentExpression
			// TODO #12 support Expression subclass
			// ......net.sf.saxon.expr.DefaultedArgumentExpression.DefaultCollationArgument
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.sort.SortKeyDefinition
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.sort.SortKeyDefinitionList
			// TODO #12 support Expression subclass ....net.sf.saxon.pattern.Pattern (abstract)
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.AncestorQualifiedPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.AnchorPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.BasePatternWithPredicate
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.BooleanExpressionPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.GeneralNodePattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.GeneralPositionalPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.ItemTypePattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.NodeSetPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.NodeTestPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.PatternThatSetsCurrent
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.SimplePositionalPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.StreamingFunctionArgumentPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.UniversalPattern
			// TODO #12 support Expression subclass ......net.sf.saxon.pattern.VennPattern (abstract)
			// TODO #12 support Expression subclass ........net.sf.saxon.pattern.ExceptPattern
			// TODO #12 support Expression subclass ........net.sf.saxon.pattern.IntersectPattern
			// TODO #12 support Expression subclass ........net.sf.saxon.pattern.UnionPattern
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.RangeExpression
			"net.sf.saxon.expr.RootExpression, RootExpressionItem",
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.SimpleExpression (abstract)
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.SuppliedParameterReference
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.TryCatch
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.UnaryExpression (abstract)
			"net.sf.saxon.expr.AdjacentTextNodeMerger, UnaryExpressionItem",
			"net.sf.saxon.expr.AtomicSequenceConverter, UnaryExpressionItem",
			"net.sf.saxon.expr.UntypedSequenceConverter, UnaryExpressionItem",
			"net.sf.saxon.expr.Atomizer, UnaryExpressionItem",
			"net.sf.saxon.expr.CardinalityChecker, UnaryExpressionItem",
			"net.sf.saxon.expr.CastExpression, UnaryExpressionItem",
			"net.sf.saxon.expr.CastableExpression, UnaryExpressionItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.CompareToConstant (abstract)
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.CompareToIntegerConstant
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.CompareToStringConstant
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.ConsumingOperand
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.EmptyTextNodeRemover
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.HomogeneityChecker
			"net.sf.saxon.expr.InstanceOfExpression, UnaryExpressionItem",
			"net.sf.saxon.expr.ItemChecker, UnaryExpressionItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.LookupAllExpression
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.NegateExpression
			"net.sf.saxon.expr.FirstItemExpression, UnaryExpressionItem",
			"net.sf.saxon.expr.LastItemExpression, UnaryExpressionItem",
			"net.sf.saxon.expr.SubscriptExpression, UnaryExpressionItem",
			"net.sf.saxon.expr.SingletonAtomizer, UnaryExpressionItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.TailCallLoop
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.TailExpression
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.OnEmptyExpr
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.OnNonEmptyExpr
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.SequenceInstr
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.WherePopulated
			"net.sf.saxon.expr.sort.DocumentSorter, UnaryExpressionItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.functions.hof.FunctionSequenceCoercer
			"net.sf.saxon.expr.GlobalVariableReference, NoOperationItem",
			"net.sf.saxon.expr.LocalVariableReference, NoOperationItem",
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.flwor.FLWORExpression
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.flwor.TupleExpression
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.instruct.EvaluateInstr
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.instruct.Instruction (abstract)
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.AnalyzeString
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.ApplyNextMatchingTemplate (abstract)
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.instruct.ApplyImports
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.instruct.NextMatch
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.ApplyTemplates
			"net.sf.saxon.expr.instruct.Block, BlockItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.BreakInstr
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.CallTemplate
			"net.sf.saxon.expr.instruct.Choose, ChooseItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.ComponentTracer
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.ConditionalBlock
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.CopyOf
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.Doctype
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.ForEach
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.ForEachGroup
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.Fork
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.IterateInstr
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.LocalParam
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.LocalParamBlock
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.MessageInstr
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.NextIteration
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.ParentNodeConstructor (abstract)
			"net.sf.saxon.expr.instruct.DocumentInstr, DocumentInstrItem",
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.instruct.ElementCreator (abstract)
			// TODO #12 support Expression subclass ........net.sf.saxon.expr.instruct.ComputedElement
			// TODO #12 support Expression subclass ........net.sf.saxon.expr.instruct.Copy
			// TODO #12 support Expression subclass ........net.sf.saxon.expr.instruct.FixedElement
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.ResultDocument
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.SimpleNodeConstructor (abstract)
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.instruct.AttributeCreator (abstract)
			// TODO #12 support Expression subclass ........net.sf.saxon.expr.instruct.ComputedAttribute
			// TODO #12 support Expression subclass ........net.sf.saxon.expr.instruct.FixedAttribute
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.instruct.Comment
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.instruct.NamespaceConstructor
			// TODO #12 support Expression subclass ......net.sf.saxon.expr.instruct.ProcessingInstruction
			"net.sf.saxon.expr.instruct.ValueOf, ValueOfItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.SourceDocument
			"net.sf.saxon.expr.instruct.TraceExpression, TraceExpressionItem",
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.instruct.UseAttributeSet
			// TODO #12 support Expression subclass ....net.sf.saxon.expr.sort.MergeInstr
			"net.sf.saxon.expr.instruct.NumberInstruction, NumberInstructionItem",
			// TODO #12 support Expression subclass ..net.sf.saxon.expr.sort.ConditionalSorter
			"net.sf.saxon.expr.sort.SortExpression, SortExpressionItem",
			// TODO #12 support Expression subclass ..net.sf.saxon.functions.CurrentGroupCall
			// TODO #12 support Expression subclass ..net.sf.saxon.functions.CurrentGroupingKeyCall
			// TODO #12 support Expression subclass ..net.sf.saxon.functions.hof.PartialApply
			// TODO #12 support Expression subclass ..net.sf.saxon.functions.hof.UserFunctionReference
			// TODO #12 support Expression subclass ..net.sf.saxon.ma.arrays.SquareArrayConstructor
			// TODO #12 support Expression subclass ..net.sf.saxon.xpath.JAXPVariableReference
			"net.sf.saxon.expr.Expression, UnsupportedExpressionItem"
	})
	void testCreateItem_Expression(String expressionClassName, String itemClassName) throws ClassNotFoundException {
		final var expressionClass = Class.forName(expressionClassName);
		final var itemClass = Class.forName("org.x2vc.schema.evolution.items." + itemClassName);
		final Expression expression = (Expression) mock(expressionClass);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(itemClass, item);
		assertSame(this.schema, item.getSchema());
	}

	@Test
	void testCreateItem_FilterExpression_Simple() {
		final AxisExpression baseExpression = mock();
		when(baseExpression.getAxis()).thenReturn(AxisInfo.SELF);
		final AxisExpression filterExpression = mock();
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(FilterExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@ParameterizedTest
	@ValueSource(ints = {
			AxisInfo.DESCENDANT,
			AxisInfo.DESCENDANT_OR_SELF
	})
	void testCreateItem_FilterExpression_DescendantWithoutAxisFilter(Integer baseExpressionAxis) {
		final AxisExpression baseExpression = mock();
		when(baseExpression.getAxis()).thenReturn(baseExpressionAxis);
		final AxisExpression filterExpression = mock();
		when(filterExpression.getAxis()).thenReturn(AxisInfo.SELF);
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(FilterExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@ParameterizedTest
	@ValueSource(ints = {
			AxisInfo.DESCENDANT,
			AxisInfo.DESCENDANT_OR_SELF
	})
	void testCreateItem_FilterExpression_DescendantWithAxisFilter(Integer baseExpressionAxis) {
		final AxisExpression baseExpression = mock();
		when(baseExpression.getAxis()).thenReturn(baseExpressionAxis);
		final AxisExpression filterExpression = mock();
		when(filterExpression.getAxis()).thenReturn(AxisInfo.PARENT);
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(SlashExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@Test
	void testCreateItem_FilterExpression_DescendantWithoutAxisFilter_WrappedBaseExists() {
		final AxisExpression wrappedBaseExpression = mock();
		when(wrappedBaseExpression.getAxis()).thenReturn(AxisInfo.DESCENDANT);
		final SystemFunction wrappingFunction = mock();
		when(wrappingFunction.getArity()).thenReturn(1);
		final OperandRole operandRole = mock();
		when(wrappingFunction.getOperandRoles()).thenReturn(new OperandRole[] { operandRole });
		final SystemFunctionCall baseExpression = mock();
		when(baseExpression.getFunctionName()).thenReturn(new StructuredQName("", NamespaceUri.FN, "exists"));
		when(baseExpression.getArg(0)).thenReturn(wrappedBaseExpression);
		when(baseExpression.getTargetFunction()).thenReturn(wrappingFunction);
		final AxisExpression filterExpression = mock();
		when(filterExpression.getAxis()).thenReturn(AxisInfo.PARENT);
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(SlashExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@Test
	void testCreateItem_FilterExpression_DescendantWithoutAxisFilter_WrappedBaseWrongNS() {
		final SystemFunctionCall baseExpression = mock();
		when(baseExpression.getFunctionName()).thenReturn(new StructuredQName("", "foobar", "exists"));
		final AxisExpression filterExpression = mock();
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(FilterExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@Test
	void testCreateItem_FilterExpression_DescendantWithoutAxisFilter_WrappedBaseCount() {
		final SystemFunctionCall baseExpression = mock();
		when(baseExpression.getFunctionName()).thenReturn(new StructuredQName("", NamespaceUri.FN, "count"));
		final AxisExpression filterExpression = mock();
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(FilterExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@Test
	void testCreateItem_FilterExpression_DescendantWithAxisFilter_WrappedFilterExists() {
		final AxisExpression baseExpression = mock();
		when(baseExpression.getAxis()).thenReturn(AxisInfo.DESCENDANT);
		final AxisExpression wrappedFilterExpression = mock();
		when(wrappedFilterExpression.getAxis()).thenReturn(AxisInfo.PARENT);
		final SystemFunction wrappingFunction = mock();
		when(wrappingFunction.getArity()).thenReturn(1);
		final OperandRole operandRole = mock();
		when(wrappingFunction.getOperandRoles()).thenReturn(new OperandRole[] { operandRole });
		final SystemFunctionCall filterExpression = mock();
		when(filterExpression.getFunctionName()).thenReturn(new StructuredQName("", NamespaceUri.FN, "exists"));
		when(filterExpression.getArg(0)).thenReturn(wrappedFilterExpression);
		when(filterExpression.getTargetFunction()).thenReturn(wrappingFunction);
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(SlashExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@Test
	void testCreateItem_FilterExpression_DescendantWithAxisFilter_WrappedFilterWrongNS() {
		final AxisExpression baseExpression = mock();
		when(baseExpression.getAxis()).thenReturn(AxisInfo.DESCENDANT);
		final SystemFunctionCall filterExpression = mock();
		when(filterExpression.getFunctionName()).thenReturn(new StructuredQName("", "foobar", "exists"));
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(FilterExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@Test
	void testCreateItem_FilterExpression_DescendantWithAxisFilter_WrappedFilterCount() {
		final AxisExpression baseExpression = mock();
		when(baseExpression.getAxis()).thenReturn(AxisInfo.DESCENDANT);
		final SystemFunctionCall filterExpression = mock();
		when(filterExpression.getFunctionName()).thenReturn(new StructuredQName("", NamespaceUri.FN, "count"));
		final FilterExpression expression = mock();
		when(expression.getBase()).thenReturn(baseExpression);
		when(expression.getFilter()).thenReturn(filterExpression);
		final IEvaluationTreeItem item = this.factory.createItemForExpression(expression);
		assertInstanceOf(FilterExpressionItem.class, item);
		assertSame(this.schema, item.getSchema());
	}

	@ParameterizedTest
	@CsvSource({
			"net.sf.saxon.pattern.AnyNodeTest, AnyNodeTestItem",
			"net.sf.saxon.pattern.CombinedNodeTest, CombinedNodeTestItem",
			// TODO #12 support NodeTest subclass net.sf.saxon.pattern.DocumentNodeTest
			// TODO #12 support NodeTest subclass net.sf.saxon.pattern.ErrorType
			// TODO #12 support NodeTest subclass net.sf.saxon.pattern.LocalNameTest
			"net.sf.saxon.pattern.MultipleNodeKindTest, MultipleNodeKindTestItem",
			// TODO #12 support NodeTest subclass net.sf.saxon.pattern.NamespaceTest
			"net.sf.saxon.pattern.NameTest, NameTestItem",
			"net.sf.saxon.pattern.NodeKindTest, NodeKindTestItem",
			// TODO #12 support NodeTest subclass net.sf.saxon.pattern.NodeSelector
			// TODO #12 support NodeTest subclass net.sf.saxon.pattern.SameNameTest
			"net.sf.saxon.pattern.NodeTest, UnsupportedNodeTestItem"
	})
	void testCreateItem_NodeTestString(String nodeTestClassName, String itemClassName) throws ClassNotFoundException {
		final var nodeTestClass = Class.forName(nodeTestClassName);
		final var itemClass = Class.forName("org.x2vc.schema.evolution.items." + itemClassName);
		final NodeTest nodeTest = (NodeTest) mock(nodeTestClass);
		final IEvaluationTreeItem item = this.factory.createItemForNodeTest(nodeTest);
		assertInstanceOf(itemClass, item);
		assertSame(this.schema, item.getSchema());
	}

	@Test
	void testInitializeAllCreatedItems_SingleItem() {
		final IEvaluationTreeItem item = mock();
		this.factory.injectUninitializedItem(item);
		this.factory.initializeAllCreatedItems();
		final ArgumentCaptor<IEvaluationTreeItemFactory> factoryCaptor = ArgumentCaptor
			.forClass(IEvaluationTreeItemFactory.class);
		verify(item).initialize(factoryCaptor.capture());
		assertSame(this.factory, factoryCaptor.getValue());
	}

	@Test
	void testInitializeAllCreatedItems_MultipleItems() {
		final IEvaluationTreeItem rootItem = mock();
		final IEvaluationTreeItem subItem1 = mock();
		final IEvaluationTreeItem subItem2 = mock();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				EvaluationTreeItemFactoryTest.this.factory.injectUninitializedItem(subItem1);
				EvaluationTreeItemFactoryTest.this.factory.injectUninitializedItem(subItem2);
				return null;
			}
		}).when(rootItem).initialize(this.factory);
		this.factory.injectUninitializedItem(rootItem);
		this.factory.initializeAllCreatedItems();
		verify(rootItem).initialize(this.factory);
		verify(subItem1).initialize(this.factory);
		verify(subItem1).initialize(this.factory);
	}

}
