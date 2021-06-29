package com.simple.bz.dao;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ContextQuery {
    private final EntityManager entityManager;

    public <T> Page<T> queryPage(String querySql, Map<String, Object> params, Pageable pageable, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        Query listQuery = entityManager.createNativeQuery(querySql);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        listQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//自动映射成map[稍微影响一点查询效率，但是封装后比较方便查看对应的字段，不需要再使用Object[] obj,obj[0],obj[1]...的方式]
        for (Map.Entry<String, Object> entry : params.entrySet()) {//加入参数
            listQuery.setParameter(entry.getKey(), entry.getValue());
        }

        int startIndex = querySql.indexOf("select") + "select".length();
        int endIndex = querySql.indexOf("from");
        String totalSql = StringUtils.overlay(querySql, " count(*) ", startIndex, endIndex);
        System.out.println("Total SQL ===> " + totalSql);
        Query countQuery = entityManager.createNativeQuery(totalSql);

        List<Map<String, Object>> list = listQuery.getResultList();//由于上面已经将结果映射成了map所以这里直接转化成Map没问题
        List<T> resultList = new ArrayList<>();
        for (Map<String, Object> map : list) {//遍历map将map转化为实体类bean
            T bean = clazz.newInstance();//实例化T，可能会抛出两个异常IllegalAccessException、InstantiationException
            for (Map.Entry<String, Object> entry : map.entrySet()) {//格式化Timestamp为String类型,数据库中日期类型为Timestamp，在这里需要转化一下，直接在前端使用
                if (entry.getValue() instanceof BigInteger) {
                    map.put(entry.getKey(), ((BigInteger) entry.getValue()).longValue());
                }
            }
            BeanMap.create(bean).putAll(map);

            resultList.add(bean);
        }
        BigInteger count = (BigInteger) countQuery.getSingleResult();

        return new PageImpl<>(resultList, pageable, count.longValue());
    }

    public <T> Page<T> queryPage(String querySql, Map<String, Object> params, int pageIndex, int pageSize, Class<T> clazz) throws IllegalAccessException, InstantiationException {

        Query listQuery = entityManager.createNativeQuery(querySql);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        listQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//自动映射成map[稍微影响一点查询效率，但是封装后比较方便查看对应的字段，不需要再使用Object[] obj,obj[0],obj[1]...的方式]
        for (Map.Entry<String, Object> entry : params.entrySet()) {//加入参数
            listQuery.setParameter(entry.getKey(), entry.getValue());
        }

        int startIndex = querySql.indexOf("select") + "select".length();
        int endIndex = querySql.indexOf("from");
        String totalSql = StringUtils.overlay(querySql, " count(*) ", startIndex, endIndex);
        System.out.println("Total SQL ===> " + totalSql);
        Query countQuery = entityManager.createNativeQuery(totalSql);

        List<Map<String, Object>> list = listQuery.getResultList();//由于上面已经将结果映射成了map所以这里直接转化成Map没问题
        List<T> resultList = new ArrayList<>();
        for (Map<String, Object> map : list) {//遍历map将map转化为实体类bean
            T bean = clazz.newInstance();//实例化T，可能会抛出两个异常IllegalAccessException、InstantiationException
            for (Map.Entry<String, Object> entry : map.entrySet()) {//格式化Timestamp为String类型,数据库中日期类型为Timestamp，在这里需要转化一下，直接在前端使用
                if (entry.getValue() instanceof BigInteger) {
                    map.put(entry.getKey(), ((BigInteger) entry.getValue()).longValue());
                }
            }
            BeanMap.create(bean).putAll(map);

            resultList.add(bean);
        }
        BigInteger count = (BigInteger) countQuery.getSingleResult();

        return new PageImpl<>(resultList, pageable, count.longValue());
    }

    public <T> List<T> findPage(String querySql, Map<String, Object> params, int pageIndex, int pageSize, Class<T> clazz) {

        Query listQuery = entityManager.createNativeQuery(querySql);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        listQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//自动映射成map[稍微影响一点查询效率，但是封装后比较方便查看对应的字段，不需要再使用Object[] obj,obj[0],obj[1]...的方式]
        for (Map.Entry<String, Object> entry : params.entrySet()) {//加入参数
            listQuery.setParameter(entry.getKey(), entry.getValue());
        }

        int startIndex = querySql.indexOf("select") + "select".length();
        int endIndex = querySql.indexOf("from");
        String totalSql = StringUtils.overlay(querySql, " count(*) ", startIndex, endIndex);
        System.out.println("Total SQL ===> " + totalSql);
        Query countQuery = entityManager.createNativeQuery(totalSql);

        List<Map<String, Object>> list = listQuery.getResultList();//由于上面已经将结果映射成了map所以这里直接转化成Map没问题
        List<T> resultList = new ArrayList<>();
        try {
            for (Map<String, Object> map : list) {//遍历map将map转化为实体类bean
                T bean = clazz.newInstance();//实例化T，可能会抛出两个异常IllegalAccessException、InstantiationException
                for (Map.Entry<String, Object> entry : map.entrySet()) {//格式化Timestamp为String类型,数据库中日期类型为Timestamp，在这里需要转化一下，直接在前端使用
                    if (entry.getValue() instanceof BigInteger) {
                        map.put(entry.getKey(), ((BigInteger) entry.getValue()).longValue());
                    }
                }
                BeanMap.create(bean).putAll(map);

                resultList.add(bean);
            }
            BigInteger count = (BigInteger) countQuery.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create list of Beans");
        }
        return resultList;
    }
    public <T> List<T> findPage(String querySql, int pageIndex, int pageSize, Class<T> clazz) {

        Query listQuery = entityManager.createNativeQuery(querySql);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        listQuery.setFirstResult((int) pageable.getOffset());
        listQuery.setMaxResults(pageable.getPageSize());
        listQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//自动映射成map[稍微影响一点查询效率，但是封装后比较方便查看对应的字段，不需要再使用Object[] obj,obj[0],obj[1]...的方式]

        int startIndex = querySql.indexOf("select") + "select".length();
        int endIndex = querySql.indexOf("from");
        String totalSql = StringUtils.overlay(querySql, " count(*) ", startIndex, endIndex);
        System.out.println("Total SQL ===> " + totalSql);
        Query countQuery = entityManager.createNativeQuery(totalSql);

        List<Map<String, Object>> list = listQuery.getResultList();//由于上面已经将结果映射成了map所以这里直接转化成Map没问题
        List<T> resultList = new ArrayList<>();
        try {
            for (Map<String, Object> map : list) {//遍历map将map转化为实体类bean
                T bean = clazz.newInstance();//实例化T，可能会抛出两个异常IllegalAccessException、InstantiationException
                for (Map.Entry<String, Object> entry : map.entrySet()) {//格式化Timestamp为String类型,数据库中日期类型为Timestamp，在这里需要转化一下，直接在前端使用
                    if (entry.getValue() instanceof BigInteger) {
                        map.put(entry.getKey(), ((BigInteger) entry.getValue()).longValue());
                    }
                }
                BeanMap.create(bean).putAll(map);

                resultList.add(bean);
            }
            BigInteger count = (BigInteger) countQuery.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create list of Beans");
        }
        return resultList;
    }

    public <T> List<T> findList(String querySql, Map<String, Object> params, Class<T> clazz) {
        Query listQuery = entityManager.createNativeQuery(querySql);
        listQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//自动映射成map[稍微影响一点查询效率，但是封装后比较方便查看对应的字段，不需要再使用Object[] obj,obj[0],obj[1]...的方式]
        if (null != params) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {//加入参数
                listQuery.setParameter(entry.getKey(), entry.getValue());
            }
        }

        List<Map<String, Object>> list = listQuery.getResultList();//由于上面已经将结果映射成了map所以这里直接转化成Map没问题
        List<T> resultList = new ArrayList<>();
        try {
            for (Map<String, Object> map : list) {//遍历map将map转化为实体类bean
                T bean = clazz.newInstance();//实例化T，可能会抛出两个异常IllegalAccessException、InstantiationException
                for (Map.Entry<String, Object> entry : map.entrySet()) {//格式化Timestamp为String类型,数据库中日期类型为Timestamp，在这里需要转化一下，直接在前端使用
                    if (entry.getValue() instanceof BigInteger) {
                        map.put(entry.getKey(), ((BigInteger) entry.getValue()).longValue());
                    }
                }
                BeanMap.create(bean).putAll(map);
                resultList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create list of Beans");
        }

        return resultList;
    }
    public <T> List<T> findList(String querySql, Class<T> clazz) {
        Query listQuery = entityManager.createNativeQuery(querySql);
        listQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//自动映射成map[稍微影响一点查询效率，但是封装后比较方便查看对应的字段，不需要再使用Object[] obj,obj[0],obj[1]...的方式]

        List<Map<String, Object>> list = listQuery.getResultList();//由于上面已经将结果映射成了map所以这里直接转化成Map没问题
        List<T> resultList = new ArrayList<>();
        try {
            for (Map<String, Object> map : list) {//遍历map将map转化为实体类bean
                T bean = clazz.newInstance();//实例化T，可能会抛出两个异常IllegalAccessException、InstantiationException
                for (Map.Entry<String, Object> entry : map.entrySet()) {//格式化Timestamp为String类型,数据库中日期类型为Timestamp，在这里需要转化一下，直接在前端使用
                    if (entry.getValue() instanceof BigInteger) {
                        map.put(entry.getKey(), ((BigInteger) entry.getValue()).longValue());
                    }
                }
                BeanMap.create(bean).putAll(map);
                resultList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create list of Beans");
        }

        return resultList;
    }

}