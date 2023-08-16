package org.x2vc.xml.value;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.x2vc.schema.ISchemaManager;
import org.x2vc.schema.structure.*;
import org.x2vc.stylesheet.IStylesheetInformation;
import org.x2vc.stylesheet.IStylesheetManager;
import org.x2vc.xml.request.*;
import org.x2vc.xml.value.IPrefixSelector.PrefixData;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.thedeanda.lorem.LoremIpsum;

/**
 * Standard implementation of {@link IValueGenerator}.
 */
public class ValueGenerator implements IValueGenerator {

	private record GeneratedValue(String value, boolean requested) {
	}

	/**
	 * When an element or attribute with non-fixed discrete values is encountered:
	 * What should be the ratio of the discrete values selected as opposed to
	 * randomly generated values?
	 */
	private static final double DISCRETE_VALUE_SELECTION_RATIO = 0.75;

	/**
	 * When a string value is generated, how many words should be generated
	 * randomly?
	 */
	private static final int DEFAULT_STRING_WORD_COUNT_MIN = 10;
	private static final int DEFAULT_STRING_WORD_COUNT_MAX = 50;

	// TODO Infrastructure: Make these values configurable.

	private static final Logger logger = LogManager.getLogger();

	private IPrefixSelector prefixSelector;
	private IStylesheetInformation stylesheet;
	private IXMLSchema schema;

	private String valuePrefix;
	private Integer valueLength;
	private List<IValueDescriptor> valueDescriptors;
	private LoremIpsum textGenerator;
	int nextGeneratedValueCounter = 1;

	/**
	 * Creates a new value generator.
	 *
	 * @param stylesheetManager
	 * @param schemaManager
	 * @param prefixSelector
	 * @param request
	 */
	public ValueGenerator(IStylesheetManager stylesheetManager, ISchemaManager schemaManager,
			IPrefixSelector prefixSelector, IDocumentRequest request) {
		this.prefixSelector = prefixSelector;
		this.stylesheet = stylesheetManager.get(request.getStylesheeURI());
		this.schema = schemaManager.getSchema(request.getSchemaURI(), request.getSchemaVersion());
		this.valueDescriptors = Lists.newArrayList();
		this.textGenerator = LoremIpsum.getInstance();
	}

	@Override
	public String generateValue(ISetAttributeRule rule) {
		logger.traceEntry("for attribute {}", rule.getAttributeID());
		final IXMLAttribute attribute = this.schema.getObjectByID(rule.getAttributeID()).asAttribute();
		final Optional<IRequestedValue> requestedValue = rule.getRequestedValue();
		final GeneratedValue genValue = generateValueForDataObject(attribute, requestedValue);
		this.valueDescriptors
			.add(new ValueDescriptor(rule.getAttributeID(), rule.getID(), genValue.value, genValue.requested));
		return logger.traceExit("with generated value \"{}\"", genValue.value);
	}

	@Override
	public String generateValue(IAddDataContentRule rule) {
		logger.traceEntry("for element {}", rule.getElementID());
		final IXMLElementType element = this.schema.getObjectByID(rule.getElementID()).asElement();
		final Optional<IRequestedValue> requestedValue = rule.getRequestedValue();
		final GeneratedValue genValue = generateValueForDataObject(element, requestedValue);
		this.valueDescriptors
			.add(new ValueDescriptor(rule.getElementID(), rule.getID(), genValue.value, genValue.requested));
		return logger.traceExit("with generated value \"{}\"", genValue.value);
	}

	/**
	 * Generate a value for a schema object (an attribute or an element), taking
	 * into account a requested value.
	 *
	 * @param schemaObject
	 * @param requestedValue
	 * @return a generated value for the schema object
	 */
	private GeneratedValue generateValueForDataObject(IXMLDataObject schemaObject,
			Optional<IRequestedValue> requestedValue) {
		logger.traceEntry("for schema object {}", schemaObject.getID());
		GeneratedValue value = null;

		// see if a value was requested and if so, if it matches the
		if (requestedValue.isPresent()) {
			final String rV = requestedValue.get().getValue();
			if (requestedValueIsValidForDataObject(rV, schemaObject)) {
				value = new GeneratedValue(rV, true);
			}
		}

		// if no value was requested or the requested value was invalid, generate a new
		// value
		if (value == null) {
			value = new GeneratedValue(generateValueForDataObject(schemaObject), false);
		}

		return logger.traceExit("with generated value \"{}\"", value);
	}

	/**
	 * Determines whether a requested value is valid for a schema object.
	 *
	 * @param value
	 * @param schemaObject
	 * @return whether the requested value is valid for the schema object
	 */
	private boolean requestedValueIsValidForDataObject(String value, IXMLDataObject schemaObject) {
		switch (schemaObject.getDatatype()) {
		case BOOLEAN:
			return (value.equals("true") || value.equals("false"));
		case INTEGER:
			return requestedValueIsValidForIntegerObject(value, schemaObject);
		case STRING:
			return requestedValueIsValidForStringObject(value, schemaObject);
		default:
			// no validation for other types at the moment
			return true;
		}
	}

