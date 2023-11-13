package info.patriceallary.myapplicationsboard.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "enterprise")
public class Enterprise implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String phone = null;

    @Lob
    @Column(name = "NOTES", columnDefinition = "CLOB")
    private String notes;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.MERGE})
    @JoinColumn(name = "type_id")
    private EnterpriseType type;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "activity_id")
    private EnterpriseActivity activity;

    protected Enterprise() {
    }

    public Enterprise(String name, String phone, String notes, Address address, EnterpriseType type, EnterpriseActivity activity) {
        this.name = name;
        this.phone = phone;
        this.notes = notes;
        this.address = address;
        this.type = type;
        this.activity = activity;
    }

    public Enterprise(String name, Address address, EnterpriseType type, EnterpriseActivity activity) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.activity = activity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public EnterpriseType getType() {
        return type;
    }

    public void setType(EnterpriseType type) {
        this.type = type;
    }

    public EnterpriseActivity getActivity() {
        return activity;
    }

    public void setActivity(EnterpriseActivity activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "Enterprise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", notes=" + notes +
                ", address=" + address +
                ", type=" + type +
                ", activity=" + activity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enterprise that = (Enterprise) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(phone, that.phone) && Objects.equals(notes, that.notes) && Objects.equals(address, that.address) && Objects.equals(type, that.type) && Objects.equals(activity, that.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, notes, address, type, activity);
    }
}
