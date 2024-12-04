package dogveloper.vojoge.walk.domain;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.location.domain.Location;
import dogveloper.vojoge.walk.dto.request.RequestWalkDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkDetailDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkDto;
import dogveloper.vojoge.walk.dto.response.ResponseWalkHistoryDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "walk")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Walk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id")
    private Dog dog;

    @OneToMany(mappedBy = "walk", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations;


    private LocalDateTime startTime;


    private LocalDateTime endTime;


    private Float dogConsumableCalories;


    private Float userConsumableCalories;


    private Float distance;

    @Builder
    public Walk(Dog dog, RequestWalkDto requestWalkDto){
        this.dog = dog;
        this.startTime = requestWalkDto.getStartTime();
        this.endTime = requestWalkDto.getEndTime();
        this.dogConsumableCalories = requestWalkDto.getDogConsumableCalories();
        this.userConsumableCalories = requestWalkDto.getUserConsumableCalories();
        this.distance = requestWalkDto.getDistance();
    }

    public ResponseWalkHistoryDto toResponseHistoryDto(){
        return new ResponseWalkHistoryDto(
                this.id,
                this.endTime,
                this.distance,
                this.dogConsumableCalories,
                this.userConsumableCalories
        );
    }

    public ResponseWalkDetailDto toResponseDetailDto(){
        return new ResponseWalkDetailDto(
                this.id,
                this.startTime,
                this.endTime,
                this.dogConsumableCalories,
                this.userConsumableCalories,
                this.distance,
                this.locations.stream().map(Location::toResponseLocationDto).toList()
        );
    }

}
