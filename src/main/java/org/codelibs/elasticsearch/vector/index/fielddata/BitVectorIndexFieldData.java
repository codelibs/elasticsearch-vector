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

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SortField;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.fielddata.IndexFieldData;
import org.elasticsearch.index.fielddata.IndexFieldData.XFieldComparatorSource.Nested;
import org.elasticsearch.index.fielddata.IndexFieldDataCache;
import org.elasticsearch.index.fielddata.plain.DocValuesIndexFieldData;
import org.elasticsearch.index.mapper.MappedFieldType;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.indices.breaker.CircuitBreakerService;
import org.elasticsearch.search.DocValueFormat;
import org.elasticsearch.search.MultiValueMode;
import org.elasticsearch.search.sort.BucketedSort;
import org.elasticsearch.search.sort.BucketedSort.ExtraData;
import org.elasticsearch.search.sort.SortOrder;

public class BitVectorIndexFieldData extends DocValuesIndexFieldData implements IndexFieldData<BitVectorAtomicFieldData> {

    public BitVectorIndexFieldData(final Index index, final String fieldName) {
        super(index, fieldName);
    }

    @Override
    public BitVectorAtomicFieldData load(final LeafReaderContext context) {
         return new BitVectorAtomicFieldData(context.reader(), fieldName);
    }

    @Override
    public BitVectorAtomicFieldData loadDirect(final LeafReaderContext context) throws Exception {
         return load(context);
    }

    @Override
    public SortField sortField(final Object missingValue, final MultiValueMode sortMode, final Nested nested, final boolean reverse) {
        throw new UnsupportedOperationException("Unsupported method invocation.");
    }

    public static class Builder implements IndexFieldData.Builder {

        @Override
        public IndexFieldData<?> build(final IndexSettings indexSettings, final MappedFieldType fieldType, final IndexFieldDataCache cache,
                final CircuitBreakerService breakerService, final MapperService mapperService) {
             return new BitVectorIndexFieldData(indexSettings.getIndex(), fieldType.name());
        }

    }

    @Override
    public BucketedSort newBucketedSort(BigArrays bigArrays, Object missingValue, MultiValueMode sortMode, Nested nested,
            SortOrder sortOrder, DocValueFormat format, int bucketSize, ExtraData extra) {
        throw new UnsupportedOperationException("Unsupported method invocation.");
    }

}
