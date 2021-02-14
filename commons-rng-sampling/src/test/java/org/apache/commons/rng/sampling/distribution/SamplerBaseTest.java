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
package org.apache.commons.rng.sampling.distribution;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the {@link SamplerBase}. The class is deprecated but is public. The
 * methods should be tested to ensure correct functionality.
 */
public class SamplerBaseTest {
	public static SamplerBase mockSamplerBase1(UniformRandomProvider rng) {
		SamplerBase mockInstance = spy(new SamplerBase(rng));
		doAnswer((stubInvo) -> {
			return stubInvo.callRealMethod();
		}).when(mockInstance).nextLong();
		doAnswer((stubInvo) -> {
			return stubInvo.callRealMethod();
		}).when(mockInstance).nextDouble();
		doAnswer((stubInvo) -> {
			return stubInvo.callRealMethod();
		}).when(mockInstance).nextInt();
		doAnswer((stubInvo) -> {
			return stubInvo.callRealMethod();
		}).when(mockInstance).nextInt(anyInt());
		return mockInstance;
	}

	@Test
	public void testNextMethods() {
		final UniformRandomProvider rng1 = RandomSource.create(RandomSource.SPLIT_MIX_64, 0L);
		final UniformRandomProvider rng2 = RandomSource.create(RandomSource.SPLIT_MIX_64, 0L);
		final SamplerBase sampler = SamplerBaseTest.mockSamplerBase1(rng2);
		final int n = 256;
		for (int i = 0; i < 3; i++) {
			Assert.assertEquals(rng1.nextDouble(), sampler.nextDouble(), 0);
			Assert.assertEquals(rng1.nextInt(), sampler.nextInt());
			Assert.assertEquals(rng1.nextInt(n), sampler.nextInt(n));
			Assert.assertEquals(rng1.nextLong(), sampler.nextLong());
		}
	}

	@Test
	public void testToString() {
		final UniformRandomProvider rng = RandomSource.create(RandomSource.SPLIT_MIX_64, 0L);
		final SamplerBase sampler = SamplerBaseTest.mockSamplerBase1(rng);
		Assert.assertTrue(sampler.toString().contains("rng"));
	}
}
