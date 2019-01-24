package club.tempvs.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WelcomePageDto {

    private String greeting;
    private String buttonText;
    private boolean adminPanelAvailable;
    private boolean roleRequestAvailable;
    private String role;

    public WelcomePageDto(
            String greeting, String buttonText, boolean adminPanelAvailable, boolean roleRequestAvailable, String role) {
        this.greeting = greeting;
        this.buttonText = buttonText;
        this.adminPanelAvailable = adminPanelAvailable;
        this.roleRequestAvailable = roleRequestAvailable;
        this.role = role;
    }
}
