package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Unit tests for CompensationServiceImpl
// I am using Mockito to mock the CompensationRepository dependency and test the service logic in isolation.

@RunWith(MockitoJUnitRunner.class)
public class CompensationServiceImplTest {

    @Mock
    private CompensationRepository compensationRepository;

    @InjectMocks
    private CompensationServiceImpl compensationService;

    @Test
    public void testCreateRead() {
        Compensation comp = new Compensation();
        comp.setSalary("50000");

        when(compensationRepository.insert(any(Compensation.class))).thenReturn(comp);
        when(compensationRepository.findByEmployee_EmployeeId("123")).thenReturn(comp);

        // Test Create
        Compensation created = compensationService.create(comp);
        assertEquals("50000", created.getSalary());

        // Test Read
        Compensation read = compensationService.read("123");
        assertEquals("50000", read.getSalary());
    }

    @Test(expected = RuntimeException.class)
    public void testRead_NotFound() {
        // Case: Asking for compensation for an employee who has none
        when(compensationRepository.findByEmployee_EmployeeId("no-comp-id")).thenReturn(null);
        
        compensationService.read("no-comp-id");
    }
}