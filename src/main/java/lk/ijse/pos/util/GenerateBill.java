package lk.ijse.pos.util;

import javafx.event.ActionEvent;
import lk.ijse.pos.db.DBConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GenerateBill {
    public static File getPDFFile(String orderId) throws JRException, SQLException, ClassNotFoundException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Order_Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String, Object> data = new HashMap<>();
        data.put("ORDERID", orderId);

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        jasperReport,
                        data,
                        DBConnection.getDbConnection().getConnection());

        // Export the report to a PDF file
        File pdfFile = new File("Order Receipt.pdf");
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());

        return pdfFile;
    }

    public static void viewBill(String orderId) throws JRException, SQLException, ClassNotFoundException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/report/Order_Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String, Object> data = new HashMap<>();
        data.put("ORDERID", orderId);

        JasperPrint jasperPrint =
                JasperFillManager.fillReport(
                        jasperReport,
                        data,
                        DBConnection.getDbConnection().getConnection());

        JasperViewer.viewReport(jasperPrint,false);
    }
}
