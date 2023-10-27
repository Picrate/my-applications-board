package info.patriceallary.myapplicationsboard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import info.patriceallary.myapplicationsboard.domain.Apply;
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

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class MyApplicationsBoardApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	// DateTimeFormatter to ISO8601 Datetime
	DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnAnApplyWhenDataIsSaved(){
		ResponseEntity<String> response = restTemplate
				.getForEntity("/applies/1", String.class);

		// Check URL and Get Method exists HTTP 200
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Check Response Body contains apply with id = 1, name="My-first-Apply" and dateCreated = "2023-10-12T00:00:00"
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(1);
		String name = documentContext.read("$.name");
		assertThat(name).isEqualTo("My-First-Apply");
		String dateCreated = documentContext.read("$.dateCreated");
		assertThat(dateCreated).isEqualTo("2023-10-12T00:00:00");
	}

	@Test
	public void shouldNotReturnAnApplyWithAnUnkwnownId() {
		ResponseEntity<String> response = restTemplate
				.getForEntity("/applies/99", String.class);

		// Check That Controller return 404 and empty body
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	void shouldCreateANewApply(){
		Apply newApply = new Apply("4th Apply", LocalDateTime.parse("2023-10-26T11:40:00"));
		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/applies", newApply, Void.class);

		// Check POST request URL is OK and HTTP response = 201 CREATED
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// check URL contained in response header is OK And GET New Apply is OK
		URI locationOfNewApply = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate
				.getForEntity(locationOfNewApply, String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		String name = documentContext.read("$.name");
		LocalDateTime dateTime = LocalDateTime.parse(documentContext.read("$.dateCreated"), formatter);
		assertThat(id).isNotNull();
		assertThat(id).isEqualTo(4);
		assertThat(name).isEqualTo("4th Apply");
		assertThat(dateTime).isEqualTo("2023-10-26T11:40:00");
	}

	@Test
	@DirtiesContext
	void shouldReturnAListOfApplies() {
		ResponseEntity<String> getResponse = restTemplate
				.getForEntity("/applies", String.class);

		// Check GET Method exists
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		// Check body contains list of applies
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		// check all applies are in body
		int appliesCount = documentContext.read("$.length()");
		assertThat(appliesCount).isEqualTo(3);
		// check each apply
		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).asList().containsExactlyInAnyOrder(1,2,3);
		JSONArray names = documentContext.read("$..name");
		assertThat(names)
				.asList().containsExactlyInAnyOrder("My-First-Apply","My-Second-Apply","My-Third-Apply");
		JSONArray createdDates = documentContext.read("$..dateCreated");
		assertThat(createdDates).asList().containsExactlyInAnyOrder("2023-10-12T00:00:00", "2023-10-13T23:59:00", "2023-10-14T12:01:00");
	}

	@Test
	@DirtiesContext
	void shouldReturnAPageOfApplies() {
		ResponseEntity<String> getResponse = restTemplate
				.getForEntity("/applies?page=0&size=1", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		JSONArray pages = documentContext.read("$[*]");
		assertThat(pages.size()).isEqualTo(1);
	}

	@Test
	@DirtiesContext
	void shouldReturnADefaultSortedPageOfApplies() {
		ResponseEntity<String> getResponse = restTemplate
				.getForEntity("/applies?page=0", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		JSONArray list = documentContext.read("$[*]");
		assertThat(list.size()).isEqualTo(3);
		JSONArray dates = documentContext.read("$..dateCreated");
		assertThat(dates).asList().containsExactly("2023-10-12T00:00:00", "2023-10-13T23:59:00", "2023-10-14T12:01:00");
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingApply() {
		// Create new Apply with name modified
		Apply updatedApply = new Apply("My-Modified-Apply",LocalDateTime.parse("2023-10-13T23:59:00", formatter));
		// Create HTTP entity with updatedApply in Body
		HttpEntity<Apply> request = new HttpEntity<>(updatedApply);
		// send PUT request
		ResponseEntity<Void> response = restTemplate
				.exchange("/applies/2", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
		// check if apply was modified
		ResponseEntity<String> getResponse = restTemplate
				.getForEntity("/applies/2", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		String name = documentContext.read("$.name");
		assertThat(name).isEqualTo("My-Modified-Apply");
	}

	@Test
	@DirtiesContext
	void shouldNotUpdateAnApplyThatDoesNotExists() {
		// Create new Apply with name modified
		Apply updatedApply = new Apply("My-Modified-Apply",LocalDateTime.parse("2023-10-13T23:59:00", formatter));
		// Create HTTP entity with updatedApply in Body
		HttpEntity<Apply> request = new HttpEntity<>(updatedApply);
		// send PUT request for apply #99 that does not exist
		ResponseEntity<Void> response = restTemplate
				.exchange("/applies/99", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNull();
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingApply(){
		ResponseEntity<Void> responseEntity = restTemplate
				.exchange("/applies/3", HttpMethod.DELETE, null, Void.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		ResponseEntity<String> getResponse = restTemplate
				.getForEntity("/applies/3", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldNotDeleteAnApplyThatDoesNotExist(){
		ResponseEntity<Void> responseEntity = restTemplate
				.exchange("/applies/99", HttpMethod.DELETE, null, Void.class);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
