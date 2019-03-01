package club.tempvs.library.domain;

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

    @AllArgsConstructor
    public enum Classification {

        CLOTHING("classification.clothing"),
        FOOTWEAR("classification.footwear"),
        HOUSEHOLD("classification.household"),
        WEAPON("classification.weapon"),
        ARMOR("classification.armor"),
        OTHER("classification.other");

        @Getter
        String key;
    }

    @AllArgsConstructor
    public enum Type {

        WRITTEN("type.written"),
        GRAPHIC("type.graphic"),
        ARCHAEOLOGICAL("type.archaeological"),
        OTHER("type.other");

        @Getter
        String key;
    }

    @AllArgsConstructor
    public enum Period {

        ANCIENT("period.ancient"),
        ANTIQUITY("period.antiquity"),
        EARLY_MIDDLE_AGES("period.early-middle-ages"),
        HIGH_MIDDLE_AGES("period.high-middle-ages"),
        LATE_MIDDLE_AGES("period.late-middle-ages"),
        RENAISSANCE("period.renaissance"),
        MODERN("period.modern"),
        WWI("period.wwi"),
        WWII("period.wwii"),
        CONTEMPORARY("period.contemporary"),
        OTHER("period.other");

        @Getter
        String key;
    }
}
