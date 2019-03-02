package club.tempvs.library.dto;

import static club.tempvs.library.domain.Source.Classification;
import static club.tempvs.library.domain.Source.Type;
import static club.tempvs.library.domain.Source.Period;

import club.tempvs.library.domain.Source;
import lombok.Data;

@Data
public class SourceDto {

    private Long id;
    private String name;
    private String description;
    private String classification;
    private String type;
    private String period;

    public Source toSource() {
        Source source = new Source();
        source.setName(this.name);
        source.setDescription(this.description);
        source.setClassification(Classification.valueOf(this.classification));
        source.setType(Type.valueOf(this.type));
        source.setPeriod(Period.valueOf(this.period));
        return source;
    }
}
