package dogveloper.vojoge.location.domain;

import dogveloper.vojoge.location.dto.RequestWalkLocationDto;
import dogveloper.vojoge.location.dto.ResponseLocationDto;
import dogveloper.vojoge.walk.domain.Walk;
import dogveloper.vojoge.walk.dto.response.ResponseWalkDetailDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "location")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id")
    private Walk walk;

    @Column
    private float latitude;

    @Column
    private float longitude;

    @Column
    private LocalDateTime date;

    @Builder
    public Location(Walk walk, RequestWalkLocationDto requestWalkLocationDto){
        this.walk = walk;
        this.latitude = requestWalkLocationDto.getLatitude();
        this.longitude = requestWalkLocationDto.getLongitude();
        this.date = requestWalkLocationDto.getDate();
    }

    public ResponseLocationDto toResponseLocationDto(){
        return new ResponseLocationDto(
                this.latitude,
                this.longitude,
                this.date
        );
    }

}