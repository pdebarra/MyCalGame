package com.example.customlayout;
import java.util.List;
import java.util.ArrayList;

public class Pool<T> {

	public interface PoolObjectFactory<T>{
		public T createObject();
	}

	private final List<T> freeObjects;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;

	public Pool(PoolObjectFactory<T> factory, int maxSize){
		this.maxSize = maxSize;
		this.factory = factory;
		this.freeObjects = new ArrayList<T>(maxSize);
	}

	public T newObject(){
		T object = null;

		if(freeObjects.size() == 0)
			object = factory.createObject();
		else
			object = freeObjects.remove(freeObjects.size() - 1);

		return object;
	}

	public void free(T object){
		if(freeObjects.size() <  maxSize)
			freeObjects.add(object);
	}
}
