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
    public String saveRegisters( @RequestParam("reg5") int reg5,
                                    @RequestParam("reg6") int reg6,
                                    @RequestParam("reg7") int reg7,
                                    @RequestParam("reg8") int reg8,
                                    @RequestParam("reg19") int reg19,
                                    @RequestParam("reg20") int reg20,
                                    @RequestParam("reg51") int reg51,
                                    @RequestParam("reg52") int reg52,
                                    @RequestParam("reg123") int reg123,
                                    @RequestParam("reg124") int reg124,
                                    @RequestParam("reg125") int reg125,
                                    @RequestParam("reg126") int reg126,
                                    @RequestParam("reg143") int reg143,
                                    @RequestParam("reg144") int reg144

    ) {
        double e1 = decode(reg19, reg20);
        double t1 = decode(reg5, reg6);
        double t2 = decode(reg7, reg8);
        double v1 = decode(reg51, reg52);
        double customerNo = decodeInt(reg143, reg144);
        double infoCode = decodeInt(reg123, reg124);
        double operatingHours = decodeInt(reg125, reg126);

        Data dataEntity = new Data((int) customerNo, (float) e1, (float) v1, (float) t1, (float) t2, (int) infoCode, (int) operatingHours);
        dataService.saveData(dataEntity);

        return "Registers saved successfully.";
    }


}
