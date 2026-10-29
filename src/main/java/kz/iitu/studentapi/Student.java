package kz.iitu.studentapi;

import jakarta.persistence.*;

@Entity
@Table(name = "students", uniqueConstraints = @UniqueConstraint(name = "uk_students_email", columnNames = "email"))
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String firstName;
    @Column(nullable = false, length = 80)
    private String lastName;
    @Column(nullable = false, length = 254)
    private String email;
    @Column(nullable = false, length = 30)
    private String groupName;

    protected Student() {}
    public Student(StudentRequest request) { apply(request); }
    public void apply(StudentRequest request) {
        firstName = request.firstName();
        lastName = request.lastName();
        email = request.email();
        groupName = request.groupName();
    }
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getGroupName() { return groupName; }
}
