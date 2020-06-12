package com.ruochu.edata.model;

import com.ruochu.edata.EdataBaseDisplay;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author : RanPengCheng
 * @date : 2020/3/14 17:14
 */
public class AssetModel {
    private String assetName;
    private String assetCode;
    private ValueTypeDisplay valueType;
    private BigDecimal assetValue;
    private Integer amount;
    private Date obtainDate;
    private List<UseIntentionDisplay> useIntention;

    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private ZonedDateTime zonedDateTime;

    private int enumTest;

    private AAA aaa = new AAA();

    public AAA getAaa() {
        return aaa;
    }

    public void setAaa(AAA aaa) {
        this.aaa = aaa;
    }

    public int getEnumTest() {
        return enumTest;
    }

    public void setEnumTest(int enumTest) {
        this.enumTest = enumTest;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public ValueTypeDisplay getValueType() {
        return valueType;
    }

    public void setValueType(ValueTypeDisplay valueType) {
        this.valueType = valueType;
    }

    public BigDecimal getAssetValue() {
        return assetValue;
    }

    public void setAssetValue(BigDecimal assetValue) {
        this.assetValue = assetValue;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getObtainDate() {
        return obtainDate;
    }

    public void setObtainDate(Date obtainDate) {
        this.obtainDate = obtainDate;
    }

    public List<UseIntentionDisplay> getUseIntention() {
        return useIntention;
    }

    public void setUseIntention(List<UseIntentionDisplay> useIntention) {
        this.useIntention = useIntention;
    }


    public static class AAA implements EdataBaseDisplay {
        private String name = "aaaName";
        private String code = "aaaCode";

        @Override
        public String display() {
            return name;
        }
    }
}
