package com.nowcoder.community;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.service.DiscussPostService;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ElasticSearchTest {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

//    @Test
//    public void saveData() throws IOException {
//
//        XContentBuilder mapping = XContentFactory.jsonBuilder()
//                .startObject()
//                .startObject("properties")
//                .startObject("id").field("type", "integer").endObject()
//                .startObject("userId").field("type", "integer").endObject()
//                .startObject("type").field("type", "integer").endObject()
//                .startObject("status").field("type", "integer").endObject()
//                .startObject("commentCount").field("type", "integer").endObject()
//                .startObject("title").field("type", "text").field("analyzer", "ik_max_word").field("searchAnalyzer", "ik_smart").endObject()
//                .startObject("content").field("type", "text").field("analyzer", "ik_max_word").field("searchAnalyzer", "ik_smart").endObject()
//                .startObject("createTime").field("type", "date").endObject()
//                .startObject("score").field("type", "double").endObject()
//                .endObject()
//                .endObject();
//        RestClient restClient = RestClient.builder(
//                new HttpHost("localhost", 9200)).build();
//
//        // create the transport with a Mapper
//        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//
//        // create the api client
//        ElasticsearchClient client = new ElasticsearchClient(transport);
//
//        DiscussPost post = discussPostService.findDiscussPostById(289);
//
//        IndexResponse response = client.index(i->i
//                .index("discusspost")
//                .id(String.valueOf(post.getId()))
//                .document(post));
//
//
//        System.out.println(response.version());
//    }
//
//    @Test
//    public void saveDataList() throws IOException {
//
//        List<DiscussPost> discussPostList = discussPostService.findDiscussPostList(132, 0, 10);
//        if (!discussPostList.isEmpty()) {
//            BulkRequest.Builder br = new BulkRequest.Builder();
//
//            for (DiscussPost discussPost : discussPostList) {
//                br.operations(op -> op
//                        .index(idx -> idx
//                                .index("discusspost")
//                                .id(String.valueOf(discussPost.getId()))
//                                .document(discussPost))
//                );
//            }
//
//            RestClient restClient = RestClient.builder(
//                    new HttpHost("localhost", 9200)).build();
//
//            // create the transport with a Mapper
//            ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//
//            // create the api client
//            ElasticsearchClient client = new ElasticsearchClient(transport);
//
//            // create the api client
//            BulkResponse response = client.bulk(br.build());
//
//            if (response.errors()) {
//                System.out.println("Bulk error");
//                for (BulkResponseItem item : response.items()) {
//                    if (item.error() != null) {
//                        System.out.println(item.error().reason());
//                    }
//                }
//            }
//        }
//    }
//
//    @Test
//    public void testDelete() throws IOException {
//        RestClient restClient = RestClient.builder(
//                new HttpHost("localhost", 9200)).build();
//
//        // create the transport with a Mapper
//        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//
//        // create the api client
//        ElasticsearchClient client = new ElasticsearchClient(transport);
//
//        DeleteResponse response = client.delete(i->i
//                .index("discusspost")
//                .id(String.valueOf(289)));
//
//        System.out.println(response.result());
//    }
//
//    @Test
//    public void testDelete2() throws IOException {
//        RestClient restClient = RestClient.builder(
//                new HttpHost("localhost", 9200)).build();
//
//        // create the transport with a Mapper
//        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//
//        // create the api client
//        ElasticsearchClient client = new ElasticsearchClient(transport);
//
//        DeleteRequest request = new DeleteRequest.Builder().index("discusspost").id(String.valueOf(244)).build();
//        DeleteResponse response = client.delete(request);
//        System.out.println(response.result());
//    }
//
//   @Test
//    public void testSearch() throws IOException {
//       RestClient restClient = RestClient.builder(
//               new HttpHost("localhost", 9200)).build();
//
//       // create the transport with a Mapper
//       ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//
//       // create the api client
//       ElasticsearchClient client = new ElasticsearchClient(transport);
//
//       SearchResponse<DiscussPost> response = client.search(s->s
//               .index("discusspost")
//               .query(q->q
//                       .match(t->t
//                               .field("title")
//                               .query("测试")
//                       )
//               ),
//               DiscussPost.class
//       );
//
////       TotalHits total = response.hits().total();
//
//       List<Hit<DiscussPost>> hits = response.hits().hits();
//       for(Hit<DiscussPost> hit: hits){
//           DiscussPost post = hit.source();
//           System.out.println(post);
//       }
//   }

    @Test
    public void testDelete4() {
        discussPostRepository.deleteAll();
    }

    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(168, 0, 10));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(101,0,100));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(102,0,100));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(103,0,100));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(111,0,100));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(112,0,100));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(131,0,100));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(132,0,100));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(133,0,100));
        discussPostRepository.saveAll(discussPostService.findDiscussPostList(134,0,100));
    }

    @Test
    public void highlightQuery() throws Exception{
        SearchRequest searchRequest;    // discusspost是索引名，就是表名
        searchRequest = new SearchRequest("discusspost");
        Map<String,Object> res = new HashMap<>();

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0)// 指定从哪条开始查询
                .size(10)// 需要查出的总记录条数
                .highlighter(highlightBuilder);//高亮
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<DiscussPost> list = new ArrayList<>();
        long total = searchResponse.getHits().getTotalHits().value;
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);

            // 处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
//            System.out.println(discussPost);
            list.add(discussPost);
        }
        res.put("list",list);
        res.put("total",total);
        if(res.get("list")!= null){
            for (DiscussPost post : list = (List<DiscussPost>) res.get("list")) {
                System.out.println(post);
            }
            System.out.println(res.get("total"));
        }
    }
}
