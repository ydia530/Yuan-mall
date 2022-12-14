package com.yuan.mall.search.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yuan.common.to.es.SkuEsModel;
import com.yuan.common.utils.R;
import com.yuan.mall.search.config.EsConfig;
import com.yuan.mall.search.constant.EsConstant;
import com.yuan.mall.search.feign.ProductFeignService;
import com.yuan.mall.search.service.SearchService;
import com.yuan.mall.search.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.yuan.common.constant.SearchConstant;

/**
 * <p>Title: MallServiceImpl</p>
 * Description???
 * date???2020/6/12 23:06
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public SearchResult search(SearchParam Param) {

        SearchResult result = null;
        // 1.??????????????????
        SearchRequest searchRequest = buildSearchRequest(Param);
        try {
            // 2.??????????????????
            SearchResponse response = restHighLevelClient.search(searchRequest, EsConfig.COMMON_OPTIONS);

            // 3.??????????????????
            result = buildSearchResult(response, Param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public GoodListVo searchVx(SearchParam Param) {
        GoodListVo result = null;
        // 1.??????????????????
        SearchRequest searchRequest = buildSearchRequest(Param);
        try {
            // 2.??????????????????
            SearchResponse response = restHighLevelClient.search(searchRequest, EsConfig.COMMON_OPTIONS);

            // 3.??????????????????
            result = buildSearchResultVx(response, Param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Set<String> getSearchHistory() {
        return redisTemplate.opsForZSet().reverseRangeByScore(SearchConstant.HOT_SEARCH, 0, 20);
    }

    private GoodListVo buildSearchResultVx(SearchResponse response, SearchParam param) {

        GoodListVo result = new GoodListVo();
        // 1.?????????????????????????????????
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        if(hits.getHits() != null &&  hits.getHits().length > 0){
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                // ES????????????????????????
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if(!StringUtils.isEmpty(param.getKeyword())){
                    // 1.1 ???????????????????????????
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlightFields = skuTitle.getFragments()[0].string();
                    // 1.2 ??????????????????
                    esModel.setSkuTitle(highlightFields);
                }
                esModels.add(esModel);
            }
        }

        esModels.forEach(System.out::println);
        List<GoodVo> goods = new ArrayList<>();
        esModels.forEach(skuEsModel -> {
            GoodVo goodVo = new GoodVo();
            goodVo.setAvailable(1);
            goodVo.setCategoryIds(skuEsModel.getCatalogId());
            goodVo.setPrice(skuEsModel.getSkuPrice());
            goodVo.setSpuTagList(new ArrayList<>());
            goodVo.setThumb(skuEsModel.getSkuImg());
            goodVo.setTitle( skuEsModel.getSkuTitle());
            goodVo.setSpuId(skuEsModel.getSpuId());
            goodVo.setOriginPrice(skuEsModel.getSkuPrice().add(new BigDecimal(200)));
            goods.add(goodVo);
        });
        result.setSpuList(goods);
        // 5.????????????-??????
        result.setPageNum(param.getPageNum());

        // ????????????
        long total = hits.getTotalHits().value;

        result.setTotalCount(total);

        return result;
    }

    /**
     * ??????????????????  [??????????????????]
     */
    private SearchRequest buildSearchRequest(SearchParam Param) {
        // ???????????????DSL?????????
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 1. ???????????? ??????(??????????????????????????????????????????????????????) ?????????????????????Query
        // 1.1 must
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(Param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",Param.getKeyword()));
        }
        // 1.2 bool - filter Catalog3Id
        if(!StringUtils.isEmpty(Param.getCategory())){
            boolQuery.filter(QueryBuilders.termQuery("catalogId", Param.getCategory()));
        }
        // 1.2 bool - brandId [??????]
        if(Param.getBrandId() != null && Param.getBrandId().size() > 0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId", Param.getBrandId()));
        }
        // ????????????
        if(Param.getAttrs() != null && Param.getAttrs().size() > 0){

            for (String attrStr : Param.getAttrs()) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                // ?????????id  ?????????????????????
                String attrId = s[0];
                String[] attrValue = s[1].split(":");
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                // ?????????????????????Query ???????????????????????????????????? nested ??????
                NestedQueryBuilder attrsQuery = QueryBuilders.nestedQuery("attrs", boolQueryBuilder, ScoreMode.None);
                boolQuery.filter(attrsQuery);
            }
        }
        // 1.2 bool - filter [??????]
        if(Param.getHasStock() != null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",Param.getHasStock() == 1));
        }
        // 1.2 bool - filter [????????????]
        if(!StringUtils.isEmpty(Param.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = Param.getSkuPrice().split("_");
            if(s.length == 2){
                // ???????????? ????????????
                rangeQuery.gte(s[0]).lte(s[1]);
            }else if(s.length == 1){
                // ????????????
                if(Param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(s[0]);
                }
                if(Param.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }

        // ??????????????????????????????????????????
        sourceBuilder.query(boolQuery);

        // 1.??????
//        if(!StringUtils.isEmpty(Param.getSort())){
//            String sort = Param.getSort();
//            // sort=hotScore_asc/desc
//            String[] s = sort.split("_");
//            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
//            sourceBuilder.sort(s[0], order);
//        }
        // 2.?????? pageSize ??? 5
        sourceBuilder.from((Param.getPageNum()-1) * EsConstant.PRODUCT_PASIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PASIZE);

        // 3.??????
        if(!StringUtils.isEmpty(Param.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }
        // ????????????
        // TODO 1.????????????
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        // ????????????????????????
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        // ????????????????????? sourceBuilder
        sourceBuilder.aggregation(brand_agg);
        // TODO 2.????????????
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        // ????????????????????? sourceBuilder
        sourceBuilder.aggregation(catalog_agg);
        // TODO 3.???????????? attr_agg ?????????????????????
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        // 3.1 ????????????????????????attrId
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 3.1.1 ?????????????????????attrId?????????attrName
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 3.1.2 ?????????????????????attrId?????????????????????????????????attrValue	???????????????????????????????????? ?????????50
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        // 3.2 ???????????????????????????????????????
        attr_agg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attr_agg);
        log.info("\n???????????????->\n" + sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * ?????????????????? ??????catalogId ???brandId???attrs.attrId??????????????????????????????0-6000??????????????????????????????skuTitle???????????????
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam Param) {
        SearchResult result = new SearchResult();
        // 1.?????????????????????????????????
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        if(hits.getHits() != null &&  hits.getHits().length > 0){
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                // ES????????????????????????
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if(!StringUtils.isEmpty(Param.getKeyword())){
                    // 1.1 ???????????????????????????
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlightFields = skuTitle.getFragments()[0].string();
                    // 1.2 ??????????????????
                    esModel.setSkuTitle(highlightFields);
                }
                esModels.add(esModel);
            }
        }
        result.setProduct(esModels);
        esModels.forEach(System.out::println);

        // 2.????????????????????????????????????????????????
        ArrayList<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 2.1 ???????????????id
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            // 2.2 ?????????????????????
            String attr_name = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);
            // 2.3 ????????????????????????
            List<String> attr_value = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attr_value);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        // 3.????????????????????????????????????????????????
        ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            // 3.1 ???????????????id
            long brnadId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brnadId);
            // 3.2 ??????????????????
            String brand_name = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brand_name);
            // 3.3 ?????????????????????
            String brand_img = ((ParsedStringTerms) (bucket.getAggregations().get("brand_img_agg"))).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        // 4.??????????????????????????????????????????
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            // ????????????id
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            // ???????????????
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        // ================????????????????????????????????????
        // 5.????????????-??????
        result.setPageNum(Param.getPageNum());

        // ????????????
        long total = hits.getTotalHits().value;

        result.setTotal(total);

        // ????????????????????????
        int totalPages = (int)(total / EsConstant.PRODUCT_PASIZE + 0.999999999999);
        result.setTotalPages(totalPages);
        // ???????????????
        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 1;i <= totalPages; i++){
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        // 6.???????????????????????????
        if(Param.getAttrs() != null){
            List<SearchResult.NavVo> navVos = Param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.getAttrsInfo(Long.parseLong(s[0]));
                // ??????????????????????????????????????? ????????????????????????
                result.getAttrIds().add(Long.parseLong(s[0]));
                if(r.getCode() == 0){
                    AttrResponseVo data = r.getData(new TypeReference<AttrResponseVo>(){});
                    navVo.setName(data.getAttrName());
                }else{
                    // ???????????????id????????????
                    navVo.setName(s[0]);
                }
                // ???????????????????????? ??????????????????
                String replace = replaceQueryString(Param, attr, "attrs");
                navVo.setLink("http://search.yuanmall.top/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }

        // ???????????????
        if(Param.getBrandId() != null && Param.getBrandId().size() > 0){
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setName("??????");
            // TODO ????????????????????????
            R r = productFeignService.brandInfo(Param.getBrandId());
            if(r.getCode() == 0){
                List<BrandVo> brand = r.getData("data", new TypeReference<List<BrandVo>>() {});
                StringBuffer buffer = new StringBuffer();
                // ??????????????????ID
                String replace = "";
                for (BrandVo brandVo : brand) {
                    buffer.append(brandVo.getBrandName() + ";");
                    replace = replaceQueryString(Param, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.yuanmall.top/list.html?" + replace);
            }
            navs.add(navVo);
        }
        return result;
    }

    /**
     * ????????????
     * key ??????????????????key
     */
    private String replaceQueryString(SearchParam Param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value,"UTF-8");
            // ??????????????????????????????java????????????
            encode = encode.replace("+","%20");
            encode = encode.replace("%28", "(").replace("%29",")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Param.get_queryString().replace("&" + key + "=" + encode, "");
    }
}
