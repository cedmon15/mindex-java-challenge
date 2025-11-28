package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Unit tests for CompensationController
// I am using MockMvc to simulate HTTP requests and test the controller layer in isolation.

@RunWith(SpringRunner.class)
@WebMvcTest(CompensationController.class)
public class CompensationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompensationService compensationService;

    @Test
    public void testCreate() throws Exception {
        Compensation comp = new Compensation();
        comp.setSalary("99000");

        when(compensationService.create(any(Compensation.class))).thenReturn(comp);

        mockMvc.perform(post("/compensation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"salary\": \"99000\"}"))
                .andExpect(status().isOk());
    }
}