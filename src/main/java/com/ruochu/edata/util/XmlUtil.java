package com.ruochu.edata.util;

import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.reflect.FieldDefaultValueProvider;
import com.ruochu.edata.xml.ExcelConf;
import com.ruochu.edata.xml.init.ExcelInitiator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.ruochu.edata.util.EmptyChecker.isEmpty;

/**
 * xml配置工具类
 *
 * @author RanPengCheng
 * @date 2019/7/14 21:59
 */
public class XmlUtil {
    private XmlUtil(){}

    /** 导入的xml缓存 */
    private static final Map<String, ExcelConf> READ_XML_CACHE = new HashMap<>();
    /** 导出的xml缓存 */
    private static final Map<String, ExcelConf> WRITE_XML_CACHE = new HashMap<>();


    private static final int LOCK_POOL_SIZE = 16;
    private static final ReentrantReadWriteLock[] LOCK_POOL = new ReentrantReadWriteLock[LOCK_POOL_SIZE];

    static {
        for (int i = 0; i < LOCK_POOL_SIZE; i++) {
            LOCK_POOL[i] = new ReentrantReadWriteLock();
        }
    }

    public static ExcelConf parseXmlConfig(String xmlPath, boolean isRead){
        // 获得分段锁
        ReentrantReadWriteLock readWriteLock = LOCK_POOL[Math.abs(xmlPath.hashCode()) % LOCK_POOL_SIZE];

        readWriteLock.readLock().lock();
        try {
            // 读写分离
            if (READ_XML_CACHE.containsKey(xmlPath)){
                return READ_XML_CACHE.get(xmlPath).deepClone();
            } else if(!isRead && WRITE_XML_CACHE.containsKey(xmlPath)) {
                return WRITE_XML_CACHE.get(xmlPath);
            }
        }finally {
            readWriteLock.readLock().unlock();
        }

        readWriteLock.writeLock().lock();
        try {
            // 双重检查
            if (READ_XML_CACHE.containsKey(xmlPath)){
                return READ_XML_CACHE.get(xmlPath).deepClone();
            } else if(!isRead && WRITE_XML_CACHE.containsKey(xmlPath)) {
                return WRITE_XML_CACHE.get(xmlPath);
            }

            XStream xStream = new XStream(new FieldDefaultValueProvider(),new DomDriver("UTF-8"));
            XStream.setupDefaultSecurity(xStream);
            xStream.allowTypes(new Class[]{ExcelConf.class});
            xStream.processAnnotations(ExcelConf.class);

            URL resource = XmlUtil.class.getClassLoader().getResource(xmlPath);
            if (isEmpty(resource)) {
                throw new ERuntimeException("未找到xml文件: %s", xmlPath);
            }
            ExcelConf excel = (ExcelConf)xStream.fromXML(resource);
            new ExcelInitiator(excel, isRead).init();
            if (isRead) {
                READ_XML_CACHE.put(xmlPath, excel);
            } else {
                WRITE_XML_CACHE.put(xmlPath, excel);
            }
            return excel.deepClone();

        }finally {
            readWriteLock.writeLock().unlock();
        }

    }

}