	/**
	 * Determines whether a requested value is valid for an integer schema object.
	 *
	 * @param value
	 * @param schemaObject
	 * @return
	 */
	private boolean requestedValueIsValidForIntegerObject(String value, IXMLDataObject schemaObject) {
		logger.traceEntry();
		boolean result = true;
		try {
			final int intValue = Integer.parseInt(value);
			final Optional<Integer> minValue = schemaObject.getMinValue();
			if (minValue.isPresent() && (intValue < minValue.get())) {
				logger.warn("requested value {} is lower than the minimum value {} and will be disregarded", value,
						minValue.get());
				result = false;
			}
			final Optional<Integer> maxValue = schemaObject.getMaxValue();
			if (maxValue.isPresent() && (intValue > maxValue.get())) {
				logger.warn("requested value {} is greater than the maximum value {} and will be disregarded", value,
						maxValue.get());
				result = false;
			}
			final Set<IXMLDiscreteValue> discreteValues = schemaObject.getDiscreteValues();
			if (!discreteValues.isEmpty() && schemaObject.isFixedValueset().orElse(false) && (discreteValues.stream()
				.noneMatch(dv -> (dv.getType() == XMLDatatype.INTEGER) && (dv.asInteger() == intValue)))) {
				logger.warn("requested value {} is not part of the fixed value set", value);
				result = false;

			}
		} catch (final NumberFormatException e) {
			logger.warn("requested value {} is not a valid integer and will be disregarded", value);
			result = false;
		}
		return logger.traceExit(result);
	}

	/**
	 * Determines whether a requested value is valid for a string schema object.
	 *
	 * @param value
	 * @param schemaObject
	 * @return
	 */
	private boolean requestedValueIsValidForStringObject(String value, IXMLDataObject schemaObject) {
		logger.traceEntry();
		boolean result = true;
		final Optional<Integer> maxLength = schemaObject.getMaxLength();
		if (maxLength.isPresent() && (value.length() > maxLength.get())) {
			logger.warn("requested value \"{}\" is longer than the maximum length {} and will be disregarded", value,
					maxLength.get());
			result = false;
		}
		final Set<IXMLDiscreteValue> discreteValues = schemaObject.getDiscreteValues();
		if (!discreteValues.isEmpty() && schemaObject.isFixedValueset().orElse(false) && (discreteValues.stream()
			.noneMatch(dv -> (dv.getType() == XMLDatatype.STRING) && (dv.asString().equals(value))))) {
			logger.warn("requested value {} is not part of the fixed value set", value);
			result = false;

		}
		return logger.traceExit(result);
	}

	/**
	 * Generate a value for a schema object (an attribute or an element).
	 *
	 * @param schemaObject
	 * @return
	 */
	private String generateValueForDataObject(IXMLDataObject schemaObject) {
		switch (schemaObject.getDatatype()) {
		case BOOLEAN:
			return (ThreadLocalRandom.current().nextBoolean()) ? "true" : "false";
		case INTEGER:
			return generateValueForIntegerObject(schemaObject);
		case STRING:
			return generateValueForStringObject(schemaObject);
		default:
			// no generation for other types at the moment
			throw new IllegalStateException(
					String.format("no value generation for data type %s implemented", schemaObject.getDatatype()));
		}
	}

	/**
	 * @param schemaObject
	 * @return
	 */
	private String generateValueForIntegerObject(IXMLDataObject schemaObject) {
		logger.traceEntry();
		String result = null;

		// handle discrete values first
		if (!schemaObject.getDiscreteValues().isEmpty()) {
			final IXMLDiscreteValue[] discreteValues = schemaObject.getDiscreteValues()
				.toArray(new IXMLDiscreteValue[0]);
			// check whether we have to select from a fixed value set
			boolean selectDiscreteValue = false;
			if (schemaObject.isFixedValueset().orElse(false)) {
				// yes - select one of the values all of the time
				selectDiscreteValue = true;
			} else {
				// no, the values are just "interesting" values - select one of these, but not
				// all of the time
				selectDiscreteValue = (ThreadLocalRandom.current().nextDouble() < DISCRETE_VALUE_SELECTION_RATIO);
			}
			if (selectDiscreteValue) {
				final int index = ThreadLocalRandom.current().nextInt(discreteValues.length);
				logger.debug("selecting discrete value {} of {} values available", index + 1, discreteValues.length);
				result = Integer.toString(discreteValues[index].asInteger());
			}
		}

		// if no discrete value was used, generate a new one within the limits specified
		if (result == null) {
			final Integer minValue = schemaObject.getMinValue().orElse(Integer.MIN_VALUE);
			final Integer maxValue = schemaObject.getMaxValue().orElse(Integer.MAX_VALUE - 1);
			result = Integer.toString(ThreadLocalRandom.current().nextInt(minValue, maxValue + 1));
		}
		return logger.traceExit(result);
	}

