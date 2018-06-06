package com.qa.file.window;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayModel extends AbstractTableModel {

    private String columnNames[] = {"Name", "Date Modified", "Type", "Size"};
    private List<File> fileList = new ArrayList<>();
    private File file;

    public void addRow(File file) {
        fileList.add(file);
        fireTableRowsInserted(fileList.size() - 1, fileList.size() - 1);
    }

    public void clearRow() {
        fileList.clear();
        fireTableRowsDeleted(fileList.size() - 1, fileList.size() - 1);
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return fileList.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        file = fileList.get(row);

        switch (col) {
            case 0:
                return file.getName();
            case 1:
                return new SimpleDateFormat("dd-MM-yyyy h:mm a").format(new Date(file.lastModified()));
            case 2:
                return file.getName().replaceAll(".*\\.", "");
            case 3:
                if (file.length() / 1024 == 0) return "";
                else return file.length() / 1024 + " KB"; //kilobytes
            default:
                return null;
        }
    }

    File getFile() {
        return file;
    }

    public List<File> getFileList() {
        return fileList;
    }
}
