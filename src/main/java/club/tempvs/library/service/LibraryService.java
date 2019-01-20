package club.tempvs.library.service;

import club.tempvs.library.model.User;
import club.tempvs.library.dto.WelcomePageDto;

public interface LibraryService {

    WelcomePageDto getWelcomePage(User user);
}
