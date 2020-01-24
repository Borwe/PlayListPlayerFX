package com.borwe.playerfx;

import com.borwe.playlistPlayerFx.data.repos.PlayListRepo;
import com.borwe.playlistPlayerFx.springServices.PlayListService;
import com.borwe.playlistPlayerFx.springServices.TypeService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=com.borwe.playlistPlayerFx.springConfigs.MainConfig.class)
public class RepoTests{
    @Autowired
    TypeService typeService;
    
    @Autowired
    PlayListService playListService;

    @Test
    public void testTypeService(){
        //check that service returns atleast 3 video types
        Long size=0L;
        size=typeService.getAlltypes().count().blockingGet();
        
        //assert that we got more than 3 or 3.
        Assertions.assertTrue(size>=3);
    }
    
    @Test
	public void testReadingPlayListsFromDB() {
		Long count=null;
		//now get object count of playlists
		
		count=playListService.getPlayLists().count().blockingGet();
		//make sure that count now should not be null
		Assertions.assertTrue(count!=null);
	}
}
