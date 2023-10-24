package info.patriceallary.myapplicationsboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.assertj.core.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@JsonTest
public class JobJsonTest {


    @Autowired
    private JacksonTester<Apply> json;
    private JacksonTester<Apply[]> jsonList;
    private Apply[] applies;

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    @BeforeEach
    void setup() throws ParseException {
        applies = Arrays.array(
                new Apply(1L, "My-First-Apply", formatter.parse("12/10/2023")),
                new Apply(2L, "My-Second-Apply", formatter.parse("13/10/2023")),
                new Apply(3L, "My-Third-Apply", formatter.parse("14/10/2023"))
        );
    }

    @Test
    public void JobSerializationTest() throws ParseException, IOException {

        Apply apply = new Apply(1L, "My-First-Apply", formatter.parse("12/10/2023"));

        // Check if apply object is esqual to json object store in "expected.json" file
        assertThat(json.write(apply)).isStrictlyEqualToJson("expected.json");



    }



}
