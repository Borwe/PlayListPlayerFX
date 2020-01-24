package com.borwe.playerfx;

import com.borwe.playlistPlayerFx.springServices.TypeService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=com.borwe.playlistPlayerFx.springConfigs.MainConfig.class)
public class RepoTests{
    @Autowired
    TypeService typeService;

    @Test
    public void testTypeService(){
        //check that service returns atleast 3 video types
        Long size=0L;
        size=typeService.getAlltypes().count().blockingGet();
        
        //assert that we got more than 3 or 3.
        Assertions.assertTrue(size>=3);
    }
}
