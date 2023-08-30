package org.x2vc.xml.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddElementRuleTest {

	/**
	 * Test method for {@link org.x2vc.xml.request.AddElementRule#normalize()}.
	 */
	@Test
	void testNormalize() {
		final ISetAttributeRule originalAttributeRule = mock(ISetAttributeRule.class);
		final ISetAttributeRule normalizedAttributeRule = mock(ISetAttributeRule.class);
		when(originalAttributeRule.normalize()).thenReturn(normalizedAttributeRule);

		// add two content rules - order must be preserved

		final IContentGenerationRule originalContentRule1 = mock(IContentGenerationRule.class);
		final IContentGenerationRule normalizedContentRule1 = mock(IContentGenerationRule.class);
		when(originalContentRule1.normalize()).thenReturn(normalizedContentRule1);

		final IContentGenerationRule originalContentRule2 = mock(IContentGenerationRule.class);
		final IContentGenerationRule normalizedContentRule2 = mock(IContentGenerationRule.class);
		when(originalContentRule2.normalize()).thenReturn(normalizedContentRule2);

		final UUID ruleID = UUID.randomUUID();
		final UUID elementReferenceID = UUID.randomUUID();

		final AddElementRule originalRule = new AddElementRule.Builder(elementReferenceID).withRuleID(ruleID)
			.addAttributeRule(originalAttributeRule).addContentRule(originalContentRule1)
			.addContentRule(originalContentRule2).build();
		final AddElementRule normalizedRule = (AddElementRule) originalRule.normalize();

		assertNotSame(originalRule, normalizedRule);
		assertEquals(UUID.fromString("0000-00-00-00-000000"), normalizedRule.getID());
		assertEquals(elementReferenceID, normalizedRule.getElementReferenceID());
		assertEquals(Set.of(normalizedAttributeRule), normalizedRule.getAttributeRules());
		assertEquals(List.of(normalizedContentRule1, normalizedContentRule2), normalizedRule.getContentRules());
	}

}