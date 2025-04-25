package mymusic.spring_mymusic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Min;
import mymusic.spring_mymusic.dto.response.PlaylistResponse;
import mymusic.spring_mymusic.dto.response.RoleResponse;
import mymusic.spring_mymusic.repository.criteria.JobSearchCriteriaQueryConsumer;
import mymusic.spring_mymusic.repository.criteria.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import mymusic.spring_mymusic.dto.basic.ArtistBasic;
import mymusic.spring_mymusic.dto.request.SongRequest;
import mymusic.spring_mymusic.dto.response.PageResponse;
import mymusic.spring_mymusic.dto.response.SongResponse;
import mymusic.spring_mymusic.entity.*;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.mapper.AlbumMapper;
import mymusic.spring_mymusic.mapper.ArtistMapper;
import mymusic.spring_mymusic.mapper.SongMapper;
import mymusic.spring_mymusic.repository.AlbumRepository;
import mymusic.spring_mymusic.repository.ArtistRepository;
import mymusic.spring_mymusic.repository.GenreRepository;
import mymusic.spring_mymusic.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import mymusic.spring_mymusic.specification.SongSpecification;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class SongService {
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final ArtistMapper artistMapper;
    private final PageableService pageableService;
    private final AlbumMapper albumMapper;
    private final SongMapper songMapper;
    @PersistenceContext
    private EntityManager entityManager;


    public SongResponse create(SongRequest request){
        if(songRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.SONG_EXISTED);
        }
       Song song = songMapper.toSong(request);

        Set<Song> songSet = new HashSet<>();
        songSet.add(song);

        // Album
        if(request.getAlbum() != null){
           Album album  = albumRepository.findById(request.getAlbum())
                   .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_EXISTED));
           if(album != null){
               song.setAlbum(album);
               album.setSongs(songSet);
               album.setTotalTracks(album.getTotalTracks() + 1);
               album.setTotalHours(album.getTotalHours() + song.getDuration());
           }
        }
        // Artist
        if(request.getArtists() != null && !request.getArtists().isEmpty()){
            List<Artist> artistList = artistRepository
                    .findAllByIdIn(request.getArtists());
                song.setArtists(new HashSet<>(artistList));
        }else{
            song.setArtists(new HashSet<>());
        }

        // Genre
        if(request.getGenre() != null){
            Genre genre = genreRepository.findById(request.getGenre())
                    .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));
            song.setGenre(genre);
        }

        song.setListener(song.getListener() + 1);

       return convertSongResponse(songRepository.save(song));
    }

    public SongResponse fetchById(long id){
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));

        song.setListener(song.getListener() + 1);

        return convertSongResponse(songRepository.save(song));
    }

    public SongResponse update(long id, SongRequest request) {
        Song songDB = songRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));

        if(songDB.getAlbum() != null) {
            songDB.getAlbum().setTotalHours(songDB.getAlbum().getTotalHours() - songDB.getDuration());
        }

        Song song = songMapper.update(songDB, request);
        Set<Song> songSet = new HashSet<>();
        songSet.add(song);
        // Album
        if(request.getAlbum() != null){
            Album album  = albumRepository.findById(request.getAlbum())
                    .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_EXISTED));
            if(album != null){
                song.setAlbum(album);
                album.setSongs(songSet);
                album.setTotalHours(album.getTotalHours() + song.getDuration()/3600);
            }
        }

        // Artist
        if(request.getArtists() != null && !request.getArtists().isEmpty()){
            List<Artist> artistList = artistRepository.findAllByIdIn(request.getArtists());
            song.setArtists(new HashSet<>(artistList));
        }else{
            song.setArtists(new HashSet<>());
        }

        // Genre
        if(request.getGenre() != null){
            Genre genre = genreRepository.findById(request.getGenre())
                    .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));
            song.setGenre(genre);
        }

        song.setListener(song.getListener() + 1);

        return songMapper.toSongResponse(songRepository.save(song));
    }


    public void delete(long id){
        Song songDB = songRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));

        songRepository.delete(songDB);
    }

    public List<SongResponse> fetchSongsByGenre(long genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));
        List<Song> songList = songRepository.findAllByGenre(genre);

        return convertListSongResponse(songList);
    }

    public SongResponse convertSongResponse(Song song){
        SongResponse response = songMapper.toSongResponse(song);

        response.setAlbum(Optional.ofNullable(song.getAlbum())
                .map(albumMapper::toAlbumBasic).orElse(null));

        List<ArtistBasic> artistBasicList = song.getArtists()
                .stream().map(artistMapper::toArtistBasic).toList();
        response.setArtists(new HashSet<>(artistBasicList));

        return response;
    }

    public List<SongResponse> convertListSongResponse(List<Song> songList){
        List<SongResponse> songResponseList = new ArrayList<>();
        for(Song song : songList){
            SongResponse response = convertSongResponse(song);
            songResponseList.add(response);
        }
        return songResponseList;
    }

    public PageResponse<SongResponse> fetchAll(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo - 1;

        Pageable pageable = pageableService.createPageable(pageNo, pageSize, sortBy, Song.class);

        Page<Song> songPage = songRepository.findAll(pageable);

        return PageResponse.<SongResponse>builder()
                .page(songPage.getNumber() + 1)
                .size(songPage.getSize())
                .totalPages(songPage.getTotalPages())
                .totalItems(songPage.getTotalElements())
                .items(convertListSongResponse(songPage.getContent()))
                .build();
    }

    public PageResponse<SongResponse> searchSongs(int pageNo, int pageSize, String sortBy, List<String> search) {
        pageNo = pageNo - 1;

        List<SearchCriteria> criteriaList = new ArrayList<>();

        // lay danh sach cac dieu kien search
        if(search != null){
            for(String s : search){
                Pattern pattern = Pattern.compile("(\\w+?)(~|>|<)(.*)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<Song> songList = getSongs(pageNo, pageSize, sortBy, criteriaList);

        // tong so phan tu
        Long totalElements = getTotalElements(criteriaList);
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return PageResponse.<SongResponse>builder()
                .page(pageNo + 1)
                .size(pageSize)
                .totalPages(totalPages)
                .totalItems(totalElements)
                .items(convertListSongResponse(songList))
                .build();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Song> root = countQuery.from(Song.class);

        // Xu ly dieu kien tim kiem
        Predicate predicate = builder.conjunction();

        if(criteriaList != null && !criteriaList.isEmpty()){ // search job
            JobSearchCriteriaQueryConsumer queryConsumer = new JobSearchCriteriaQueryConsumer(builder, predicate, root);
            criteriaList.forEach(queryConsumer);
            predicate = builder.and(predicate, queryConsumer.getPredicate());
        }

        countQuery.select(builder.count(root));
        countQuery.where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Song> getSongs(int pageNo, int pageSize, String sortBy, List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Song> query = builder.createQuery(Song.class);
        Root<Song> root = query.from(Song.class);

        // Xu ly dieu kien tim kiem
        Predicate predicate = builder.conjunction();

        if(criteriaList != null && !criteriaList.isEmpty()){ // search job
            JobSearchCriteriaQueryConsumer queryConsumer = new JobSearchCriteriaQueryConsumer(builder, predicate, root);
            criteriaList.forEach(queryConsumer);
            predicate = builder.and(predicate, queryConsumer.getPredicate());
        }

        query.where(predicate);

        // Sort
        if(StringUtils.hasLength(sortBy)){
            Pattern pattern = Pattern.compile("(\\w+?)(-)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("desc")){
                    query.orderBy(builder.desc(root.get(columnName)));
                }else{
                    query.orderBy(builder.asc(root.get(columnName)));
                }
            }
        }

        return entityManager.createQuery(query)
                .setFirstResult(pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
}
