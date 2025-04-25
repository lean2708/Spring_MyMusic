package mymusic.spring_mymusic.service;

import jakarta.validation.constraints.Min;
import mymusic.spring_mymusic.dto.response.ArtistResponse;
import mymusic.spring_mymusic.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import mymusic.spring_mymusic.dto.request.GenreRequest;
import mymusic.spring_mymusic.dto.response.GenreResponse;
import mymusic.spring_mymusic.dto.response.PageResponse;
import mymusic.spring_mymusic.entity.Genre;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.mapper.GenreMapper;
import mymusic.spring_mymusic.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreMapper genreMapper;
    private final GenreRepository genreRepository;
    private final PageableService pageableService;


    public GenreResponse create(GenreRequest request){
        if(genreRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.GENRE_EXISTED);
        }
        Genre genre = genreMapper.toGenre(request);

        return genreMapper.toGenreResponse(genreRepository.save(genre));
    }

    public GenreResponse fetchById(long id){
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));

        return genreMapper.toGenreResponse(genre);
    }



    public GenreResponse update(long id, GenreRequest request){
        Genre genreDB = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));

        Genre genre = genreMapper.update(genreDB,request);

        return genreMapper.toGenreResponse(genreRepository.save(genre));
    }


    public void delete(long id){
        Genre genreDB = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));

        genreRepository.delete(genreDB);
    }

    public List<GenreResponse> convertListGenreToResponse(List<Genre> genreList){
        List<GenreResponse> genreResponseList = new ArrayList<>();
        genreList.forEach(genre -> {
            genreResponseList.add(genreMapper.toGenreResponse(genre));
        });

        return genreResponseList;
    }

    public PageResponse<GenreResponse> fetchAll(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo - 1;

        Pageable pageable = pageableService.createPageable(pageNo, pageSize, sortBy, Genre.class);

        Page<Genre> genrePage = genreRepository.findAll(pageable);

        return PageResponse.<GenreResponse>builder()
                .page(genrePage.getNumber() + 1)
                .size(genrePage.getSize())
                .totalPages(genrePage.getTotalPages())
                .totalItems(genrePage.getTotalElements())
                .items(convertListGenreToResponse(genrePage.getContent()))
                .build();
    }
}
