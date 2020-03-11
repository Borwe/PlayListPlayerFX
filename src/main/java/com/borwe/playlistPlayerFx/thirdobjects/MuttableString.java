package com.borwe.playlistPlayerFx.thirdobjects;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MuttableString{

    private String string;

    private ReentrantReadWriteLock lock;

    public MuttableString(){
        lock=new ReentrantReadWriteLock(true);
        this.string="";
    }

    public MuttableString(String string){
        lock=new ReentrantReadWriteLock(true);
        this.string=string;
    }

    public void setString(String string){
        lock.writeLock().lock();
        this.string=new String(string);
        lock.writeLock().unlock();
    }

    public String getString(){
        lock.readLock().lock();
        String toReturn= new String(string);
        lock.readLock().unlock();
        return toReturn;
    }
    
    public void strip() {
    	this.string=string.strip();
    }

    public String toString(){
        return getString();
    }

	public boolean isEmpty() {
		return getString().isEmpty();
	}
	
	public boolean isNull() {
		return getString()==null;
	}
}
