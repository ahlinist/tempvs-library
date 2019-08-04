package club.tempvs.library.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class GetSourcesDto {

    @Size(max = 20)
    private List<Long> ids;
}
