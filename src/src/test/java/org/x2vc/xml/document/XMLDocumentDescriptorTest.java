package org.x2vc.xml.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.x2vc.xml.value.IValueDescriptor;

import com.google.common.collect.ImmutableSet;

@ExtendWith(MockitoExtension.class)
class XMLDocumentDescriptorTest {

	@Mock
	private IDocumentModifier modifier;

	@Mock
	private IValueDescriptor valueDescriptor;

	@Test
	void testMinimal() {
		final XMLDocumentDescriptor descriptor = new XMLDocumentDescriptor.Builder("abcd", 8).build();
		assertEquals("abcd", descriptor.getValuePrefix());
		assertEquals(8, descriptor.getValueLength());
		assertFalse(descriptor.getModifier().isPresent());
	}

	@Test
	void testModifier() {
		final XMLDocumentDescriptor descriptor = new XMLDocumentDescriptor.Builder("abcd", 8)
			.withModifier(this.modifier).build();
		final Optional<IDocumentModifier> mod = descriptor.getModifier();
		assertTrue(mod.isPresent());
		assertSame(this.modifier, mod.get());
	}

	@ParameterizedTest
	@CsvSource({
		"qwer,qwer",
		"abcd0000,abcd0000",
		"abcd0000 Foo Bar,abcd0000",
		"Foo abcd0000 Bar,abcd0000",
		"abcd0000,abcd0000 Foo Bar",
		"abcd0000,Foo abcd0000 Bar",
		"abcd0000 Foo Bar,abcd0000 Foo Bar",
		"Foo abcd0000 Bar,abcd0000 Foo Bar",
		"abcd0000 Foo Bar,Foo abcd0000 Bar",
		"Foo abcd0000 Bar,Foo abcd0000 Bar"
	})
	void testValueDescriptor_Match(String testValue, String testQuery) {
		when(this.valueDescriptor.getValue()).thenReturn(testValue);
		final XMLDocumentDescriptor descriptor = new XMLDocumentDescriptor.Builder("abcd", 8)
				.addValueDescriptor(this.valueDescriptor).build();
		final Optional<ImmutableSet<IValueDescriptor>> vd = descriptor.getValueDescriptors(testQuery);
		assertTrue(vd.isPresent());
		assertEquals(1, vd.get().size());
		assertSame(this.valueDescriptor, vd.get().iterator().next());
	}

	@ParameterizedTest
	@CsvSource({
		"qwer,yxcv",
		"abcd0000,abcd1234",
		"abcd0000 Foo Bar,abcd1234",
		"Foo abcd0000 Bar,abcd1234",
		"abcd0000,abcd1234 Foo Bar",
		"abcd0000,Foo abcd1234 Bar",
		"abcd0000 Foo Bar,abcd1234 Foo Bar",
		"Foo abcd0000 Bar,abcd1234 Foo Bar",
		"abcd0000 Foo Bar,Foo abcd1234 Bar",
		"Foo abcd0000 Bar,Foo abcd1234 Bar"
	})
	void testValueDescriptor_NoMatch(String testValue, String testQuery) {
		when(this.valueDescriptor.getValue()).thenReturn(testValue);
		final XMLDocumentDescriptor descriptor = new XMLDocumentDescriptor.Builder("abcd", 8)
				.addValueDescriptor(this.valueDescriptor).build();
		final Optional<ImmutableSet<IValueDescriptor>> vd = descriptor.getValueDescriptors(testQuery);
		assertFalse(vd.isPresent());
	}

}
