package com.borwe.playlistPlayerFx.springServices;

import com.borwe.playlistPlayerFx.data.Type;
import com.borwe.playlistPlayerFx.data.repos.TypeRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import lombok.ToString;

@Service
@ToString
public class TypeService{
    
	@Autowired
    private TypeRepo typeRepo;
    
    @Autowired
    private ApplicationContext context;
    
    private synchronized TypeRepo gettypeRepo() {
    	if(typeRepo==null) {
    		typeRepo=context.getBean(TypeRepo.class);
    	}
    	return typeRepo;
    }

    /**
     * Check that there atleast 3 types in the db,
     * if not then create them, save them, then return
     * the saved objects from the database
     * @return
     */
    public Observable<Type> getAlltypes(){
		if(typeRepo.count()==0) {
		   //then create the objects here of the types
		   //mp4,3gp, and mkv
		   Type mp4=context.getBean(Type.class);
		   Type g3p=context.getBean(Type.class);
		   Type mkv=context.getBean(Type.class);
		   mp4.setType("mp4");
		   g3p.setType("3gp");
		   mkv.setType("mkv");
		   
		   //now save this objects
		   typeRepo.save(mp4);
		   typeRepo.save(g3p);
		   typeRepo.save(mkv);
		}
		
		return Observable.defer(()->Observable.fromIterable(typeRepo.findAll()));
    }
    
    /**
     * 
     * @param typesObservable
     * @return
     * Used for removing Types from the database that match the ones passed 
     * if not in database no need to remove something that doesn't exist to
     * start with,
     * returns a single true if object existed and is deleted, false otherwise
     */
    public Single<Boolean> removeTypes(Single<Type> typesObservable){
    	return typesObservable.map(typeObject->{
    		var types=typeRepo.findByType(typeObject.getType());
    		if(types.size()>0) {
    			typeRepo.deleteById(types.get(0).getId());
    			return true;
    		}else {
    			return false;
    		}
    	});
    }
}
