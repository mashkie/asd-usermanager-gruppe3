package at.ac.fhcampuswien.asd.entity.models;

import at.ac.fhcampuswien.asd.helper.Hashing;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String username;
    String firstName;
    String lastName;
    int failedLoginCounter = 0;
    private byte[] password;
    private byte[] salt;

    public User(Long id, String username, String firstName, String lastName, int failedLoginCounter, String password) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.failedLoginCounter = failedLoginCounter;
        ArrayList<byte[]> list = Hashing.generateHash(password);
        this.salt = list.get(0);
    }

    public void setPassword(String password) {
        ArrayList<byte[]> list = Hashing.generateHash(password);
        this.salt = list.get(0);
        this.password = list.get(1);
    }
}
