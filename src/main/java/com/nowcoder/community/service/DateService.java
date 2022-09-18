package com.nowcoder.community.service;

import com.nowcoder.community.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DateService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    // 将指定IP计入UV
    public void recordUV(String ip){
        String UVKey = RedisUtils.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(UVKey, ip);
    }

    // 统计时间范围内的UV，去重统计即可
    public long calculateUV(Date start, Date end){
        if(start == null || end == null)
            throw new IllegalArgumentException("参数不能为空！");
        if(start.after(end))
            throw new IllegalArgumentException("起始日期要在结束日期之后！");
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){  // 最好不用before，会缺少start和end同一天时的结果
            String key = RedisUtils.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }
        String tarKey = RedisUtils.getUVKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(tarKey, keyList.toArray());
        return redisTemplate.opsForHyperLogLog().size(tarKey);
    }

    // 将指定用户计入DAU
    public void recordDAU(int id){
        String DAUKey = RedisUtils.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(DAUKey, id, true);
    }

    // 统计时间范围内的UV，去重统计即可
    public long calculateDAU(Date start, Date end){
        if(start == null || end == null)
            throw new IllegalArgumentException("参数不能为空！");
        if(start.after(end))
            throw new IllegalArgumentException("起始日期要在结束日期之后！");
        List<byte[]> resList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){  // 最好不用before，会缺少start和end同一天时的结果
            String key = RedisUtils.getDAUKey(df.format(calendar.getTime()));
            resList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String key = RedisUtils.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        key.getBytes(), resList.toArray(new byte[0][0]));
                return  connection.bitCount(key.getBytes());
            }
        });
    }
}
