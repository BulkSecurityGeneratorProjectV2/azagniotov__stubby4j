package io.github.azagniotov.stubby4j.handlers;

import io.github.azagniotov.stubby4j.cli.ANSITerminal;
import io.github.azagniotov.stubby4j.database.StubRepository;
import io.github.azagniotov.stubby4j.yaml.stubs.StubHttpLifecycle;
import io.github.azagniotov.stubby4j.yaml.stubs.StubTypes;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author: Alexander Zagniotov
 * Created: 8/18/13 1:48 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class AjaxResourceContentHandlerTest {

    @Mock
    private PrintWriter mockPrintWriter;

    @Mock
    private StubRepository mockStubRepository;

    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @Mock
    private HttpServletResponse mockHttpServletResponse;

    @Mock
    private Request mockRequest;

    @Captor
    private ArgumentCaptor<String> fieldCaptor;

    @Captor
    private ArgumentCaptor<Integer> responseSequenceCaptor;

    @Captor
    private ArgumentCaptor<Integer> httpCycleIndexCaptor;

    @Captor
    private ArgumentCaptor<StubTypes> stubTypeCaptor;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ANSITerminal.muteConsole(true);
    }

    @Before
    public void beforeEach() throws Exception {
        when(mockHttpServletResponse.getWriter()).thenReturn(mockPrintWriter);
        when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.GET.asString());
    }

    @Test
    public void verifyBehaviourWhenAjaxSubmittedToFetchStubbedRequestContent() throws Exception {

        final String requestURI = "/ajax/resource/5/request/post";
        final AjaxResourceContentHandler ajaxResourceContentHandler = new AjaxResourceContentHandler(mockStubRepository);
        final AjaxResourceContentHandler spyAjaxResourceContentHandler = Mockito.spy(ajaxResourceContentHandler);

        when(mockHttpServletRequest.getRequestURI()).thenReturn(requestURI);
        when(mockStubRepository.matchStubByIndex(anyInt())).thenReturn(new StubHttpLifecycle());

        spyAjaxResourceContentHandler.handle(requestURI, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

        verify(spyAjaxResourceContentHandler).throwErrorOnNonExistentResourceIndex(any(HttpServletResponse.class), httpCycleIndexCaptor.capture());
        verify(spyAjaxResourceContentHandler, times(1)).renderAjaxResponseContent(any(HttpServletResponse.class), stubTypeCaptor.capture(), fieldCaptor.capture(), any(StubHttpLifecycle.class));
        verify(spyAjaxResourceContentHandler, never()).renderAjaxResponseContent(any(HttpServletResponse.class), anyInt(), anyString(), any(StubHttpLifecycle.class));

        assertThat(httpCycleIndexCaptor.getValue()).isEqualTo(5);
        assertThat(stubTypeCaptor.getValue()).isEqualTo(StubTypes.REQUEST);
        assertThat(fieldCaptor.getValue()).isEqualTo("post");
    }

    @Test
    public void verifyBehaviourWhenAjaxSubmittedToFetchStubbedResponseContent() throws Exception {

        final String requestURI = "/ajax/resource/15/response/file";
        final AjaxResourceContentHandler ajaxResourceContentHandler = new AjaxResourceContentHandler(mockStubRepository);
        final AjaxResourceContentHandler spyAjaxResourceContentHandler = Mockito.spy(ajaxResourceContentHandler);

        when(mockHttpServletRequest.getRequestURI()).thenReturn(requestURI);
        when(mockStubRepository.matchStubByIndex(anyInt())).thenReturn(new StubHttpLifecycle());

        spyAjaxResourceContentHandler.handle(requestURI, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

        verify(spyAjaxResourceContentHandler).throwErrorOnNonExistentResourceIndex(any(HttpServletResponse.class), httpCycleIndexCaptor.capture());
        verify(spyAjaxResourceContentHandler, times(1)).renderAjaxResponseContent(any(HttpServletResponse.class), stubTypeCaptor.capture(), fieldCaptor.capture(), any(StubHttpLifecycle.class));
        verify(spyAjaxResourceContentHandler, never()).renderAjaxResponseContent(any(HttpServletResponse.class), anyInt(), anyString(), any(StubHttpLifecycle.class));

        assertThat(httpCycleIndexCaptor.getValue()).isEqualTo(15);
        assertThat(stubTypeCaptor.getValue()).isEqualTo(StubTypes.RESPONSE);
        assertThat(fieldCaptor.getValue()).isEqualTo("file");
    }

    @Test
    public void verifyBehaviourWhenAjaxSubmittedToFetchStubbedSequencedResponseContent() throws Exception {

        final String requestURI = "/ajax/resource/15/response/8/file";
        final AjaxResourceContentHandler ajaxResourceContentHandler = new AjaxResourceContentHandler(mockStubRepository);
        final AjaxResourceContentHandler spyAjaxResourceContentHandler = Mockito.spy(ajaxResourceContentHandler);

        when(mockHttpServletRequest.getRequestURI()).thenReturn(requestURI);
        when(mockStubRepository.matchStubByIndex(anyInt())).thenReturn(new StubHttpLifecycle());

        spyAjaxResourceContentHandler.handle(requestURI, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

        verify(spyAjaxResourceContentHandler).throwErrorOnNonExistentResourceIndex(any(HttpServletResponse.class), httpCycleIndexCaptor.capture());
        verify(spyAjaxResourceContentHandler, times(1)).renderAjaxResponseContent(any(HttpServletResponse.class), responseSequenceCaptor.capture(), fieldCaptor.capture(), any(StubHttpLifecycle.class));
        verify(spyAjaxResourceContentHandler, never()).renderAjaxResponseContent(any(HttpServletResponse.class), any(StubTypes.class), anyString(), any(StubHttpLifecycle.class));

        assertThat(httpCycleIndexCaptor.getValue()).isEqualTo(15);
        assertThat(responseSequenceCaptor.getValue()).isEqualTo(8);
        assertThat(fieldCaptor.getValue()).isEqualTo("file");
    }

    @Test
    public void verifyBehaviourWhenAjaxSubmittedToFetchContentForWrongStubType() throws Exception {

        final String requestURI = "/ajax/resource/5/WRONG-STUB-TYPE/post";
        final AjaxResourceContentHandler ajaxResourceContentHandler = new AjaxResourceContentHandler(mockStubRepository);
        final AjaxResourceContentHandler spyAjaxResourceContentHandler = Mockito.spy(ajaxResourceContentHandler);

        when(mockHttpServletRequest.getRequestURI()).thenReturn(requestURI);

        spyAjaxResourceContentHandler.handle(requestURI, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

        verify(spyAjaxResourceContentHandler, times(1)).throwErrorOnNonExistentResourceIndex(any(HttpServletResponse.class), anyInt());
        verify(mockPrintWriter, times(1)).println("Could not fetch the content for stub type: WRONG-STUB-TYPE");

        verify(spyAjaxResourceContentHandler, never()).renderAjaxResponseContent(any(HttpServletResponse.class), any(StubTypes.class), anyString(), any(StubHttpLifecycle.class));
        verify(spyAjaxResourceContentHandler, never()).renderAjaxResponseContent(any(HttpServletResponse.class), anyInt(), anyString(), any(StubHttpLifecycle.class));
    }
}
