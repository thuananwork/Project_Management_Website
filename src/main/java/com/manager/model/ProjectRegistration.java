package com.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Table(name = "ProjectRegistrations")
public class ProjectRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationId;

    @ManyToOne
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ChoXacNhan,
        XacNhan,
        Huy
    }//Trạng thái (0: Đang chờ, 1:Xác nhận, 2:hủy)

    private Date registrationDate;

    // Getters and Setters
}