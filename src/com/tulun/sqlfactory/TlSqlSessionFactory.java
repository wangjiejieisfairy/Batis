package com.tulun.sqlfactory;

import java.sql.Connection;

/**
 * 描述:
 *
 * @Author shilei
 * @Date 2018/11/11
 */
public class TlSqlSessionFactory {

    private String driver;
    private String url;
    private String name;
    private String password;

    private String interfacePath;
    private String sqlXmlFilePath;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInterfacePath() {
        return interfacePath;
    }

    public void setInterfacePath(String interfacePath) {
        this.interfacePath = interfacePath;
    }

    public String getSqlXmlFilePath() {
        return sqlXmlFilePath;
    }

    public void setSqlXmlFilePath(String sqlXmlFilePath) {
        this.sqlXmlFilePath = sqlXmlFilePath;
    }

    /**
     * 返回一个SqlSession连接
     * @return
     */
    public TlSqlSession openSession(){
        TlSqlSession sqlSession = new TlSqlSession(this.driver,this.url,this.name,this.password);
        sqlSession.SetCommit(false);
        return sqlSession;
    }

    /**
     * @param auto  表示框架是否自动提交事务
     * @return
     */
    public TlSqlSession openSession(boolean auto){
        TlSqlSession sqlSession = new TlSqlSession(this.driver,this.url,this.name,this.password);
        sqlSession.SetCommit(auto);
        return sqlSession;
    }
}
