package mymusic.spring_mymusic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Min;
import mymusic.spring_mymusic.dto.response.*;
import mymusic.spring_mymusic.entity.*;
import mymusic.spring_mymusic.repository.criteria.JobSearchCriteriaQueryConsumer;
import mymusic.spring_mymusic.repository.criteria.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import mymusic.spring_mymusic.dto.request.PlaylistRequest;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.mapper.PlaylistMapper;
import mymusic.spring_mymusic.repository.PlaylistRepository;
import mymusic.spring_mymusic.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import mymusic.spring_mymusic.repository.UserRepository;
import mymusic.spring_mymusic.specification.PlaylistSpecification;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final PlaylistMapper playlistMapper;
    private final PageableService pageableService;
    private final SongService songService;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public PlaylistResponse create(PlaylistRequest request) {
        if(playlistRepository.existsByTitle(request.getTitle())){
            throw new AppException(ErrorCode.PLAYLIST_EXISTED);
        }
        Playlist playlist = playlistMapper.toPlaylist(request);

        // Songs
        if(request.getSongs() != null && !request.getSongs().isEmpty()){
            List<Song> songList = songRepository.findAllByIdIn(request.getSongs());
            playlist.setSongs(new HashSet<>(songList));
            playlist.setTotalTracks(songList.size());
            for(Song song : songList){
                playlist.setTotalHours(playlist.getTotalHours() + song.getDuration());
            }
        }

        playlist.setFollower(playlist.getFollower() + 1);

        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        playlist.setCreator(user.getName());

        Set<Playlist> playlistSet = new HashSet<>();
        playlistSet.add(playlist);
        user.setCreatedPlaylists(playlistSet);
        userRepository.save(user);

        playlistRepository.save(playlist);

        return convertPlaylistResponse(playlist);
    }

    public PlaylistResponse fetchById(long id){
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYLIST_NOT_EXISTED));

        playlist.setFollower(playlist.getFollower() + 1);
        playlist.setListener(playlist.getListener() + 1);

        return convertPlaylistResponse(playlist);
    }




    public PlaylistResponse update(long id, PlaylistRequest request){
        Playlist playlistDB = playlistRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYLIST_NOT_EXISTED));

        Playlist playlist = playlistMapper.update(playlistDB,request);

        // Songs
        if(request.getSongs() != null && !request.getSongs().isEmpty()){
            List<Song> songList = songRepository.findAllByIdIn(request.getSongs());
            Set<Song> songSet = playlist.getSongs();
            songSet.addAll(songList);
            playlist.setSongs(songSet);
            playlist.setTotalTracks(songList.size());
            for(Song song : songList){
                playlist.setTotalHours(playlist.getTotalHours() + song.getDuration());
                song.setPlaylists(Set.of(playlist));
            }
        }
        playlist.setFollower(playlist.getFollower() + 1);

        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        playlist.setCreator(user.getName());

        Set<Playlist> playlistSet = Set.of(playlist);
        user.setCreatedPlaylists(playlistSet);
        userRepository.save(user);

        return convertPlaylistResponse(playlistRepository.save(playlist));
    }

    public void delete(long id){
        Playlist playlistDB = playlistRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYLIST_NOT_EXISTED));
        playlistRepository.delete(playlistDB);
    }

    public void removeSongFromPlaylist(long playlistId, long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYLIST_NOT_EXISTED));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
        Set<Song> songSet = playlist.getSongs();
        if (songSet != null && songSet.contains(song)) {
            songSet.remove(song);
            playlistRepository.save(playlist);
        } else {
            throw new AppException(ErrorCode.SONG_NOT_IN_PLAYLIST);
        }
    }


    public PlaylistResponse convertPlaylistResponse(Playlist playlist) {
        PlaylistResponse response = playlistMapper.toPlaylistResponse(playlist);

        List<Song> songList = new ArrayList<>(playlist.getSongs());
        List<SongResponse> songResponseList = songService.convertListSongResponse(songList);

        response.setSongs(new HashSet<>(songResponseList));

        return response;
    }

    public List<PlaylistResponse> convertListPlaylistResponse(List<Playlist> playlistList){
        List<PlaylistResponse> playlistResponseList = new ArrayList<>();
        for (Playlist playlist : playlistList){
            PlaylistResponse response = convertPlaylistResponse(playlist);
            playlistResponseList.add(response);
        }
        return playlistResponseList;
    }

    public PageResponse<PlaylistResponse> fetchAll(int pageNo, int pageSize, String sortBy) {
        pageNo = pageNo - 1;

        Pageable pageable = pageableService.createPageable(pageNo, pageSize, sortBy, Playlist.class);

        Page<Playlist> playlistPage = playlistRepository.findAll(pageable);

        return PageResponse.<PlaylistResponse>builder()
                .page(playlistPage.getNumber() + 1)
                .size(playlistPage.getSize())
                .totalPages(playlistPage.getTotalPages())
                .totalItems(playlistPage.getTotalElements())
                .items(convertListPlaylistResponse(playlistPage.getContent()))
                .build();
    }

    public PageResponse<PlaylistResponse> searchPlaylists(int pageNo, int pageSize, String sortBy, List<String> search) {
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

        List<Playlist> playlistList = getPlaylists(pageNo, pageSize, sortBy, criteriaList);

        // tong so phan tu
        Long totalElements = getTotalElements(criteriaList);
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return PageResponse.<PlaylistResponse>builder()
                .page(pageNo + 1)
                .size(pageSize)
                .totalPages(totalPages)
                .totalItems(totalElements)
                .items(convertListPlaylistResponse(playlistList))
                .build();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Playlist> root = countQuery.from(Playlist.class);

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

    private List<Playlist> getPlaylists(int pageNo, int pageSize, String sortBy, List<SearchCriteria> criteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Playlist> query = builder.createQuery(Playlist.class);
        Root<Playlist> root = query.from(Playlist.class);

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
