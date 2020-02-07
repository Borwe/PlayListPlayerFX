package com.borwe.playlistPlayerFx.data.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.borwe.playlistPlayerFx.data.Type;

public interface TypeRepo extends CrudRepository<Type, Long>{

	void deleteByType(String type);
	List<Type> findByType(String type);
}
