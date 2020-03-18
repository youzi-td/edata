package com.ruochu.edata.xml;

import com.ruochu.edata.exception.ERuntimeException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel配置类
 *
 * @author RanPengCheng
 * @date 2019/07/06
 */
@XStreamAlias("excel")
public class ExcelConf implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * sheet
     */
    @XStreamImplicit(itemFieldName = "sheet")
    private List<SheetConf> sheets;

    /**
     * 全局过滤器
     */
    @XStreamAsAttribute
    private Filter globalFilter = new Filter();

    /**
     * 是否校验模板
     */
    @XStreamAsAttribute
    private Boolean checkTemplate = Boolean.TRUE;

    @XStreamAsAttribute
    private Boolean checkSheetSequence = Boolean.FALSE;

    private Map<String, SheetConf> sheetMap;

    public SheetConf getSheetBySheetName(String sheetName){
        for (SheetConf sheetConf : sheets){
            if (sheetConf.getSheetName().equals(sheetName)){
                return sheetConf;
            }
        }
        return null;
    }

    public SheetConf getSheetBySheetCode(String sheetCode){
        return getSheetMap().get(sheetCode);
    }

    private Map<String, SheetConf> getSheetMap() {
        if (sheetMap == null) {
            sheetMap = new HashMap<>(16);
            for (SheetConf sheet : sheets) {
                sheetMap.put(sheet.getSheetCode(), sheet);
            }
        }
        return sheetMap;
    }

    public List<SheetConf> getSheets() {
        return sheets;
    }

    public Filter getGlobalFilter() {
        return globalFilter;
    }

    public Boolean getCheckTemplate() {
        return checkTemplate;
    }

    public Boolean getCheckSheetSequence() {
        return checkSheetSequence;
    }

    public ExcelConf deepClone() {
        try {
            //将对象写入流中
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(this);
            //从流中取出
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (ExcelConf) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new ERuntimeException("深度拷贝时异常", e);
        }
    }
}
