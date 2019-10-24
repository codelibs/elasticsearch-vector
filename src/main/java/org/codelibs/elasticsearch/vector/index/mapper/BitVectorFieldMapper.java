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
package org.codelibs.elasticsearch.vector.index.mapper;

import static org.elasticsearch.common.xcontent.XContentParserUtils.ensureExpectedToken;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.DocValuesFieldExistsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.codelibs.elasticsearch.vector.index.query.VectorDVIndexFieldData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser.Token;
import org.elasticsearch.index.fielddata.IndexFieldData;
import org.elasticsearch.index.mapper.ArrayValueMapperParser;
import org.elasticsearch.index.mapper.FieldMapper;
import org.elasticsearch.index.mapper.MappedFieldType;
import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.index.mapper.ParseContext;
import org.elasticsearch.index.query.QueryShardContext;
import org.elasticsearch.search.DocValueFormat;

/**
 * A {@link FieldMapper} for indexing a bit vector.
 */
public class BitVectorFieldMapper extends FieldMapper implements ArrayValueMapperParser {

    public static final String CONTENT_TYPE = "bit_vector";
    public static final int MAX_DIMS_COUNT = 1024 * 32; //maximum allowed number of dimensions

    public static class Defaults {
        public static final MappedFieldType FIELD_TYPE = new BitVectorFieldType();

        static {
            FIELD_TYPE.setTokenized(false);
            FIELD_TYPE.setIndexOptions(IndexOptions.NONE);
            FIELD_TYPE.setHasDocValues(true);
            FIELD_TYPE.setOmitNorms(true);
            FIELD_TYPE.freeze();
        }
    }

    public static class Builder extends FieldMapper.Builder<Builder, BitVectorFieldMapper> {

        public Builder(String name) {
            super(name, Defaults.FIELD_TYPE, Defaults.FIELD_TYPE);
            builder = this;
        }

        @Override
        public BitVectorFieldType fieldType() {
            return (BitVectorFieldType) super.fieldType();
        }

        @Override
        public BitVectorFieldMapper build(BuilderContext context) {
            setupFieldType(context);
            return new BitVectorFieldMapper(
                    name, fieldType, defaultFieldType,
                    context.indexSettings(), multiFieldsBuilder.build(this, context), copyTo);
        }
    }

    public static class TypeParser implements Mapper.TypeParser {
        @Override
        public Mapper.Builder<?,?> parse(String name, Map<String, Object> node, ParserContext parserContext) throws MapperParsingException {
            BitVectorFieldMapper.Builder builder = new BitVectorFieldMapper.Builder(name);
            return builder;
        }
    }

    public static final class BitVectorFieldType extends MappedFieldType {

        public BitVectorFieldType() {}

        protected BitVectorFieldType(BitVectorFieldType ref) {
            super(ref);
        }

        public BitVectorFieldType clone() {
            return new BitVectorFieldType(this);
        }

        @Override
        public String typeName() {
            return CONTENT_TYPE;
        }

        @Override
        public DocValueFormat docValueFormat(String format, ZoneId timeZone) {
            throw new UnsupportedOperationException(
                "Field [" + name() + "] of type [" + typeName() + "] doesn't support docvalue_fields or aggregations");
        }

        @Override
        public Query existsQuery(QueryShardContext context) {
            return new DocValuesFieldExistsQuery(name());
        }

        @Override
        public IndexFieldData.Builder fielddataBuilder(String fullyQualifiedIndexName) {
            return new VectorDVIndexFieldData.Builder(true);
        }

        @Override
        public Query termQuery(Object value, QueryShardContext context) {
            throw new UnsupportedOperationException(
                "Field [" + name() + "] of type [" + typeName() + "] doesn't support queries");
        }
    }

    private BitVectorFieldMapper(String simpleName, MappedFieldType fieldType, MappedFieldType defaultFieldType,
                                   Settings indexSettings, MultiFields multiFields, CopyTo copyTo) {
        super(simpleName, fieldType, defaultFieldType, indexSettings, multiFields, copyTo);
        assert fieldType.indexOptions() == IndexOptions.NONE;
    }

    @Override
    protected BitVectorFieldMapper clone() {
        return (BitVectorFieldMapper) super.clone();
    }

    @Override
    public BitVectorFieldType fieldType() {
        return (BitVectorFieldType) super.fieldType();
    }

    @Override
    public void parse(ParseContext context) throws IOException {
        if (context.externalValueSet()) {
            throw new IllegalArgumentException("Field [" + name() + "] of type [" + typeName() + "] can't be used in multi-fields");
        }

        byte[] buf = new byte[0];
        int pos = 7;
        int offset = 0;
        int dim = 0;
        for (Token token = context.parser().nextToken(); token != Token.END_ARRAY; token = context.parser().nextToken()) {
            ensureExpectedToken(Token.VALUE_NUMBER, token, context.parser()::getTokenLocation);
            short value = context.parser().shortValue(true);
            if (buf.length < (offset + 1)) {
                buf = ArrayUtil.grow(buf, offset + 1);
            }
            if (value != 0) {
                value = 1;
            }
            buf[offset] |= (byte) (value << pos);
            pos--;
            if (pos < 0) {
                offset++;
                pos = 7;
            }
            if (dim++ >= MAX_DIMS_COUNT) {
                throw new IllegalArgumentException("Field [" + name() + "] of type [" + typeName()
                        + "] has exceeded the maximum allowed number of dimensions of [" + MAX_DIMS_COUNT + "]");
            }
        }
        offset++;
        while (offset % 4 != 0) {
            buf = ArrayUtil.grow(buf, offset + 1);
            buf[offset] = 0;
            offset++;
        }
        BinaryDocValuesField field = new BinaryDocValuesField(fieldType().name(), new BytesRef(buf, 0, offset));
        if (context.doc().getByKey(fieldType().name()) != null) {
            throw new IllegalArgumentException("Field [" + name() + "] of type [" + typeName() +
                "] doesn't not support indexing multiple values for the same field in the same document");
        }
        context.doc().addWithKey(fieldType().name(), field);
    }

    @Override
    protected void parseCreateField(ParseContext context, List<IndexableField> fields) {
        throw new AssertionError("parse is implemented directly");
    }

    @Override
    protected String contentType() {
        return CONTENT_TYPE;
    }
}
