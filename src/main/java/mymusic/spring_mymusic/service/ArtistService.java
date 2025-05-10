package mymusic.spring_mymusic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.dto.response.AlbumResponse;
import mymusic.spring_mymusic.repository.criteria.JobSearchCriteriaQueryConsumer;
import mymusic.spring_mymusic.repository.criteria.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import mymusic.spring_mymusic.dto.basic.AlbumBasic;
import mymusic.spring_mymusic.dto.basic.SongBasic;
import mymusic.spring_mymusic.dto.request.ArtistRequest;
import mymusic.spring_mymusic.dto.response.ArtistResponse;
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
import mymusic.spring_mymusic.specification.ArtistSpecification;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final SongMapper songMapper;
    private final AlbumMapper albumMapper;
    private final ArtistMapper artistMapper;
    private final PageableService pageableService;
    @PersistenceContext
    private EntityManager entityManager;


    public ArtistResponse create(ArtistRequest request) {
       if(artistRepository.existsByName(request.getName())){
           throw new AppException(ErrorCode.ARTIST_EXISTED);
       }
       Artist artist = artistMapper.toArtist(request);

        artist.setFollower(artist.getFollower() + 1);

       return convertArtistToResponse(artistRepository.save(artist));
    }

    public ArtistResponse fetchById(long id){
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ARTIST_NOT_EXISTED));

        artist.setFollower(artist.getFollower() + 1);

        return convertArtistToResponse(artist);
    }

    public ArtistResponse update(long id, ArtistRequest request) {
        Artist artistDB = artistRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ARTIST_NOT_EXISTED));

        Artist artist = artistMapper.update(artistDB,request);

        artist.setFollower(artist.getFollower() + 1);

        return convertArtistToResponse(artistRepository.save(artist));
    }

    public void delete(long id){
        Artist artistDB = artistRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ARTIST_NOT_EXISTED));

        artistDB.getAlbums().forEach(album -> album.getArtists().remove(artistDB));

        artistDB.getSongs().forEach(song -> song.getArtists().remove(artistDB));

        artistRepository.delete(artistDB);
    }

    public List<ArtistResponse> convertListArtistToResponse(List<Artist> artistList) {
        List<ArtistResponse> artistResponses = new ArrayList<>();
        for (Artist artist : artistList) {
            ArtistResponse response = convertArtistToResponse(artist);
            artistResponses.add(response);
        }
        return artistResponses;
    }


    public ArtistResponse convertArtistToResponse(Artist artist) {
        ArtistResponse response = artistMapper.toArtistResponse(artist);

        Set<AlbumBasic> albumBasicList = artist.getAlbums()
                .stream().map(albumMapper::toAlbumBasic).collect(Collectors.toSet());
        response.setAlbums(albumBasicList);

        Set<SongBasic> songBasicList = artist.getSongs()
                .stream().map(songMapper::toSongBasic).collect(Collectors.toSet());
        response.setSongs(songBasicList);

        return response;
    }



    public PageResponse<ArtistResponse> fetchAll(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo - 1;

        Pageable pageable = pageableService.createPageable(pageNo, pageSize, sortBy, Artist.class);

        Page<Artist> artistPage = artistRepository.findAll(pageable);

        return PageResponse.<ArtistResponse>builder()
                .page(artistPage.getNumber() + 1)
                .size(artistPage.getSize())
                .totalPages(artistPage.getTotalPages())
                .totalItems(artistPage.getTotalElements())
                .items(convertListArtistToResponse(artistPage.getContent()))
                .build();
    }

    public PageResponse<ArtistResponse> searchArtists(int pageNo, int pageSize, String sortBy, List<String> search) {
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

        List<Artist> artistList = getArtists(pageNo, pageSize, sortBy, criteriaList);

        // tong so phan tu
        Long totalElements = getTotalElements(criteriaList);
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return PageResponse.<ArtistResponse>builder()
                .page(pageNo + 1)
                .size(pageSize)
                .totalPages(totalPages)
                .totalItems(totalElements)
                .items(convertListArtistToResponse(artistList))
                .build();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Artist> root = countQuery.from(Artist.class);

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

    private List<Artist> getArtists(int pageNo, int pageSize, String sortBy, List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Artist> query = builder.createQuery(Artist.class);
        Root<Artist> root = query.from(Artist.class);

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
