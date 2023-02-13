package com.lc.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.lc.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Qualifier("esRestClient")
    @Autowired
    private RestHighLevelClient client;
    @Data
    @ToString
     static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
        public void setAccount_number(int account_number) {
            this.account_number = account_number;
        }
        public int getAccount_number() {
            return account_number;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
        public int getBalance() {
            return balance;
        }
     }

    /**
     * {
     *     suId:1
     *     skuTitle:华为xx
     *     price:998
     *     saleCount:99
     *     attrs[
     *        {尺寸:5寸}
     *        {cpu:高通845
     *        {分辨率:全高清}
     *     ]
     *
     *
     * }
     * @throws IOException
     */

    @Test
    public void searchData() throws IOException {
        //1.创建检索请求
        SearchRequest request = new SearchRequest();
        request.indices("bank");
        //指定DSL 检索条件 SearchSourceBuilder searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.1构造检索条件
//        searchSourceBuilder.query();
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//        searchSourceBuilder.aggregation();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

        //1.2按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        request.source(searchSourceBuilder);
        //1.3计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);
        System.out.println("检索条件:"+searchSourceBuilder.toString());
        //2.执行检索
        SearchResponse response=client.search(request, GulimallElasticSearchConfig.COMMON_OPTIONS);


        //3.分析结果
        System.out.println(response.toString());
        Map map = JSON.parseObject(response.toString(), Map.class);
        //3.1获取所有查到的数据
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String string = hit.getSourceAsString();
            Account account = JSON.parseObject(string, Account.class);
            System.out.println("account:" + account.toString());
        }
        //3.2获取这次检索到的分析信息
        Aggregations aggregations = response.getAggregations();
//        for (Aggregation aggregation : aggregations.asList()) {
//            System.out.println("当前聚合:" + aggregation.getName());
//
//        }
        Terms ageAgg1 = aggregations.get("ageAgg");

        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            Number keyAsNumber = bucket.getKeyAsNumber();
            System.out.println("年龄：" + keyAsNumber+":"+bucket.getDocCount());
        }
        Avg balanceAVG = aggregations.get("balanceAvg");
        System.out.println("平均薪资" + balanceAVG.getValue());


    }




    /**可以存储和更新数据
     *
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        //indexRequest.source("username","zhangsan","age","18","gender","男");
        User user = new User();
        user.setUserName("zhangsan");
        user.setAge(18);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString,XContentType.JSON);//要保存的内容
        //执行操作
        IndexResponse index = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);


        //提取有用的响应数据
        System.out.println(index);

    }
    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    public void contextLoads(){
        System.out.println(client);

    }

}
