/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.elasticsearch.vector.util;

import java.util.List;

import org.apache.lucene.util.BytesRef;
import org.codelibs.elasticsearch.vector.index.fielddata.BitVectorScriptDocValues;

public final class VectorScriptUtils {

    private VectorScriptUtils() {
        // noop
    }

    public static final class HammingDistance {
        final int[] queryVector;

        public HammingDistance(final List<Number> queryVector) {
            int size = queryVector.size() / 32;
            if (queryVector.size() % 32 > 0) {
                size++;
            }
            final int[] buf = new int[size];
            int pos = 31;
            int offset = 0;
            for (final Number i : queryVector) {
                int value = i.intValue();
                if (value != 0) {
                    value = 1;
                }
                buf[offset] |= value << pos;
                pos--;
                if (pos < 0) {
                    offset++;
                    pos = 31;
                }
            }
            this.queryVector = buf;
        }

        public double pairwiseHammingDistance(final BitVectorScriptDocValues dvs, final boolean recip) {
            final BytesRef value = dvs.getValue();
            if (value == null) {
                if (recip) {
                    return 0;
                } else {
                    return Double.MAX_VALUE;
                }
            }
            int count = 0;
            for (int i = 0; i < queryVector.length; i++) {
                final int v1 = queryVector[i];
                final int j = i * 4;
                final int v2 = (value.bytes[j] & 0xFF) << 24 | (value.bytes[j + 1] & 0xFF) << 16 | (value.bytes[j + 2] & 0xFF) << 8
                        | (value.bytes[j + 3] & 0xFF);
                count += Integer.bitCount(v1 ^ v2);
            }

            if (recip) {
                return 1.0 / (1.0 + count);
            } else {
                return count;
            }
        }
    }
}
