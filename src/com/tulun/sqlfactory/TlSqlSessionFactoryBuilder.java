package com.tulun.sqlfactory;

import com.tulun.parsexml.App;

import java.io.InputStream;

/**
 * 描述:
 *
 * @Author shilei
 * @Date 2018/11/11
 */
public class TlSqlSessionFactoryBuilder {

    /**
     * 用户会从Mybatis.xml读取配置，放到一个InputStream输入对象里面，
     * 该方法主要是从InputStream里面获取
     * driver, url, name, password
     * 接口或者sql映射文件的路径
     * @param in
     * @return
     */
    public TlSqlSessionFactory build(InputStream in){
        App app = new App();
        app.analysis(in);
        TlSqlSessionFactory factory = new TlSqlSessionFactory();
        factory.setDriver(app.getDriver());
        factory.setUrl(app.getUrl());
        factory.setName(app.getName());
        factory.setPassword(app.getPassword());
        return factory;
    }
}
