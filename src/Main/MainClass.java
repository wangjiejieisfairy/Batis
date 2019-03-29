package Main;

/**
 * 在进行延迟加载的时候，需要用到CGLIB（一个动态代理---》代理的是类，Proxy代理的是接口）
 * 进行延迟加载，在加载类的时候不会执行第二个SQl语句，只有在调用getList（）方法时才会执行第二个SQl语句。
 * 这个时候CGLIB需要产生一个类动态代理bean类，拿到bean的get方法，对这个方法进行重写，重写时先执行SQL语句
 * 再进行组装对象，返回对象。
 */
public class MainClass {
    public static void main(String[] args) {

    }
}
