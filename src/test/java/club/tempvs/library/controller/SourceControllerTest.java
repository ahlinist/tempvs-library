package club.tempvs.library.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.library.dto.SourceDto;
import club.tempvs.library.service.SourceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SourceControllerTest {

    private SourceController controller;

    @Mock
    private SourceDto sourceDto;
    @Mock
    private SourceService sourceService;

    @Before
    public void setUp() {
        controller = new SourceController(sourceService);
    }

    @Test
    public void testCreate() {
        when(sourceService.create(sourceDto)).thenReturn(sourceDto);

        SourceDto result = controller.create(sourceDto);

        verify(sourceService).create(sourceDto);
        verifyNoMoreInteractions(sourceService);

        assertEquals("SourceDto is returned", sourceDto, result);
    }

    @Test
    public void testGet() {
        Long id = 1L;

        when(sourceService.get(id)).thenReturn(sourceDto);

        SourceDto result = controller.get(id);

        verify(sourceService).get(id);
        verifyNoMoreInteractions(sourceService);

        assertEquals("SourceDto is returned", sourceDto, result);
    }
}
