package info.patriceallary.myapplicationsboard.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "contacts")
public class Contact implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstname;

    private String lastname;

    private String phone = null;

    private String email = null;

    @Column(name = "linkedin_profile")
    private String linkedInURL = null;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private ContactRole contactRole;

    @ManyToOne
    @JoinColumn(name = "title_id")
    private ContactTitle title;

    @ManyToOne(optional = false)
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;

    protected Contact() {
    }

    public Contact(String firstname, String lastname, String phone, String email, String linkedInURL, Address address, ContactRole contactRole, ContactTitle title, Enterprise enterprise) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.linkedInURL = linkedInURL;
        this.address = address;
        this.contactRole = contactRole;
        this.title = title;
        this.enterprise = enterprise;
    }

    public Contact(String firstname, String lastname, Address address, ContactRole contactRole, ContactTitle title, Enterprise enterprise) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.contactRole = contactRole;
        this.title = title;
        this.enterprise = enterprise;
    }

    public Contact(String firstname, String lastname, ContactRole contactRole, Enterprise enterprise) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.contactRole = contactRole;
        this.enterprise = enterprise;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkedInURL() {
        return linkedInURL;
    }

    public void setLinkedInURL(String linkedInURL) {
        this.linkedInURL = linkedInURL;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ContactRole getContactRole() {
        return contactRole;
    }

    public void setContactRole(ContactRole contactRole) {
        this.contactRole = contactRole;
    }

    public ContactTitle getTitle() {
        return title;
    }

    public void setTitle(ContactTitle title) {
        this.title = title;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", linkedInURL='" + linkedInURL + '\'' +
                ", address=" + address +
                ", role=" + contactRole +
                ", title=" + title +
                ", enterprise" + enterprise +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id) && Objects.equals(firstname, contact.firstname) && Objects.equals(lastname, contact.lastname) && Objects.equals(phone, contact.phone) && Objects.equals(email, contact.email) && Objects.equals(linkedInURL, contact.linkedInURL) && Objects.equals(address, contact.address) && Objects.equals(contactRole, contact.contactRole) && Objects.equals(title, contact.title) && Objects.equals(enterprise, contact.enterprise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, phone, email, linkedInURL, address, contactRole, title, enterprise);
    }
}
