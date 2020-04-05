package online.wenbei.webchat.service;

import online.wenbei.webchat.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: cityuu#163.com
 * @Date: 2018-12-10 15:36
 * @version: v1.0
 * @Description: Redis 缓存操作接口
 */
@Slf4j
@Service("redisService")
public abstract class BaseRedisService {


    private static int seconds = 3600 * 24;

    private static String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";// lua脚本，用来释放分布式锁


    @Qualifier("stringRedisTemplate")
    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    private RedisSerializer<String> serializer;

    public class  CacheZSet{
        private String value;

        private String score;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }


    @Bean
    @Scope("singleton")
    public RedisSerializer<String> initSerializer() {
        serializer = redisTemplate.getStringSerializer();
        return serializer;
    }
    

    
    public boolean set(final String key, final String value) throws Exception {
        Assert.hasText(key, "Key is not empty.");
        boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            //serializer = redisTemplate.getStringSerializer();
            connection.set(serializer.serialize(key), serializer.serialize(value));
            return true;
        });
        return result;
    }

    public String get(final String key) throws Exception {
        Assert.hasText(key, "Key is not empty.");
        String result = redisTemplate.execute((RedisCallback<String>) connection -> {
            //serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(serializer.serialize(key));
            return serializer.deserialize(value);
        });
        return result;
    }

    public void del(final String key) throws Exception {

        Assert.hasText(key, "Key is not empty.");

        redisTemplate.execute((RedisCallback<Long>) conn -> {

            serializer = redisTemplate.getStringSerializer();

            return conn.del(serializer.serialize(key));
        });
    }


    
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
    }

    
    public boolean expire(long expire, String... key) throws Exception {
        for (String k : key) {
            expire(k, expire);
        }
        return true;
    }

    
    public <T> boolean setList(String key, List<T> list) throws Exception {
        Assert.hasText(key, "Key is not empty.");

        String value = JsonUtil.parseToJSON(list);
        return set(key, value);
    }

    
    public <T> List<T> getList(String key, Class<T> clz) throws Exception {

        Assert.hasText(key, "Key is not empty.");

        String json = get(key);
        if (json != null) {
            List<T> list = JsonUtil.parseJSONList(json, clz);
            return list;
        }
        return null;
    }

    
    public long lpush(final String key, String... valueArray) throws Exception {
        Assert.hasText(key, "Key is not empty.");

        return redisTemplate.execute(new RedisCallback<Long>() {
            
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[] bytes[] = new byte[valueArray.length][];
                for (int i = 0; i < valueArray.length; i++) {
                    bytes[i] = serializer.serialize(valueArray[i]);
                }

                return redisConnection.lPush(serializer.serialize(key), bytes);
            }
        });
    }

    
    public long rpush(final String key, String... valueArray) throws Exception {
        Assert.hasText(key, "Key is not empty.");

        return redisTemplate.execute(new RedisCallback<Long>() {
            
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[] bytes[] = new byte[valueArray.length][];
                for (int i = 0; i < valueArray.length; i++) {
                    bytes[i] = serializer.serialize(valueArray[i]);
                }

                return redisConnection.rPush(serializer.serialize(key), bytes);
            }
        });
    }

    
    public void hmset(String key, Map map) throws Exception {
        Assert.hasText(key, "Key is not empty.");

        redisTemplate.opsForHash().putAll(key, map);
    }

    
    public void hset(String key,String field, String value) throws Exception {
        redisTemplate.opsForHash().put(key,field,value);
    }

    
    public long hDel(String key, String... field) throws Exception {
        return redisTemplate.opsForHash().delete(key, field);
    }

    
    public <T> T hget(String key, Class<T> clz) throws Exception {
        Assert.hasText(key, "Key is not empty.");

        return redisTemplate.execute((RedisCallback<T>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();

            Map<String, Object> result;

            Map<byte[], byte[]> data = connection.hGetAll(serializer.serialize(key));
            result = new HashMap<>();
            for (Map.Entry<byte[], byte[]> entry : data.entrySet()) {
                result.put(serializer.deserialize(entry.getKey()), serializer.deserialize(entry.getValue()));
            }

            return JsonUtil.parseToClass(JsonUtil.parseToJSON(result), clz);
        });
    }

    
    public Object hget(String key, String field) throws Exception {
        return redisTemplate.opsForHash().get(key, field);
    }

    
    public Map<String, String> hGetAll(String key) throws Exception {
        Assert.hasText(key, "Key is not empty.");

        return redisTemplate.execute((RedisCallback<Map<String, String>>) connection -> {

            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();

            Map<byte[], byte[]> data = connection.hGetAll(serializer.serialize(key));
            Map<String, String> result = new HashMap<>();

            for (Map.Entry<byte[], byte[]> entry : data.entrySet()) {
                result.put(serializer.deserialize(entry.getKey()), serializer.deserialize(entry.getValue()));
            }

            return result;
        });
    }

    
    public String lpop(final String key) throws Exception {
        Assert.hasText(key, "Key is not empty.");

        String result = redisTemplate.execute(new RedisCallback<String>() {
            
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] res = connection.lPop(serializer.serialize(key));
                return serializer.deserialize(res);
            }
        });
        return result;
    }

    
    public List<String> lRange(String key, Long start, Long end) throws Exception {
        return redisTemplate.execute(new RedisCallback<List<String>>() {
            
            public List<String> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                List<byte[]> list = redisConnection.lRange(serializer.serialize(key), start, end);

                List<String> resultList = new ArrayList<>();

                for (byte[] bytes : list) {
                    resultList.add(serializer.deserialize(bytes));
                }
                return resultList;
            }
        });
    }


    
    public Long incr(String key) throws Exception {
        return redisTemplate.execute(new RedisCallback<Long>() {
            
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.incr(serializer.serialize(key));
            }
        });
    }

    
    public Long decr(String key) throws Exception {
        return redisTemplate.execute(new RedisCallback<Long>() {
            
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.decr(serializer.serialize(key));
            }
        });
    }


    
    public boolean getLuaLock(String key, Long expire) throws Exception {
        return false;
    }


    
    public int sadd(String key, String data) {
        return redisTemplate.execute(new RedisCallback<Integer>() {
            
            public Integer doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.sAdd(serializer.serialize(key), serializer.serialize(data));
                return 1;
            }

        });
    }

    
    public int sadd(String key,String [] data) {
        return redisTemplate.execute(new RedisCallback<Integer>() {
            
            public Integer doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for(String value:data) {
                    redisConnection.sAdd(serializer.serialize(key), serializer.serialize(value));
                }
                return data.length;
            }

        });
    }

    
    public Set<?> sMembers(String key) throws Exception {
        return redisTemplate.opsForSet().members(key);
    }

    
    public Set<String> getRandomSet(String key, Long size) {
        return (Set<String>) redisTemplate.opsForSet().distinctRandomMembers(key,size);

    }

    
    public Long sRem(String key , String field) {
       return redisTemplate.opsForSet().remove(key,field);
    }

    
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    
    public boolean lRem(String key) {
        return redisTemplate.delete(key);
    }

    
    public Long lRem(String key, Object value) {
        return redisTemplate.opsForList().remove(key, 0, value);
    }

    
    public boolean sExistis(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key,value);
    }

    
    public Long llen(String key) throws Exception {
        return redisTemplate.opsForList().size(key);
    }

    
    public void ltrim(String key, long start, long end) throws Exception {
        redisTemplate.opsForList().trim(key,start,end);
    }

    
    public void setNX(String key, String value) {
        redisTemplate.execute(new RedisCallback<Integer>() {
            
            public Integer doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String cmd = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end\n";
                return 1;
            }

        });
    }

    
    public boolean zadd(String key, String value, Double score) {
        if (score == null) {
            score = 0.0;
        }
        Double finalScore = score;
        return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> redisConnection.zAdd(serializer.serialize(key), finalScore, serializer.serialize(value)));
    }

    
    public Long zadd(String key, Set set) throws Exception {
        if(set.size()<=0){
            return Long.valueOf(set.size());
        }

        return redisTemplate.opsForZSet().add(key, set);
    }

    
    public Set zRange(String key, Long start, Long end) throws Exception {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    
    public Long zRank(String key, String id) throws Exception {
        return redisTemplate.opsForZSet().rank(key,id);
    }

    
    public Long zRevRank(String key, String id) throws Exception {
        return redisTemplate.opsForZSet().reverseRank(key,id);
    }

    
    public Long zremRangeByScore(String key, Double start, Double end) {
        return redisTemplate.execute((RedisCallback<Long>) redisConnection -> redisConnection.zRemRangeByScore(serializer.serialize(key), start, end));

    }

    
    public Long zrem(String key, String ... value) throws Exception {
        return redisTemplate.opsForZSet().remove(key,value);
    }

    
    public Double zScore(String key, String member) throws Exception {
        return redisTemplate.opsForZSet().score(key,member);
    }

    
    public Long getExpireTime(String key) {
        if (key == null) {
            return null;
        }
        return redisTemplate.getExpire(key)* 1000;
    }

    
    public <T> List<T> zrevrange(String key, Long start, Long end, Class<T> tClass) {
        return redisTemplate.execute((RedisCallback<List<T>>) redisConnection -> {
            Set<byte[]> set = redisConnection.zRevRange(serializer.serialize(key), start, end);
            List<String> list = new ArrayList<>();
            for (byte[] bytes : set) {
                list.add(serializer.deserialize(bytes));
            }
            return (List<T>) list;
        });

    }

    
    public Set<ZSetOperations.TypedTuple<Object>> zrevrangeWithScores(String key, Long start, Long end) throws Exception {
        Set<? extends ZSetOperations.TypedTuple<?>> set = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, start, end);
        return (Set<ZSetOperations.TypedTuple<Object>>) set;
    }

    
    public Long zCard(String key) throws Exception {
        if(redisTemplate.hasKey(key)) {
            return redisTemplate.opsForZSet().zCard(key);
        }else{
            return -1L;
        }
    }

    
    public Long zCount(String key, double min, double max) throws Exception {
        return redisTemplate.opsForZSet().count(key,min,max);
    }


}
