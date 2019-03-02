package club.tempvs.library.dto;

import static club.tempvs.library.domain.Source.Classification;
import static club.tempvs.library.domain.Source.Type;
import static club.tempvs.library.domain.Source.Period;

import lombok.Data;

import java.util.List;

@Data
public class FindSourceDto {

    private String query;
    private Period period;
    private List<Classification> classifications;
    private List<Type> types;
}
