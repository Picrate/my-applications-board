package info.patriceallary.myapplicationsboard.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "job")
public class Job implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
    @Column(name = "date_send")
    private LocalDateTime dateSent = null;
    @Column(name = "date_raised")
    private LocalDateTime dateRaised = null;
    @Column(name = "date_interview")
    private LocalDateTime dateInterview = null;
    @Column(name = "url")
    private String url = null;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private JobResult jobResult;
    @ManyToOne
    private Enterprise enterprise;
    protected Job() {}

    public Job(String name, LocalDateTime dateCreated, JobResult jobResult, Enterprise enterprise) {
        this.name = name;
        this.dateCreated = dateCreated;
        this.jobResult = jobResult;
        this.enterprise = enterprise;
    }

    public Job(String name, LocalDateTime dateCreated, LocalDateTime dateSent, LocalDateTime dateRaised, LocalDateTime dateInterview, String url, JobResult jobResult, Enterprise enterprise) {
        this.name = name;
        this.dateCreated = dateCreated;
        this.dateSent = dateSent;
        this.dateRaised = dateRaised;
        this.dateInterview = dateInterview;
        this.url = url;
        this.jobResult = jobResult;
        this.enterprise = enterprise;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDateTime getDateRaised() {
        return dateRaised;
    }

    public void setDateRaised(LocalDateTime dateRaised) {
        this.dateRaised = dateRaised;
    }

    public LocalDateTime getDateInterview() {
        return dateInterview;
    }

    public void setDateInterview(LocalDateTime dateInterview) {
        this.dateInterview = dateInterview;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JobResult getJobResult() {
        return jobResult;
    }

    public void setJobResult(JobResult jobResult) {
        this.jobResult = jobResult;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateSent=" + dateSent +
                ", dateRaised=" + dateRaised +
                ", dateInterview=" + dateInterview +
                ", url='" + url + '\'' +
                ", jobResult=" + jobResult +
                ", enterprise=" + enterprise +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(id, job.id) && Objects.equals(name, job.name) && Objects.equals(dateCreated, job.dateCreated) && Objects.equals(dateSent, job.dateSent) && Objects.equals(dateRaised, job.dateRaised) && Objects.equals(dateInterview, job.dateInterview) && Objects.equals(url, job.url) && Objects.equals(jobResult, job.jobResult) && Objects.equals(enterprise, job.enterprise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateCreated, dateSent, dateRaised, dateInterview, url, jobResult, enterprise);
    }
}
