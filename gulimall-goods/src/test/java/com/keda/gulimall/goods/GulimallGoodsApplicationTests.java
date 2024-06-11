package com.keda.gulimall.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keda.common.to.BoundsTo;
import com.keda.common.utils.R;
import com.keda.gulimall.fegin.feginClients.BoundsClients;
import com.keda.gulimall.fegin.feginClients.PmsSkuInfoClient;
import com.keda.gulimall.goods.common.ElasticsearchOption;
import com.keda.gulimall.goods.common.ProductEsModel;
import com.keda.gulimall.goods.dao.SkuInfoDao;
import com.keda.gulimall.goods.dao.SpuInfoDao;
import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.entity.AttrGroupEntity;
import com.keda.gulimall.goods.entity.CategoryEntity;
import com.keda.gulimall.goods.entity.SpuInfoEntity;
import com.keda.gulimall.goods.service.AttrAttrgroupRelationService;
import com.keda.gulimall.goods.service.AttrGroupService;
import com.keda.gulimall.goods.service.CategoryService;
import com.keda.gulimall.goods.service.impl.AttrGroupServiceImpl;
import com.keda.gulimall.goods.service.impl.SpuInfoServiceImpl;
import com.keda.gulimall.goods.vo.Catalog2Vo;
import com.keda.gulimall.goods.vo.CatalogVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
class GulimallGoodsApplicationTests {

    @Resource
    private AttrGroupServiceImpl attrGroupService;

    @Resource
    private AttrAttrgroupRelationService relationService;

    @Resource
    private RestHighLevelClient esClient;

    @Resource
    private SpuInfoDao spuInfoDao;

    @Resource
    private SkuInfoDao skuInfoDao;

    @Resource
    private BoundsClients boundsClients;

