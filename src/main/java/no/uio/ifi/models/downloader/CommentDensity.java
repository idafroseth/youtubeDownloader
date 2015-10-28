package no.uio.ifi.models.downloader;

public class CommentDensity {
	static int videosWithCommentCount = 0;
	public static int addComment(){
		return ++videosWithCommentCount;
	}
	public static int getVideoCount(){
		return videosWithCommentCount;
	}
}
