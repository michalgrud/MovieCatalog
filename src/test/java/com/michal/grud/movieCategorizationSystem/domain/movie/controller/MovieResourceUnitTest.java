package com.michal.grud.movieCategorizationSystem.domain.movie.controller;

import com.michal.grud.movieCategorizationSystem.domain.movie.dto.MovieDetailsDTO;
import com.michal.grud.movieCategorizationSystem.domain.movie.service.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieResourceUnitTest {
    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieResource controller;


    @Test
    void createUpdate_shouldDelegateToService_andReturnDto() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "video.bin", "application/octet-stream",
                "bytes".getBytes(StandardCharsets.UTF_8)
        );
        MovieDetailsDTO dto = MovieDetailsDTO.builder()
                .title("The Matrix").productionYear("1999").director("The Wachowskis")
                .fileId("The Matrix.mp4").fileSize(5L).rankScore(123).build();

        when(movieService.createUpdateMovie("The Matrix", "1999", "The Wachowskis", file))
                .thenReturn(ResponseEntity.ok(dto));

        var resp = controller.createUpdate(file, "The Matrix", "The Wachowskis", "1999");

        verify(movieService).createUpdateMovie("The Matrix", "1999", "The Wachowskis", file);
        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).isEqualTo(dto);
    }

    @Test
    void search_shouldReturnPageFromService() {
        var dto = MovieDetailsDTO.builder().title("A").rankScore(2).fileSize(200L).build();
        Page<MovieDetailsDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(movieService.search("rankScore", "desc", 0, 10)).thenReturn(page);

        Page<MovieDetailsDTO> out = controller.search("rankScore", "desc", 0, 10);

        verify(movieService).search("rankScore", "desc", 0, 10);
        assertThat(out).isSameAs(page);
        assertThat(out.getContent()).containsExactly(dto);
    }

    @Test
    void download_shouldReturnResponseFromService() throws Exception {
        byte[] bytes = "stream".getBytes(StandardCharsets.UTF_8);
        StreamingResponseBody body = out -> out.write(bytes);
        ResponseEntity<StreamingResponseBody> serviceResp = ResponseEntity.ok(body);

        when(movieService.downloadMovie("LOTR")).thenReturn(serviceResp);

        ResponseEntity<StreamingResponseBody> resp = controller.download("LOTR");

        verify(movieService).downloadMovie("LOTR");
        assertThat(resp).isSameAs(serviceResp);

        var bos = new java.io.ByteArrayOutputStream();
        resp.getBody().writeTo(bos);
        assertThat(bos.toByteArray()).isEqualTo(bytes);
    }

    @Test
    void delete_shouldReturnServiceResponse() {
        when(movieService.deleteMovie("Kiler")).thenReturn(ResponseEntity.ok().build());

        var resp = controller.delete("Kiler");

        verify(movieService).deleteMovie("Kiler");
        assertThat(resp.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void recalculateRankScore_shouldReturnListFromService() {
        List<MovieDetailsDTO> list = List.of(
                MovieDetailsDTO.builder().title("A").rankScore(100).build(),
                MovieDetailsDTO.builder().title("B").rankScore(200).build()
        );
        when(movieService.recalculateRankScore()).thenReturn(ResponseEntity.ok(list));

        var resp = controller.recalculateRankScore();

        verify(movieService).recalculateRankScore();
        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).containsExactlyInAnyOrderElementsOf(list);
    }
}
