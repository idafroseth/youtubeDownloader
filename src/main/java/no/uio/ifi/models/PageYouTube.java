package no.uio.ifi.models;

import java.util.List;

/**
 * 
 * This is the representation of a YouTube page containing all meta data.
 * 
 * @author Stefan Leicht
 * @version 0.11
 *
 */
public class PageYouTube {
	private List<String> linkedUrls;
	private String title;
	private String videoID;
	private List<String> keywords;
	private String author;
	private String length;
	private boolean familyFriendly;
	private String regionsAllowed;
	private String views;
	private String datePublished;
	private String genre;
	private String linkPreviewImage;
	private String likes;
	private String dislikes;
	private List<String> description;
	private String year;
	
	public void setLinkedUrls(List<String> linkedUrls){
		this.linkedUrls = linkedUrls;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setVideoID(String videoID){
		this.videoID = videoID;
	}
	
	public void setKeywords(List<String> keywords){
		this.keywords = keywords;
	}
	
	public void setAuthor(String author){
		this.author = author;
	}
	
	public void setLength(String length){
		this.length = length;
	}
	
	public void setFamilyFriendly(boolean familyFriendly){
		this.familyFriendly = familyFriendly;
	}
	
	public void setRegionsAllowed(String regionsAllowed){
		this.regionsAllowed = regionsAllowed;
	}
	
	public void setViews(String views){
		this.views = views;
	}
	
	public void setDatePublished(String datePublished){
		this.datePublished = datePublished;
	}
	
	public void setGenre(String genre){
		this.genre = genre;
	}
	
	public void setLinkPreviewImage(String linkPreviewImage){
		this.linkPreviewImage = linkPreviewImage;
	}
	
	public void setLikes(String likes){
		this.likes = likes;
	}
	
	public void setDislikes(String dislikes){
		this.dislikes = dislikes;
	}
	
	public void setDescription(List<String> description){
		this.description = description;
	}
	
	public List<String> getLinkedUrls(){
		return this.linkedUrls;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getVideoID(){
		return this.videoID;
	}
	
	public List<String> getKeywords(){
		return this.keywords;
	}
	
	public String getAuthor(){
		return this.author;
	}
	
	public String getLength(){
		return this.length;
	}
	
	public boolean getFamilyFriendly(){
		return this.familyFriendly;
	}
	
	public String getRegionsAllowed(){
		return this.regionsAllowed;
	}
	
	public String getViews(){
		return this.views;
	}
	
	public String getDatePublished(){
		return this.datePublished;
	}
	
	public String getGenre(){
		return this.genre;
	}
	
	public String getLinkPreviewImage(){
		return this.linkPreviewImage;
	}
	
	public String getLikes(){
		return this.likes;
	}
	
	public String getDislikes(){
		return this.dislikes;
	}
	
	public List<String> getDescription(){
		return this.description;
	}
	
}
