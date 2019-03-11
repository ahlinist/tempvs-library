package club.tempvs.library.domain;

import club.tempvs.library.dto.SourceDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Source {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Classification classification;
    @NotNull
    private Type type;
    @NotNull
    private Period period;
    @ElementCollection
    private List<String> images = new ArrayList<>();
    @ElementCollection
    private List<Long> comments = new ArrayList<>();
    @CreatedDate
    private Instant createdDate;

    public enum Classification {

        CLOTHING,
        FOOTWEAR,
        HOUSEHOLD,
        WEAPON,
        ARMOR,
        OTHER
    }

    public enum Type {

        WRITTEN,
        GRAPHIC,
        ARCHAEOLOGICAL,
        OTHER
    }

    public enum Period {

        ANCIENT,
        ANTIQUITY,
        EARLY_MIDDLE_AGES,
        HIGH_MIDDLE_AGES,
        LATE_MIDDLE_AGES,
        RENAISSANCE,
        MODERN,
        WWI,
        WWII,
        CONTEMPORARY,
        OTHER
    }

    public SourceDto toSourceDto() {
        SourceDto sourceDto = new SourceDto();
        sourceDto.setId(this.id);
        sourceDto.setName(this.name);
        sourceDto.setDescription(this.description);
        sourceDto.setClassification(this.classification);
        sourceDto.setType(this.type);
        sourceDto.setPeriod(this.period);
        sourceDto.setImages(this.images);
        return sourceDto;
    }
}
