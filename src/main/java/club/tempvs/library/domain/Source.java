package club.tempvs.library.domain;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Source {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    private Classification classification;

    private Type type;

    private Period period;

    @ElementCollection
    private List<String> images = new ArrayList<>();

    @ElementCollection
    private List<Long> comments = new ArrayList<>();

    @CreatedDate
    private Instant createdDate;

    public enum Classification {

        CLOTHING("classification.clothing"),
        FOOTWEAR("classification.footwear"),
        HOUSEHOLD("classification.household"),
        WEAPON("classification.weapon"),
        ARMOR("classification.armor"),
        OTHER("classification.other");

        @Getter
        String key;

        Classification(String key) {
            this.key = key;
        }
    }

    public enum Type {

        WRITTEN("type.written"),
        GRAPHIC("type.graphic"),
        ARCHAEOLOGICAL("type.archaeological"),
        OTHER("type.other");

        @Getter
        String key;

        Type (String key) {
            this.key = key;
        }
    }

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

        Period (String key) {
            this.key = key;
        }
    }
}
