package org.x2vc.schema.evolution.items;

import java.util.Set;

import org.x2vc.schema.evolution.IModifierCreationCoordinator;
import org.x2vc.schema.evolution.ISchemaElementProxy;
import org.x2vc.schema.structure.IXMLSchema;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.sf.saxon.expr.FilterExpression;

/**
 * {@link IEvaluationTreeItemFactory} to represent an {@link FilterExpression}.
 */
public class FilterExpressionItem extends AbstractEvaluationTreeItem<FilterExpression> {

	private IEvaluationTreeItem baseItem;
	private IEvaluationTreeItem filterItem;

	FilterExpressionItem(IXMLSchema schema, IModifierCreationCoordinator coordinator, FilterExpression target) {
		super(schema, coordinator, target);
	}

	@Override
	protected void initialize(IEvaluationTreeItemFactory itemFactory, FilterExpression target) {
		this.baseItem = itemFactory.createItemForExpression(target.getBase());
		this.filterItem = itemFactory.createItemForExpression(target.getFilter());
	}

	@Override
	@SuppressWarnings("java:S4738") // suggestion is nonsense, java type does not fit
	protected ImmutableCollection<ISchemaElementProxy> evaluate(ISchemaElementProxy contextItem,
			FilterExpression target) {
		final Set<ISchemaElementProxy> result = Sets.newHashSet();
		final ImmutableCollection<ISchemaElementProxy> selectedBaseItems = this.baseItem.evaluate(contextItem);
		for (final ISchemaElementProxy selectedBaseItem : selectedBaseItems) {
			result.addAll(this.filterItem.evaluate(selectedBaseItem));
		}
		return ImmutableSet.copyOf(result);
	}

}
