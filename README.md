Elasticsearch Vector Plugin
=======================

## Overview

Vector Plugin provides vector type for searcing documents.

## Version

[Versions in Maven Repository](http://central.maven.org/maven2/org/codelibs/elasticsearch-vector/)

### Issues/Questions

Please file an [issue](https://github.com/codelibs/elasticsearch-vector/issues "issue").

## Installation

    $ $ES_HOME/bin/elasticsearch-plugin install org.codelibs:elasticsearch-vector:7.2.0

## Getting Started

### Set Vector Filed Type

This plugin provides `dense_float_vector` and `sparse_float_vector` field type.

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

### Add Vector Data

    $ curl -XPUT "localhost:9200/my_index/_doc/1" -d '{
    {
      "my_text" : "text1",
      "my_vector" : [0.5, 10, 6]
    }'

