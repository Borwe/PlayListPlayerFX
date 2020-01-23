package com.borwe.playerfx;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = com.borwe.playlistPlayerFx.springConfigs.MainConfig.class)
class ApplicationTests {
	
	@Autowired
	ApplicationContext context;

	@Test
	void contextLoads() {
		assertNotNull(context,"Shit man, why context null? WHY!!!!!!??!");
	}

}
