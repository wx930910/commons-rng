/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rng.core.source64;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

import org.junit.Assert;
import org.junit.Test;

/**
 * The tests the caching of calls to {@link LongProvider#nextLong()} are used as
 * the source for {@link LongProvider#nextInt()} and
 * {@link LongProvider#nextBoolean()}.
 */
public class LongProviderTest {
	public static LongProvider mockLongProvider1(long value) {
		long[] mockFieldVariableValue = new long[1];
		LongProvider mockInstance = spy(LongProvider.class);
		mockFieldVariableValue[0] = ~value;
		doAnswer((stubInvo) -> {
			mockFieldVariableValue[0] = ~mockFieldVariableValue[0];
			return mockFieldVariableValue[0];
		}).when(mockInstance).next();
		return mockInstance;
	}

	/**
	 * A simple class to return a fixed value as the source for
	 * {@link LongProvider#next()}.
	 */
	static final class FixedLongProvider extends LongProvider {
		/** The value. */
		private long value;

		/**
		 * @param value the value
		 */
		FixedLongProvider(long value) {
			this.value = value;
		}

		@Override
		public long next() {
			return value;
		}
	}

	/**
	 * This test ensures that the call to {@link LongProvider#nextInt()} returns the
	 * upper and then lower 32-bits from {@link LongProvider#nextLong()}.
	 */
	@Test
	public void testNextInt() {
		final int max = 5;
		for (int i = 0; i < max; i++) {
			for (int j = 0; j < max; j++) {
				// Pack into upper then lower bits
				final long value = (((long) i) << 32) | (j & 0xffffffffL);
				final LongProvider provider = new FixedLongProvider(value);
				Assert.assertEquals("1st call not the upper 32-bits", i, provider.nextInt());
				Assert.assertEquals("2nd call not the lower 32-bits", j, provider.nextInt());
				Assert.assertEquals("3rd call not the upper 32-bits", i, provider.nextInt());
				Assert.assertEquals("4th call not the lower 32-bits", j, provider.nextInt());
			}
		}
	}

	/**
	 * This test ensures that the call to {@link LongProvider#nextBoolean()} returns
	 * each of the bits from a call to {@link LongProvider#nextLong()}.
	 *
	 * <p>
	 * The order should be from the least-significant bit.
	 */
	@Test
	public void testNextBoolean() {
		for (int i = 0; i < Long.SIZE; i++) {
			// Set only a single bit in the source
			final long value = 1L << i;
			final LongProvider provider = LongProviderTest.mockLongProvider1(value);
			// Test the result for a single pass over the long
			for (int j = 0; j < Long.SIZE; j++) {
				final boolean expected = i == j;
				Assert.assertEquals("Pass 1, bit " + j, expected, provider.nextBoolean());
			}
			// The second pass should use the opposite bits
			for (int j = 0; j < Long.SIZE; j++) {
				final boolean expected = i != j;
				Assert.assertEquals("Pass 2, bit " + j, expected, provider.nextBoolean());
			}
		}
	}
}
