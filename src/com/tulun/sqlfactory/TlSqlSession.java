package com.tulun.sqlfactory;

import com.tulun.annotation.TlDelete;
import com.tulun.annotation.TlInsert;
import com.tulun.annotation.TlSelect;
import com.tulun.annotation.TlUpdate;
import com.tulun.cache.Excuter;

import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述:
 *
 * @Author shilei
 * @Date 2018/11/11
 */
public class TlSqlSession {

    // 在这里实现MyBatis框架的一级缓存机制
    Excuter excuter;


    /**
     * 构造函数
     * @param driver 数据库启动需要的参数
     * @param url
     * @param name
     * @param password
     */
    TlSqlSession(String driver,String url,String name,String password){
        excuter = new Excuter(driver,url,name,password);
    }

    public void SetCommit(boolean auto){
        // 禁止jdbc自动提交事务
        try {
            excuter.getConnection().setAutoCommit(auto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这个是框架动态代理的核心入口
     * @param c
     * @param <T>
     * @return
     */

    // StudentMapper sm = session.getMapper(StudentMapper.class);
    // 实现Mapper方法

    /**
     * 两级缓存基于Mapper实现
     * @param c
     * @param <T>
     * @return
     * private Map<String, second> second = new HashMap<String, second>();
     */
    public <T> T getMapper(Class<T> c){
        if(!c.isInterface()){
            throw new IllegalArgumentException(c.toString()+" is not an interface !");
        }

        return  (T) Proxy.newProxyInstance(TlSqlSession.class.getClassLoader(),
                new Class[]{c}, new MyHandler());
    }


    /**
     * 关闭连接SqlSession
     */
    public void close(){
        excuter.close();
    }

    /**
     * 提交事务
     */
    public void commit(){
        excuter.commit();
    }

    /**
     * 回滚事务
     */
    public void rollback(){
       excuter.rollback();
    }




    // 给$Proxy0代理对象使用的  作为该代理对象的成员变量
    class MyHandler implements InvocationHandler {


        /**
         * 处理查询语句
         * @param annotation
         * @param method
         */
        private Object MySelect(TlSelect annotation,Method method,Object[] args) throws Exception{

            if (annotation == null) {
                return null;
            }
            return excuter.MySelect(annotation,method,args);

        }

        /**
         * 处理插入语句
         * @param annotation
         * @param method
         */
        private Object MyInsert(TlInsert annotation, Method method,Object[] args) throws Exception {
            if (annotation == null) {
                return null;
            }
            return excuter.MyInsert(annotation,method,args);

        }

        /**
         * 处理更新语句
         * @param annotation
         * @param method
         */
        private Object MyUpdate(TlUpdate annotation, Method method, Object[] args) throws Exception {
            if (annotation == null) {
                return null;
            }
            return excuter.MyUpdate(annotation,method,args);
        }

        /**
         * 处理删除语句
         * @param annotation
         * @param method
         */
        private Object MyDelete(TlDelete annotation, Method method, Object[] args) throws Exception {
            if (annotation == null) {
                return null;
            }
            return excuter.MyDelete(annotation,method,args);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // method
            // 通过反射访问method的注解 @MySelect
            // @MySelect value => select * from student where id = #{id}
            //   MySelect s1 = method.getDeclaredAnnotations(MySelect.class);
            Annotation[] anns = method.getDeclaredAnnotations();
            // TlSelect s = null;
            Annotation annq = null;

            for (Annotation ann : anns) {
                if (ann instanceof TlSelect || (ann instanceof TlDelete)
                        || ann instanceof TlInsert || ann instanceof TlUpdate) {
                    annq = ann;
                    break;
                }
            }
            if(annq instanceof TlSelect){
                return MySelect((TlSelect)annq,method,args);
            }

            if(annq instanceof TlInsert){
                return MyInsert((TlInsert)annq,method,args);
            }

            if(annq instanceof TlDelete){
                return MyDelete((TlDelete)annq,method,args);
            }

            if(annq instanceof TlUpdate){
                return MyUpdate((TlUpdate)annq,method,args);
            }
            return null;

        }



    }

}




