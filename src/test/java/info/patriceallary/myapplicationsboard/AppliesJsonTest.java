package info.patriceallary.myapplicationsboard;

import info.patriceallary.myapplicationsboard.domain.*;
import info.patriceallary.myapplicationsboard.repositories.EnterpriseRepository;
import info.patriceallary.myapplicationsboard.repositories.JobResultRepository;
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
    private JacksonTester<Job> json;

    @Autowired
    private JacksonTester<Job[]> jsonList;

    private Job[] applies;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @BeforeEach
    void setup() throws ParseException {

        Address testAddress = new Address("address1", "City");
        EnterpriseType testEnterpriseType = new EnterpriseType("testType");
        EnterpriseActivity testEnterpriseActivity = new EnterpriseActivity("testActivity");
        Enterprise testEnterprise = new Enterprise("testEnterprise", testAddress, testEnterpriseType, testEnterpriseActivity);
        JobResult testJobResult = new JobResult("testResult");

        applies = Arrays.array(
                new Job( "My-First-Apply", LocalDateTime.parse("2023-10-12T00:00:00.000+00:00", formatter),testJobResult,testEnterprise),
                new Job("My-Second-Apply", LocalDateTime.parse("2023-10-13T23:59:00.000+00:00", formatter),testJobResult,testEnterprise),
                new Job( "My-Third-Apply", LocalDateTime.parse("2023-10-14T12:01:00.000+00:00", formatter),testJobResult,testEnterprise)
        );
    }

    @Test
    public void applySerializationTest() throws ParseException, IOException {

        Job job = applies[0];

        System.out.println(json.write(job));
        // Check if job object is esqual to json object store in "expected.json" file
        assertThat(json.write(job)).isStrictlyEqualToJson("expected.json");
        // Check json has id parameter and is esqual to 1
        assertThat(json.write(job)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(job)).extractingJsonPathNumberValue("@.id").isEqualTo(0);
        // Check if json has "name paramter and is equal to "My-First-Apply" ignoring case
        assertThat(json.write(job)).hasJsonPathStringValue("@.name");
        assertThat(json.write(job)).extractingJsonPathStringValue("@.name").isEqualToIgnoringCase("My-First-Apply");
        // Check Json has dateCreated paramter with "2023-10-13T23:59:00" Value
        assertThat(json.write(job)).hasJsonPathStringValue("@.dateCreated");
        assertThat(json.write(job)).extractingJsonPathStringValue("@.dateCreated").isEqualToIgnoringWhitespace(job.getDateCreated().format(formatter));

    }

    @Test
    public void applyDeserializationTest() throws IOException {
        String expected = """
                {
                  "id": 0,
                  "name": "My-First-Apply",
                  "dateCreated" : "2023-10-12T00:00:00",
                  "dateInterview" : null,
                  "dateRaised" : null,
                  "dateSent" : null,
                  "enterprise" :
                  {
                    "id": 0,
                    "name" : "testEnterprise",
                    "phone": null,
                    "notes" :  null,
                    "address" :
                    {
                      "id" : 0,
                      "address1" : "address1",
                      "address2" : null,
                      "zipCode" : null,
                      "city" : "City"
                    },
                    "type" :
                    {
                      "id" : 0,
                      "type" : "testType"
                    },
                    "activity" :
                    {
                      "id" : 0,
                      "activity" : "testActivity"
                    },
                  "contacts" : null
                  },
                  "jobResult" :
                  {
                    "id" : 0,
                    "result" : "testResult"
                  },
                  "url" : null
                }
                """;
        Job testJob = applies[0];

        assertThat(json.parseObject(expected)).isEqualTo(testJob);
        assertThat(json.parseObject(expected).getId()).isEqualTo(0);
        assertThat(json.parseObject(expected).getName()).isEqualTo("My-First-Apply");
        assertThat(json.parseObject(expected).getDateCreated()).isEqualTo(testJob.getDateCreated().format(formatter));
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
    "id": 0,
    "name": "My-First-Apply",
    "dateCreated": "2023-10-12T00:00:00",
    "dateInterview": null,
    "dateRaised": null,
    "dateSent": null,
    "enterprise": {
      "id": 0,
      "name": "testEnterprise",
      "phone": null,
      "notes": null,
      "address": {
        "id": 0,
        "address1": "address1",
        "address2": null,
        "zipCode": null,
        "city": "City"
      },
      "type": {
        "id": 0,
        "type": "testType"
      },
      "activity": {
        "id": 0,
        "activity": "testActivity"
      },
      "contacts": null
    },
    "jobResult": {
      "id": 0,
      "result": "testResult"
    },
    "url": null
  },
  {
    "id": 0,
    "name": "My-Second-Apply",
    "dateCreated": "2023-10-13T23:59:00",
    "dateInterview": null,
    "dateRaised": null,
    "dateSent": null,
    "enterprise": {
      "id": 0,
      "name": "testEnterprise",
      "phone": null,
      "notes": null,
      "address": {
        "id": 0,
        "address1": "address1",
        "address2": null,
        "zipCode": null,
        "city": "City"
      },
      "type": {
        "id": 0,
        "type": "testType"
      },
      "activity": {
        "id": 0,
        "activity": "testActivity"
      },
      "contacts": null
    },
    "jobResult": {
      "id": 0,
      "result": "testResult"
    },
    "url": null
  },
  {
    "id": 0,
    "name": "My-Third-Apply",
    "dateCreated": "2023-10-14T12:01:00",
    "dateInterview": null,
    "dateRaised": null,
    "dateSent": null,
    "enterprise": {
      "id": 0,
      "name": "testEnterprise",
      "phone": null,
      "notes": null,
      "address": {
        "id": 0,
        "address1": "address1",
        "address2": null,
        "zipCode": null,
        "city": "City"
      },
      "type": {
        "id": 0,
        "type": "testType"
      },
      "activity": {
        "id": 0,
        "activity": "testActivity"
      },
      "contacts": null
    },
    "jobResult": {
      "id": 0,
      "result": "testResult"
    },
    "url": null
  }
]
""";
        assertThat(jsonList.parse(expectedList)).isEqualTo(applies);
    }



}
