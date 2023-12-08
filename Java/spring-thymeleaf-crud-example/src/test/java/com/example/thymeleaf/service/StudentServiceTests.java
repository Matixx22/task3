package com.example.thymeleaf.service;

import com.example.thymeleaf.entity.Address;
import com.example.thymeleaf.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.repeat;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class StudentServiceTests {

    @Autowired
    private StudentService studentService;

    @Test
    void saveValidNameTest() {
        Address address = new Address();

        List<String> validInputs = Arrays.asList(
                "Name",
                "Zbyszek",
                "Artur Dariusz"
        );
        validInputs.forEach(value -> {
            Student s = new Student(value, null, null, address);
            assertThat(studentService.save(new Student(value, null, null, address)).getName()).isEqualTo(s.getName());
        });
    }

    @Test
    void saveInvalidNameTest() {
        Address address = new Address();
        List<String> invalidInputs = Arrays.asList(
                null,
                "",
                " ",
                "\n",
                "\t",
                "!@#$%^[]{}:\";'<>,.?/~`"
        );

        invalidInputs.forEach(value -> {
            Student s = new Student(value, null, null, address);
            assertThat(studentService.save(new Student(value, null, null, address)).getName()).isEqualTo(s.getName());
        });
    }

    @Test
    void saveSqliNameTest() {
        Address address = new Address();
        List<String> sqliInputs = Arrays.asList(
                "' OR '1",
                "' OR 1 -- -",
                "\" OR \"\" = \"",
                "\" OR 1 = 1 -- -",
                "?id=1 and substring(version(),1,1)=5",
                "1 and (select sleep(10) from dual where database() like '%')#",
                "UniOn Select 1,2,3,4,...,gRoUp_cOncaT(0x7c,schema_name,0x7c)+fRoM+information_schema.schemata",
                "'='",
                "'LIKE'",
                "'=0--+"
        );

        sqliInputs.forEach(value -> {
            Student s = new Student(value, null, null, address);
            assertThat(studentService.save(new Student(value, null, null, address)).getName()).isEqualTo(s.getName());
        });
    }

    @Test
    void saveExtremeNameTest() {
        Address address = new Address();
        List<String> extremeInputs = Arrays.asList(
                repeat("X", 100),
                repeat("X", 1000),
                repeat("X", 10000),
                repeat("X", 100000),
                repeat("X", 1000000)
        );

        extremeInputs.forEach(value -> {
            Student s = new Student(value, null, null, address);
            assertThat(studentService.save(new Student(value, null, null, address)).getName()).isEqualTo(s.getName());
        });
    }
}