	/**
	 * @param schemaObject
	 * @return
	 */
	private String generateValueForStringObject(IXMLDataObject schemaObject) {
		logger.traceEntry();
		String result = null;

		// handle discrete values first
		if (!schemaObject.getDiscreteValues().isEmpty()) {
			final IXMLDiscreteValue[] discreteValues = schemaObject.getDiscreteValues()
				.toArray(new IXMLDiscreteValue[0]);
			// check whether we have to select from a fixed value set
			boolean selectDiscreteValue = false;
			if (schemaObject.isFixedValueset().orElse(false)) {
				// yes - select one of the values all of the time
				selectDiscreteValue = true;
			} else {
				// no, the values are just "interesting" values - select one of these, but not
				// all of the time
				selectDiscreteValue = (ThreadLocalRandom.current().nextDouble() < DISCRETE_VALUE_SELECTION_RATIO);
			}
			if (selectDiscreteValue) {
				final int index = ThreadLocalRandom.current().nextInt(discreteValues.length);
				logger.debug("selecting discrete value {} of {} values available", index + 1, discreteValues.length);
				result = discreteValues[index].asString();
			}
		}

		// if no discrete value was used, generate a new one within the limits specified
		if (result == null) {
			// start with a prefixed value to provide a means of identification
			final int counterLength = this.getValueLength() - this.getValuePrefix().length();
			final String format = "%s%0" + counterLength + "d";
			final String prefixValue = String.format(format, this.valuePrefix, this.nextGeneratedValueCounter++);
			final String text = this.textGenerator.getWords(DEFAULT_STRING_WORD_COUNT_MIN,
					DEFAULT_STRING_WORD_COUNT_MAX);
			result = prefixValue + " " + text;

			// ensure the length restriction is met, if any is specified
			final Optional<Integer> maxLength = schemaObject.getMaxLength();
			if (maxLength.isPresent()) {
				final Integer maxLengthValue = maxLength.get();
				if (result.length() > maxLengthValue) {
					result = result.substring(0, maxLengthValue - 1);
				}
			}
		}
		return logger.traceExit(result);
	}

	@Override
	public String generateValue(IAddRawContentRule rule) {
		logger.traceEntry("for element {}", rule.getElementID());
		String value = null;
		final Optional<IRequestedValue> requestedValue = rule.getRequestedValue();
		if (requestedValue.isPresent()) {
			value = requestedValue.get().getValue();
			this.valueDescriptors.add(new ValueDescriptor(rule.getElementID(), rule.getID(), value, true));
		} else {
			// generate some raw content: some text with a few tags inside
			value = switch (ThreadLocalRandom.current().nextInt(6)) {
			case 1 -> "#TEXT# <b>#TEXT#</b> #TEXT#";
			case 2 -> "#TEXT# <i>#TEXT#</i> #TEXT#";
			case 3 -> "#TEXT# <a href=\"foobar\">#TEXT#</a> #TEXT#";
			case 4 -> "#TEXT# <p>#TEXT#</p> #TEXT#";
			case 5 -> "#TEXT# <div id=\"foobar\">#TEXT#</div> #TEXT#";
			default -> "#TEXT# <br/> #TEXT#";
			};
			while (value.contains("#TEXT#")) {
				value = value.replaceFirst("#TEXT#", this.textGenerator.getWords(5, 50));
			}
			this.valueDescriptors.add(new ValueDescriptor(rule.getElementID(), rule.getID(), value));
		}
		return logger.traceExit("with generated value \"{}\"", value);
	}

	@Override
	public String getValuePrefix() {
		if (this.valuePrefix == null) {
			selectPrefixAndLength();
		}
		return this.valuePrefix;
	}

	@Override
	public int getValueLength() {
		if (this.valueLength == null) {
			selectPrefixAndLength();
		}
		return this.valueLength;
	}

	/**
	 * Uses the {@link IPrefixSelector} to determine a unique prefix and a default
	 * length for the generated values.
	 */
	private void selectPrefixAndLength() {
		logger.traceEntry();
		final PrefixData data = this.prefixSelector.selectPrefix(this.stylesheet.getURI());
		logger.debug("will use prefix {} and a length {} to generate values", data.prefix(), data.valueLength());
		this.valuePrefix = data.prefix();
		this.valueLength = data.valueLength();
		logger.traceExit();
	}

	@Override
	public ImmutableSet<IValueDescriptor> getValueDescriptors() {
		return ImmutableSet.copyOf(this.valueDescriptors);
	}

}