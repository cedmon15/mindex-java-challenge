package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Collections;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

// Unit tests for EmployeeServiceImpl
// I am using Mockito to mock the EmployeeRepository dependency and test the service logic in isolation.

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    public void testGetReportingStructure() {
        // Setup a hierarchy: John -> Paul -> George
        // John has 1 direct report (Paul).
        // Paul has 1 direct report (George).
        // George has 0.
        // Total reports for John = 2.

        Employee john = new Employee(); john.setEmployeeId("1");
        Employee paul = new Employee(); paul.setEmployeeId("2");
        Employee george = new Employee(); george.setEmployeeId("3");

        john.setDirectReports(Collections.singletonList(paul));
        paul.setDirectReports(Collections.singletonList(george));
        george.setDirectReports(null);

        // Mock the DB calls
        when(employeeRepository.findByEmployeeId("1")).thenReturn(john);
        when(employeeRepository.findByEmployeeId("2")).thenReturn(paul);
        when(employeeRepository.findByEmployeeId("3")).thenReturn(george);

        // Execute logic
        ReportingStructure result = employeeService.getReportingStructure("1");

        // Verify
        assertEquals(2, result.getNumberOfReports());
    }

    @Test(expected = RuntimeException.class)
    public void testGetReportingStructure_InvalidEmployeeId() {
        // Case: The ID requested doesn't exist
        when(employeeRepository.findByEmployeeId("bad-id")).thenReturn(null);
        
        employeeService.getReportingStructure("bad-id");
    }

    @Test
    public void testGetReportingStructure_BrokenHierarchy() {
        // Case: John lists Paul as a report, but Paul is missing from the DB
        Employee john = new Employee(); john.setEmployeeId("1");
        Employee paulReference = new Employee(); paulReference.setEmployeeId("2");

        john.setDirectReports(Collections.singletonList(paulReference));

        when(employeeRepository.findByEmployeeId("1")).thenReturn(john);
        when(employeeRepository.findByEmployeeId("2")).thenReturn(null); // Paul is M.I.A.

        ReportingStructure result = employeeService.getReportingStructure("1");

        // Should count Paul but stop there safely
        assertEquals(1, result.getNumberOfReports()); 
    }
}