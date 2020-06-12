package com.ruochu.edata;

import org.apache.poi.ss.usermodel.Workbook;

import java.awt.image.BufferedImage;

/**
 * @author : RanPengCheng
 * @date : 2020/6/10 18:17
 */
public class Image {
    private BufferedImage bufferedImage;

    private Type type;

    public Image(BufferedImage bufferedImage, Type type) {
        this.bufferedImage = bufferedImage;
        this.type = type;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public Type getType() {
        return type;
    }


    public enum Type{
        PNG(Workbook.PICTURE_TYPE_PNG),
        JPEG(Workbook.PICTURE_TYPE_JPEG);

        private int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
