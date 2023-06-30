package gr.uowm.deyakwebapp.controllers;

import gr.uowm.deyakwebapp.data.service.DataService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@WebMvcTest(ArudinoDataController.class)
public class ArudinoDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DataService dataService;

    @InjectMocks
    private ArudinoDataController registerController;

    @Test
    public void testSaveRegisters() throws Exception {
        // Prepare test data
        Map<String, Integer> registers = new HashMap<>();
        registers.put("19", 10);
        registers.put("20", 20);
        // Add more registers as needed

        // Define the expected values after decoding
        double expectedE1 = 15.0;
        double expectedT1 = 5.0;
        double expectedT2 = 10.0;
        double expectedV1 = 25.0;
        double expectedCustomerNo = 50.0;
        double expectedInfoCode = 20.0;
        double expectedOperatingHours = 15.0;

        // Convert the registers to MultiValueMap
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Integer> entry : registers.entrySet()) {
            requestParams.add(entry.getKey(), entry.getValue().toString());
        }

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/registers")
                        .params(requestParams))
                .andExpect(MockMvcResultMatchers.status().isOk());


        // Verify that the saveData method is called with the expected DataEntity
        verify(dataService, times(1)).saveData(
                argThat(dataEntity ->
                        dataEntity.getE1() == expectedE1 &&
                                dataEntity.getT1() == expectedT1 &&
                                dataEntity.getT2() == expectedT2 &&
                                dataEntity.getV1() == expectedV1 &&
                                dataEntity.getCustomerNo() == expectedCustomerNo &&
                                dataEntity.getInfoCode() == expectedInfoCode &&
                                dataEntity.getOperatingHours() == expectedOperatingHours
                )
        );
    }
}