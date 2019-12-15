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
package org.codelibs.elasticsearch.vector.index.fielddata;

import java.io.IOException;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.index.fielddata.ScriptDocValues;

public class BitVectorScriptDocValues extends ScriptDocValues<BytesRef> {

    private final BinaryDocValues docValues;

    private BytesRef value;

    public BitVectorScriptDocValues(final BinaryDocValues docValues) {
        this.docValues = docValues;
    }

    @Override
    public void setNextDocId(final int docId) throws IOException {
        if (docValues.advanceExact(docId)) {
            value = docValues.binaryValue();
        } else {
            value = null;
        }
    }

    @Override
    public BytesRef get(final int arg0) {
        throw new UnsupportedOperationException("Unsupported method invocation.");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Unsupported method invocation.");
    }

    public BytesRef getValue() {
        return value;
    }

}
