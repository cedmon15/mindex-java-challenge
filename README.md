## 1. Reporting Structure (Task 1)
I added the ability to calculate the total number of reports for an employee.

* **`data/ReportingStructure.java`**: New class to hold the output (employee + report count).
* **`controller/ReportingStructureController.java`**: New endpoint `GET /reportingStructure/{id}`.
* **`service/impl/EmployeeServiceImpl.java`**: Updated to include the calculation logic.

## 2. Compensation (Task 2)
I added a full create/read feature for employee compensation.

* **`data/Compensation.java`**: New class to store salary and effective date.
* **`dao/CompensationRepository.java`**: Interface to save/load compensation from the database.
* **`service/CompensationService.java`** & **`Impl`**: Handles the business logic.
* **`controller/CompensationController.java`**: New endpoints:
    * `POST /compensation`
    * `GET /compensation/{id}`

## 3. Testing
I organized the tests to separate Integration from Unit tests.

* **`ChallengeIntegrationTest.java`**: A new file that tests both tasks end-to-end with the database.
* **`service/impl/EmployeeServiceImplTest.java`**: Converted this to a Unit Test to check logic in isolation.
* **`service/impl/CompensationServiceImplTest.java`**: New Unit Test for the compensation logic.
* **`controller/CompensationControllerTest.java.`** **` EmployeeControllerTest.java`**: New Unit Tests for the controller.
