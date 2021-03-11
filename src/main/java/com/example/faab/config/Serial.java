package com.example.faab.config;

import org.apache.ibatis.cache.CacheException;

import java.io.*;

public class Serial<T> {

    public byte[] serial(T data){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos  = new ObjectOutputStream(bos);
            oos.writeObject(data);
            oos.flush();
            oos.close();;
            return bos.toByteArray();
        } catch (IOException e) {
            throw new CacheException("Error serializing object.  Cause: " + e, e);
        }
    }

    public T deserial(byte[] value) {
        T result;
        try {
            //反序列化核心就是ByteArrayInputStream
            ByteArrayInputStream bis = new ByteArrayInputStream(value);
            ObjectInputStream ois = new ObjectInputStream(bis);
            result = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            throw new CacheException("Error deserializing object.  Cause: " + e, e);
        }
        return result;
    }
}
