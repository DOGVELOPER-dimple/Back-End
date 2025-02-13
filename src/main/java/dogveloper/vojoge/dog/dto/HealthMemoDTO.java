package dogveloper.vojoge.dog.dto;

import dogveloper.vojoge.dog.domain.HealthMemo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HealthMemoDTO {
    private Long id;
    private String title;
    private LocalDate memoDate;
    private String notes;

    @Builder
    public HealthMemoDTO(Long id, String title, LocalDate memoDate, String notes) {
        this.id = id;
        this.title = title;
        this.memoDate = memoDate;
        this.notes = notes;
    }

    public static HealthMemoDTO fromEntity(HealthMemo healthMemo) {
        return HealthMemoDTO.builder()
                .id(healthMemo.getId())
                .title(healthMemo.getTitle())
                .memoDate(healthMemo.getMemoDate())
                .notes(healthMemo.getNotes())
                .build();
    }
}
