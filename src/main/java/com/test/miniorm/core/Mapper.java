package com.test.miniorm.core;

import java.util.HashMap;
import java.util.Map;

public class Mapper {
    private String className;  //类名
    private String tableName;  //表名
    private Map<String,String> idMapper=new HashMap();  //主键字段和属性
    private Map<String,String> propMapping=new HashMap();  //非主键字段和属性

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getIdMapper() {
        return idMapper;
    }

    public void setIdMapper(Map<String, String> idMapper) {
        this.idMapper = idMapper;
    }

    public Map<String, String> getPropMapping() {
        return propMapping;
    }

    public void setPropMapping(Map<String, String> propMapping) {
        this.propMapping = propMapping;
    }
}