package com.borwe.playlistPlayerFx.springServices;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.borwe.playlistPlayerFx.data.Type;
import com.borwe.playlistPlayerFx.data.repos.TypeRepo;

import io.reactivex.Maybe;
import io.reactivex.Observable;
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
		
		return Observable.defer(()->Observable.just(typeRepo.count())
				.map(count->{
					if(count==0) {
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
					
					return typeRepo.findAll();
				}).flatMap(typesIterable->{
					return Observable.fromIterable(typesIterable);
				}));
    }
    
    /**
     * Used for checking for getting extention
     * type of a file
     * @param file
     * @return
     */
    public Maybe<Type> getTypeFromFile(File file){
    	return Maybe.defer(()->Maybe.create((emitter)->{
    		try {
    			
				String extention=file.getName().toLowerCase().replace(".", " ").split(" ")[1];
				getAlltypes()
					.filter(t->t.getType().equals(extention)?true:false)
					.forEach(t->emitter.onSuccess(t));
    		}catch(Exception ex) {
    			
    		}
    		
    		emitter.onComplete();
    	}));
    }
    
    /**
     * Used for checking a file has an extention
     * supported by the user or not
     * @param file
     * @return
     */
    public Single<Boolean> checkIfFileContainsUserType(File file){
    	return Single.defer(()->Single.just(file)
    			.map(f->{
	            		try {
	            			
	            			String fileName=file.getName();
	            			//check if file has an extention, if not, return false
	            			if(fileName.contains(".")==false) {
	            				return false;
	            			}
	            			
	            			//otherwise get extention
	            			String extention=fileName.substring(fileName.lastIndexOf(".")+1);
	            			
//							String extention=file.getName().toLowerCase().replace(".", " ").split(" ")[1];
//							
							if(getTypesAsSmallString().contains(extention).blockingGet()) {
								return true;
							}else {
								return false;
							}
	            		}catch (Exception e) {
	            			e.printStackTrace();
	            			return false;
						}
    	}));
    }
    
    /**
     * Get types  as strings in small
     * @return
     */
    public Observable<String> getTypesAsSmallString(){
    	return getAlltypes().map(type->{
    		return type.getType().toLowerCase();
    	});
    }
    
    /**
     * Get types as string in large
     * @return
     */
    public Observable<String> getTypesAsLargString() {
    	return getTypesAsSmallString().map(type->type.toUpperCase());
    }
    
    /**
     * Used for adding Types to the database
     * reactively
     * @param type
     * @return
     */
    public Single<Boolean> addType(Type type){
    	return Single.just(type).map(t->{
    		//if type exists, no need of saving just return true
    		if(typeRepo.findByType(t.getType()).size()>0) {
    			return false;
    		}
    		
    		//otherwise means type not in database, so try save it
    		typeRepo.save(t);
    		return true;
    	});
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
