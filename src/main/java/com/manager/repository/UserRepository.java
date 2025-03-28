package com.manager.repository;

import com.manager.model.Role;
import com.manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

    @Query(value = "select * from user where email like %?1%", nativeQuery = true)
    List<User> findAllByEmail(String email);

    @Query(value = "select * from user where email like %?1% and role_id=?2", nativeQuery = true)
    List<User> findAllByEmailAndRoleId(String email, int id);
    User findByUsernameOrEmail(String username, String email);

    @Query(value = "SELECT u.* FROM User u JOIN role r ON u.role_id = r.role_id WHERE r.role_name NOT IN ('Admin', 'Student')", nativeQuery = true)
    List<User> findAllUsersExcludingRoles();

    @Query(value = "SELECT u.* FROM User u JOIN role r ON u.role_id = r.role_id WHERE r.role_name NOT IN ('Lecturer', 'Student')", nativeQuery = true)
    List<User> findAllUsersExcluding();

    List<User> findAllByRole(Role role);

    User findById(long id);

    @Query(value = "SELECT * FROM user where user_id = ?1",nativeQuery = true)
    User findUserById(Long id);

    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = 2")
    Long countStudentUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = 3")
    Long countLecturerUsers();
}
