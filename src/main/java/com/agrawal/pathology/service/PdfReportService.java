package com.agrawal.pathology.service;

import com.agrawal.pathology.dto.ReportResponse;
import com.agrawal.pathology.dto.ReportResultResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfReportService {

    private final ReportService reportService;

    public PdfReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public byte[] generatePdf(Long reportId) {

        ReportResponse report = reportService.findById(reportId);

        try {

            // ==========================================
            // LOAD JASPER TEMPLATE
            // ==========================================

            InputStream template =
                    getClass().getResourceAsStream(
                            "/reports/pathology-report.jrxml"
                    );

            if (template == null) {
                throw new RuntimeException(
                        "Jasper report template not found"
                );
            }

            JasperReport jasperReport =
                    JasperCompileManager.compileReport(template);


            // ==========================================
            // REPORT PARAMETERS
            // ==========================================

            Map<String, Object> parameters = new HashMap<>();

            parameters.put(
                    "patientName",
                    report.patientName()
            );

            parameters.put(
                    "sex",
                    report.sex()
            );

            parameters.put(
                    "referenceNo",
                    report.referenceNo()
            );

            parameters.put(
                    "referredBy",
                    report.referredBy()
            );


            // ==========================================
            // CALCULATE AGE
            // ==========================================

            LocalDate dateOfBirth = report.dateOfBirth();

            String age = "";

            if (dateOfBirth != null) {

                int years =
                        Period.between(
                                dateOfBirth,
                                LocalDate.now()
                        ).getYears();

                age = years + " Years";
            }

            parameters.put("age", age);


            // ==========================================
            // REPORT DATE
            // ==========================================

            parameters.put(
                    "reportDate",
                    report.reportDate() != null
                            ? report.reportDate().toString()
                            : ""
            );


            // ==========================================
            // SORT RESULTS
            // ==========================================

            List<ReportResultResponse> sortedResults =
                    new ArrayList<>(report.results());

//            sortedResults.sort(
//                    Comparator
//                            .comparing(
//                                    ReportResultResponse::sectionName,
//                                    Comparator.nullsLast(String::compareTo)
//                            )
//                            .thenComparing(
//                                    ReportResultResponse::displayOrder,
//                                    Comparator.nullsLast(Integer::compareTo)
//                            )
//            );


            // ==========================================
            // CONVERT RESULTS TO JASPER MAPS
            // ==========================================

            List<Map<String, ?>> rows =
                    new ArrayList<>();

            for (ReportResultResponse result : sortedResults) {

                rows.add(toMap(result));
            }


            // ==========================================
            // DATA SOURCE
            // ==========================================

            JRMapCollectionDataSource dataSource =
                    new JRMapCollectionDataSource(rows);


            // ==========================================
            // GENERATE REPORT
            // ==========================================

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport,
                            parameters,
                            dataSource
                    );


            // ==========================================
            // EXPORT PDF
            // ==========================================

            return JasperExportManager.exportReportToPdf(
                    jasperPrint
            );

        } catch (JRException e) {

            throw new RuntimeException(
                    "Failed to generate PDF",
                    e
            );
        }
    }


    // ==========================================
    // CONVERT RESULT TO MAP
    // ==========================================

    private Map<String, Object> toMap(
            ReportResultResponse result) {

        Map<String, Object> map =
                new HashMap<>();

        map.put(
                "sectionName",
                result.sectionName()
        );

        map.put(
                "testName",
                result.testName()
        );

        map.put(
                "resultValue",
                result.resultValue()
        );

        map.put(
                "unit",
                result.unit()
        );

        map.put(
                "normalRange",
                result.normalRange()
        );

        map.put(
                "displayOrder",
                result.displayOrder()
        );

        return map;
    }
}