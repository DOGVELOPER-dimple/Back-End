package dogveloper.vojoge.alarm.repository;

import dogveloper.vojoge.alarm.domain.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("SELECT a FROM Alarm a WHERE a.fromDogId <> :dog_id and a.targetId = :dog_id")
    Page<Alarm> findAllByDog(@Param("dog_id") Long dog_id, Pageable pageable);

}
