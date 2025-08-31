package org.dnc.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public final class QrCodeUtil {
    private QrCodeUtil() {}

    public static Image fromText(String text, int size) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);

        int w = matrix.getWidth();
        int h = matrix.getHeight();
        WritableImage img = new WritableImage(w, h);
        PixelWriter pw = img.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pw.setColor(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return img;
    }
}
