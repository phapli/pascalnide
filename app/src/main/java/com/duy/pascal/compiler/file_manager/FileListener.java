package com.duy.pascal.compiler.file_manager;

import java.io.File;

public interface FileListener {
    void onFileClick(File file);

    void onFileLongClick(File file);

    boolean doRemoveFile(File file);
}