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
package org.codelibs.elasticsearch.vector;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codelibs.elasticsearch.vector.index.mapper.BitVectorFieldMapper;
import org.codelibs.elasticsearch.vector.index.mapper.DenseVectorFieldMapper;
import org.codelibs.elasticsearch.vector.index.mapper.SparseVectorFieldMapper;
import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.plugins.MapperPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;

public class VectorPlugin extends Plugin implements MapperPlugin, SearchPlugin {

    @Override
    public Map<String, Mapper.TypeParser> getMappers() {
        Map<String, Mapper.TypeParser> mappers = new LinkedHashMap<>();
        mappers.put(DenseVectorFieldMapper.CONTENT_TYPE, new DenseVectorFieldMapper.TypeParser());
        mappers.put(SparseVectorFieldMapper.CONTENT_TYPE, new SparseVectorFieldMapper.TypeParser());
        mappers.put(BitVectorFieldMapper.CONTENT_TYPE, new BitVectorFieldMapper.TypeParser());
        return Collections.unmodifiableMap(mappers);
    }
}
