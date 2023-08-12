package org.x2vc.xml.request;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.x2vc.schema.structure.IXMLElementReference;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Standard implementation of {@link IAddElementRule}.
 */
public class AddElementRule extends AbstractGenerationRule implements IAddElementRule {

	private static final long serialVersionUID = 7708211517867960929L;
	private UUID elementReferenceID;
	private ImmutableSet<ISetAttributeRule> attributeRules;
	private ImmutableList<IContentGenerationRule> contentRules;

	private AddElementRule(UUID ruleID, Builder builder) {
		super(ruleID);
		this.elementReferenceID = builder.elementReferenceID;
		this.attributeRules = ImmutableSet.copyOf(builder.attributeRules);
		this.contentRules = ImmutableList.copyOf(builder.contentRules);
	}

	private AddElementRule(Builder builder) {
		super();
		this.elementReferenceID = builder.elementReferenceID;
		this.attributeRules = ImmutableSet.copyOf(builder.attributeRules);
		this.contentRules = ImmutableList.copyOf(builder.contentRules);
	}

	@Override
	public UUID getElementReferenceID() {
		return this.elementReferenceID;
	}

	@Override
	public ImmutableSet<ISetAttributeRule> getAttributeRules() {
		return this.attributeRules;
	}

	@Override
	public ImmutableList<IContentGenerationRule> getContentRules() {
		return this.contentRules;
	}

	/**
	 * Creates a builder to build {@link AddElementRule} and initialize it with the
	 * given object.
	 *
	 * @param addElementRule to initialize the builder with
	 * @return created builder
	 */
	public static Builder builderFrom(AddElementRule addElementRule) {
		return new Builder(addElementRule);
	}

	/**
	 * Builder to build {@link AddElementRule}.
	 */
	public static final class Builder {
		private UUID elementReferenceID;
		private UUID ruleID;
		private Set<ISetAttributeRule> attributeRules = Sets.newHashSet();
		private List<IContentGenerationRule> contentRules = Lists.newArrayList();

		/**
		 * Creates a new builder
		 *
		 * @param elementReferenceID
		 */
		public Builder(UUID elementReferenceID) {
			this.elementReferenceID = elementReferenceID;
		}

		/**
		 * Creates a new builder
		 *
		 * @param elementReference
		 */
		public Builder(IXMLElementReference elementReference) {
			this.elementReferenceID = elementReference.getID();
		}

		private Builder(AddElementRule addElementRule) {
			this.elementReferenceID = addElementRule.elementReferenceID;
			this.attributeRules.addAll(addElementRule.attributeRules);
			this.contentRules.addAll(addElementRule.contentRules);
		}

		/**
		 * Sets a rule ID (default is to generate a random rule ID).
		 *
		 * @param ruleID
		 * @return builder
		 */
		public Builder withRuleID(UUID ruleID) {
			this.ruleID = ruleID;
			return this;
		}

		/**
		 * Adds an attribute rule to the builder.
		 *
		 * @param attributeRule the rule to add
		 * @return builder
		 */
		public Builder addAttributeRule(ISetAttributeRule attributeRule) {
			this.attributeRules.add(attributeRule);
			return this;
		}

		/**
		 * Adds a content rule to the builder
		 *
		 * @param contentRule the rule to add
		 * @return builder
		 */
		public Builder addContentRule(IContentGenerationRule contentRule) {
			this.contentRules.add(contentRule);
			return this;
		}

		/**
		 * Builder method of the builder.
		 *
		 * @return built class
		 */
		public AddElementRule build() {
			if (this.ruleID == null) {
				return new AddElementRule(this);
			} else {
				return new AddElementRule(this.ruleID, this);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(this.attributeRules, this.contentRules, this.elementReferenceID);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AddElementRule other = (AddElementRule) obj;
		return Objects.equals(this.attributeRules, other.attributeRules)
				&& Objects.equals(this.contentRules, other.contentRules)
				&& Objects.equals(this.elementReferenceID, other.elementReferenceID);
	}

}
