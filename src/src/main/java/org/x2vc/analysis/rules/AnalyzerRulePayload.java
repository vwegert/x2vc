package org.x2vc.analysis.rules;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Standard implementation of {@link IAnalyzerRulePayload}.
 */
public class AnalyzerRulePayload implements IAnalyzerRulePayload {

	private String injectedValue;
	private UUID schemaElementID;
	private String elementSelector;
	private String elementName;
	private String attributeName;

	private AnalyzerRulePayload(Builder builder) {
		this.injectedValue = builder.injectedValue;
		this.schemaElementID = builder.schemaElementID;
		this.elementSelector = builder.elementSelector;
		this.elementName = builder.elementName;
		this.attributeName = builder.attributeName;
	}

	@Override
	public Optional<String> getInjectedValue() {
		return Optional.ofNullable(this.injectedValue);
	}

	@Override
	public Optional<UUID> getSchemaElementID() {
		return Optional.ofNullable(this.schemaElementID);
	}

	@Override
	public Optional<String> getElementSelector() {
		return Optional.ofNullable(this.elementSelector);
	}

	@Override
	public Optional<String> getElementName() {
		return Optional.ofNullable(this.elementName);
	}

	@Override
	public Optional<String> getAttributeName() {
		return Optional.ofNullable(this.attributeName);
	}

	/**
	 * Creates builder to build {@link AnalyzerRulePayload}.
	 *
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link AnalyzerRulePayload}.
	 */
	public static final class Builder {
		private String injectedValue;
		private UUID schemaElementID;
		private String elementSelector;
		private String elementName;
		private String attributeName;

		private Builder() {
		}

		/**
		 * Builder method for injectedValue parameter.
		 *
		 * @param injectedValue field to set
		 * @return builder
		 */
		public Builder withInjectedValue(String injectedValue) {
			this.injectedValue = injectedValue;
			return this;
		}

		/**
		 * Builder method for schemaElementID parameter.
		 *
		 * @param schemaElementID field to set
		 * @return builder
		 */
		public Builder withSchemaElementID(UUID schemaElementID) {
			this.schemaElementID = schemaElementID;
			return this;
		}

		/**
		 * Builder method for elementSelectors parameter.
		 *
		 * @param elementSelector field to set
		 * @return builder
		 */
		public Builder withElementSelector(String elementSelector) {
			this.elementSelector = elementSelector;
			return this;
		}

		/**
		 * Builder method for elementName parameter.
		 *
		 * @param elementName field to set
		 * @return builder
		 */
		public Builder withElementName(String elementName) {
			this.elementName = elementName;
			return this;
		}

		/**
		 * Builder method for attributeName parameter.
		 *
		 * @param attributeName field to set
		 * @return builder
		 */
		public Builder withAttributeName(String attributeName) {
			this.attributeName = attributeName;
			return this;
		}

		/**
		 * Builder method of the builder.
		 *
		 * @return built class
		 */
		public AnalyzerRulePayload build() {
			return new AnalyzerRulePayload(this);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.attributeName, this.elementName, this.elementSelector, this.injectedValue,
					this.schemaElementID);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Builder other = (Builder) obj;
			return Objects.equals(this.attributeName, other.attributeName)
					&& Objects.equals(this.elementName, other.elementName)
					&& Objects.equals(this.elementSelector, other.elementSelector)
					&& Objects.equals(this.injectedValue, other.injectedValue)
					&& Objects.equals(this.schemaElementID, other.schemaElementID);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.attributeName, this.elementSelector, this.injectedValue, this.schemaElementID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AnalyzerRulePayload other = (AnalyzerRulePayload) obj;
		return Objects.equals(this.attributeName, other.attributeName)
				&& Objects.equals(this.elementSelector, other.elementSelector)
				&& Objects.equals(this.injectedValue, other.injectedValue)
				&& Objects.equals(this.schemaElementID, other.schemaElementID);
	}

}
