Elasticsearch Vector Plugin
=======================

## Overview

Vector Plugin provides bit\_vector type for searcing documents.

## Version

[Versions in Maven Repository](http://central.maven.org/maven2/org/codelibs/elasticsearch-vector/)

### Issues/Questions

Please file an [issue](https://github.com/codelibs/elasticsearch-vector/issues "issue").

## Installation

    $ $ES_HOME/bin/elasticsearch-plugin install org.codelibs:elasticsearch-vector:7.5.0

## Getting Started

### Set Vector Field Type

This plugin provides the following field type:

- bit\_vector

```
    $ curl -XPUT 'localhost:9200/my_index' -d '{
    {
      "mappings": {
        "properties": {
          "my_vector": {
            "type": "bit_vector"
          },
          "my_text" : {
            "type" : "keyword"
          }
        }
      }
    }'
```

### Add Vector Data

```
    $ curl -XPUT "localhost:9200/my_index/_doc/1" -d '{
    {
      "my_text" : "text1",
      "my_vector" : [0, 1, 1]
    }'
```

### Search By Vectors

This plugin provides the following metrics functions:

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
        \"source\": \"pairwiseHammingDistance(params.query_vector, doc['my_bit_vector'], true)\",
        \"params\": {
          \"query_vector\": [1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0]
        }
      }
    }
  }
}"
```
