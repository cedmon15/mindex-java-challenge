package com.mindex.challenge;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


// I want to run the full integration tests here, and have isolated unit tests for services/controllers 
// elsewhere, so these are the end-to-end tests.

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChallengeIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String compensationUrl;
    private String compensationIdUrl;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

     //TEST 1: Moved from EmployeeServiceImplTest.java
     
    @Test
    public void testEmployeeDirect() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        // Read
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);

        // Update
        readEmployee.setPosition("Development Manager");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Employee updatedEmployee = restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();
        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

     //TEST 2: Reporting Structure
     
    @Test
    public void testReportingStructure() {
        // Using the data bootstrapped in the DB (John Lennon)
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        
        ReportingStructure result = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employeeId).getBody();

        assertNotNull(result);
        assertEquals(4, result.getNumberOfReports());
    }

     //TEST 3: Compensation
     
    @Test
    public void testCompensation() {
        Employee employee = new Employee();
        employee.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");

        Compensation comp = new Compensation();
        comp.setEmployee(employee);
        comp.setSalary("123000");
        comp.setEffectiveDate(LocalDate.now());

        // Create
        Compensation created = restTemplate.postForEntity(compensationUrl, comp, Compensation.class).getBody();
        assertNotNull(created);
        assertEquals("123000", created.getSalary());

        // Read
        Compensation read = restTemplate.getForEntity(compensationIdUrl, Compensation.class, employee.getEmployeeId()).getBody();
        assertNotNull(read);
        assertEquals("123000", read.getSalary());
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    @Test
    public void testReportingStructure_EdgeCases() {
        // 1. Invalid ID 
        ResponseEntity<ReportingStructure> response = restTemplate.getForEntity(
                reportingStructureUrl, 
                ReportingStructure.class, 
                "bad_id_123"
        );
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCompensation_EdgeCases() {
        // 1. Create with missing data (null employee) 
        Compensation emptyComp = new Compensation();
        ResponseEntity<Compensation> createResponse = restTemplate.postForEntity(
                compensationUrl, 
                emptyComp, 
                Compensation.class
        );
        
        assertNotNull(createResponse.getBody()); 

        // 2. Read non-existent compensation
        ResponseEntity<Compensation> readResponse = restTemplate.getForEntity(
                compensationIdUrl, 
                Compensation.class, 
                "ghost_employee"
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, readResponse.getStatusCode());
    }
}