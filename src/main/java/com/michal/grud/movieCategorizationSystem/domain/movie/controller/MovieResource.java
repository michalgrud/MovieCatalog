package com.michal.grud.movieCategorizationSystem.domain.movie.controller;

import com.michal.grud.movieCategorizationSystem.domain.movie.dto.MovieDetailsDTO;
import com.michal.grud.movieCategorizationSystem.domain.movie.service.MovieService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

import static com.michal.grud.movieCategorizationSystem.domain.movie.controller.MovieResource.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
@RequiredArgsConstructor
public class MovieResource {
    public static final String BASE_PATH = "/api/movies";

    private final MovieService movieService;


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MovieDetailsDTO> createUpdate(@RequestPart(value = "file", required = true) MultipartFile file,
                                                        @RequestParam(value = "title", required = true) @Size(min = 1, max = 300) String title,
                                                        @RequestParam(value = "director", required = true) @NotBlank String director,
                                                        @RequestParam(value = "year", required = true) @Pattern(regexp = "^\\d{4}$",
                                                                message = "\"The production year must be in a 4-digit format: YYYY ") String year) {
        return movieService.createUpdateMovie(title, year, director, file);
    }


    @GetMapping
    public Page<MovieDetailsDTO> search(@RequestParam(value = "sortBy", defaultValue = "rankScore") String sortBy,
                                        @RequestParam(value = "dir", defaultValue = "desc") String dir,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        return movieService.search(sortBy, dir, page, size);
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download(@RequestParam(value = "title", required = true) @NotBlank String title) {
        return movieService.downloadMovie(title);
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> delete(@PathVariable(value = "title", required = true) @NotBlank String title) {
        return movieService.deleteMovie(title);
    }

    @PostMapping("/recalculate")
    public ResponseEntity<List<MovieDetailsDTO>> recalculateRankScore() {
        return movieService.recalculateRankScore();
    }


}
