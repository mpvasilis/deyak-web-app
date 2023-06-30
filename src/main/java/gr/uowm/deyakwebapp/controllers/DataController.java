package gr.uowm.deyakwebapp.controllers;

import gr.uowm.deyakwebapp.data.entity.Data;
import gr.uowm.deyakwebapp.data.service.DataService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/export-csv")
    public ResponseEntity<InputStreamResource> exportFilteredDataToCSV(
            @RequestParam(required = false) String customerNo,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        List<Data> allData = dataService.getAll();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Date startDateObj = null;
        Date endDateObj = null;

        if (startDate != null) {
            try {
                startDateObj = formatter.parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (endDate != null) {
            try {
                endDateObj = formatter.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Date finalStartDateObj = startDateObj;
        Date finalEndDateObj = endDateObj;
        allData.stream()
                .filter(data -> customerNo == null || data.getCustomerNo() == Integer.parseInt(customerNo))
                .filter(data -> finalStartDateObj == null || data.getDate().after(finalStartDateObj))
                .filter(data -> finalEndDateObj == null || data.getDate().before(finalEndDateObj))
                .collect(Collectors.toList());

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("customerNo,Date,Heat Energy (E1),Volume (V1),Temperature (T1),Temperature (T2)\n");
        allData.forEach(data -> {
            csvContent.append(String.format("%s,%s,%s MWh,%s m3,%s °C,%s °C\n",
                    data.getCustomerNo(), data.getDate(), data.getE1(), data.getV1(), data.getT1(), data.getT2()));
        });

        ByteArrayInputStream stream = new ByteArrayInputStream(csvContent.toString().getBytes(StandardCharsets.UTF_8));
        InputStreamResource resource = new InputStreamResource(stream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=export_data.csv")
                .body(resource);
    }
}