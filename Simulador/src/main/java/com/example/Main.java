package com.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("TXT process file", "txt"));
        jfc.setAcceptAllFileFilterUsed(true);
        int seleccion =jfc.showOpenDialog(null);
        if( seleccion == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            new SO(file);
        }else {
            System.err.println("Debe seleccionar un archivo");
        }
    }
}
