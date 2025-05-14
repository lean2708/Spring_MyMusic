package mymusic.spring_mymusic.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_EMPTY(1001, "Tệp tin rỗng, vui lòng tải tệp hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1002, "Chưa xác thực người dùng", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "Bạn chưa được phân quyền truy cập", HttpStatus.FORBIDDEN),
    USER_EXISTED(1004, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    EMAIL_IMMUTABLE(1006, "Email không thể thay đổi sau khi đã được đăng ký", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1007, "Email đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    SONG_EXISTED(1008, "Bài hát đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    SONG_NOT_EXISTED(1009, "Bài hát không tồn tại", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(1010, "Vai trò đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1011, "Vai trò không tồn tại", HttpStatus.NOT_FOUND),
    PLAYLIST_EXISTED(1012, "Playlist đã tồn tại", HttpStatus.BAD_REQUEST),
    PLAYLIST_NOT_EXISTED(1013, "Playlist không tồn tại", HttpStatus.NOT_FOUND),
    PERMISSION_EXISTED(1014, "Quyền truy cập đã tồn tại", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(1015, "Quyền truy cập không tồn tại", HttpStatus.NOT_FOUND),
    GENRE_EXISTED(1016, "Thể loại đã tồn tại", HttpStatus.BAD_REQUEST),
    GENRE_NOT_EXISTED(1017, "Thể loại không tồn tại", HttpStatus.NOT_FOUND),
    ARTIST_EXISTED(1019, "Nghệ sĩ đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    ARTIST_NOT_EXISTED(1019, "Nghệ sĩ không tồn tại", HttpStatus.NOT_FOUND),
    ALBUM_EXISTED(1020, "Album đã tồn tại", HttpStatus.BAD_REQUEST),
    ALBUM_NOT_EXISTED(1021, "Album không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_PREMIUM_TYPE(1009, "Invalid premium type", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1022, "Mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    EMAIL_SEND_FAILED(1023, "Lỗi khi gửi email", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_NOT_FOUND(1024, "Mã xác nhận không tồn tại", HttpStatus.NOT_FOUND),
    VERIFICATION_CODE_EXPIRED(1025, "Mã xác nhận đã hết hạn", HttpStatus.BAD_REQUEST),
    SONG_NOT_IN_ALBUM(1026, "Song không tồn tại trong Album", HttpStatus.NOT_FOUND ),
    SONG_NOT_IN_PLAYLIST(1027, "Song không tồn tại trong Playlist", HttpStatus.NOT_FOUND ),
    PLAYLIST_NOT_IN_USER(1028, "Playlist không tồn tại trong User", HttpStatus.NOT_FOUND ),
    SONG_NOT_FOUND_FOR_GENRE(1029, "Song không tồn tại trong Genre", HttpStatus.NOT_FOUND),
    GENRE_NOT_FOUND(1030, "Không tồn tại thể loại nào trong hệ thống", HttpStatus.NOT_FOUND),
    PLAYLIST_NOT_FOUND(1031, "Không tồn tại playlist nào trong hệ thống", HttpStatus.NOT_FOUND),
    ALBUM_NOT_FOUND(1032, "Không tồn tại album nào trong hệ thống", HttpStatus.NOT_FOUND),
    SONG_NOT_FOUND(1033, "Không tồn tại bài hát nào trong hệ thống", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1034, "Không tồn tại user nào trong hệ thống", HttpStatus.NOT_FOUND),
    ARTIST_NOT_FOUND(1035, "Không tồn tại nghệ sĩ nào trong hệ thống", HttpStatus.NOT_FOUND),
    INVALID_SORT_FIELD(1022, "Thuộc tính không hợp lệ để sắp xếp", HttpStatus.BAD_REQUEST),
    INVALID_SORT_FORMAT(1023, "Định dạng sortBy không hợp lệ phải là: field-asc hoặc field-desc", HttpStatus.BAD_REQUEST),
    TOKEN_TYPE_INVALID(1035, "Loại token không hợp lệ", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1034, "Refresh token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),
    PASSWORD_MISMATCH(1056, "Mật khẩu nhập lại không khớp", HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD(1024, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST),
    FORGOT_PASSWORD_TOKEN_NOT_FOUND(1025, "Token đặt lại mật khẩu không tồn tại hoặc đã hết hạn", HttpStatus.NOT_FOUND),
    PLAYLIST_ALREADY_SAVED(1012, "Playlist đã được lưu bởi người dùng này", HttpStatus.BAD_REQUEST),
            ;

    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}
