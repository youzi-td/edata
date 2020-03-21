package com.ruochu.edata.read.result;

import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.util.MapToBeanUtil;
import com.ruochu.edata.xml.CellConf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 导入的数据集
 *
 * @author RanPengCheng
 * @date 2019/7/14 17:15
 */
public class DataMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, String> data;
    private List<CellConf> cells;
    private String sheetCode;
    private Integer rowIndex;


    public DataMap(String sheetCode, List<CellConf> cells) {
        this.sheetCode = sheetCode;
        this.cells = cells;
        this.data = new HashMap<>(cells.size());
    }

    public DataMap(String sheetCode,  Integer rowIndex, List<CellConf> cells) {
        this.sheetCode = sheetCode;
        this.rowIndex = rowIndex;
        this.cells = cells;
        this.data = new HashMap<>(cells.size());
    }

    public <T> T getObject(Class<T> clazz) {
        try {
            return MapToBeanUtil.transfer(data, clazz, cells);
        } catch (ReflectiveOperationException e) {
            throw new ERuntimeException(e);
        }
    }

    public void put(String key, String value){
        data.put(key, value);
    }

    public String get(String key){
        return data.get(key);
    }

    public Set<String> keySet(){
        return data.keySet();
    }

    public int size(){
        return data.size();
    }

    public boolean containsKey(String key){
        return data.containsKey(key);
    }

    public boolean isEmpty(){
        return data.isEmpty();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
