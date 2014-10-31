
import java.util.Date;

public class Deadline {
	private Date mDate;
	
	public Deadline(Date pDate) {
		mDate = pDate;
	} // End constructor Deadline

	long timeUntil() {
		return mDate.getTime() - (new Date()).getTime();
	} // End TimeUntil
} // End class Deadline