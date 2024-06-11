package com.keda.gulimall.goods.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.keda.gulimall.goods.entity.CategoryEntity;
import com.keda.gulimall.goods.service.CategoryService;
import com.keda.gulimall.goods.vo.Catalog2Vo;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Jmd
 * @create 2024-04-2024/4/20-17:04
 * @Description：
 */

@Controller
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    @Resource
    private CategoryService categoryService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redisson;

    @RequestMapping({"/index.html","/index"})
    public String visitIndex(Model model, HttpServletRequest request) {

        /*
        * 直接返回字符串、转发和重定向的区别
        * 1. 直接返回字符串，是将请求交给一个默认的view去处理，本项目这，前缀为 classpath:templates/ 后缀为 html
        * 2. 转发 将请求转发给服务内的另一个请求，即对应一个控制器方法
        * 3. 重定向 让浏览器向新的地址再发起一次请求
        *
        * 直接返回字符串和转发 ， request中的属性可以使用，而重定向，会构建一个新的请求
        * */
        System.out.println(123);

        List<CategoryEntity> categoryEntities = categoryService.getAllFristCategory();
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }

    /*@RequestMapping({"/index/json/catalog.json"})
    @ResponseBody
    public Map<Long, List<Catalog2Vo>> getCatalog() {

        long l = System.currentTimeMillis();

        List<CategoryEntity> stairCategoryList = categoryService.getAllFristCategory();

        Map<Long, List<Catalog2Vo>> cataLogMap = stairCategoryList.stream().collect(Collectors.toMap(key -> key.getCatId(), value -> {

            // 查询2级菜单
            List<CategoryEntity> CategoryEntityList =
                    categoryService.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", value.getCatId()));

            // 对二级菜单进行封装
            List<Catalog2Vo> catalog2List = CategoryEntityList.stream().map(category2 -> {
                // 创建对应的vo并封装属性
                Catalog2Vo catalogVo = new Catalog2Vo();
                catalogVo.setCatalog1Id(category2.getParentCid());
                catalogVo.setId(category2.getCatId());
                catalogVo.setName(category2.getName());
                // 查询子菜单
                List<CategoryEntity> category3List =
                        categoryService.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", category2.getCatId()));
                // 查询子菜单对应的vo并封装属性
                List<Catalog2Vo.Catalog3Vo> catalog3List = category3List.stream().map(category3 -> {
                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo();
                    catalog3Vo.setCatalog2Id(category3.getParentCid());
                    catalog3Vo.setId(category3.getCatId());
                    catalog3Vo.setName(category3.getName());

                    return catalog3Vo;
                }).collect(Collectors.toList());

                // 设置子菜单
                catalogVo.setCatalog3List(catalog3List);
                return catalogVo;
            }).collect(Collectors.toList());
            // 返回1级菜单对应的所有二级菜单作为key
            return catalog2List;
        }));



        log.info("花费时间：{}" ,System.currentTimeMillis()-l );

        return cataLogMap;
    }*/


    /*@RequestMapping({"/index/json/catalog.json"})
    @ResponseBody
    public Map<Long, List<Catalog2Vo>> getCatalog() {

        long l = System.currentTimeMillis();

        List<CategoryEntity> stairCategoryList = categoryService.getAllFristCategory();

        Map<Long, List<CategoryEntity>> level2listMap = new HashMap<>();
        Map<Long, List<CategoryEntity>> level3listMap = new HashMap<>();


        List<CategoryEntity> allCategoryList =
                categoryService.list(new QueryWrapper<CategoryEntity>().eq("cat_level", 3).or().eq("cat_level", 2));

        allCategoryList.forEach(categoryEntity -> {

            if (categoryEntity.getCatLevel()==2){
                Long parentCid = categoryEntity.getParentCid();
                // computeIfAbsent 查看该键对应的值是否存在，如果不存在，则指定后边的作为值
                level2listMap.computeIfAbsent(parentCid, v -> new ArrayList<CategoryEntity>());

                level2listMap.get(parentCid).add(categoryEntity);
            }else {
                Long parentCid = categoryEntity.getParentCid();
                level3listMap.computeIfAbsent(parentCid, v -> new ArrayList<CategoryEntity>());

                level3listMap.get(parentCid).add(categoryEntity);
            }


        });


        Map<Long, List<Catalog2Vo>> cataLogMap = stairCategoryList.stream().collect(Collectors.toMap(key -> key.getCatId(), value -> {

            // 查询2级菜单
            List<CategoryEntity> CategoryEntityList = level2listMap.get(value.getCatId());

            // 对二级菜单进行封装
            List<Catalog2Vo> catalog2List = CategoryEntityList.stream().map(category2 -> {

                // 创建对应的vo并封装属性
                Catalog2Vo catalogVo = new Catalog2Vo();
                catalogVo.setCatalog1Id(category2.getParentCid());
                catalogVo.setId(category2.getCatId());
                catalogVo.setName(category2.getName());
                // 查询子菜单

                List<CategoryEntity> category3List = level3listMap.get(category2.getCatId());

                // 查询子菜单对应的vo并封装属性
                List<Catalog2Vo.Catalog3Vo> catalog3List = category3List.stream().map(category3 -> {
                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo();
                    catalog3Vo.setCatalog2Id(category3.getParentCid());
                    catalog3Vo.setId(category3.getCatId());
                    catalog3Vo.setName(category3.getName());

                    return catalog3Vo;
                }).collect(Collectors.toList());

                // 设置子菜单
                catalogVo.setCatalog3List(catalog3List);
                return catalogVo;
            }).collect(Collectors.toList());
            // 返回1级菜单对应的所有二级菜单作为key
            return catalog2List;
        }));

        log.info("花费时间：{}" ,System.currentTimeMillis()-l );

        return cataLogMap;
    }*/

    /*
    *
    * 下面的两个方法，updateCatalog更新菜单数据，更新时删除缓存中的数据，也就是通过失效模式来尽量保证缓存一致性
     *             getCatalogJson读取关于菜单的数据，如果缓存中有就从缓存中获取，没有去获取分布式锁，获取到了查询数据库
    *
    * 重点在于，前者是写，后者是读，只通过失效模式难以很好的保证缓存的一致性，由此可以采用读写锁来解决。
    * 在写方法是采用写锁，读方法是采用读锁，保证同时只能有一个写操作，写操作时不能读。
    * */
    @RequestMapping({"/index/update/Catalog"})
    @ResponseBody
    public String updateCatalog(@RequestParam("catalogId")Long catalogId,
                                @RequestParam("catalogName")String catalogName) throws InterruptedException {

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("catalog:readWrite");
        RLock rLock = readWriteLock.writeLock();
        boolean getLock = rLock.tryLock(3000, TimeUnit.MILLISECONDS);

        if (!getLock) {
            return "获取写锁失败";
        }

        boolean update = categoryService.update(new UpdateWrapper<CategoryEntity>()
                .eq("cat_id", catalogId)
                .set("name", catalogName));
        Thread.sleep(10000);
        if (update){
            Boolean delete = redisTemplate.delete("catalog::json");

            rLock.unlock();

            if (delete) {
                log.info("更新了id为{}的catalog，删除缓存成功",catalogId);
                return "更新成功并删除缓存";
            }else return "更新成功，但删除缓存失败";
        }else {
            rLock.unlock();
            return "更新失败";
        }

    }


    @RequestMapping({"/index/json/catalog.json"})
    @ResponseBody
    public Map<Long, List<Catalog2Vo>> getCatalogJson() throws InterruptedException {

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("catalog:readWrite");
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        // Thread.sleep(10000);

        // 使用redis缓存
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("catalog::json");

        // 如果缓存没有获取到调用下面的方法查询
        if (entries.isEmpty()) {

            log.info("准备查询数据库......");
            // Map<Long, List<Catalog2Vo>> catalog = getCatalogFromDbWithRedisLock();
            Map<Long, List<Catalog2Vo>> catalog = getCatalogFromDbWithRedissonLock();

            rLock.unlock();
            return catalog;
        }

        rLock.unlock();

        // 类型的转换
        Map<Long, List<Catalog2Vo>> longListMap
                = JSON.parseObject(JSON.toJSONString(entries), new TypeReference<Map<Long, List<Catalog2Vo>>>() {
        });

        return longListMap;

    }

    // 使用Redisson来实现分布式锁，功能与getCatalogFromDbWithRedisLock()一致
    private Map<Long, List<Catalog2Vo>> getCatalogFromDbWithRedissonLock() throws InterruptedException {

        RLock lock = redisson.getLock("catalog::lock");

        while (!lock.tryLock(100, 10000, TimeUnit.MILLISECONDS)) {
            /* 这里的代码和尝试获取锁的代码是在很短时间内一起执行的，
             *  如果这里查询不到，那么也很难获取到锁，
             *  如果查询到了就返回了，无需获取锁
             * */
            Map<Object, Object> entries = redisTemplate.opsForHash().entries("catalog::json");

            if (!entries.isEmpty()){
                Map<Long, List<Catalog2Vo>> longListMap
                        = JSON.parseObject(JSON.toJSONString(entries), new TypeReference<Map<Long, List<Catalog2Vo>>>() {
                });
                return longListMap;
            }
        }

        log.info("获取到分布式的锁......");
        // 获取到锁之后再判断一次redis中是否有这个数据，防止每次获取到锁后都要查询一次数据库
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("catalog::json");
        if (!entries.isEmpty()){
            // 从缓存中获取到内容后，再返回前解锁
            lock.unlock();
            Map<Long, List<Catalog2Vo>> longListMap
                    = JSON.parseObject(JSON.toJSONString(entries), new TypeReference<Map<Long, List<Catalog2Vo>>>() {
            });

            return longListMap;
        }

        // redis中没有数据则从数据库中查询
        log.info("实际查询数据库......");
        Map<Long, List<Catalog2Vo>> cataLogMap = getCataLogMap();

        // 查询后在redis中存储一份
        redisTemplate.opsForHash().putAll("catalog::json", cataLogMap);
        redisTemplate.expire("catalog::json",3600, TimeUnit.SECONDS);

        lock.unlock();

        return cataLogMap;
    }

    @RequestMapping({"/index/hello"})
    @ResponseBody
    public String hello(){

        // 通过锁的名称获取分布式锁，只要名字一致，所有微服务都用同一个锁
        RLock lock = redisson.getLock("my-lock");

        // 加锁
        /*
        * lock():
        *       默认的加锁方式，锁的过期时间为30秒，并且有一个定时任务由当前线程调用作为看门狗，每1/3的过期时间，即10秒为锁续期。
        *
        * lock.lock(10, TimeUnit.SECONDS);
        *       手动设置过期时间的加锁方式，这种方式加锁方式的过期时间手动设置，
        *       且没有看门狗定时续期，如果当前线程的锁过期而代码没有执行完毕，则其他线程也可以获取到锁。
        *
        * 一般还是手动设置一个足够业务完成的过期时间，如果该时间内没有完成业务，说明出现问题需要解决
        * */

        // lock.lock();

        lock.lock(10, TimeUnit.SECONDS);

        try {
            log.info("获取到分布式锁，开始执行业务......");
            Thread.sleep(30000);
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            // 解锁
            lock.unlock();
        }

        return "hello";
    }

    @RequestMapping({"/index/write"})
    @ResponseBody
    public String write(){
        String string = UUID.randomUUID().toString();

        // 1. 获取读写锁
        RReadWriteLock rwLock = redisson.getReadWriteLock("rw:lock");
        // 2. 在写数据的方法中获取写锁
        RLock rLock = rwLock.writeLock();
        // 3. 写数据前上锁
        rLock.lock();
        try {
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("rw:value",string);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 在finally中释放锁
            rLock.unlock();
        }

        return string;

    }

    // 读写锁的作用可以保证同时只有一个线程去写数据，且在写数据时，其他线程无法读数据
    // 读的时候可以同时多个线程一起读，写的时候只能一个线程写
    @RequestMapping({"/index/read"})
    @ResponseBody
    public String read(){
        String string = "";

        // 1. 获取读写锁
        RReadWriteLock rwLock = redisson.getReadWriteLock("rw:lock");
        // 2. 在写数据的方法中获取读锁
        RLock rLock = rwLock.readLock();
        // 3. 读数据前上锁, 读锁的作用是在读数据之前检查是否有写锁，如果有写锁则等待
        rLock.lock();
        try {
            Thread.sleep(10000);
            string = redisTemplate.opsForValue().get("rw:value").toString();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 在finally中释放锁
            rLock.unlock();
        }

        return string;

    }


    @RequestMapping({"/closeDoor"})
    @ResponseBody
    public String closeDoor() throws InterruptedException {

        RCountDownLatch closeDoor = redisson.getCountDownLatch("closeDoor");

        closeDoor.trySetCount(5);
        log.info("闭锁{}初始化数量成功","closeDoor");
        closeDoor.await();

        return "所有班级已下课，关门";
    }

    @RequestMapping({"/classIsOver"})
    @ResponseBody
    public String ClassIsOver(@RequestParam("classId") Integer classId){

        RCountDownLatch closeDoor = redisson.getCountDownLatch("closeDoor");

        closeDoor.countDown();

        return "ok";
    }

    @RequestMapping({"/inPark"})
    @ResponseBody
    public String inPark() throws InterruptedException {

        String info = "";

        RSemaphore park = redisson.getSemaphore("park");
       /* park.acquire();
        info = "进入停车场";*/

        boolean b = park.trySetPermits(6);
        if (b){
            info = "初始化信号量成功";
        }else {
            park.acquire();
            info = "进入停车场";
        }


        return info;
    }

    @RequestMapping({"/outPark"})
    @ResponseBody
    public String outPark(){

        RSemaphore park = redisson.getSemaphore("park");

        park.release();

        return "已从停车场离开";
    }


    private Map<Long, List<Catalog2Vo>> getCatalogFromDbWithRedisLock() throws InterruptedException {

        // 获取一个uuid用来设置value
        String uuid = UUID.randomUUID().toString();

        // 首先通过setIfAbsent()方法添加锁，如果添加不成功通过自旋来不断添加获取锁
        while (Boolean.FALSE.equals(redisTemplate.opsForValue().
                // 这里设置锁过期时间是一个原子性的操作，防止在设置锁后而没设置锁的过期时间时服务宕机
                setIfAbsent("catalog::lock", uuid, 10000, TimeUnit.MILLISECONDS))) {

            // 使用redis中列表数据类型的阻塞等待方法，如果没有获取到列表中的数据，那么就等待十秒钟，以此起到阻塞的效果，减少sleep对线程性能的影响
            redisTemplate.opsForList().leftPop("catalog:notification", 100, TimeUnit.MILLISECONDS);

            Map<Object, Object> entries = redisTemplate.opsForHash().entries("catalog::json");
            if (!entries.isEmpty()){
                Map<Long, List<Catalog2Vo>> longListMap
                        = JSON.parseObject(JSON.toJSONString(entries), new TypeReference<Map<Long, List<Catalog2Vo>>>() {
                });
                return longListMap;
            }
        }

        log.info("获取到分布式的锁......");
        // 获取到锁之后再判断一次redis中是否有这个数据，防止每次获取到锁后都要查询一次数据库
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("catalog::json");
        if (!entries.isEmpty()){
            Map<Long, List<Catalog2Vo>> longListMap
                    = JSON.parseObject(JSON.toJSONString(entries), new TypeReference<Map<Long, List<Catalog2Vo>>>() {
            });
            return longListMap;
        }


        // redis中没有数据则从数据库中查询
        log.info("实际查询数据库......");
        Map<Long, List<Catalog2Vo>> cataLogMap = getCataLogMap();

        // 查询后在redis中存储一份
        redisTemplate.opsForHash().putAll("catalog::json", cataLogMap);
        redisTemplate.expire("catalog::json",3600, TimeUnit.SECONDS);

        // 获取锁的值，确定值相同是自己的锁后再删除，防止程序执行较慢，自己的锁已经超时而这里的代码误删了其他线程的锁
        // 这里获取锁的值和删除锁不是原子性操作，可能在获取锁的时候锁还没有过期，恰好在删除锁之前锁过期了，为了解决这个问题，获取锁和删除锁需要是原子操作
       /* String lockValue = redisTemplate.opsForValue().get("catalog::lock").toString();
        if (uuid.equals(lockValue)) {
            log.info("是自己的锁，可以删除");
            Boolean delete = redisTemplate.delete("catalog::lock");

            if (delete) {
                // 通知其他线程释放了锁
                log.info("删除成功，通知其他线程");
                redisTemplate.opsForList().leftPush("catalog:notification","unlock");
            }
        }*/

        // 在上边的代码中，读并判断操作和删除操作不是原子性的，可能在获取值之后，原本的锁过期了，导致删除了其他线程的锁，为了解决这个问题，使用lua脚本保证操作原子性

        // 使用lua脚本，get的结果等于uuid后再进行删除操作, 这里脚本的返回值是Long类型的
        String luaString = "if redis.call('get', KEYS[1]) == ARGV[1] then\n" +
                "    return tonumber(redis.call('del', KEYS[1]))\n" +
                "else\n" +
                "    return 0\n" +
                "end\n";

        // 使用execute()方法配合lua脚本达成判断和删除的原子性
        System.out.println(redisTemplate.opsForValue().get("catalog::lock"));

        Long lock = redisTemplate.execute(
                new DefaultRedisScript<>(luaString, Long.class),
                Arrays.asList("catalog::lock"),
                uuid);

        if (lock.intValue() == 1){
            log.info("删除成功，通知其他线程");
            redisTemplate.opsForList().leftPush("catalog:notification","unlock");
        }


        return cataLogMap;
    }

    private Map<Long, List<Catalog2Vo>> getCataLogMap() {
        List<CategoryEntity> allCategoryList = categoryService.list();


        Map<Long, List<CategoryEntity>> listMap = new HashMap<>();

        List<CategoryEntity> stairCategoryList = new ArrayList<>();

        allCategoryList.forEach(categoryEntity -> {

            if (categoryEntity.getCatLevel() == 1) {
                stairCategoryList.add(categoryEntity);
            } else {
                Long parentCid = categoryEntity.getParentCid();
                // computeIfAbsent 查看该键对应的值是否存在，如果不存在，则指定后边的作为值
                listMap.computeIfAbsent(parentCid, v -> new ArrayList<CategoryEntity>());

                listMap.get(parentCid).add(categoryEntity);
            }

        });


        Map<Long, List<Catalog2Vo>> cataLogMap = stairCategoryList.stream().collect(Collectors.toMap(key -> key.getCatId(), value -> {

            // 查询2级菜单
            List<CategoryEntity> CategoryEntityList = listMap.get(value.getCatId());

            // 对二级菜单进行封装
            List<Catalog2Vo> catalog2List = CategoryEntityList.stream().map(category2 -> {

                // 创建对应的vo并封装属性
                Catalog2Vo catalogVo = new Catalog2Vo();
                catalogVo.setCatalog1Id(category2.getParentCid());
                catalogVo.setId(category2.getCatId());
                catalogVo.setName(category2.getName());
                // 查询子菜单

                List<CategoryEntity> category3List = listMap.get(category2.getCatId());

                // 查询子菜单对应的vo并封装属性
                List<Catalog2Vo.Catalog3Vo> catalog3List = category3List.stream().map(category3 -> {
                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo();
                    catalog3Vo.setCatalog2Id(category3.getParentCid());
                    catalog3Vo.setId(category3.getCatId());
                    catalog3Vo.setName(category3.getName());

                    return catalog3Vo;
                }).collect(Collectors.toList());

                // 设置子菜单
                catalogVo.setCatalog3List(catalog3List);
                return catalogVo;
            }).collect(Collectors.toList());
            // 返回1级菜单对应的所有二级菜单作为key
            return catalog2List;
        }));
        return cataLogMap;
    }


    private Map<Long, List<Catalog2Vo>> getCatalogFromDbWithLocalLock() {

        /*
        * 为了解决缓存击穿的问题，在每次查询之前去设置一个锁，只有获取到这个锁才能继续查询
        *
        * 1. 设置锁的方案一，使用synchronized关键字
        * */

        synchronized (this){

            // 获取到锁之后，首先再检查一下缓存中有没有内容，如果有的话直接返回，防止之前获取到锁的数据库查询了之后，后续获取到锁的线程再次查询
            if (redisTemplate.hasKey("catalog::json")) {

                Map<Object, Object> entries = redisTemplate.opsForHash().entries("catalog::json");
                Map<Long, List<Catalog2Vo>> longListMap
                        = JSON.parseObject(JSON.toJSONString(entries), new TypeReference<Map<Long, List<Catalog2Vo>>>() {
                });
                return longListMap;
            }

            long l = System.currentTimeMillis();
            long l2 = System.currentTimeMillis();

            Map<Long, List<Catalog2Vo>> cataLogMap = getCataLogMap();


            // 可以走到这里说明缓存中没有，既然缓存中没有查出来以后往里边存放一份
            redisTemplate.opsForHash().putAll("catalog::json", cataLogMap);
            if (!cataLogMap.isEmpty()) {
                // 如果不会空，设置一个较长的时间为一个小时
                redisTemplate.expire("catalog::json",3600, TimeUnit.SECONDS);
            }else {
                // 如果为空，设置一个较短的时间防止缓存穿透，解决短时间访问一个不存在的数据给数据库带来压力的问题
                redisTemplate.expire("catalog::json",100, TimeUnit.SECONDS);
            }


            log.info("花费时间：{}" ,System.currentTimeMillis()-l );
            log.info("查询花费时间：{}" ,l2-l );
            return cataLogMap;
        }


    }


   /* @RequestMapping("/index")
    public String visitIndex1(HttpServletRequest request, HttpServletResponse response) {

        *//*
         * 直接返回字符串、转发和重定向的区别
         * 1. 直接返回字符串，是将请求交给一个默认的view去处理，本项目这，前缀为 classpath:templates/ 后缀为 html
         * 2. 转发 将请求转发给服务内的另一个请求，即对应一个控制器方法
         * 3. 重定向 让浏览器向新的地址再发起一次请求
         *
         * 直接返回字符串和转发 ， request中的属性可以使用，而重定向，会构建一个新的请求
         * *//*
        System.out.println(456);
        System.out.println(request.getAttribute("test"));
        return "index";
    }*/


}
