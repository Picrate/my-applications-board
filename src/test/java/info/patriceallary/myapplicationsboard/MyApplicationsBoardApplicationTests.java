package info.patriceallary.myapplicationsboard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import info.patriceallary.myapplicationsboard.domain.Address;
import info.patriceallary.myapplicationsboard.domain.ContactRole;
import info.patriceallary.myapplicationsboard.domain.Enterprise;
import info.patriceallary.myapplicationsboard.domain.Job;
import info.patriceallary.myapplicationsboard.repositories.AddressRepository;
import info.patriceallary.myapplicationsboard.repositories.EnterpriseRepository;
import info.patriceallary.myapplicationsboard.repositories.JobResultRepository;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import javax.swing.text.AbstractDocument;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyApplicationsBoardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    EnterpriseRepository enterpriseRepository;
    @Autowired
    JobResultRepository jobResultRepository;

    // DateTimeFormatter to ISO8601 Datetime
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void contextLoads() {
    }

    /**
     * Address REST Tests
     */
    // Check that Get Address exists | HTTP Response : 200 OK | BODY Address JSON Object
    @Test
    void shouldReturnAnAddressIfExists() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/address/1", String.class);
        // Check if HTTP response is 200 - OK
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Check Content of response body
        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String address1 = context.read("$.address1");
        assertThat(address1).isEqualTo("0452 Summer Ridge Street");
        String address2 = context.read("$.address2");
        assertThat(address2).isEqualTo("Apt 268");
        String zipCode = context.read("$.zipCode");
        assertThat(zipCode).isEqualTo("92119 CEDEX");
        String city = context.read("$.city");
        assertThat(city).isEqualTo("Clichy");
    }

    // Check that Get Address does not exist | HTTP Response : 404 NOT_FOUND | BODY empty
    @Test
    void shouldNotReturnAnAddressThatDoesNotExist(){
        ResponseEntity<String> response = restTemplate
                .getForEntity("/address/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    // Address Update in PUT Requests | HTTP Response : 204 NO_CONTENT | BODY empty
    @Test
    @DirtiesContext
    void shouldUpdateAnExistingAddress(){
        Address modifiedAddress = new Address("Modified Street", "123", "", "New City");
        HttpEntity<Address> httpEntity = new HttpEntity<>(modifiedAddress);
        ResponseEntity<Void> response = restTemplate
                .exchange("/address/1", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        // Check if address was updated
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/address/1", String.class);
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String address1 = context.read("$.address1");
        assertThat(address1).isEqualTo("Modified Street");
        String address2 = context.read("$.address2");
        assertThat(address2).isEqualTo("123");
        String zipCode = context.read("$.zipCode");
        assertThat(zipCode).isBlank();
        String city = context.read("$.city");
        assertThat(city).isEqualTo("New City");
    }

    // Check not Updating a non existing address in PUT Requests | HTTP Response : 404 NOT_FOUND
    @Test
    @DirtiesContext
    void shouldNotUpdateANonExistingAddress(){
        Address modifiedAddress = new Address("Modified Street", "123", "", "New City");
        HttpEntity<Address> httpEntity = new HttpEntity<>(modifiedAddress);
        ResponseEntity<Void> response = restTemplate
                .exchange("/address/99", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    // Create a new address Response | HTTP Response : 201 CREATED | HEADER : URL of new Address
    @Test
    @DirtiesContext
    public void shouldCreateANewAddress(){
        Address newAddress = new Address("New Address","New City");
        HttpEntity<Address> httpEntity = new HttpEntity<Address>(newAddress);
        ResponseEntity<Void> response = restTemplate
                .postForEntity("/address",httpEntity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI newAddressUri = response.getHeaders().getLocation();
        assertThat(newAddressUri.getPath()).isNotNull();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(newAddressUri.getPath(), String.class);
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        String address1 = context.read("$.address1");
        assertThat(address1).isEqualTo("New Address");
        String city = context.read("$.city");
        assertThat(city).isEqualTo("New City");
    }

    // Delete an existingAddress | HTTP Response : 204 NO_CONTENT | BODY empty
    @Test
    @DirtiesContext
    public void shouldDeleteAnExistingAddress(){

        Address newAddress = new Address("New Address","New City");
        HttpEntity<Address> httpEntity = new HttpEntity<Address>(newAddress);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/address",httpEntity, Void.class);
        URI newAddressUri = postResponse.getHeaders().getLocation();

        ResponseEntity<Void> response = restTemplate
                .exchange(newAddressUri.getPath(), HttpMethod.DELETE,null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(newAddressUri.getPath(), String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isBlank();
    }

    // Delete a non existing Address | HTTP Response : 404 NOT_FOUND
    @Test
    @DirtiesContext
    public void shouldNotDeleteAnNonExistingAddress(){
        ResponseEntity<Void> response = restTemplate
                .exchange("/address/99", HttpMethod.DELETE,null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * ContactRole REST API Tests
     */

    // Get an existing ContactRole | HTTP Response : 200 OK | BODY ContactRole JSON Object
    @Test
    public void shouldGetAnExistingContactRole(){
        ResponseEntity<String> response = restTemplate
                .getForEntity("/contacts/roles/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String contactRole = context.read("$.role");
        assertThat(contactRole).isEqualTo("Chargé de recrutement IT");
    }

    // Do not get a non existing ContactRole | HTTP Response : 404 NOT_FOUND | BODY : Empty
    @Test
    public void shouldNotGetANonExistingContactRole(){
        ResponseEntity<String> response = restTemplate
                .getForEntity("/contacts/roles/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    // ContactRole Update in PUT Requests | HTTP Response : 204 NO_CONTENT | BODY empty
    @Test
    @DirtiesContext
    public void shouldUpdateAnExistingContactRole(){
        ContactRole newRole = new ContactRole("New Role");
        HttpEntity<ContactRole> httpEntity = new HttpEntity<>(newRole);
        ResponseEntity<Void> updateResponse = restTemplate
                .exchange("/contacts/roles/1", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(updateResponse.getBody()).isNull();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/roles/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String contactRole = context.read("$.role");
        assertThat(contactRole).isEqualTo("New Role");
    }

    // Non Existing ContactRole Update in PUT Requests | HTTP Response : 404 NOT_FOUND | BODY empty
    @Test
    @DirtiesContext
    public void shouldNotUpdateANonExistingContactRole(){
        ContactRole newRole = new ContactRole("New Role");
        HttpEntity<ContactRole> httpEntity = new HttpEntity<>(newRole);
        ResponseEntity<Void> updateResponse = restTemplate
                .exchange("/contacts/roles/99", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateResponse.getBody()).isNull();
    }

    // New Role Saved in POST Request | HTTP Rersponse : 201 CREATED | HEADER : URL of new ContactRole
    @Test
    @DirtiesContext
    public void shouldCreateANewContactRole() {
        ContactRole newRole = new ContactRole("New Role");
        HttpEntity<ContactRole> httpEntity = new HttpEntity<>(newRole);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/contacts/roles", httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getHeaders().getLocation().getPath()).contains("/contacts/roles/4");
    }



    /**
     * Jobs REST Tests
     */
    @Test
    void shouldReturnAJob() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/jobs/1", String.class);

        // Check URL and Get Method exists HTTP 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Check Response Body contains Job with id = 1, name="My-first-Job" and dateCreated = "2023-10-12T00:00:00"
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("My-First-Apply");
        String dateCreated = documentContext.read("$.dateCreated");
        assertThat(dateCreated).isEqualTo("2023-10-12T00:00:00");
    }

    @Test
    public void shouldNotReturnAnJobWithAnUnkwnownId() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/jobs/99", String.class);

        // Check That Controller return 404 and empty body
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldCreateANewJob() {

        Job newJob = new Job("4th Job", LocalDateTime.parse("2023-10-26T11:40:00"), jobResultRepository.findById(1L), enterpriseRepository.findById(1L));
        ResponseEntity<Void> createResponse = restTemplate
                .postForEntity("/jobs", newJob, Void.class);

        // Check POST request URL is OK and HTTP response = 201 CREATED
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // check URL contained in response header is OK And GET New Job is OK
        URI locationOfNewJob = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(locationOfNewJob, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        LocalDateTime dateTime = LocalDateTime.parse(documentContext.read("$.dateCreated"), formatter);
        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(4);
        assertThat(name).isEqualTo("4th Job");
        assertThat(dateTime).isEqualTo("2023-10-26T11:40:00");
    }

    @Test
    @DirtiesContext
    void shouldReturnAListOfJobs() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/jobs", String.class);

        // Check GET Method exists
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Check body contains list of jobs
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        // check all jobs are in body
        int jobsCount = documentContext.read("$.length()");
        assertThat(jobsCount).isEqualTo(3);
        // check each Job
        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).asList().containsExactlyInAnyOrder(1, 2, 3);
        JSONArray names = documentContext.read("$..name");
        assertThat(names)
                .asList().containsExactlyInAnyOrder("My-First-Job", "My-Second-Job", "My-Third-Job");
        JSONArray createdDates = documentContext.read("$..dateCreated");
        assertThat(createdDates).asList().containsExactlyInAnyOrder("2023-10-12T00:00:00", "2023-10-13T23:59:00", "2023-10-14T12:01:00");
    }

    @Test
    @DirtiesContext
    void shouldReturnAPageOfJobs() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/jobs?page=0&size=1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        JSONArray pages = documentContext.read("$[*]");
        assertThat(pages.size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void shouldReturnADefaultSortedPageOfJobs() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/jobs?page=0", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        JSONArray list = documentContext.read("$[*]");
        assertThat(list.size()).isEqualTo(3);
        JSONArray dates = documentContext.read("$..dateCreated");
        assertThat(dates).asList().containsExactly("2023-10-12T00:00:00", "2023-10-13T23:59:00", "2023-10-14T12:01:00");
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingJob() {
        // Create new Job with name modified
        Job updatedJob = new Job("My-Modified-Job", LocalDateTime.parse("2023-10-13T23:59:00", formatter), jobResultRepository.findById(2L), enterpriseRepository.findById(1L));
        // Create HTTP entity with updatedJob in Body
        HttpEntity<Job> request = new HttpEntity<>(updatedJob);
        // send PUT request
        ResponseEntity<Void> response = restTemplate
                .exchange("/jobs/2", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        // check if Job was modified
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/jobs/2", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("My-Modified-Job");
    }

    @Test
    @DirtiesContext
    void shouldNotUpdateAnJobThatDoesNotExists() {
        // Create new Job with name modified
        Job updatedJob = new Job(
                "My-Modified-Job",
                LocalDateTime.parse("2023-10-13T23:59:00", formatter),
                jobResultRepository.findById(2L),
                enterpriseRepository.findById(1L)
        );
        // Create HTTP entity with updatedJob in Body
        HttpEntity<Job> request = new HttpEntity<>(updatedJob);
        // send PUT request for Job #99 that does not exist
        ResponseEntity<Void> response = restTemplate
                .exchange("/jobs/99", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingJob() {
        ResponseEntity<Void> responseEntity = restTemplate
                .exchange("/jobs/3", HttpMethod.DELETE, null, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/jobs/3", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldNotDeleteAnJobThatDoesNotExist() {
        ResponseEntity<Void> responseEntity = restTemplate
                .exchange("/jobs/99", HttpMethod.DELETE, null, Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
