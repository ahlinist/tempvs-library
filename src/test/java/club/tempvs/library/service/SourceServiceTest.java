package club.tempvs.library.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static club.tempvs.library.domain.Source.*;

import club.tempvs.library.dto.ErrorsDto;
import club.tempvs.library.dto.ImageDto;
import club.tempvs.library.exception.ForbiddenException;
import club.tempvs.library.dao.SourceRepository;
import club.tempvs.library.domain.Source;
import club.tempvs.library.domain.User;
import club.tempvs.library.holder.UserHolder;
import club.tempvs.library.model.Role;
import club.tempvs.library.service.impl.SourceServiceImpl;
import club.tempvs.library.util.ValidationHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class SourceServiceTest {

    @InjectMocks
    private SourceServiceImpl service;

    @Mock
    private User user;
    @Mock
    private Source source;
    @Mock
    private ErrorsDto errorsDto;
    @Mock
    private ImageDto imageDto;
    @Mock
    private SourceRepository sourceRepository;
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private UserHolder userHolder;
    @Mock
    private ImageService imageService;

    @Test
    public void testCreate() {
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);
        String sourceName = "lorica segmentata";
        Source source = new Source();
        source.setName(sourceName);
        source.setClassification(Classification.ARMOR);
        source.setType(Type.ARCHAEOLOGICAL);
        source.setPeriod(Period.ANTIQUITY);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(validationHelper.getErrors()).thenReturn(errorsDto);
        when(sourceRepository.save(source)).thenReturn(source);

        Source result = service.create(source);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(validationHelper).getErrors();
        verify(validationHelper).processErrors(errorsDto);
        verify(sourceRepository).save(source);
        verifyNoMoreInteractions(user, sourceRepository, validationHelper);

        assertEquals("Source is returned", source, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateForMissingName() {
        List<Role> roles = Arrays.asList(Role.ROLE_SCRIBE);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);
        when(validationHelper.getErrors()).thenReturn(errorsDto);
        doThrow(new IllegalArgumentException()).when(validationHelper).processErrors(errorsDto);

        service.create(new Source());
    }

    @Test(expected = ForbiddenException.class)
    public void testCreateForInsufficientAuthorities() {
        List<Role> roles = Arrays.asList(Role.ROLE_USER);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.create(new Source());
    }

    @Test
    public void testGet() {
        Long id = 1L;

        when(sourceRepository.findById(id)).thenReturn(Optional.of(source));

        Source result = service.get(id);

        verify(sourceRepository).findById(id);
        verifyNoMoreInteractions(sourceRepository);

        assertEquals("Source object is returned", source, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetForMissingSource() {
        Long id = 1L;

        when(sourceRepository.findById(id)).thenReturn(Optional.empty());

        service.get(id);
    }

    @Test
    public void testGetAll() {
        List<Long> ids = Arrays.asList(1L);
        List<Source> sources = Arrays.asList(source);

        when(sourceRepository.findAllById(ids)).thenReturn(sources);

        List<Source> result = service.getAll(ids);

        verify(sourceRepository).findAllById(ids);
        verifyNoMoreInteractions(sourceRepository);

        assertEquals("A list of sources is returned", sources, result);
    }

    @Test
    public void testFind() {
        int page = 0;
        int size = 40;
        String query = "query";
        Period period = Period.EARLY_MIDDLE_AGES;
        List<Classification> classifications = Arrays.asList(Classification.ARMOR);
        List<Type> types = Arrays.asList(Type.WRITTEN);
        List<Source> sources = Arrays.asList(source, source);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(sourceRepository.find(period, types, classifications, query, pageable)).thenReturn(sources);

        List<Source> result = service.find(query, period, classifications, types, page, size);

        verify(sourceRepository).find(period, types, classifications, query, pageable);
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
        List<Source> sources = Arrays.asList(source, source);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(sourceRepository.find(period, types, classifications, query, pageable)).thenReturn(sources);

        List<Source> result = service.find(query, period, classifications, types, page, size);

        verify(sourceRepository).find(period, types, classifications, query, pageable);
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
        List<Source> sources = Arrays.asList(source, source);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(sourceRepository.find(period, types, classifications, query, pageable)).thenReturn(sources);

        List<Source> result = service.find(query, period, classifications, types , page, size);

        verify(sourceRepository).find(period, types, classifications, query, pageable);
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

        service.find(query, null, classifications, types, page, size);
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
        List<Source> sources = Arrays.asList(source, source);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        when(sourceRepository.find(period, types, classifications, correctedQuery, pageable)).thenReturn(sources);

        List<Source> result = service.find(query, period, classifications, types, page, size);

        verify(sourceRepository).find(period, types, classifications, correctedQuery, pageable);
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

        Source result = service.updateName(id, name);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(validationHelper).getErrors();
        verify(validationHelper).processErrors(errorsDto);
        verify(sourceRepository).findById(id);
        verify(source).setName(name);
        verify(sourceRepository).save(source);
        verifyNoMoreInteractions(sourceRepository, source, userHolder, user, validationHelper);

        assertEquals("SourceDto object is returned", source, result);
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

        Source result = service.updateDescription(id, description);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(sourceRepository).findById(id);
        verify(source).setDescription(description);
        verify(sourceRepository).save(source);
        verifyNoMoreInteractions(sourceRepository, source, userHolder, user, validationHelper);

        assertEquals("SourceDto object is returned", source, result);
    }

    @Test(expected = ForbiddenException.class)
    public void testDeleteSourceForInsufficientAuthorities() {
        Long id = 1L;
        List<Role> roles = Collections.singletonList(Role.ROLE_SCRIBE);

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
        verify(imageService).delete("source", id);
        verify(sourceRepository).deleteById(id);
        verifyNoMoreInteractions(sourceRepository, userHolder, user, imageService);
    }

    @Test
    public void testAddImage() {
        Long id = 1L;
        List<Role> roles = Arrays.asList(Role.ROLE_CONTRIBUTOR);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.addImage(id, imageDto);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(imageDto).setBelongsTo("source");
        verify(imageDto).setEntityId(id);
        verify(imageService).store(imageDto);
        verifyNoMoreInteractions(sourceRepository, userHolder, user, imageDto, source);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddImageForInsufficientRights() {
        Long id = 1L;
        List<Role> roles = Arrays.asList();

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.addImage(id, imageDto);
    }

    @Test(expected = ForbiddenException.class)
    public void testDeleteImageForInsufficientRights() {
        Long sourceId = 1L;
        String objectId = "objectId";
        List<Role> roles = Arrays.asList(Role.ROLE_CONTRIBUTOR);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.deleteImage(sourceId, objectId);
    }

    @Test
    public void testDeleteImage() {
        Long sourceId = 1L;
        String objectId = "objectId";
        List<String> objectIds = Collections.singletonList(objectId);
        List<Role> roles = Collections.singletonList(Role.ROLE_SCRIBE);

        when(userHolder.getUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(roles);

        service.deleteImage(sourceId, objectId);

        verify(userHolder).getUser();
        verify(user).getRoles();
        verify(imageService).delete(objectIds);
        verifyNoMoreInteractions(user, userHolder, sourceRepository, imageService);
    }
}
