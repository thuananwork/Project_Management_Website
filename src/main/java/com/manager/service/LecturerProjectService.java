package com.manager.service;

import com.manager.DTO.LecturerProjectCustom;
import com.manager.model.Project;
import com.manager.model.User;
import com.manager.repository.LecturerProjectRepository;
import com.manager.repository.ProjectRepository;
import com.manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class LecturerProjectService {

    @Autowired
    private LecturerProjectRepository lecturerProjectRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public List<LecturerProjectCustom> getAllLecturerProject() {
        List<Object[]> results = lecturerProjectRepository.findAllProject();
        List<LecturerProjectCustom> projectCustoms = new ArrayList<>();

        for (Object[] result : results) {
            LecturerProjectCustom custom = new LecturerProjectCustom(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (String) result[2],
                    (String) result[3],
                    (Date) result[4],
                    (Date) result[5]
            );
            projectCustoms.add(custom);
        }
        return projectCustoms;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<User> getAllUser() {
        return userRepository.findAllUsersExcludingRoles();
    }
}
