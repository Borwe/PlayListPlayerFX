package com.borwe.playerfx;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.reactivex.schedulers.Schedulers;

import com.borwe.playlistPlayerFx.springServices.FilesHandler;



@SpringBootTest(classes =com.borwe.playlistPlayerFx.springConfigs.MainConfig.class)
public class ServiceTests {
	
	@Autowired
	FilesHandler handler;
	
	Logger log=LoggerFactory.getLogger(ServiceTests.class);
	
	@Test
	public void testGettingFolders() {
		long size=0;
        size=handler.getFilesInDirectory(new File("./").getAbsolutePath()).cacheWithInitialCapacity(10)
            .subscribeOn(Schedulers.io()).count().blockingGet();
        //test showing the files gotten
        handler.getFilesInDirectory(new File("./").getAbsolutePath()).toList().blockingGet()
            .forEach(f->{
                System.out.println("FILE: "+f.getAbsolutePath());    
            });
		assertTrue(size>0);
	}
	
}
