package info.patriceallary.myapplicationsboard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import info.patriceallary.myapplicationsboard.domain.*;
import info.patriceallary.myapplicationsboard.repositories.*;
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

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyApplicationsBoardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    EnterpriseRepository enterpriseRepository;
    @Autowired
    EnterpriseActivityRepository enterpriseActivityRepository;
    @Autowired
    EnterpriseTypeRepository enterpriseTypeRepository;
    @Autowired
    JobResultRepository jobResultRepository;
    @Autowired
    ContactRoleRepository contactRoleRepository;
    @Autowired
    ContactTitleRepository contactTitleRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ContactRepository contactRepository;

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
    void shouldNotReturnAnAddressThatDoesNotExist() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/address/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    // Address Update in PUT Requests | HTTP Response : 204 NO_CONTENT | BODY empty
    @Test
    @DirtiesContext
    void shouldUpdateAnExistingAddress() {
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
    void shouldNotUpdateANonExistingAddress() {
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
    public void shouldCreateANewAddress() {
        Address newAddress = new Address("New Address", "New City");
        HttpEntity<Address> httpEntity = new HttpEntity<Address>(newAddress);
        ResponseEntity<Void> response = restTemplate
                .postForEntity("/address", httpEntity, Void.class);
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
    public void shouldDeleteAnExistingAddress() {

        Address newAddress = new Address("New Address", "New City");
        HttpEntity<Address> httpEntity = new HttpEntity<Address>(newAddress);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/address", httpEntity, Void.class);
        URI newAddressUri = postResponse.getHeaders().getLocation();

        ResponseEntity<Void> response = restTemplate
                .exchange(newAddressUri.getPath(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(newAddressUri.getPath(), String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isBlank();
    }

    // Delete a non existing Address | HTTP Response : 404 NOT_FOUND
    @Test
    @DirtiesContext
    public void shouldNotDeleteAnNonExistingAddress() {
        ResponseEntity<Void> response = restTemplate
                .exchange("/address/99", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * ContactRole REST API Tests
     */

    // Get an existing ContactRole | HTTP Response : 200 OK | BODY ContactRole JSON Object
    @Test
    public void shouldGetAnExistingContactRole() {
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
    public void shouldNotGetANonExistingContactRole() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/contacts/roles/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    // ContactRole Update in PUT Requests | HTTP Response : 204 NO_CONTENT | BODY empty
    @Test
    @DirtiesContext
    public void shouldUpdateAnExistingContactRole() {
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
    public void shouldNotUpdateANonExistingContactRole() {
        ContactRole newRole = new ContactRole("New Role");
        HttpEntity<ContactRole> httpEntity = new HttpEntity<>(newRole);
        ResponseEntity<Void> updateResponse = restTemplate
                .exchange("/contacts/roles/99", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateResponse.getBody()).isNull();
    }

    // New Role Saved in POST Request | HTTP Response : 201 CREATED | HEADER : URL of new ContactRole
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

    // Delete existing Role | HTTP Response : 204 NO_CONTENT
    @Test
    @DirtiesContext
    public void shouldDeleteAnExistingContactRole() {
        // Create a new Role
        ContactRole newRole = new ContactRole("New Role");
        HttpEntity<ContactRole> httpEntity = new HttpEntity<>(newRole);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/contacts/roles", httpEntity, Void.class);
        // Delete the new Role
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/contacts/roles/4", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        // Check newRole has been deleted
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/roles/4", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Not Delete non existing Role | HTTP Response : 404 NOT_FOUND
    @Test
    public void shouldNotDeleteANonExistingContactRole() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/contacts/roles/4", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Get All Roles | HTTP Rersponse : 200 OK | BODY : All ContactRoles
    @Test
    public void shouldReturnAListOfContactRoles() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/roles", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number count = documentContext.read("$.length()");
        assertThat(count).isEqualTo(3);
        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(1, 2, 3);
        JSONArray roles = documentContext.read("$..role");
        assertThat(roles).containsExactlyInAnyOrder(
                "Chargé de recrutement IT",
                "Responsable des Ressources Humaines",
                "Dirigeant"
        );
    }

    // Get a Page Of ContactRoles | HTTP Response : 200 OK | BODY : Only 2 roles sorts by role ASC
    @Test
    public void shouldReturnAPageOfContactRoles() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/roles?page=0&size=2", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number count = context.read("$.length()");
        assertThat(count).isEqualTo(2);
        JSONArray roles = context.read("$..role");
        assertThat(roles).containsExactly(
                "Chargé de recrutement IT",
                "Dirigeant"
        );
    }

    /**
     * ContactTitle RESTS Tests
     */

    @Test
    public void shouldReturnAExisitingContactTitle() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/contacts/titles/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String title = context.read("$.title");
        assertThat(title).isEqualTo("Mr");
    }

    @Test
    public void shouldNotReturnANonExisitingContactTitle() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/contacts/titles/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    public void shouldUpdateAnExistingContactTitle() {
        ContactTitle updatedTitle = new ContactTitle("Updated Title");
        HttpEntity<ContactTitle> httpEntity = new HttpEntity<>(updatedTitle);
        ResponseEntity<Void> updateResponse = restTemplate
                .exchange("/contacts/titles/1", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(updateResponse.getBody()).isNull();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/titles/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String title = context.read("$.title");
        assertThat(title).isEqualTo("Updated Title");
    }

    @Test
    @DirtiesContext
    public void shouldNotUpdateAnNonExistingContactTitle() {
        ContactTitle updatedTitle = new ContactTitle("Updated Title");
        HttpEntity<ContactTitle> httpEntity = new HttpEntity<>(updatedTitle);
        ResponseEntity<Void> updateResponse = restTemplate
                .exchange("/contacts/titles/99", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updateResponse.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    public void shouldSaveANewContactTitle() {
        ContactTitle newTitle = new ContactTitle("New Title");
        HttpEntity<ContactTitle> httpEntity = new HttpEntity<>(newTitle);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/contacts/titles", httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI locationOfNewTitle = postResponse.getHeaders().getLocation();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(locationOfNewTitle, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(3);
        String title = context.read("$.title");
        assertThat(title).isEqualTo("New Title");
    }

    @Test
    @DirtiesContext
    public void shouldNotSaveAnEmptyContactTitle() {
        ContactTitle newTitle = new ContactTitle("");
        HttpEntity<ContactTitle> httpEntity = new HttpEntity<>(newTitle);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/contacts/titles", httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext
    public void shouldDeleteAnExistingContactTitle() {

        ContactTitle newTitle = new ContactTitle("New Title");
        HttpEntity<ContactTitle> httpEntity = new HttpEntity<>(newTitle);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/contacts/titles", httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI locationOfNewTitle = postResponse.getHeaders().getLocation();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(locationOfNewTitle, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldNotDeleteANonExistingContactTitle() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/contacts/titles/99", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturnAListOfContactTitle() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/titles", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number count = context.read("$.length()");
        assertThat(count).isEqualTo(2);
        JSONArray titles = context.read("$..title");
        assertThat(titles).containsExactlyInAnyOrder("Mr", "Mme");
    }

    /**
     * Contact REST Tests
     */

    @Test
    public void shouldReturnAContactById() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);
    }

    @Test
    public void shouldNotReturnAContactThtDoesNotExist() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/99", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    public void shouldUpdateAnExistingContact() {
        Contact updatedContact = this.contactRepository.findById(1);
        updatedContact.setFirstname("firstname");
        updatedContact.setLastname("lastname");
        System.out.println(updatedContact);
        HttpEntity<Contact> httpEntity = new HttpEntity<>(updatedContact);
        ResponseEntity<Void> updateResponse = restTemplate
                .exchange("/contacts/1", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        // check modifications have been applied
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String firstname = context.read("$.firstname");
        String lastname = context.read("$.lastname");
        assertThat(firstname).isEqualTo("firstname");
        assertThat(lastname).isEqualTo("lastname");
    }

    @Test
    @DirtiesContext
    public void shouldNotUpdateAnNonExistingContact() {
        Contact updatedContact = this.contactRepository.findById(1);
        updatedContact.setFirstname("firstname");
        updatedContact.setLastname("lastname");
        System.out.println(updatedContact);
        HttpEntity<Contact> httpEntity = new HttpEntity<>(updatedContact);
        ResponseEntity<Void> updateResponse = restTemplate
                .exchange("/contacts/99", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    public void shouldCreateANewContact() {

        Contact newContact = new Contact(
                "firstname",
                "lastname",
                "0123456789",
                "contact@foo.bar",
                "https://www.linkedin.com/newcontact",
                this.addressRepository.findById(1),
                this.contactRoleRepository.findById(1),
                this.contactTitleRepository.findById(1), this.enterpriseRepository.findById(1)
        );
        HttpEntity<Contact> httpEntity = new HttpEntity<>(newContact);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/contacts", httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI locationOfNewContact = postResponse.getHeaders().getLocation();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(locationOfNewContact, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(4);
        String firstname = documentContext.read("$.firstname");
        assertThat(firstname).isEqualTo(newContact.getFirstname());
        String lastname = documentContext.read("$.lastname");
        assertThat(lastname).isEqualTo(newContact.getLastname());
        assertThat((String) documentContext.read("$.phone")).isEqualTo(newContact.getPhone());
        assertThat((String) documentContext.read("$.email")).isEqualTo(newContact.getEmail());
        assertThat((String) documentContext.read("$.linkedInURL")).isEqualTo(newContact.getLinkedInURL());
        List<String> getAddress = documentContext.read("$['address'][*]");
        assertThat(getAddress).contains(
                "0452 Summer Ridge Street",
                "Apt 268",
                "92119 CEDEX",
                "Clichy"
        );
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingContact() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/contacts/1", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/contacts/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteAnNonExistingContact() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/contacts/99", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * EnterpriseType REST Tests
     */
    @Test
    void shouldReturnAnExistingEnterpriseType() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/types/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String typeName = context.read("$.type");
        assertThat(typeName).isEqualTo("PME");
    }

    @Test
    void shouldNotReturnANonExistingEnterpriseType() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/types/99", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingEnterpriseType() {
        EnterpriseType updatedType = new EnterpriseType("Updated");
        HttpEntity<EnterpriseType> httpEntity = new HttpEntity<>(updatedType);
        ResponseEntity<Void> putResponse = restTemplate
                .exchange("/enterprises/types/1", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/types/1", String.class);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);
        String typeName = documentContext.read("$.type");
        assertThat(typeName).isEqualTo("Updated");
    }

    @Test
    void shouldNotUpdateANonExistingEnterpriseType() {
        EnterpriseType updatedType = new EnterpriseType("Updated");
        HttpEntity<EnterpriseType> httpEntity = new HttpEntity<>(updatedType);
        ResponseEntity<Void> putResponse = restTemplate
                .exchange("/enterprises/types/99", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnAListOfEnterpriseType() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/types", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number count = documentContext.read("$.length()");
        assertThat(count).isEqualTo(3);
        JSONArray typesIds = documentContext.read("$[*].id");
        assertThat(typesIds).containsExactlyInAnyOrder(1, 2, 3);
        JSONArray typesNames = documentContext.read("$[*].type");
        assertThat(typesNames).asList().containsExactlyInAnyOrder(
                "PME",
                "Grand Groupe",
                "ETI"
        );
    }

    @Test
    @DirtiesContext
    void shouldCreateANewEnterpriseType() {
        EnterpriseType newType = new EnterpriseType("New");
        HttpEntity<EnterpriseType> httpEntity = new HttpEntity<>(newType);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/enterprises/types", httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = postResponse.getHeaders().getLocation();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(4);
        String typeName = context.read("$.type");
        assertThat(typeName).isEqualTo("New");
    }
    @Test
    @DirtiesContext
    void shouldDeleteAnExistingEnterpriseType() {
        EnterpriseType newType = new EnterpriseType("New");
        HttpEntity<EnterpriseType> httpEntity = new HttpEntity<>(newType);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/enterprises/types", httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = postResponse.getHeaders().getLocation();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(location, HttpMethod.DELETE, null, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteANonExistingEnterpriseType(){
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/enterprises/types/99", HttpMethod.DELETE, null, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * EnterpriseActivity REST Tests
     */
    @Test
    void shouldReturnAnExistingEnterpriseActivity(){
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/activities/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String activity = context.read("$.activity");
        assertThat(activity).isEqualTo("Assurance");
    }

    @Test
    void shouldNotReturnANonExistingEnterpriseActivity(){
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/activities/99", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isBlank();
    }

    @Test
    void shouldReturnAListOfEnterpriseActivity(){
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/activities", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number count = context.read("$.length()");
        assertThat(count).isEqualTo(3);
        JSONArray ids = context.read("$[*].id");
        assertThat(ids).asList().containsExactlyInAnyOrder(1,2,3);
        JSONArray activities = context.read("$[*].activity");
        assertThat(activities).asList().containsExactlyInAnyOrder(
                "Assurance",
                "Banque",
                "ESN"
        );
    }

    @Test
    @DirtiesContext
    void shouldCreateANewEnterpriseActivity(){
        EnterpriseActivity newActivity = new EnterpriseActivity("New Activity");
        HttpEntity<EnterpriseActivity> httpEntity = new HttpEntity<>(newActivity);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/enterprises/activities",httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getHeaders().getLocation()).isNotNull();

        URI location = postResponse.getHeaders().getLocation();

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(4);
        String activity = context.read("$.activity");
        assertThat(activity).isEqualTo("New Activity");
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingEnterpriseActivity(){
        EnterpriseActivity updatedActivity = new EnterpriseActivity("Updated Activity");
        HttpEntity<EnterpriseActivity> httpEntity = new HttpEntity<>(updatedActivity);
        ResponseEntity<Void> putResponse = restTemplate
                .exchange("/enterprises/activities/1",HttpMethod.PUT,httpEntity,Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/activities/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext context = JsonPath.parse(getResponse.getBody());
        Number id = context.read("$.id");
        assertThat(id).isEqualTo(1);
        String activity = context.read("$.activity");
        assertThat(activity).isEqualTo("Updated Activity");
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingEnterpriseActivity(){
        // Create new Activity
        EnterpriseActivity newActivity = new EnterpriseActivity("New Activity");
        HttpEntity<EnterpriseActivity> httpEntity = new HttpEntity<>(newActivity);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/enterprises/activities",httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getHeaders().getLocation()).isNotNull();

        URI location = postResponse.getHeaders().getLocation();
        // Delete new Activity
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(location,HttpMethod.DELETE,null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        // Check New Activity has been really deleted
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    /**
     * Enterprises REST Tests
     */
    @Test
    void shouldReturnAnExistingEnterprise(){
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Yadel");
        String phone = documentContext.read("$.phone");
        assertThat(phone).isEqualTo("6672553534");
        String notes = documentContext.read("$.notes");
        assertThat(notes).isEqualTo("quis orci eget orci vehicula");
    }

    @Test
    void shouldNotReturnAnUnknownEnterprise(){
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/99", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponse.getBody()).isBlank();
    }

    @Test
    void shouldReturnAListOfEnterprise(){
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number count = documentContext.read("$.length()");
        assertThat(count).isEqualTo(3);
        JSONArray ids = documentContext.read("$[*].id");
        assertThat(ids).containsExactlyInAnyOrder(1,2,3);
        JSONArray names = documentContext.read("$[*].name");
        assertThat(names).asList().containsExactlyInAnyOrder(
                "Yadel",
                "Flashdog",
                "Feedfire"
        );
    }

    @Test
    void shouldReturnAPageOfEnterprises(){
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises?page=0&size=2", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number count = documentContext.read("$.length()");
        assertThat(count).isEqualTo(2);
        JSONArray ids = documentContext.read("$[*].id");
        assertThat(ids).containsExactlyInAnyOrder(2,3);
        JSONArray names = documentContext.read("$[*].name");
        assertThat(names).asList().containsExactlyInAnyOrder(
                "Flashdog",
                "Feedfire"
        );
    }

    @Test
    @DirtiesContext
    void shouldCreateANewEnterprise() {
        EnterpriseActivity activity = this.enterpriseActivityRepository.findById(1);
        EnterpriseType type = this.enterpriseTypeRepository.findById(1);
        Address address = new Address("New address", "City");
        Enterprise newEnterprise = new Enterprise("New Enterprise", "1234567890", "notes", address,type,activity);
        HttpEntity<Enterprise> httpEntity = new HttpEntity<>(newEnterprise);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/enterprises",httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getHeaders().getLocation()).isNotNull();

        URI location = postResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(location,String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(4);
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("New Enterprise");
        String phone = documentContext.read("$.phone");
        assertThat(phone).isEqualTo("1234567890");
        String notes = documentContext.read("$.notes");
        assertThat(notes).isEqualTo("notes");

        Number addressId = documentContext.read("$.address.id");
        assertThat(addressId).isEqualTo(4);
        Number typeId = documentContext.read("$.type.id");
        assertThat(typeId).isEqualTo(1);
        Number activityId = documentContext.read("$.activity.id");
        assertThat(activityId).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingEnterprise() {
        EnterpriseActivity activity = this.enterpriseActivityRepository.findById(2);
        EnterpriseType type = this.enterpriseTypeRepository.findById(2);
        Address address = new Address("New address", "City");
        Enterprise newEnterprise = new Enterprise("New Enterprise", "1234567890", "notes", address,type,activity);
        HttpEntity<Enterprise> httpEntity = new HttpEntity<>(newEnterprise);

        ResponseEntity<Void> putResponse = restTemplate
                .exchange("/enterprises/1",HttpMethod.PUT,httpEntity, Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/enterprises/1", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("New Enterprise");
        String phone = documentContext.read("$.phone");
        assertThat(phone).isEqualTo("1234567890");
        String notes = documentContext.read("$.notes");
        assertThat(notes).isEqualTo("notes");

        Number addressId = documentContext.read("$.address.id");
        assertThat(addressId).isEqualTo(4);
        Number typeId = documentContext.read("$.type.id");
        assertThat(typeId).isEqualTo(2);
        Number activityId = documentContext.read("$.activity.id");
        assertThat(activityId).isEqualTo(2);
    }
    @Test
    void shouldNotUpdateAnNonExistingEnterprise() {
        EnterpriseActivity activity = this.enterpriseActivityRepository.findById(2);
        EnterpriseType type = this.enterpriseTypeRepository.findById(2);
        Address address = new Address("New address", "City");
        Enterprise newEnterprise = new Enterprise("New Enterprise", "1234567890", "notes", address, type, activity);
        HttpEntity<Enterprise> httpEntity = new HttpEntity<>(newEnterprise);

        ResponseEntity<Void> putResponse = restTemplate
                .exchange("/enterprises/99", HttpMethod.PUT, httpEntity, Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingEnterprise(){
        EnterpriseActivity activity = this.enterpriseActivityRepository.findById(1);
        EnterpriseType type = this.enterpriseTypeRepository.findById(1);
        Address address = new Address("New address", "City");
        Enterprise newEnterprise = new Enterprise("New Enterprise", "1234567890", "notes", address,type,activity);
        HttpEntity<Enterprise> httpEntity = new HttpEntity<>(newEnterprise);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/enterprises",httpEntity, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getHeaders().getLocation()).isNotNull();

        URI location = postResponse.getHeaders().getLocation();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(location,HttpMethod.DELETE,null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
        JSONArray ids = documentContext.read("$[*].id");
        assertThat(ids).asList().containsExactlyInAnyOrder(1, 2, 3);
        JSONArray names = documentContext.read("$[*].name");
        assertThat(names)
                .asList().containsExactlyInAnyOrder("My-First-Apply", "My-Second-Apply", "My-Third-Apply");
        JSONArray createdDates = documentContext.read("$..dateCreated");
        assertThat(createdDates).asList().containsExactlyInAnyOrder("2023-10-12T00:00:00", "2023-10-13T23:59:00", "2023-10-14T12:01:00");
    }

    @Test
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
