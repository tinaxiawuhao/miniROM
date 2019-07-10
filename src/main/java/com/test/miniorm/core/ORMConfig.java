package com.test.miniorm.core;

import com.test.miniorm.utils.AnnotationUtil;
import com.test.miniorm.utils.Dom4jUtil;
import org.dom4j.Document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ORMConfig {

    public static String classpath;  //类路径
    public static File cfgFile;  //核心配置文件
    public static Map<String, String> propConfig; //核心配置文件数据
    public static Set<String> mappingSet; //映射配置文件
    public static Set<String> entitySet; //实体类
    public static List<Mapper> mapperList; //解析出来的Mapper

    // 从classpath中加载框架的核心配置文件miniORM.cfg.xml
    static {
        classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        if (classpath == null) {
            classpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        }
        try {
            classpath = java.net.URLDecoder.decode(classpath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        cfgFile = new File(classpath + "miniORM.cfg.xml");
        if (cfgFile.exists()) {
            Document document = Dom4jUtil.getXMLByFilePath(cfgFile.getPath());
            propConfig = Dom4jUtil.Elements2Map(document, "property", "name");
            mappingSet = Dom4jUtil.Elements2Set(document, "mapping", "resource");
            entitySet = Dom4jUtil.Elements2Set(document, "entity", "package");
        } else {
            cfgFile = null;
            System.out.println("未找到核心配置文件miniORM.cfg.xml");
        }
    }

	//从propConfig获得信息，连接数据库
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        String url = propConfig.get("connection.url");
        String driverClass = propConfig.get("connection.driverClass");
        String username = propConfig.get("connection.username");
        String password = propConfig.get("connection.password");
        // 加载驱动程序
        Class.forName(driverClass);
        Connection connection = DriverManager.getConnection(url, username, password);
        // setAutoCommit 默认为true，即每条SQL语句在各自的一个事务中执行。
        connection.setAutoCommit(true);
        return connection;
    }

    //从mappingSet中挨个解析mapper.xml配置文件，获得实体类和表之间的映射信息
    //从entitySet中挨个解析实体类中的注解，获得实体类和表之间的映射信息
    private void getMapping() throws ClassNotFoundException {
        mapperList = new ArrayList<>();
        for (String xmlPath : mappingSet) {
            Document document = Dom4jUtil.getXMLByFilePath(classpath + xmlPath);

            Map<String, String> mapping = Dom4jUtil.Elements2Map(document);

            String className = Dom4jUtil.getPropValue(document, "class", "name");
            String tableName = Dom4jUtil.getPropValue(document, "class", "table");
            Map<String, String> id_id = Dom4jUtil.ElementsID2Map(document);

            Mapper mapper = new Mapper();
            mapper.setClassName(className);
            mapper.setTableName(tableName);
            mapper.setIdMapper(id_id);
            mapper.setPropMapping(mapping);

            mapperList.add(mapper);
        }

        for (String packagePath : entitySet) {
            Set<String> nameSet = AnnotationUtil.getClassNameByPackage(packagePath);
            for (String name : nameSet) {
                Class clz = Class.forName(name);
                String className = AnnotationUtil.getClassName(clz);
                String tableName = AnnotationUtil.getTableName(clz);
                Map<String, String> id_id = AnnotationUtil.getIdMapper(clz);
                Map<String, String> mapping = AnnotationUtil.getPropMapping(clz);
                Mapper mapper = new Mapper();
                mapper.setClassName(className);
                mapper.setTableName(tableName);
                mapper.setIdMapper(id_id);
                mapper.setPropMapping(mapping);

                mapperList.add(mapper);
            }
        }
    }

public ORMSession buildORMSession() throws Exception {
        //从propConfig获得信息，连接数据库
        Connection connection = getConnection();

        //从mappingSet中挨个解析mapper.xml配置文件，获得实体类和表之间的映射信息
        getMapping();

        //创建ORMSession对象
        return new ORMSession(connection);
    }
}