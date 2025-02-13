package dogveloper.vojoge.social.user;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sub;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email = "no-email@provider.com";

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String image;
}