Elasticsearch Vector Plugin
=======================

## Overview

Vector Plugin provides vector type for searcing documents.
The vector type is forked from [mapper-extras](https://github.com/elastic/elasticsearch/tree/7f3ab4524f8745b03b1e0025a56eb2e2dfe02b7a/modules/mapper-extras).

## Version

[Versions in Maven Repository](http://central.maven.org/maven2/org/codelibs/elasticsearch-vector/)

### Issues/Questions

Please file an [issue](https://github.com/codelibs/elasticsearch-vector/issues "issue").

## Installation

    $ $ES_HOME/bin/elasticsearch-plugin install org.codelibs:elasticsearch-vector:7.2.1

## Getting Started

### Set Vector Field Type

This plugin provides the following field type:

- dense_float_vector
- sparse_float_vector
- bit_vector

```
    $ curl -XPUT 'localhost:9200/my_index' -d '{
    {
      "mappings": {
        "properties": {
          "my_vector": {
            "type": "dense_float_vector"
          },
          "my_text" : {
            "type" : "keyword"
          }
        }
      }
    }'
```

### Add Vector Data

    $ curl -XPUT "localhost:9200/my_index/_doc/1" -d '{
    {
      "my_text" : "text1",
      "my_vector" : [0.5, 10, 6]
    }'

### Search By Vectors

This plugin provides the following metrics functions:

- pairwiseCosineSimilarity
- pairwiseCosineSimilaritySparse
- pairwiseDotProduct
- pairwiseDotProductSparse
- pairwiseHammingDistance

For examples, the usage is:

```
curl -s -XPOST "localhost:9200/my_index/_search?pretty" -H "Content-Type: application/json" -d "
{
  \"query\": {
    \"script_score\": {
      \"query\": {
        \"match_all\": {}
      },
      \"script\": {
        \"source\": \"pairwiseCosineSimilarity(params.query_vector, doc['my_dense_vector']) + 1.0\",
        \"params\": {
          \"query_vector\": [10, 10, 10]
        }
      }
    }
  }
}"
```

```
curl -s -XPOST "localhost:9200/my_index/_search?pretty" -H "Content-Type: application/json" -d "
{
  \"query\": {
    \"script_score\": {
      \"query\": {
        \"match_all\": {}
      },
      \"script\": {
        \"source\": \"16.0 - pairwiseHammingDistance(params.query_vector, doc['my_bit_vector'])\",
        \"params\": {
          \"query_vector\": [1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0]
        }
      }
    }
  }
}"
```
