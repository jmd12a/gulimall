package com.keda.gulimall.goods;

import com.keda.common.utils.R;
import com.keda.gulimall.goods.entity.AttrEntity;
import com.keda.gulimall.goods.entity.AttrGroupEntity;
import com.keda.gulimall.goods.service.AttrAttrgroupRelationService;
import com.keda.gulimall.goods.service.AttrGroupService;
import com.keda.gulimall.goods.service.impl.AttrGroupServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class GulimallGoodsApplicationTests {

    @Resource
    private AttrGroupServiceImpl attrGroupService;

    @Resource
    private AttrAttrgroupRelationService relationService;

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
    public void test3(){
        List<AttrEntity> attrEntities = relationService.queryAttrByAttrGroupId(2);
        System.out.println(attrEntities);
    }

}
