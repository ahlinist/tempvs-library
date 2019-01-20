package club.tempvs.library.dto;

import lombok.Data;

@Data
public class WelcomePageDto {

    private String greeting;
    private String buttonText;
    private boolean adminPanelAvailable;
    private String role;
    private boolean roleRequest;
}
