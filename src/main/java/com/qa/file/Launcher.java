package com.qa.file;

import com.qa.file.window.Window;

import java.awt.EventQueue;

class Launcher {

    public static void main(String[] args) {
        EventQueue.invokeLater(Window::startProgram);
    }
}
