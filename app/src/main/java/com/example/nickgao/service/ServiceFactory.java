package com.example.nickgao.service;


import com.example.nickgao.logging.MktLog;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by steve.chen on 7/1/14.
 */
public class ServiceFactory {

    private static final String TAG = "ServiceFactory";

    private static ServiceFactory instance = new ServiceFactory();

    private IRequestFactory requestFactory;

    private ServiceFactory() {
        requestFactory = new RestRequestFactory();
    }

    public static ServiceFactory getInstance() {
        return instance;
    }

    public IRequestFactory getRequestFactory() {
        return requestFactory;
    }

    public void setRequestFactory(IRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public AbstractService getService(String name) {
        AbstractService service = null;
        try {
            Class c = Class.forName(name);
            Class parameterType = IRequestFactory.class;
            java.lang.reflect.Constructor constructor = null;
            constructor = c.getConstructor(parameterType);
            Object parameter = this.requestFactory;
            service = (AbstractService) constructor.newInstance(parameter);
        } catch (ClassNotFoundException e) {
            MktLog.e(TAG, "getService", e);
        } catch (NoSuchMethodException e) {
            MktLog.e(TAG, "getService", e);
        } catch (InstantiationException e) {
            MktLog.e(TAG, "getService", e);
        } catch (IllegalAccessException e) {
            MktLog.e(TAG, "getService", e);
        } catch (InvocationTargetException e) {
            MktLog.e(TAG, "getService", e);
        }

        return service;
    }
}
