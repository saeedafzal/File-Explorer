package com.qa.file.window;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

class FileTransferable implements Transferable {

    private List<File> listOfFiles;

    FileTransferable(List<File> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.javaFileListFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return DataFlavor.javaFileListFlavor.equals(dataFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor dataFlavor) {
        return listOfFiles;
    }
}
