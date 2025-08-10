package RepositoryInterfaces;

import domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryInterface extends JpaRepository<User,Integer> {

}