    @Resource
    private CategoryService categoryService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
    }


    public R upLoad(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();

        // 给文件重新起名
        String formatInfo = filename.substring(filename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        filename = uuid + formatInfo;



        // 创建文件类并判断文件是否存在，不存在则创建
        /*File localFile = new File("D:/IDE/vsProject/renren-fast-vue/" + filename);
        if (!localFile.exists()) {
            localFile.createNewFile();
        }*/


        // 将前端的输入流的内容通过输出流写入到文件中
        InputStream inputStream = file.getInputStream();
        // FileOutputStream会创建文件，或者将之前存在的文件覆盖
        FileOutputStream outputStream = new FileOutputStream("D:/IDE/vsProject/renren-fast-vue/" + filename);
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(bytes)) != -1){ // inputStream.read(bytes)返回的是往byte数组中写入的byte长度是多少
            outputStream.write(bytes);
        }
        inputStream.read(bytes,0,length);

        inputStream.close();
        outputStream.close();

        return R.ok();
    }

    public void testDownLoad(String addr) throws IOException {

        // 将文件中的内容写入byte数组，可以将byte数组返回到前端
        FileInputStream fileInputStream = new FileInputStream(addr);
        int available = fileInputStream.available();
        byte[] bytes1 = new byte[available];
        fileInputStream.read(bytes1);

        fileInputStream.close();
    }

    @Test
    public void test1(){
        String filename = "123.text";
        String formatInfo = filename.substring(filename.lastIndexOf("."));
        System.out.println(formatInfo); // ".text
    }

    @Test
    public void test2(){
        ArrayList<Long> longs = new ArrayList<>();
        attrGroupService.getCateLogPath(165l,longs);
        Collections.reverse(longs);
        longs.forEach(l ->{
            System.out.println(l);
        });
    }

    @Test
    public void test3() throws IOException {
        SearchRequest productSearch = new SearchRequest("product");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        productSearch.source(sourceBuilder);

        SearchResponse productResponse = esClient.search(productSearch, ElasticsearchOption.COMMON_OPTIONS);

        System.out.println(productResponse);

    }

    @Test
    public void test5() throws IOException {
        ProductEsModel productEsModel = spuInfoDao.selectSpuInfoForProductEsModel(1718983608961015809l);

        System.out.println(productEsModel);
    }

    @Test
    public void test5One() throws IOException {

        List<ProductEsModel> productEsModels = skuInfoDao.selectSkuInfoForProductEsModel(1718983608961015809l);

        System.out.println(productEsModels);
    }

    @Test
    public void test6(){
        List<ProductEsModel> skus = skuInfoDao.selectSkuInfoForProductEsModel(1718983608961015809l);

        ProductEsModel productEsModel = spuInfoDao.selectSpuInfoForProductEsModel(1718983608961015809l);

        String brandImage = productEsModel.getBrandImage();
        String brandName = productEsModel.getBrandName();
        String categoryName = productEsModel.getCategoryName();

        skus.forEach(sku ->{
            sku.setBrandName(brandName);
            sku.setCategoryName(categoryName);
            sku.setBrandImage(brandImage);
        });

        System.out.println();
    }

    @Test
    public void test7(){
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setId(1718983608961015809l);
        spuInfoEntity.setPublishStatus(1);
        // 这里的update方法只更新pojo中有的属性
        spuInfoDao.updateById(spuInfoEntity);
    }

    @Test
    public void test8(){
        R r = boundsClients.saveBounds(new BoundsTo());

        System.out.println();
    }

    @Test
    public void test9(){

        CatalogVo catalogVos = categoryService.selectSubCatalogByStairId(1l);
        System.out.println();
    }




    public Long getCatalog() {

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



        return System.currentTimeMillis()-l;
    }

    @Test
    public void getcatalogJson() {

        Map<Object, Object> entries = redisTemplate.opsForHash().entries("catalog::json");

        if (entries.isEmpty()) {

            Map<Long, List<Catalog2Vo>> catalog = getCatalog2();
            redisTemplate.opsForHash().putAll("catalog::json", catalog);
        }

        Map<Long, List<Catalog2Vo>> longListMap
                = JSON.parseObject(JSON.toJSONString(entries), new TypeReference<Map<Long, List<Catalog2Vo>>>() {
        });

        System.out.println(longListMap);

    }


    public Map<Long, List<Catalog2Vo>> getCatalog2() {

        long l = System.currentTimeMillis();

        List<CategoryEntity> allCategoryList = categoryService.list();


        Map<Long, List<CategoryEntity>> listMap = new HashMap<>();

        List<CategoryEntity> stairCategoryList = new ArrayList<>();

        allCategoryList.forEach(categoryEntity -> {

            if (categoryEntity.getCatLevel() == 1){
                stairCategoryList.add(categoryEntity);
            }else {
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

    @Test
    public void testRedis(){
        List<SpuInfoEntity> spuInfoEntities = spuInfoDao.selectList(null);

        SpuInfoEntity spuInfoEntity1 = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoEntities.get(0), spuInfoEntity1);
        // spuInfoEntities.add(spuInfoEntity1);

        ListOperations<String, Object> opsForList = redisTemplate.opsForList();

        // 这里要传入一个数组类型的对象，如果是一个列表类型的对象那么会导致把列表整个插入
        Long l = opsForList.leftPushAll("spuInfo:List", spuInfoEntities.toArray());

        List<Object> spuInfoList = opsForList.range("spuInfo:List", 0, -1);



        spuInfoList.forEach(spuInfoEntity -> {

            System.out.println(spuInfoEntity);
        });

    }

    @Test
    public void testRedisForStringTemplate(){
        List<SpuInfoEntity> spuInfoEntities = spuInfoDao.selectList(null);

        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();

        //    valueOperations.set("spuInfo:List", );

        // 如果是使用stringRedisTemplate,那么在存储之前要将值转换为string类型
        String jsonString = JSON.toJSONString(spuInfoEntities);

        valueOperations.set("spuInfo:List", jsonString);

        TypeReference<List<SpuInfoEntity>> typeReference = new TypeReference<List<SpuInfoEntity>>() {
        };


        String spuListString = valueOperations.get("spuInfo:List");

        // 获得之后再将string类型的转换回来
        List<SpuInfoEntity> spuInfoList = JSON.parseObject(spuListString, typeReference);

        spuInfoList.forEach(System.out::println);




    }



}
