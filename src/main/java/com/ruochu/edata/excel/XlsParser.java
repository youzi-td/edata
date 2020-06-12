package com.ruochu.edata.excel;

import com.ruochu.edata.excel.model.ESheet;
import com.ruochu.edata.excel.model.EWorkbook;
import com.ruochu.edata.util.EmptyChecker;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author RanPengCheng
 */
public class XlsParser implements ExcelParser, HSSFListener {
    private EWorkbook eWorkbook = new EWorkbook();
    private ESheet eSheet;

    /**
     * Records we pick up as we process
     */
    private SSTRecord sstRecord;

    private FormatTrackingHSSFListener formatListener;

    /**
     * 表索引
     */
    private int sheetIndex = -1;

    private BoundSheetRecord[] orderedBSRs;

    private List<BoundSheetRecord> boundSheetRecords = new ArrayList<>();

    /**
     * For handling formulas with string results
     */
    private int nextRow;

    private int nextColumn;

    private boolean outputNextStringRecord;

    /**
     * 当前行
     */
    private int curRow = 0;

    /**
     * 当前列
     */
    private int curCol = 0;


    /**
     * 遍历excel下所有的sheet
     *
     * @throws IOException
     */
    @Override
    public EWorkbook read(InputStream is) throws IOException {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);

        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(formatListener);
        factory.processWorkbookEvents(request, new POIFSFileSystem(is));
        return eWorkbook;
    }

    /**
     * HSSFListener 监听方法，处理 Record
     */
    @Override
    public void processRecord(Record record) {
        String value = "";
        switch (record.getSid()) {
            case BoundSheetRecord.sid:
                boundSheetRecords.add((BoundSheetRecord) record);
                break;
            case BOFRecord.sid:
                BOFRecord br = (BOFRecord) record;
                if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
                    sheetIndex++;
                    if (orderedBSRs == null) {
                        orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                    }
                    eSheet = new ESheet(orderedBSRs[sheetIndex].getSheetname());
                }
                break;

            case SSTRecord.sid:
                sstRecord = (SSTRecord) record;
                break;

            case BlankRecord.sid:
                BlankRecord brec = (BlankRecord) record;
                curRow = brec.getRow();
                curCol = brec.getColumn();
                break;
            case BoolErrRecord.sid: // 单元格为布尔类型
                BoolErrRecord berec = (BoolErrRecord) record;
                curRow = berec.getRow();
                curCol = berec.getColumn();
                value = String.valueOf(berec.getBooleanValue());
                break;

            case FormulaRecord.sid: // 单元格为公式类型
                FormulaRecord frec = (FormulaRecord) record;
                curRow = frec.getRow();
                curCol = frec.getColumn();

                if (Double.isNaN(frec.getValue())) {
                    // Formula result is a string
                    // This is stored in the next record
                    outputNextStringRecord = true;
                    nextRow = frec.getRow();
                    nextColumn = frec.getColumn();
                } else {
                    value = formatListener.formatNumberDateCell(frec);
                }

                break;
            case StringRecord.sid:// 单元格中公式的字符串
                if (outputNextStringRecord) {
                    // String for formula
                    StringRecord srec = (StringRecord) record;
                    value = srec.getString();
                    curRow = nextRow;
                    curCol = nextColumn;
                    outputNextStringRecord = false;
                } else {
                    value = ((StringRecord) record).getString();
                }
                break;
            case LabelRecord.sid:
                LabelRecord lrec = (LabelRecord) record;
                curRow = lrec.getRow();
                curCol = lrec.getColumn();
                value = lrec.getValue().trim();
                break;
            case LabelSSTRecord.sid: // 单元格为字符串类型
                LabelSSTRecord lsrec = (LabelSSTRecord) record;
                curRow = lsrec.getRow();
                curCol = lsrec.getColumn();
                if (sstRecord != null) {
                    value = sstRecord.getString(lsrec.getSSTIndex()).toString().trim();
                }
                break;
            case NumberRecord.sid: // 单元格为数字类型
                NumberRecord numrec = (NumberRecord) record;
                curRow = numrec.getRow();
                curCol = numrec.getColumn();
                value = formatListener.formatNumberDateCell(numrec).trim();
                break;
            case EOFRecord.sid:
                if (EmptyChecker.notEmpty(eSheet)) {
                    eWorkbook.addSheet(eSheet);
                }
                eSheet = null;
                break;
            default:
                break;
        }

        // 空值的操作
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            curRow = mc.getRow();
            curCol = mc.getColumn();
            value = "";
        }

        if (EmptyChecker.notEmpty(value)) {
            eSheet.addData(curRow + 1, curCol + 1, value.trim());
        }
    }
}
