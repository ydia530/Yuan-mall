package com.yuan.mall.member.utlis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yuan.common.constant.SearchConstant.HOT_SEARCH;
import static com.yuan.common.constant.SearchConstant.RECENT_SEARCH;

/**
 * @author Yuan Diao
 * @date 2022/8/10
 */
@Component
public class SearchRedisHelper {

    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 最近搜索的大小
     */
    public static final Integer HISTORY_SEARCH_SIZE = 10;



    /**
     * redis添加最近搜索
     */
    public void addRedisRecentSearch(Integer userId, String search) {
        String key = RECENT_SEARCH + userId;
        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();
        zSet.add(key, search, Instant.now().getEpochSecond());

        //超过上限移除最早的一次搜索记录
        if (zSet.zCard(key) > HISTORY_SEARCH_SIZE){
            zSet.removeRange(key, 0,zSet.zCard(key) - HISTORY_SEARCH_SIZE-1);
        }

        zSet.incrementScore(HOT_SEARCH, search, 1);
    }

    public Set<String> getSearchHistoryList(Integer userId){
        return redisTemplate.opsForZSet().reverseRange(RECENT_SEARCH + userId, 0, 9);
    }

    public void deleteSearchHistory(Integer userId) {
        redisTemplate.opsForZSet().removeRange(RECENT_SEARCH + userId, 0, redisTemplate.opsForZSet().zCard(RECENT_SEARCH + userId));
    }
}
