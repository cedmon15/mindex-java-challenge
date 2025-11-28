package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        int totalReports = countReportsIterative(employee);

        return new ReportingStructure(employee, totalReports);
    }

    // Counts reports using a BFS
     
    private int countReportsIterative(Employee distinctEmployee) {
        int count = 0;
        if (distinctEmployee.getDirectReports() == null) {
            return count;
        }

        // Initialize queue with the direct reports of the root employee
        Queue<Employee> queue = new LinkedList<>(distinctEmployee.getDirectReports());

        while (!queue.isEmpty()) {
            Employee report = queue.poll();
            count++; // Count this employee

            // The report object currently only has the ID.
            // We need to fetch the full details from the DB to see their direct reports.
            Employee fullReport = employeeRepository.findByEmployeeId(report.getEmployeeId());

            if (fullReport != null && fullReport.getDirectReports() != null) {
                // Add their reports to the queue to be processed
                queue.addAll(fullReport.getDirectReports());
            }
        }

        return count;
    }
}
