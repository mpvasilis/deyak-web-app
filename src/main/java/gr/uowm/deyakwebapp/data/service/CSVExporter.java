package gr.uowm.deyakwebapp.data.service;

import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import gr.uowm.deyakwebapp.data.entity.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringComponent
@UIScope
public class CSVExporter {

    public StreamResource exportFilteredDataToCSV(List<Data> filteredData) {
        // Generate CSV content
        String csvContent = generateCSVContent(filteredData);

        // Create a byte array stream from the CSV content
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // Create a StreamResource for the CSV content
        StreamResource streamResource = createStreamResource(inputStream);

        return streamResource;
    }

    private String generateCSVContent(List<Data> filteredData) {
        // Generate the CSV content based on the filtered data
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Customer No,Date,E1,V1,T1,T2\n"); // CSV header
        for (Data data : filteredData) {
            csvContent.append(data.getCustomerNo()).append(",")
                    .append(data.getDate()).append(",")
                    .append(data.getE1()).append(",")
                    .append(data.getV1()).append(",")
                    .append(data.getT1()).append(",")
                    .append(data.getT2()).append("\n");
        }
        return csvContent.toString();
    }

    private StreamResource createStreamResource(InputStream inputStream) {
        return new StreamResource("filtered_data.csv", () -> inputStream);
    }
}