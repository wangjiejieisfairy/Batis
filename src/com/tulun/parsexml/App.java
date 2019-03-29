package com.tulun.parsexml;

import org.w3c.dom.*;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 描述: 实现xml文件的解析类
 *
 * @Author shilei
 * @Date 2018/11/11
 */
public class App {
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
     * 为datasource节点
     */
    private static Node da;
    /**
     * 存放name，value
     */
    public static HashMap<String,String> hashMap = new HashMap<String, String>();

    /**
     * 解析xml文件
     */
    public  void analysis(InputStream inputStream) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();

            Document document = builder.parse(inputStream);
            /**
             * 根结点
             */
            Element root = document.getDocumentElement();
            Find(root);

        } catch (Exception e) {
            e.printStackTrace();
        }

        for(Node childNode = da.getFirstChild();childNode != null;childNode = childNode.getNextSibling()){
            if(childNode instanceof Element){
                String name = ((Element) childNode).getAttribute("name");
                String value = ((Element) childNode).getAttribute("value");
                hashMap.put(name,value);
            }
        }
        Iterator<Map.Entry<String,String>> iterator = App.hashMap.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<String,String> next = iterator.next();
            if(next.getKey().equals("driver")) {
                this.driver = next.getValue();
            }
            if(next.getKey().equals("url")) {
                this.url = next.getValue();
            }
            if(next.getKey().equals("username")) {
                this.name = next.getValue();
            }
            if(next.getKey().equals("password")) {
                this.password = next.getValue();
            }
        }

    }

    /**
     * 查找DataSource
     * @param node
     */
    public static void Find(Node node){
        if(node == null){
            return;
        }
        for(Node childNode = node.getFirstChild();childNode != null;childNode = childNode.getNextSibling()){
            if(childNode.getNodeName().equals("dataSource")){
                da = childNode;
                return;
            }
            Find(childNode);
        }
    }
}
