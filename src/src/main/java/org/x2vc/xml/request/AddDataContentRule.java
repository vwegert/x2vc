package org.x2vc.xml.request;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Standard implementation of {@link IAddDataContentRule}.
 */
public class AddDataContentRule extends AbstractGenerationRule implements IAddDataContentRule {

	private static final long serialVersionUID = -6406343863665611821L;

	private UUID elementID;
	private IRequestedValue requestedValue;

	/**
	 * Create a new rule with a random ID and a requested value specified.
	 *
	 * @param requestedValue
	 */
	AddDataContentRule(UUID elementID, IRequestedValue requestedValue) {
		super();
		this.elementID = elementID;
		this.requestedValue = requestedValue;
	}

	/**
	 * Create a new rule with a random ID and without a requested value specified.
	 *
	 * @param requestedValue
	 */
	AddDataContentRule(UUID elementID) {
		super();
		this.elementID = elementID;
	}

	@Override
	public UUID getElementID() {
		return this.elementID;
	}

	@Override
	public Optional<IRequestedValue> getRequestedValue() {
		return Optional.ofNullable(this.requestedValue);
	}

	/**
	 * @param requestedValue the requestedValue to set
	 */
	public void setRequestedValue(IRequestedValue requestedValue) {
		this.requestedValue = requestedValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(this.elementID, this.requestedValue);
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
		final AddDataContentRule other = (AddDataContentRule) obj;
		return Objects.equals(this.elementID, other.elementID)
				&& Objects.equals(this.requestedValue, other.requestedValue);
	}

}
