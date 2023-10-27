package info.patriceallary.myapplicationsboard;

import info.patriceallary.myapplicationsboard.domain.Apply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.assertj.core.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
@JsonTest
public class AppliesJsonTest {


    @Autowired
    private JacksonTester<Apply> json;

    @Autowired
    private JacksonTester<Apply[]> jsonList;
    private Apply[] applies;

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @BeforeEach
    void setup() throws ParseException {
        applies = Arrays.array(
                new Apply( "My-First-Apply", LocalDateTime.parse("2023-10-12T00:00:00.000+00:00", formatter)),
                new Apply("My-Second-Apply", LocalDateTime.parse("2023-10-13T23:59:00.000+00:00", formatter)),
                new Apply( "My-Third-Apply", LocalDateTime.parse("2023-10-14T12:01:00.000+00:00", formatter))
        );
    }

    @Test
    public void applySerializationTest() throws ParseException, IOException {

        Apply apply = new Apply("My-First-Apply", LocalDateTime.parse("2023-10-13T23:59:00", formatter));
        // Check if apply object is esqual to json object store in "expected.json" file
        assertThat(json.write(apply)).isStrictlyEqualToJson("expected.json");
        // Check json has id parameter and is esqual to 1
        assertThat(json.write(apply)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(apply)).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        // Check if json has "name paramter and is equal to "My-First-Apply" ignoring case
        assertThat(json.write(apply)).hasJsonPathStringValue("@.name");
        assertThat(json.write(apply)).extractingJsonPathStringValue("@.name").isEqualToIgnoringCase("My-First-Apply");
        // Check Json has dateCreated paramter with "2023-10-13T23:59:00" Value
        assertThat(json.write(apply)).hasJsonPathStringValue("@.dateCreated");
        assertThat(json.write(apply)).extractingJsonPathStringValue("@.dateCreated").isEqualToIgnoringWhitespace(apply.getDateCreated().format(formatter));
    }

    @Test
    public void applyDeserializationTest() throws IOException {
        String expected = """
                {
                  "id": 1,
                  "name": "My-First-Apply",
                  "dateCreated" : "2023-10-13T23:59:00"
                }
                """;
        Apply testApply = new Apply( "My-First-Apply", LocalDateTime.parse("2023-10-13T23:59:00", formatter));

        assertThat(json.parseObject(expected)).isEqualTo(testApply);
        assertThat(json.parseObject(expected).getId()).isEqualTo(1);
        assertThat(json.parseObject(expected).getName()).isEqualTo("My-First-Apply");
        assertThat(json.parseObject(expected).getDateCreated()).isEqualTo(testApply.getDateCreated().format(formatter));
    }

    @Test
    public void applyListSerializationTest() throws IOException {
        assertThat(jsonList.write(applies)).isStrictlyEqualToJson("expectedList.json");
    }

    @Test
    public void applyListDeSerializationTest() throws IOException {
        String expectedList = """
                [
                  {
                    "id": 1,
                    "name": "My-First-Apply",
                    "dateCreated": "2023-10-12T00:00:00"
                  },
                  {
                    "id": 2,
                    "name": "My-Second-Apply",
                    "dateCreated": "2023-10-13T23:59:00"
                  },
                  {
                    "id": 3,
                    "name": "My-Third-Apply",
                    "dateCreated": "2023-10-14T12:01:00"
                  }
                ]
                                
                """;
        assertThat(jsonList.parse(expectedList)).isEqualTo(applies);
    }



}
