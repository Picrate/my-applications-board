package info.patriceallary.myapplicationsboard.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "job_results")
public class JobResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String result;

    protected JobResult() {
    }

    public JobResult(String result) {
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "JobResult{" +
                "id=" + id +
                ", result='" + result + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobResult jobResult = (JobResult) o;
        return Objects.equals(id, jobResult.id) && Objects.equals(result, jobResult.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, result);
    }
}
