package app.video.download.global.util;

public record PageResponse<T>(
        T content,
        String previousPageCursor,
        String nextPageCursor
) { }
