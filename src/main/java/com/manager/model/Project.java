package com.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    // Trạng thái (0: Chưa làm, 1:Đang làm, 2: Hoàn thành, 3: hủy)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ChuaTienHanh,
        DangTienHanh,
        HoanThanh,
        Huy
    }
    private String department;
    private Integer maxStudents;
    private String file;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = true)
    private User createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(projectId, project.projectId) && Objects.equals(title, project.title) && Objects.equals(description, project.description) && Objects.equals(startDate, project.startDate) && Objects.equals(endDate, project.endDate) && Objects.equals(status, project.status) && Objects.equals(department, project.department) && Objects.equals(maxStudents, project.maxStudents) && Objects.equals(file, project.file) && Objects.equals(createdBy, project.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, title, description, startDate, endDate, status, department, maxStudents, file, createdBy);
    }
}
