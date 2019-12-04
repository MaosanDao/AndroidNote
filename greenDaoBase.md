# 封装一可操作不同数据表的公共基类
```java
package cn.venii.cfn1.db;


import cn.venii.cfn1.app.GlobalConfiguration;
import cn.venii.cfn1.db.dao.DaoSession;

import java.util.List;


/**
 * Comment: 封装一可操作不同数据表的公共基类
 *
 * @author Vangelis.Wang in UpCan
 * @date 2018/7/20
 * Email:Pei.wang@icanup.cn
 */
public abstract class BaseDataManager<T> {

    protected DaoSession daoSession = GlobalConfiguration.mDaoSession;

    public BaseDataManager() {
    }

    /**
     * 插入单个对象
     *
     * @param object 插入对象
     * @return 是否成功
     */
    public boolean insertOrReplaceObject(T object) {
        boolean flag = false;
        try {
            flag = daoSession.insertOrReplace(object) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 插入多个对象，并开启新的线程
     *
     * @param objects 多个对象
     * @return 是否插入成功
     */
    public boolean insertMultObject(final List<T> objects) {
        boolean flag;
        if (null == objects || objects.isEmpty()) {
            return false;
        }
        try {
            daoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        daoSession.insertOrReplace(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 以对象形式进行数据修改
     * 其中必须要知道对象的主键ID
     *
     * @param object 更新数据
     */
    public void updateObject(T object) {

        if (null == object) {
            return;
        }
        try {
            daoSession.update(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量更新数据
     *
     * @param objects 更新数据
     */
    @SuppressWarnings("unchecked")
    public void updateMultObject(final List<T> objects, Class clss) {
        if (null == objects || objects.isEmpty()) {
            return;
        }
        try {

            daoSession.getDao(clss).updateInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        daoSession.update(object);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除某个数据库表
     */
    public boolean deleteAll(Class clss) {
        boolean flag;
        try {
            daoSession.deleteAll(clss);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 删除某个对象
     */
    public void deleteObject(T object) {
        try {
            daoSession.delete(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步批量删除数据
     */
    @SuppressWarnings("unchecked")
    public boolean deleteMultObject(final List<T> objects, Class clss) {
        boolean flag;
        if (null == objects || objects.isEmpty()) {
            return false;
        }
        try {

            daoSession.getDao(clss).deleteInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        daoSession.delete(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }


    /**
     * 获得某个表名
     *
     * @return 表名
     */
    @SuppressWarnings("unchecked")
    public String getTablename(Class object) {
        return daoSession.getDao(object).getTablename();
    }

    /**
     * 根据主键ID来查询
     */
    @SuppressWarnings("unchecked")
    public T queryById(long id, Class object) {
        return (T) daoSession.getDao(object).loadByRowId(id);
    }

    /**
     * 查询某条件下的对象
     */
    @SuppressWarnings("unchecked")
    public List<T> queryObject(Class object, String where, String... params) {
        Object obj;
        List<T> objects = null;
        try {
            obj = daoSession.getDao(object);
            if (null == obj) {
                return null;
            }
            objects = daoSession.getDao(object).queryRaw(where, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects;
    }

    /**
     * 查询所有对象
     */
    @SuppressWarnings("unchecked")
    public List<T> queryAll(Class object) {
        List<T> objects = null;
        try {
            objects = (List<T>) daoSession.getDao(object).loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }
}


```
