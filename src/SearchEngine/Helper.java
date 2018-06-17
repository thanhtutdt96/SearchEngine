/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEngine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 *
 * @author Tu
 */
public class Helper {

    public static Helper helper = null;
    public List<Integer> dataIndex = null;
    String[] fileNames = {"a-d", "e-h", "i-l", "m-p", "q-t", "u-w", "x-z", "other"};
    List<BufferedWriter> bufferList;
    List<FileWriter> writerList;

    public String[] readDocFile(String filePath) {
        File file = null;
        WordExtractor extractor = null;
        String[] results = null;
        try {

            file = new File(filePath);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            HWPFDocument document = new HWPFDocument(fis);
            extractor = new WordExtractor(document);
            results = extractor.getParagraphText();
        } catch (Exception exep) {
            exep.printStackTrace();
        }

        return results;
    }

    public String[] readDocxFile(String filePath) {
        File file = null;
        String[] results = null;
        try {

            file = new File(filePath);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            results = new String[paragraphs.size()];
            for (int i = 0; i < paragraphs.size(); i++) {

                results[i] = paragraphs.get(i).getText();
            }
        } catch (Exception exep) {
            exep.printStackTrace();
        }
        return results;
    }

    public String[] readExcelFile(String filePath) {
        Workbook workbook = null;
        String[] results = null;
        try {
            workbook = WorkbookFactory.create(new File(filePath));

            System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            List<String> data = new ArrayList<>();
            dataIndex = new ArrayList<>();

            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                DataFormatter dataFormatter = new DataFormatter();
                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellValue = dataFormatter.formatCellValue(cell);
                        data.add(cellValue);
                        dataIndex.add(workbook.getSheetIndex(sheet));
                    }
                }
            }
            workbook.close();

            results = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                results[i] = data.get(i);
            }
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
    }

    private String[] readTxtFile(String filePath) {
        String[] results = null;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            List<String> data = new ArrayList<>();
            String line = "";
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
            results = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                results[i] = data.get(i);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
    }

    public static Helper getInstance() {
        if (helper == null) {
            helper = new Helper();
        }
        return helper;
    }

    public int getSheetByIndex(int index) {
        return dataIndex.get(index);
    }

    public String[] readFileByExtenstions(String filePath) {
        if (filePath.endsWith("doc")) {
            return readDocFile(filePath);
        }
        if (filePath.endsWith("docx")) {
            return readDocxFile(filePath);
        }
        if (filePath.endsWith("xls") || filePath.endsWith("xlsx")) {
            return readExcelFile(filePath);
        }
        if (filePath.endsWith("txt")) {
            return readTxtFile(filePath);
        }
        return null;
    }

    public boolean checkExcelExtention(String filePath) {
        return filePath.endsWith("xls") || filePath.endsWith("xlsx");
    }

    public void closeFile() {

        try {
            for (BufferedWriter buffer : bufferList) {
                buffer.close();
            }
            for (FileWriter writer : writerList) {
                writer.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void flushFile() {

        try {
            for (BufferedWriter buffer : bufferList) {
                buffer.flush();
            }
            for (FileWriter writer : writerList) {
                writer.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BufferedWriter checkDistributionRange(String term) {
        // áàảãạăắặằẳẵâấầẩẫậđéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪẬĐÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ
        try {

            Pattern pattern = Pattern.compile("^[aáàảãạăắặằẳẵâấầẩẫậbcdđAÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪBCD]");
            BufferedWriter bufferedWriter = null;
            Matcher m = pattern.matcher(term);
            if (m.find()) {
                return bufferedWriter = new BufferedWriter(new FileWriter("indexed/" + fileNames[0] + ".txt", true));

            }
            pattern = Pattern.compile("^[eéèẻẽẹêếềểễệfghEÉÈẺẼẸÊẾỀỂỄỆFGH]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedWriter = new BufferedWriter(new FileWriter("indexed/" + fileNames[1] + ".txt", true));
            }
            pattern = Pattern.compile("^[iíìỉĩịjklIÍÌỈĨỊJKL]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedWriter = new BufferedWriter(new FileWriter("indexed/" + fileNames[2] + ".txt", true));
            }
            pattern = Pattern.compile("^[mnoóòỏõọôốồổỗộơớờởỡợpMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedWriter = new BufferedWriter(new FileWriter("indexed/" + fileNames[3] + ".txt", true));
            }
            pattern = Pattern.compile("^[qrstQRST]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedWriter = new BufferedWriter(new FileWriter("indexed/" + fileNames[4] + ".txt", true));
            }
            pattern = Pattern.compile("^[uúùủũụưứừửữựvwUÚÙỦŨỤƯỨỪỬỮỰVW]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedWriter = new BufferedWriter(new FileWriter("indexed/" + fileNames[5] + ".txt", true));
            }
            pattern = Pattern.compile("^[xyýỳỷỹỵzXYÝỲỶỸỴZ]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedWriter = new BufferedWriter(new FileWriter("indexed/" + fileNames[6] + ".txt", true));
            }
            return bufferedWriter = new BufferedWriter(new FileWriter("indexed/" + fileNames[7] + ".txt", true));
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BufferedReader getDistributionRange(String term) {
        // áàảãạăắặằẳẵâấầẩẫậđéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪẬĐÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ
        try {

            Pattern pattern = Pattern.compile("^[aáàảãạăắặằẳẵâấầẩẫậbcdđAÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪBCD]");
            BufferedReader bufferedReader = null;
            Matcher m = pattern.matcher(term);
            if (m.find()) {
                return bufferedReader = new BufferedReader(new FileReader("indexed/" + fileNames[0] + ".txt"));

            }
            pattern = Pattern.compile("^[eéèẻẽẹêếềểễệfghEÉÈẺẼẸÊẾỀỂỄỆFGH]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedReader = new BufferedReader(new FileReader("indexed/" + fileNames[1] + ".txt"));
            }
            pattern = Pattern.compile("^[iíìỉĩịjklIÍÌỈĨỊJKL]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedReader = new BufferedReader(new FileReader("indexed/" + fileNames[2] + ".txt"));
            }
            pattern = Pattern.compile("^[mnoóòỏõọôốồổỗộơớờởỡợpMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedReader = new BufferedReader(new FileReader("indexed/" + fileNames[3] + ".txt"));
            }
            pattern = Pattern.compile("^[qrstQRST]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedReader = new BufferedReader(new FileReader("indexed/" + fileNames[4] + ".txt"));
            }
            pattern = Pattern.compile("^[uúùủũụưứừửữựvwUÚÙỦŨỤƯỨỪỬỮỰVW]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedReader = new BufferedReader(new FileReader("indexed/" + fileNames[5] + ".txt"));
            }
            pattern = Pattern.compile("^[xyýỳỷỹỵzXYÝỲỶỸỴZ]");
            m = pattern.matcher(term);
            if (m.find()) {
                return bufferedReader = new BufferedReader(new FileReader("indexed/" + fileNames[6] + ".txt"));
            }
            return bufferedReader = new BufferedReader(new FileReader("indexed/" + fileNames[7] + ".txt"));
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int checkPosDistributionRange(String term) {
        // áàảãạăắặằẳẵâấầẩẫậđéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪẬĐÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ
        Pattern pattern = Pattern.compile("^[aáàảãạăắặằẳẵâấầẩẫậbcdđAÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪBCD]");
        BufferedWriter bufferedWriter = null;
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return 0;
        }
        pattern = Pattern.compile("^[eéèẻẽẹêếềểễệfghEÉÈẺẼẸÊẾỀỂỄỆFGH]");
        m = pattern.matcher(term);
        if (m.find()) {
            return 1;
        }
        pattern = Pattern.compile("^[iíìỉĩịjklIÍÌỈĨỊJKL]");
        m = pattern.matcher(term);
        if (m.find()) {
            return 2;
        }
        pattern = Pattern.compile("^[mnoóòỏõọôốồổỗộơớờởỡợpMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]");
        m = pattern.matcher(term);
        if (m.find()) {
            return 3;
        }
        pattern = Pattern.compile("^[qrstQRST]");
        m = pattern.matcher(term);
        if (m.find()) {
            return 4;
        }
        pattern = Pattern.compile("^[uúùủũụưứừửữựvwUÚÙỦŨỤƯỨỪỬỮỰVW]");
        m = pattern.matcher(term);
        if (m.find()) {
            return 5;
        }
        pattern = Pattern.compile("^[xyýỳỷỹỵzXYÝỲỶỸỴZ]");
        m = pattern.matcher(term);
        if (m.find()) {
            return 6;
        }
        return 7;
    }

    public boolean isA_DFirst(String term) {
        Pattern pattern = Pattern.compile("^[aáàảãạăắặằẳẵâấầẩẫậbcdđAÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪBCD]");
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return true;
        }
        return false;
    }
    
    public boolean isE_HFirst(String term) {
        Pattern pattern = Pattern.compile("^[eéèẻẽẹêếềểễệfghEÉÈẺẼẸÊẾỀỂỄỆFGH]");
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return true;
        }
        return false;
    }
    
    public boolean isI_LFirst(String term) {
        Pattern pattern = Pattern.compile("^[iíìỉĩịjklIÍÌỈĨỊJKL]");
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return true;
        }
        return false;
    }
    
    public boolean isM_PFirst(String term) {
        Pattern pattern = Pattern.compile("^[mnoóòỏõọôốồổỗộơớờởỡợpMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]");
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return true;
        }
        return false;
    }
    
    public boolean isQ_TFirst(String term) {
        Pattern pattern = Pattern.compile("^[qrstQRST]");
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return true;
        }
        return false;
    }
    
    public boolean isX_ZFirst(String term) {
        Pattern pattern = Pattern.compile("^[xyýỳỷỹỵzXYÝỲỶỸỴZ]");
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return true;
        }
        return false;
    }
    
    public boolean isU_WFirst(String term) {
        Pattern pattern = Pattern.compile("^[uúùủũụưứừửữựvwUÚÙỦŨỤƯỨỪỬỮỰVW]");
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return true;
        }
        return false;
    }

}
