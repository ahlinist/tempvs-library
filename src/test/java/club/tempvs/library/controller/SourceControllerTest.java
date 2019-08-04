package club.tempvs.library.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static club.tempvs.library.domain.Source.*;

import club.tempvs.library.domain.Source;
import club.tempvs.library.dto.FindSourceDto;
import club.tempvs.library.dto.GetSourcesDto;
import club.tempvs.library.dto.ImageDto;
import club.tempvs.library.dto.SourceDto;
import club.tempvs.library.service.SourceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class SourceControllerTest {

    private SourceController controller;

    @Mock
    private SourceDto sourceDto;
    @Mock
    private Source source;
    @Mock
    private ImageDto imageDto;
    @Mock
    private FindSourceDto findSourceDto;
    @Mock
    private GetSourcesDto getSourcesDto;

    @Mock
    private SourceService sourceService;

    @Before
    public void setUp() {
        controller = new SourceController(sourceService);
    }

    @Test
    public void testCreate() {
        when(sourceService.create(source)).thenReturn(source);
        when(sourceDto.toSource()).thenReturn(source);
        when(source.toSourceDto()).thenReturn(sourceDto);

        SourceDto result = controller.create(sourceDto);

        verify(sourceService).create(source);
        verifyNoMoreInteractions(sourceService);

        assertEquals("SourceDto is returned", sourceDto, result);
    }

    @Test
    public void testGet() {
        Long id = 1L;

        when(sourceService.get(id)).thenReturn(source);
        when(source.toSourceDto()).thenReturn(sourceDto);

        SourceDto result = controller.get(id);

        verify(sourceService).get(id);
        verifyNoMoreInteractions(sourceService);

        assertEquals("SourceDto is returned", sourceDto, result);
    }

    @Test
    public void testGetAll() {
        List<Long> ids = Arrays.asList(1L);
        List<Source> sources = Arrays.asList(source);
        List<SourceDto> sourceDtos = Arrays.asList(sourceDto);

        when(getSourcesDto.getIds()).thenReturn(ids);
        when(sourceService.getAll(ids)).thenReturn(sources);
        when(source.toSourceDto()).thenReturn(sourceDto);

        List<SourceDto> result = controller.getAll(getSourcesDto);

        verify(sourceService).getAll(ids);
        verifyNoMoreInteractions(sourceService);

        assertEquals("SourceDto is returned", sourceDtos, result);
    }

    @Test
    public void testFind() {
        String query = "query";
        List<Classification> classifications = Arrays.asList(Classification.OTHER);
        List<Type> types = Arrays.asList(Type.OTHER);
        int page = 0;
        int size = 40;
        List<Source> sources = Arrays.asList(source, source);
        List<SourceDto> sourceDtos = Arrays.asList(sourceDto, sourceDto);

        when(findSourceDto.getQuery()).thenReturn(query);
        when(findSourceDto.getPeriod()).thenReturn(Period.OTHER);
        when(findSourceDto.getClassifications()).thenReturn(classifications);
        when(findSourceDto.getTypes()).thenReturn(types);
        when(sourceService.find(query, Period.OTHER, classifications, types, page, size)).thenReturn(sources);
        when(source.toSourceDto()).thenReturn(sourceDto);

        List<SourceDto> result = controller.find(findSourceDto, page, size);

        verify(sourceService).find(query, Period.OTHER, classifications, types, page, size);
        verify(findSourceDto).getQuery();
        verify(findSourceDto).getPeriod();
        verify(findSourceDto).getClassifications();
        verify(findSourceDto).getTypes();
        verify(source, times(2)).toSourceDto();
        verifyNoMoreInteractions(sourceService, findSourceDto, sourceDto);

        assertEquals("A list of sourceDtos is returned", sourceDtos, result);
    }

    @Test
    public void testUpdateName() {
        Long id = 1L;
        String name = "new name";
        Map<String, String> payload = new HashMap<>();
        payload.put("name", name);

        when(sourceService.updateName(id, name)).thenReturn(source);

        controller.updateName(id, payload);

        verify(sourceService).updateName(id, name);
        verifyNoMoreInteractions(sourceService, source);
    }

    @Test
    public void testUpdateDescription() {
        Long id = 1L;
        String description = "new desc";
        Map<String, String> payload = new HashMap<>();
        payload.put("description", description);

        when(sourceService.updateDescription(id, description)).thenReturn(source);

        controller.updateDescription(id, payload);

        verify(sourceService).updateDescription(id, description);
        verifyNoMoreInteractions(sourceService, source);
    }

    @Test
    public void testDelete() {
        Long id = 1L;

        controller.delete(id);

        verify(sourceService).delete(id);
        verifyNoMoreInteractions(sourceService);
    }

    @Test
    public void testAddImage() {
        Long id = 1L;

        controller.addImage(id, imageDto);

        verify(sourceService).addImage(id, imageDto);
        verifyNoMoreInteractions(sourceService);
    }

    @Test
    public void testDeleteImage() {
        Long sourceId = 1L;
        String objectId = "objectId";

        controller.deleteImage(sourceId, objectId);

        verify(sourceService).deleteImage(sourceId, objectId);
        verifyNoMoreInteractions(sourceService);
    }
}
