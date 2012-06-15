/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sirix.axis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sirix.Holder;
import org.sirix.TestHelper;
import org.sirix.api.IAxis;
import org.sirix.exception.AbsTTException;
import org.sirix.settings.EFixed;

public class AbsAxisTest {

  private Holder holder;

  @Before
  public void setUp() throws AbsTTException {
    TestHelper.deleteEverything();
    TestHelper.createTestDocument();
    holder = Holder.generateRtx();
  }

  @After
  public void tearDown() throws AbsTTException {
    holder.close();
    TestHelper.closeEverything();
  }

  public static void testIAxisConventions(final IAxis axis, final long[] expectedKeys) {
    // IAxis Convention 1.
    final long startKey = axis.getTransaction().getNode().getNodeKey();

    final long[] keys = new long[expectedKeys.length];
    int offset = 0;
    while (axis.hasNext()) {
      axis.next();
      // IAxis results.
      assertTrue(offset < expectedKeys.length);
      keys[offset++] = axis.getTransaction().getNode().getNodeKey();

      // IAxis Convention 3.
      axis.getTransaction().moveToDocumentRoot();
    }

    // IAxis Convention 5.
    assertEquals(startKey, axis.getTransaction().getNode().getNodeKey());

    // IAxis results.
    assertArrayEquals(expectedKeys, keys);
  }

  public static void testIAxisConventionsNext(final IAxis axis, final long[] expectedKeys) {
    // IAxis Convention 1.
    final long startKey = axis.getTransaction().getNode().getNodeKey();

    final long[] keys = new long[expectedKeys.length];
    int offset = 0;

    try {
      while (axis.next() != EFixed.NULL_NODE_KEY.getStandardProperty()) {
        // IAxis results.
        assertTrue(offset < expectedKeys.length);
        keys[offset++] = axis.getTransaction().getNode().getNodeKey();
      }
    } catch (final NoSuchElementException e) {
    }

    // IAxis Convention 5.
    assertEquals(startKey, axis.getTransaction().getNode().getNodeKey());

    // IAxis results.
    assertArrayEquals(expectedKeys, keys);
  }

  @Test
  public void testIAxisUserExample() throws AbsTTException {
    final IAxis axis = new DescendantAxis(holder.getRtx());
    long count = 0L;
    while (axis.hasNext()) {
      axis.next();
      count += 1;
    }
    Assert.assertEquals(10L, count);
  }

}