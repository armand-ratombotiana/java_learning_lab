package com.learning.backend04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Initializes sample data on application startup.
 * CommandLineRunner runs after the ApplicationContext is fully initialized.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DataInitializer(DepartmentRepository departmentRepository,
                           EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            log.info("Database already populated, skipping initialization");
            return;
        }

        log.info("Initializing sample data...");

        Department engineering = new Department("Engineering");
        engineering.addEmployee(new Employee("Alice", "alice@company.com", LocalDate.of(2022, 3, 1), 85000));
        engineering.addEmployee(new Employee("Bob", "bob@company.com", LocalDate.of(2023, 6, 15), 72000));
        departmentRepository.save(engineering);

        Department marketing = new Department("Marketing");
        marketing.addEmployee(new Employee("Carol", "carol@company.com", LocalDate.of(2021, 1, 10), 78000));
        departmentRepository.save(marketing);

        log.info("Sample data initialized: {} departments, {} employees",
            departmentRepository.count(), employeeRepository.count());
    }
}
