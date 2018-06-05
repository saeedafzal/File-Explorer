package com.qa.file.window;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DisplayModel extends AbstractTableModel {

    private String columnNames[] = {"Name", "Date Modified", "Type", "Size"};
    private List<File> fileList = new ArrayList<>();

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return fileList.size();
    }

    public Object getValueAt(int row, int col) {

        final File file = fileList.get(row);

        switch (col) {
            case 0:
                return file.getName();
            case 1:
                return file.lastModified();
            case 2:
                return file.getName().replaceAll(".*\\.", "");
            case 3:
                return file.getTotalSpace();
            default:
                return null;
        }
    }
}