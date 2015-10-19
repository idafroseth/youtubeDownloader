package no.uio.ifi.guis;

/**
 * This interface should display a JDialog in the center of the screen showing a
 * progress bar of the download and possibility of canceling the progress. The
 * possibility for stop download and save the current data would also be nice
 * 
 * @author Ida Marie Frøseth
 *
 */
interface PreformingSearchDialog { // extends JDialog(){
	/**
	 * Set the largest value which is the number of videos the user have chosen
	 * to download in this job
	 * 
	 * @param numbOfVideosToSearch
	 */
	void setVideosToRetrieve(int numbOfVideosToSearch);

	/**
	 * When the download is progressing it should be possible to update and
	 * display the current status of the download.
	 * @param numberOfVideosRetrived
	 */
	void updateProgressBar(int numberOfVideosRetrived);
}