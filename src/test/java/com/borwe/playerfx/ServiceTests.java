package com.borwe.playerfx;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.borwe.playlistPlayerFx.springServices.FilesHandler;



@SpringBootTest(classes =com.borwe.playlistPlayerFx.springConfigs.MainConfig.class)
public class ServiceTests {
	
	@Autowired
	FilesHandler handler;
	
	Logger log=LoggerFactory.getLogger(ServiceTests.class);
	
	@Test
	public void testGettingFolders() {
		int size=handler.getFilesInDirectory(new File("./").getAbsolutePath()).cacheWithInitialCapacity(10)
		.map(file->{
			assertNotNull(file);
			log.info("location: "+file.getAbsolutePath());
			System.out.println("location: "+file.getAbsolutePath());
			return file;
		}).toList().blockingGet().size();
		System.out.println("\nSIZE: "+size+"\n");
		assertTrue(size>0);
	}
}
