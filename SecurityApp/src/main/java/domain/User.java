package domain;

import jakarta.persistence.*;

@Entity
@Table(name="app_user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="surname", nullable=false)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable=false)
    private Role role;

    @Column(name="password", nullable=false)
    private String password;

    @Column(name="email", nullable=false)
    private String email;

    public User(){}

    public User(int id, String name, String surname, Role role, String password, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.password = password;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
