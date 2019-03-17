package club.tempvs.library.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static club.tempvs.library.domain.Source.*;

import club.tempvs.library.clients.ImageClient;
import club.tempvs.library.domain.Image;
import club.tempvs.library.dto.ErrorsDto;
import club.tempvs.library.dto.FindSourceDto;
import club.tempvs.library.dto.ImageDto;
import club.tempvs.library.dto.SourceDto;
import club.tempvs.library.exception.ForbiddenException;
import club.tempvs.library.dao.SourceRepository;
import club.tempvs.library.domain.Source;
import club.tempvs.library.domain.User;
import club.tempvs.library.holder.UserHolder;
import club.tempvs.library.model.Role;
import club.tempvs.library.service.impl.SourceServiceImpl;
import club.tempvs.library.util.ValidationHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class SourceServiceTest {

    private SourceService service;

    @Mock
    private User user;
    @Mock
    private Source source;
    @Mock
    private SourceDto sourceDto;
    @Mock
    private ErrorsDto errorsDto;
    @Mock
    private ImageDto imageDto;
    @Mock
    private Image image;
    @Mock
    private SourceRepository sourceRepository;
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private UserHolder userHolder;
    @Mock
    private ImageClient imageClient;

    @Before
    public void setUp() {
        service = new SourceServiceImpl(sourceRepository, validationHelper, userHolder, imageClient);
    }

    @Test
    public void testCreate() {
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);
        String sourceName = "lorica segmentata";
        SourceDto sourceDto = new SourceDto();
        sourceDto.setName(sourceName);
        sourceDto.setClassification(Classification.ARMOR);
        sourceDto.setType(Type.ARCHAEOLOGICAL);
        sourceDto.setPeriod(Period.ANTIQUITY);
        sourceDto.setImages(Collections.emptyList());
        Source source = sourceDto.toSource();

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(validationHelper.getErrors()).thenReturn(errorsDto);
        when(sourceRepository.save(source)).thenReturn(source);

        SourceDto result = service.create(sourceDto);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(validationHelper).getErrors();
        verify(validationHelper).processErrors(errorsDto);
        verify(sourceRepository).save(source);
        verifyNoMoreInteractions(user, sourceRepository, validationHelper);

        assertEquals("Source is returned", sourceDto, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateForInvalidPayload() {
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);
        SourceDto sourceDto = new SourceDto();

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(validationHelper.getErrors()).thenReturn(errorsDto);
        doThrow(new IllegalArgumentException()).when(validationHelper).processErrors(errorsDto);

        service.create(sourceDto);
    }

    @Test(expected = ForbiddenException.class)
    public void testCreateForInsufficientAuthorities() {
        List<Role> roles = Arrays.asList(Role.ROLE_USER);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.create(new SourceDto());
    }

    @Test
    public void testGet() {
        Long id = 1L;

        when(sourceRepository.findById(id)).thenReturn(Optional.of(source));
        when(source.toSourceDto()).thenReturn(sourceDto);

        SourceDto result = service.get(id);

        verify(sourceRepository).findById(id);
        verify(source).toSourceDto();
        verifyNoMoreInteractions(sourceRepository, source);

        assertEquals("Source object is returned", sourceDto, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetForMissingSource() {
        Long id = 1L;

        when(sourceRepository.findById(id)).thenReturn(Optional.empty());

        service.get(id);
    }

    @Test
    public void testFind() {
        int page = 0;
        int size = 40;
        String query = "query";
        Period period = Period.EARLY_MIDDLE_AGES;
        List<Classification> classifications = Arrays.asList(Classification.ARMOR);
        List<Type> types = Arrays.asList(Type.WRITTEN);
        FindSourceDto findSourceDto = new FindSourceDto();
        findSourceDto.setQuery(query);
        findSourceDto.setPeriod(period);
        findSourceDto.setClassifications(classifications);
        findSourceDto.setTypes(types);
        List<Source> sources = Arrays.asList(source, source);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(sourceRepository.find(period, types, classifications, query, pageable)).thenReturn(sources);

        List<SourceDto> result = service.find(findSourceDto, page, size);

        verify(sourceRepository).find(period, types, classifications, query, pageable);
        verify(source, times(2)).toSourceDto();
        verifyNoMoreInteractions(sourceRepository, source);

        assertEquals("2 sourceDtos are returned", 2, result.size());
    }

    @Test
    public void testFindWithoutClassifications() {
        int page = 0;
        int size = 40;
        String query = "query";
        Period period = Period.EARLY_MIDDLE_AGES;
        List<Classification> classifications = Arrays.asList(Classification.values());
        List<Type> types = Arrays.asList(Type.WRITTEN);
        FindSourceDto findSourceDto = new FindSourceDto();
        findSourceDto.setQuery(query);
        findSourceDto.setPeriod(period);
        findSourceDto.setTypes(types);
        List<Source> sources = Arrays.asList(source, source);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(sourceRepository.find(period, types, classifications, query, pageable)).thenReturn(sources);

        List<SourceDto> result = service.find(findSourceDto, page, size);

        verify(sourceRepository).find(period, types, classifications, query, pageable);
        verify(source, times(2)).toSourceDto();
        verifyNoMoreInteractions(sourceRepository, source);

        assertEquals("2 sourceDtos are returned", 2, result.size());
    }

    @Test
    public void testFindWithoutTypes() {
        int page = 0;
        int size = 40;
        String query = "query";
        Period period = Period.EARLY_MIDDLE_AGES;
        List<Classification> classifications = Arrays.asList(Classification.FOOTWEAR);
        List<Type> types = Arrays.asList(Type.values());
        FindSourceDto findSourceDto = new FindSourceDto();
        findSourceDto.setQuery(query);
        findSourceDto.setPeriod(period);
        findSourceDto.setClassifications(classifications);
        List<Source> sources = Arrays.asList(source, source);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(sourceRepository.find(period, types, classifications, query, pageable)).thenReturn(sources);

        List<SourceDto> result = service.find(findSourceDto, page, size);

        verify(sourceRepository).find(period, types, classifications, query, pageable);
        verify(source, times(2)).toSourceDto();
        verifyNoMoreInteractions(sourceRepository, source);

        assertEquals("2 sourceDtos are returned", 2, result.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testFindWithoutPeriod() {
        int page = 0;
        int size = 40;
        String query = "query";
        List<Classification> classifications = Arrays.asList(Classification.ARMOR);
        List<Type> types = Arrays.asList(Type.WRITTEN);
        FindSourceDto findSourceDto = new FindSourceDto();
        findSourceDto.setQuery(query);
        findSourceDto.setClassifications(classifications);
        findSourceDto.setTypes(types);

        service.find(findSourceDto, page, size);
    }

    @Test
    public void testFindWithWhitespaceQuery() {
        int page = 0;
        int size = 40;
        String query = "   ";
        String correctedQuery = "";
        Period period = Period.EARLY_MIDDLE_AGES;
        List<Classification> classifications = Arrays.asList(Classification.ARMOR);
        List<Type> types = Arrays.asList(Type.WRITTEN);
        FindSourceDto findSourceDto = new FindSourceDto();
        findSourceDto.setQuery(query);
        findSourceDto.setPeriod(period);
        findSourceDto.setClassifications(classifications);
        findSourceDto.setTypes(types);
        List<Source> sources = Arrays.asList(source, source);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(sourceRepository.find(period, types, classifications, correctedQuery, pageable)).thenReturn(sources);

        List<SourceDto> result = service.find(findSourceDto, page, size);

        verify(sourceRepository).find(period, types, classifications, correctedQuery, pageable);
        verify(source, times(2)).toSourceDto();
        verifyNoMoreInteractions(sourceRepository, source);

        assertEquals("2 sourceDtos are returned", 2, result.size());
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateNameForInsufficientAuthorities() {
        Long id = 1L;
        String name = "new name";
        List<Role> roles = Arrays.asList(Role.ROLE_CONTRIBUTOR);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.updateName(id, name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNameForInvalidInput() {
        Long id = 1L;
        String name = "";
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(validationHelper.getErrors()).thenReturn(errorsDto);
        doThrow(new IllegalArgumentException()).when(validationHelper).processErrors(errorsDto);

        service.updateName(id, name);
    }

    @Test(expected = NoSuchElementException.class)
    public void testUpdateNameForNotExistingSource() {
        Long id = 1L;
        String name = "new name";
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(validationHelper.getErrors()).thenReturn(errorsDto);
        when(sourceRepository.findById(id)).thenReturn(Optional.empty());

        service.updateName(id, name);
    }

    @Test
    public void testUpdateName() {
        Long id = 1L;
        String name = "new name";
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(validationHelper.getErrors()).thenReturn(errorsDto);
        when(sourceRepository.findById(id)).thenReturn(Optional.of(source));
        when(sourceRepository.save(source)).thenReturn(source);
        when(source.toSourceDto()).thenReturn(sourceDto);

        SourceDto result = service.updateName(id, name);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(validationHelper).getErrors();
        verify(validationHelper).processErrors(errorsDto);
        verify(sourceRepository).findById(id);
        verify(source).setName(name);
        verify(sourceRepository).save(source);
        verify(source).toSourceDto();
        verifyNoMoreInteractions(sourceRepository, source, sourceDto, userHolder, user, validationHelper);

        assertEquals("SourceDto object is returned", sourceDto, result);
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateDescriptionForInsufficientAuthorities() {
        Long id = 1L;
        String description = "new desc";
        List<Role> roles = Arrays.asList(Role.ROLE_CONTRIBUTOR);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.updateDescription(id, description);
    }

    @Test(expected = NoSuchElementException.class)
    public void testUpdateDescriptionForNotExistingSource() {
        Long id = 1L;
        String description = "new desc";
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(sourceRepository.findById(id)).thenReturn(Optional.empty());

        service.updateDescription(id, description);
    }

    @Test
    public void testUpdateDescription() {
        Long id = 1L;
        String description = "new desc";
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(sourceRepository.findById(id)).thenReturn(Optional.of(source));
        when(sourceRepository.save(source)).thenReturn(source);
        when(source.toSourceDto()).thenReturn(sourceDto);

        SourceDto result = service.updateDescription(id, description);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(sourceRepository).findById(id);
        verify(source).setDescription(description);
        verify(sourceRepository).save(source);
        verify(source).toSourceDto();
        verifyNoMoreInteractions(sourceRepository, source, sourceDto, userHolder, user, validationHelper);

        assertEquals("SourceDto object is returned", sourceDto, result);
    }

    @Test(expected = ForbiddenException.class)
    public void testDeleteSourceForInsufficientAuthorities() {
        Long id = 1L;
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.delete(id);
    }

    @Test
    public void testDeleteSource() {
        Long id = 1L;
        List<Role> roles = Arrays.asList(Role.ROLE_ARCHIVARIUS);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.delete(id);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(sourceRepository).deleteById(id);
        verifyNoMoreInteractions(sourceRepository, userHolder, user);
    }

    @Test
    public void testAddImage() {
        Long id = 1L;
        List<Role> roles = Arrays.asList(Role.ROLE_CONTRIBUTOR);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(sourceRepository.findById(id)).thenReturn(Optional.of(source));
        when(imageClient.store(imageDto)).thenReturn(imageDto);
        when(imageDto.toImage()).thenReturn(image);
        when(sourceRepository.save(source)).thenReturn(source);
        when(source.toSourceDto()).thenReturn(sourceDto);

        SourceDto result = service.addImage(id, imageDto);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(sourceRepository).findById(id);
        verify(imageClient).store(imageDto);
        verify(source).getImages();
        verify(imageDto).toImage();
        verify(sourceRepository).save(source);
        verify(source).toSourceDto();
        verifyNoMoreInteractions(sourceRepository, userHolder, user, imageDto, source, sourceDto);

        assertEquals("ImageDto is returned", sourceDto, result);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddImageForInsufficientRights() {
        Long id = 1L;
        List<Role> roles = Arrays.asList();

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.addImage(id, imageDto);
    }
}
