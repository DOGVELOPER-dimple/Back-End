package dogveloper.vojoge.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRedisDto {
    private Long id;

    private String name;

    private int age;

    private String puppySpecies;

    private String image;

    private double latitude;

    private double longitude;
}
