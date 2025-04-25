package mymusic.spring_mymusic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.repository.criteria.JobSearchCriteriaQueryConsumer;
import mymusic.spring_mymusic.repository.criteria.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import mymusic.spring_mymusic.dto.basic.ArtistBasic;
import mymusic.spring_mymusic.dto.basic.SongBasic;
import mymusic.spring_mymusic.dto.request.AlbumRequest;
import mymusic.spring_mymusic.dto.response.AlbumResponse;
import mymusic.spring_mymusic.dto.response.PageResponse;
import mymusic.spring_mymusic.entity.Album;
import mymusic.spring_mymusic.entity.Artist;
import mymusic.spring_mymusic.entity.Song;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.mapper.AlbumMapper;
import mymusic.spring_mymusic.mapper.ArtistMapper;
import mymusic.spring_mymusic.mapper.SongMapper;
import mymusic.spring_mymusic.repository.AlbumRepository;
import mymusic.spring_mymusic.repository.ArtistRepository;
import mymusic.spring_mymusic.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import mymusic.spring_mymusic.specification.AlbumSpecification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final AlbumMapper albumMapper;
    private final ArtistMapper artistMapper;
    private final PageableService pageableService;
    @PersistenceContext
    private EntityManager entityManager;



    public AlbumResponse create(AlbumRequest request) {
       if(albumRepository.existsByName(request.getName())){
           throw new AppException(ErrorCode.ALBUM_EXISTED);
       }
       Album album = albumMapper.toAlbum(request);

       // Artist
        if (!CollectionUtils.isEmpty(request.getArtists())) {
            List<Artist> artistList = artistRepository.findAllByIdIn(request.getArtists());
            album.setArtists(new HashSet<>(artistList));
        }else{
            album.setArtists(new HashSet<>());
        }

        // Song
        if (!CollectionUtils.isEmpty(request.getSongs())) {
            List<Song> songList = songRepository.findAllByIdIn(request.getSongs());
            if(!songList.isEmpty()){
                album.setSongs(new HashSet<>(songList));
                songList.forEach(song -> song.setAlbum(album));
                album.setTotalTracks(songList.size());
                for(Song song : songList){
                    album.setTotalHours(album.getTotalHours() + song.getDuration());
                }
            }
        }
        album.setFollower(album.getFollower() + 1);

       return convertAlbumResponse(albumRepository.save(album));
    }
    public AlbumResponse fetchById(long id){
        Album album = albumRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_EXISTED));

        album.setFollower(album.getFollower() + 1);

        return convertAlbumResponse(albumRepository.save(album));
    }


    public AlbumResponse update(long id, AlbumRequest request){
        Album albumDB = albumRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_EXISTED));
        Album album = albumMapper.update(albumDB,request);

        // Artist
        if (!CollectionUtils.isEmpty(request.getArtists())) {
            List<Artist> artistList = artistRepository.findAllByIdIn(request.getArtists());
            album.setArtists(new HashSet<>(artistList));
        }

        // Song
        if (request.getSongs() != null && !request.getSongs().isEmpty()) {
            List<Song> songList = songRepository.findAllByIdIn(request.getSongs());

            Set<Song> albumSongs = album.getSongs();
            albumSongs.addAll(songList);
                album.setSongs(albumSongs);
                songList.forEach(song -> song.setAlbum(album));
            album.setTotalTracks(songList.size());
            for(Song song : songList){
                album.setTotalHours(album.getTotalHours() + song.getDuration());
            }
        }
        else{
            album.setSongs(new HashSet<>());
        }

        album.setFollower(album.getFollower() + 1);

        return convertAlbumResponse(albumRepository.save(album));
    }



    public void delete(long id){
        Album albumDB = albumRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_EXISTED));
        if(albumDB.getSongs() != null){
            Set<Song> songSet = albumDB.getSongs();
            songRepository.deleteAll(songSet);
        }
        albumRepository.delete(albumDB);
    }

    public void deleteSongFromAlbum(long albumId, long songId) {
        Album albumDB = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_EXISTED));

        Song songToDelete = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));

        Set<Song> songSet = albumDB.getSongs();
        if (songSet != null && songSet.contains(songToDelete)) {
            songSet.remove(songToDelete);
            albumDB.setSongs(songSet);

            albumRepository.save(albumDB);

        } else {
            throw new AppException(ErrorCode.SONG_NOT_IN_ALBUM);
        }
    }

    public AlbumResponse convertAlbumResponse(Album album) {
        AlbumResponse response = albumMapper.toAlbumResponse(album);

        Set<ArtistBasic> artistBasicList = album.getArtists()
                    .stream().map(artistMapper::toArtistBasic).collect(Collectors.toSet());
            response.setArtists(artistBasicList);

            Set<SongBasic> songBasicList = album.getSongs()
                    .stream().map(songMapper::toSongBasic).collect(Collectors.toSet());
            response.setSongs(songBasicList);


        return response;
    }

    public List<AlbumResponse> convertListAlbumResponse(List<Album> albumList){
        List<AlbumResponse> albumResponseList = new ArrayList<>();
        for(Album album : albumList){
            AlbumResponse response = convertAlbumResponse(album);
            albumResponseList.add(response);
        }
        return albumResponseList;
    }

    public PageResponse<AlbumResponse> fetchAll(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo - 1;

        Pageable pageable = pageableService.createPageable(pageNo, pageSize, sortBy, Album.class);

        Page<Album> albumPage = albumRepository.findAll(pageable);

        return PageResponse.<AlbumResponse>builder()
                .page(albumPage.getNumber() + 1)
                .size(albumPage.getSize())
                .totalPages(albumPage.getTotalPages())
                .totalItems(albumPage.getTotalElements())
                .items(convertListAlbumResponse(albumPage.getContent()))
                .build();
    }

    public PageResponse<AlbumResponse> searchAlbums(int pageNo, int pageSize, String sortBy, List<String> search) {
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

        List<Album> albumList = getAlbums(pageNo, pageSize, sortBy, criteriaList);

        // tong so phan tu
        Long totalElements = getTotalElements(criteriaList);
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return PageResponse.<AlbumResponse>builder()
                .page(pageNo + 1)
                .size(pageSize)
                .totalPages(totalPages)
                .totalItems(totalElements)
                .items(convertListAlbumResponse(albumList))
                .build();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Album> root = countQuery.from(Album.class);

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

    private List<Album> getAlbums(int pageNo, int pageSize, String sortBy, List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Album> query = builder.createQuery(Album.class);
        Root<Album> root = query.from(Album.class);

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
