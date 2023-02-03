package net.fornwall.apksigner.zip;


import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

/**
 * Created by sunwanquan on 2019/9/18.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ZipSignEntry extends ZipEntry {

    public ZipSignEntry(ZipEntry entry) throws ZipException {
        super(entry);
    }

    private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
