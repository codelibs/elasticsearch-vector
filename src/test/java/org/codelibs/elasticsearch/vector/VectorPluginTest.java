package org.codelibs.elasticsearch.vector;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;

import java.util.Arrays;

import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import junit.framework.TestCase;

public class VectorPluginTest extends TestCase {

    private ElasticsearchClusterRunner runner;

    private String clusterName;

    @Override
    protected void setUp() throws Exception {
        clusterName = "es-minhash-" + System.currentTimeMillis();
        // create runner instance
        runner = new ElasticsearchClusterRunner();
        // create ES nodes
        runner.onBuild(new ElasticsearchClusterRunner.Builder() {
            @Override
            public void build(final int number, final Builder settingsBuilder) {
                settingsBuilder.put("http.cors.enabled", true);
                settingsBuilder.put("http.cors.allow-origin", "*");
                settingsBuilder.putList("discovery.seed_hosts", "127.0.0.1:9301");
                settingsBuilder.putList("cluster.initial_master_nodes", "127.0.0.1:9301");
            }
        }).build(newConfigs().clusterName(clusterName).numOfNode(1).pluginTypes("org.codelibs.elasticsearch.vector.VectorPlugin"));

        // wait for yellow status
        runner.ensureYellow();
    }

    @Override
    protected void tearDown() throws Exception {
        // close runner
        runner.close();
        // delete all files
        runner.clean();
    }

    public void test_runEs() throws Exception {

        final String index = "test_index";
        final String type = "test_type";

        {
            // create an index
            runner.createIndex(index, Settings.EMPTY);
            runner.ensureYellow(index);

            // create a mapping
            final XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()//
                    .startObject()//
                    .startObject("properties")//

                    .startObject("my_dense_vector")//
                    .field("type", "dense_float_vector")//
                    .endObject()//

                    .startObject("my_sparse_vector")//
                    .field("type", "sparse_float_vector")//
                    .endObject()//

                    .startObject("my_text")//
                    .field("type", "keyword")//
                    .endObject()//

                    .endObject()//
                    .endObject();
            runner.createMapping(index, type, mappingBuilder);
        }

        if (!runner.indexExists(index)) {
            fail();
        }

        // create 1000 documents
        for (int i = 1; i <= 1000; i++) {
            String text = "text" + i;
            float[] vector = new float[] { (float) (i), (float) (1 / i) };
            final IndexResponse indexResponse1 = runner.insert(index, type, String.valueOf(i),
                    "{\"my_text\":\"" + text + "\",\"my_dense_vector\":" + Arrays.toString(vector) + "}");
            assertEquals(Result.CREATED, indexResponse1.getResult());
        }
        runner.refresh();

        //        final Client client = runner.client();

    }

}