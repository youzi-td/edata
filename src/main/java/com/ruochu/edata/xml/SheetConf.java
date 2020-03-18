package com.ruochu.edata.xml;

import com.ruochu.edata.enums.TableTypeEnum;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.util.Map;

/**
 * sheet
 *
 * @author RanPengCheng
 * @date 2019/07/06
 */
public class SheetConf implements Serializable {
    private static final long serialVersionUID = 1L;

    /** sheetName,用于数据匹配 */
    @XStreamAsAttribute
    private String sheetName;

    /** sheet唯一标识 */
    @XStreamAsAttribute
    private String sheetCode;

    /** 表头 */
    @XStreamAsAttribute
    private HeaderConf header;

    /** 横表表体 */
    @XStreamAsAttribute
    private BodyConf horizontalBody;

    /** 竖表表体 */
    @XStreamAsAttribute
    private BodyConf verticalBody;

    @XStreamAsAttribute
    private Filter valueFilter = new Filter();

    @XStreamAsAttribute
    private Filter templateFilter = new Filter();

    private TableTypeEnum tableType;

    private Map<String, CellConf> followTitleCellMap;


    public boolean isFollowTitleCell(int rowIndex, int colIndex){
        return followTitleCellMap.containsKey(CoordinateUtil.toExcelPosition(colIndex, rowIndex));
    }

    public CellConf getCell(int rowIndex, int colIndex){
        if (EmptyChecker.notEmpty(header)){
            for (CellConf cell : header.getCells()){
                if (cell.getRowIndex().equals(rowIndex) && cell.getColIndex().equals(colIndex)){
                    return cell;
                }
            }
        }

        if (TableTypeEnum.VERTICAL.equals(tableType)){
            for (CellConf cell : verticalBody.getCells()){
                if (cell.getRowIndex().equals(rowIndex) && cell.getColIndex().equals(colIndex)){
                    return cell;
                }
            }
        }

        if (TableTypeEnum.HORIZONTAL.equals(tableType)){
            for (CellConf cell : horizontalBody.getCells()){
                if (cell.getColIndex().equals(colIndex)){
                    return cell;
                }
            }
        }
        return null;
    }

    public CellConf getCell(String field) {
        CellConf cell = TableTypeEnum.HORIZONTAL.equals(tableType) ? horizontalBody.getCell(field) : verticalBody.getCell(field);
        if (cell == null && EmptyChecker.notEmpty(header)) {
            cell = header.getCell(field);
        }
        return cell;
    }


    public String getSheetName() {
        return sheetName;
    }

    public String getSheetCode() {
        return sheetCode;
    }

    public HeaderConf getHeader() {
        return header;
    }

    public BodyConf getHorizontalBody() {
        return horizontalBody;
    }

    public Filter getValueFilter() {
        return valueFilter;
    }

    public Filter getTemplateFilter() {
        return templateFilter;
    }

    public BodyConf getVerticalBody() {
        return verticalBody;
    }

    public TableTypeEnum getTableType() {
        return tableType;
    }

    public void setTableType(TableTypeEnum tableType) {
        this.tableType = tableType;
    }

    public Map<String, CellConf> getFollowTitleCellMap() {
        return followTitleCellMap;
    }

    public void setFollowTitleCellMap(Map<String, CellConf> followTitleCellMap) {
        this.followTitleCellMap = followTitleCellMap;
    }


}
