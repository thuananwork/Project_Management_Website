package com.manager.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

//    @NotEmpty(message = "Username is required.")
    private String username;

    @NotEmpty(message = "Password is required.")
    private String password;

    @NotEmpty(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phoneNumber;

    private String address;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "image")
    private String image;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_requested_time")
    private Date otpRequestedTime;

    @Column(name = "active")
    private boolean active;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "code")
    private String code;

    @Column(name = "class_name")
    private String className;

    @Column(name = "major")
    private String major;

    @Column(name = "department")
    private String department;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications;

    public boolean isOTPRequired() {
        if (this.otp == null || this.otp.equals("")) {
            return false;
        }

        long currentTimeInMillis = System.currentTimeMillis();
        long otpRequestedTimeInMillis = this.otpRequestedTime.getTime();

        return otpRequestedTimeInMillis + 5 * 60 * 1000 >= currentTimeInMillis;
    }

    //equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    //hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
