package com.barkley;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmployeeTest {
    @Test
    public void testGetAnnualSalary() {
        Employee employeeUnderTest = new Employee(1, "Mitchell", "Barkley", 100);

        Assertions.assertEquals((100 * 12), employeeUnderTest.getAnnualSalary());
    }

    @Test
    public void testEmployeesAreEqual() {
        Employee employeeUnderTest1 = new Employee(1, "Mitchell", "Barkley", 100);
        Employee employeeUnderTest2 = new Employee(2, "Mitchell", "Barkley", 100);

        Assertions.assertEquals(employeeUnderTest1, employeeUnderTest2);
    }

    @Test
    public void testRaiseSalaryBy20Percent() {
        Employee employeeUnderTest1 = new Employee(1, "Mitchell", "Barkley", 4500);
        Employee employeeUnderTest2 = new Employee(2, "Mitchell", "Barkley", 5000);

        employeeUnderTest1.raiseSalary(20);
        employeeUnderTest2.raiseSalary(20);

        Assertions.assertNotEquals((100 * 1.20), employeeUnderTest1.getMonthlySalary(), 0.0);
        Assertions.assertNotEquals((100 * 1.20), employeeUnderTest2.getMonthlySalary(), 0.0);
    }
}
