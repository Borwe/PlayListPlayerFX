package com.borwe.playlistPlayerFx.springConfigs;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.borwe.playlistPlayerFx.data.Type;

/**
 * 
 * @author brian
 *	For handling data objects in a spring config
 */
@Configuration()
@ComponentScan(basePackages = {"com.borwe.playlistPlayerFx.data","com.borwe.playlistPlayerFx.data.repos"})
@EntityScan(basePackages ="com.borwe.playlistPlayerFx.data" )
@EnableJpaRepositories(basePackages = "com.borwe.playlistPlayerFx.data")
public class DataConfigs {
	
	@Bean
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	Type generateType() {
		Type type=new Type();
		return type;
	}
}
