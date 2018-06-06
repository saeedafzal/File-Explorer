package com.qa.file;

import com.qa.file.window.DisplayModel;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

public class DisplayModelTest {

    private DisplayModel model;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setup() {
        model = new DisplayModel();
    }

    @Test
    public void addRowTest() throws IOException {
        assertThat(model.getFileList().size(), CoreMatchers.is(0));

        model.addRow(testFolder.newFile());
        model.addRow(testFolder.newFile());
        assertThat(model.getFileList().size(), CoreMatchers.is(2));
    }

    @Test
    public void clearRowTest() throws IOException {
        model.addRow(testFolder.newFile());
        model.addRow(testFolder.newFile());
        assertThat(model.getFileList().size(), CoreMatchers.is(2));

        model.clearRow();
        assertThat(model.getFileList().size(), CoreMatchers.is(0));
    }
}
