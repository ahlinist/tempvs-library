package club.tempvs.library.dto;

import static club.tempvs.library.domain.Source.Classification;
import static club.tempvs.library.domain.Source.Type;
import static club.tempvs.library.domain.Source.Period;

import club.tempvs.library.domain.Source;
import lombok.Data;

import java.util.List;

@Data
public class SourceDto {

    private Long id;
    private String name;
    private String description;
    private Classification classification;
    private Type type;
    private Period period;
    private List<String> images;

    public Source toSource() {
        Source source = new Source();
        source.setName(this.name);
        source.setDescription(this.description);
        source.setClassification(this.classification);
        source.setType(this.type);
        source.setPeriod(this.period);
        return source;
    }
}
