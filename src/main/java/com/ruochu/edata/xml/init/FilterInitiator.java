package com.ruochu.edata.xml.init;

import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.Filter;

/**
 * 过滤器Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:38
 */
public class FilterInitiator {

    private Filter filter;
    private boolean isSheetFilter = true;

    FilterInitiator(Filter filter, boolean isSheetFilter) {
        this.filter = filter;
        this.isSheetFilter = isSheetFilter;
    }

    FilterInitiator(Filter filter){
        this.filter = filter;
    }

    void init(){
        String values = filter.getValues();
        String positions = filter.getPositions();

        // 值
        if (EmptyChecker.notEmpty(values)){
            for (String val : values.split(Constants.SEPARATOR)){
                val = val.trim();
                if (EmptyChecker.notEmpty(val)){
                    filter.addFilterVaule(val);
                }
            }
        }

        // 坐标
        if (isSheetFilter && EmptyChecker.notEmpty(positions)){
            for (String position : positions.split(Constants.SEPARATOR)){
                position = position.trim();
                if (!CoordinateUtil.isExcelPosition(position)){
                    throw new XmlConfigException("positions中的[".concat(position).concat("]不是合法的坐标"));
                }
                int[] numPosition = CoordinateUtil.toNumberPosition(position);
                filter.addFilterPosition(numPosition[1] + Constants.SEPARATOR + numPosition[0]);
            }
        }
    }
}
