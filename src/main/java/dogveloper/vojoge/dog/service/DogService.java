package dogveloper.vojoge.dog.service;

import dogveloper.vojoge.dog.domain.Dog;
import dogveloper.vojoge.dog.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DogService {
    private final DogRepository dogRepository;

    public Dog findById(Long id){
        return dogRepository.findById(id).orElse(null);
    }
}
