package gr.uowm.deyakwebapp.controllers;

import gr.uowm.deyakwebapp.data.entity.Data;
import gr.uowm.deyakwebapp.data.service.DataService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static gr.uowm.deyakwebapp.helpers.Decoder.decode;
import static gr.uowm.deyakwebapp.helpers.Decoder.decodeInt;

@RestController
public class ArudinoDataController {

    private final DataService dataService;

    public ArudinoDataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/registers")
    @PermitAll
    public String saveRegisters(@RequestParam Map<String, Integer> registers
    ) {
        for (Map.Entry<String, Integer> entry : registers.entrySet()) {
            String registerName = entry.getKey();
            int registerValue = entry.getValue();
        }

        double e1 = decode(registers.get("19"), registers.get("20"));
        double t1 = decode(registers.get("5"), registers.get("6"));
        double t2 = decode(registers.get("7"), registers.get("8"));
        double v1 = decode(registers.get("51"), registers.get("52"));
        double customerNo = decodeInt(registers.get("143"), registers.get("144"));
        double infoCode = decode(registers.get("123"), registers.get("124"));
        double operatingHours = decode(registers.get("125"), registers.get("126"));

        Data dataEntity = new Data((int) customerNo, (float) e1, (float) v1, (float) t1, (float) t2, (int) infoCode, (int) operatingHours);
        dataService.saveData(dataEntity);

        return "Registers saved successfully.";
    }


}
