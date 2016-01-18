package com.example.nhs3108.fels102.utils;

import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PdfUtils {
    public void writeWordList(String fname, ArrayList<Word> words, String fpath) throws IOException, DocumentException{
        File file = new File(String.format("%s/%s.pdf", fpath, fname));
        if (!file.exists()) {
            file.createNewFile();
        }

        Document document = new Document();

        PdfWriter.getInstance(document,
                new FileOutputStream(file.getAbsoluteFile()));
        document.open();
        for(Word word : words) {
            document.add(new Paragraph(word.getContent() + " --- " + word.getCorrectAnswer().getContent()));
        }
        document.close();
    }
}
