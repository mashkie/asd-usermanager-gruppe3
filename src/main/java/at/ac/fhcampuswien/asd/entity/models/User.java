package at.ac.fhcampuswien.asd.entity.models;

import at.ac.fhcampuswien.asd.helper.Hashing;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    final Long VALID_SESSION_TIME = Duration.ofMinutes(2)
            .toMillis();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String username;
    String firstName;
    String lastName;
    int failedLoginCounter = 0;
    Long lockedUntil = null;
    Long sessionValidUntil;
    UUID session;
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

    public void refreshSession() {
        this.sessionValidUntil = new Date().getTime() + VALID_SESSION_TIME;
    }
}
