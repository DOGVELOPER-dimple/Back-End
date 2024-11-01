package dogveloper.vojoge.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long user_id;

    @Column
    private String sub;

    @Column
    private String name;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    @Column
    private Provider provider;

    @Column
    private String image;
}
