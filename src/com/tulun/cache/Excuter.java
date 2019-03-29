package com.tulun.cache;

import com.tulun.annotation.TlDelete;
import com.tulun.annotation.TlInsert;
import com.tulun.annotation.TlSelect;
import com.tulun.annotation.TlUpdate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Excuter {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    /**
     * 一级缓存cache，由于是基于SQlSession的，不可能发生多线程的情况，所以不用考虑线程安全
     */
    private Map<String, Object> firstLevelCache = new HashMap<String, Object>();

    public Excuter(String driver,String url,String name,String password){
        try {

            Class.forName(driver);
            connection = DriverManager.getConnection(url,name,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**创建出来一个唯一的key值
     *
     * @param s
     * @return
     */
    public String createKey(String s){
        StringBuilder s1 = new StringBuilder();
        boolean flag = false;
        for(int i = 0;i < s.length();i++) {
            if(flag){
                s1.append(s.charAt(i));
            }
            if(s.charAt(i) == ':'){
                flag = true;
            }
        }
        return s1.toString();
    }

    /**
     * 实现一级缓存
     */
    public Boolean havedMap(String key){

        if(firstLevelCache.containsKey(key)){
            return true;
        }
        return false;

    }
    public Object getMap(String key){
        Iterator<Map.Entry<String,Object>> iterator = firstLevelCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,Object> next = iterator.next();
            if(next.getKey().equals(key)){
                return next.getValue();
            }
        }
        return null;
    }


    public Object MySelect(TlSelect annotation, Method method, Object[] args) throws Exception{
        // 解析args，把参数拿出来，填到上面的sql语句中
        // Preparestatement => server  => sql
        String sql = annotation.value();

        for (; ; ) {
            int begin = sql.indexOf("#{");
            if (begin == -1) {
                break;
            }
            int end = sql.indexOf("}", begin);

            sql = sql.substring(0, begin) + "?" + sql.substring(end + 1);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        /**
         * 将？填上
         */
        if(args != null){
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
        }



        if(havedMap(createKey(preparedStatement.toString()))){
            return getMap(createKey(preparedStatement.toString()));
        }else{
            ResultSet resultSet = preparedStatement.executeQuery();

            //获取简要的返回值类型的名字method.getReturnType().getSimpleName();
            //Class.forName(Map);  获取对象

            Class<?> returnType = method.getReturnType();
            //获得底层方法的正式返回类型如ArrayList<Student> 返回即为 java.util.List<MyMyBatis.Student>

            Type genericReturnType = method.getGenericReturnType();
            //判断其是否为泛型化的字段

            if(genericReturnType instanceof ParameterizedType) {
                //获得其中的MyBatis.Student
                returnType = (Class) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                /**
                 * 返回值类型对应的属性
                 */
                Field[] fields = returnType.getDeclaredFields();
                ArrayList<Object> objects = new ArrayList<Object>();

                while(resultSet.next()){
                    Object object = returnType.newInstance();

                    for(Field field: fields){

                        field.setAccessible(true);
                        //获取结果集中的每一个参数
                        Object value = resultSet.getObject(field.getName());
                        //修改属性值
                        field.set(object,value);
                    }
                    objects.add(object);
                }
                String key = createKey(preparedStatement.toString());
                firstLevelCache.put(key,objects);
                return objects;
            }else{
                Field[] fields = returnType.getDeclaredFields();
                Object object = returnType.newInstance();
                if (resultSet.next()) {

                    for(Field field: fields){

                        field.setAccessible(true);
                        //获取结果集中的每一个参数
                        Object value = resultSet.getObject(field.getName());
                        //修改属性值
                        field.set(object,value);
                    }
                }
                String key = createKey(preparedStatement.toString());
                firstLevelCache.put(key,object);
                return object;
            }
        }


    }

    public Object MyInsert(TlInsert annotation, Method method, Object[] args) throws Exception{
        // 解析args，把参数拿出来，填到上面的sql语句中
        // Preparestatement => server  => sql
        String sql = annotation.value();

        for (; ; ) {
            int begin = sql.indexOf("#{");
            if (begin == -1) {
                break;
            }
            int end = sql.indexOf("}", begin);

            sql = sql.substring(0, begin) + "?" + sql.substring(end + 1);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        /**
         * 将？填上
         */
        for (int i = 0; i < args.length; i++) {
            Field[] fields = args[i].getClass().getDeclaredFields();
//            System.out.println(args[i].toString());
            for(int j = 0;j < fields.length;j++) {
                fields[j].setAccessible(true);
//                System.out.println(fields[j].get(args[i]));
                preparedStatement.setObject(j+1,fields[j].get(args[i]));
            }
        }

        Integer reslut = preparedStatement.executeUpdate();
        firstLevelCache.clear();

        return reslut;
    }


    public Object MyDelete(TlDelete annotation, Method method, Object[] args) throws Exception{
        // 解析args，把参数拿出来，填到上面的sql语句中
        // Preparestatement => server  => sql
        String sql = annotation.value();

        for (; ; ) {
            int begin = sql.indexOf("#{");
            if (begin == -1) {
                break;
            }
            int end = sql.indexOf("}", begin);

            sql = sql.substring(0, begin) + "?" + sql.substring(end + 1);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        if(args != null){
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
        }

        Integer reslut = preparedStatement.executeUpdate();
        firstLevelCache.clear();

        return reslut;
    }

    public Object MyUpdate(TlUpdate annotation, Method method, Object[] args) throws Exception{
        // 解析args，把参数拿出来，填到上面的sql语句中
        // Preparestatement => server  => sql
        String sql = annotation.value();

        for (; ; ) {
            int begin = sql.indexOf("#{");
            if (begin == -1) {
                break;
            }
            int end = sql.indexOf("}", begin);

            sql = sql.substring(0, begin) + "?" + sql.substring(end + 1);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        /**
         * 将？填上
         */
        for (int i = 0; i < args.length; i++) {
            Field[] fields = args[i].getClass().getDeclaredFields();
            //     System.out.println(args[i].toString());
            int j;
            for(j = 1;j < fields.length;j++) {
                fields[j].setAccessible(true);
                preparedStatement.setObject(j,fields[j].get(args[i]));

            }
            fields[0].setAccessible(true);
            preparedStatement.setObject(j,fields[0].get(args[i]));
        }

        Integer reslut = preparedStatement.executeUpdate();
        firstLevelCache.clear();

        return reslut;
    }

    public void SetCommit(boolean auto){
        // 禁止jdbc自动提交事务
        try {
            connection.setAutoCommit(auto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭连接SqlSession
     */
    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交事务
     */
    public void commit(){
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回滚事务
     */
    public void rollback(){
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
