package project;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 *  Inventory of the datasets in DATA_FILE_FOLDER, kept in DATA_FILE_NAME
    @author tesic
    @author tarek
 */
public class DataAnalysis{
    
	/**
 	* 
 	* @return reference to list sorted by rogue .. meaning product avg and review avg differ the most 
 	*/
	 public static List<AbstractRatingSummary> sortByDegree(List<AbstractRatingSummary> inList) {
		
		// sorting reviewer by ratings of products differs from respective average product ratings to most 
		Collections.sort(inList, new Comparator<AbstractRatingSummary>() {
		    public int compare(AbstractRatingSummary r1, AbstractRatingSummary r2) {
				return Long.compare(r2.getDegree(), r1.getDegree());
		    }
		});
		return inList;
	}

    /**
 	* 
 	* @return reference to list sorted by rogue .. meaning product avg and review avg differ the most 
 	*/
	public static List<AbstractRatingSummary> sortByAvgDiff(List<AbstractRatingSummary> inList) {
		
		// sorting reviewer by ratings of products differs from respective average product ratings to most 
		Collections.sort(inList, new Comparator<AbstractRatingSummary>() {
		    public int compare(AbstractRatingSummary r1, AbstractRatingSummary r2) {
				return r2.avgScore().compareTo(r1.avgScore());
		    }
		});
		return inList;
    }

    /**
 	* 
 	* @return reference to list sorted by rogue .. meaning product avg and review avg differ the most 
 	*/
	public static List<AbstractRatingSummary> sortByStDevDiff(List<AbstractRatingSummary> inList) {
		
		// sorting reviewer by ratings of products differs from respective average product ratings to most 
		Collections.sort(inList, new Comparator<AbstractRatingSummary>() {
		    public int compare(AbstractRatingSummary r1, AbstractRatingSummary r2) {
				return r2.stDevScore().compareTo(r1.stDevScore());
		    }
		});
		return inList;
	}
	
	/**
	 * 
	 * @param k top K to be saved in the report 
	 * @return string object with full report 
	 */
	public static String printReport(List<AbstractRatingSummary> inList,int k){

		// filtering the reviewers
		List<AbstractRatingSummary> reviewers = inList.stream()
				  .filter(rs -> rs.getNodeID().startsWith("A")).collect(Collectors.toList());
		
		int counter = reviewers.size();

		String separator =  "--------------------------------------------------"+DataAnalysis.LINE_SEP;
		String reportPrint = separator + "Highest "+Math.min(counter,k)+" number of reviews per reviewer" + DataAnalysis.SUMMARY_HEADER;

		sortByDegree(reviewers);
		for(AbstractRatingSummary rr: reviewers.subList((counter>k)?counter-k:0,counter)){
			reportPrint += rr.toString();
		}
		
		reportPrint +=separator + "Highest "+Math.min(counter,k)+" rating discrepancies per reviewer (wrt other reviewers)" + DataAnalysis.SUMMARY_HEADER+DataAnalysis.LINE_SEP;
		sortByAvgDiff(reviewers);
		for(AbstractRatingSummary rr: reviewers.subList((counter>k)?counter-k:0,counter)){
			reportPrint += rr.toString();
		}

		reportPrint +=separator + "Highest "+Math.min(counter,k)+" rating variation per reviewer (wrt other reviewers)" + DataAnalysis.SUMMARY_HEADER;
		sortByStDevDiff(reviewers);
		for(AbstractRatingSummary rr: reviewers.subList((counter>k)?counter-k:0,counter)){
			reportPrint += rr.toString();
		}

		// filtering the products
		List<AbstractRatingSummary> products = inList.stream()
				  .filter(rs -> rs.getNodeID().startsWith("B")).collect(Collectors.toList());
		
		counter = products.size();

		reportPrint += separator + "Highest "+Math.min(counter,k)+" number of reviews per product" +  DataAnalysis.SUMMARY_HEADER;
		sortByDegree(products);
		for(AbstractRatingSummary rr: products.subList((counter>k)?counter-k:0, counter)){
			reportPrint += rr.toString();
		}
		
		reportPrint +=separator + "Highest "+Math.min(counter,k)+" rating discrepancies per product (wrt other products)" +  DataAnalysis.SUMMARY_HEADER;
		sortByAvgDiff(products);
		for(AbstractRatingSummary rr: products.subList((counter>k)?counter-k:0, counter)){
			reportPrint += rr.toString();
		}

		reportPrint +=separator + "Highest "+Math.min(counter,k)+" rating variation per reviewer (wrt other reviewers)" + DataAnalysis.SUMMARY_HEADER;
		sortByStDevDiff(products);
		for(AbstractRatingSummary rr: products.subList((counter>k)?counter-k:0, counter)){
			reportPrint += rr.toString();
		}


		return reportPrint;
	}

	public static final String LINE_SEP = System.lineSeparator();
	/**
	 * The file name of where the database is going to be saved.
	 */
	public static final String DELIMITER = ",";
	public static final String DB_FOLDER = "data";
	public static final String DB_FILENAME = "data.csv";
	public static final String STAT_FILE_TEMPLATE = "ratingSummary_<dataID>.csv";
	public static final String REPORT_FILE_TEMPLATE = "report_<dataID>.csv";
	public static final String RESULTS_FILE_TEMPLATE = "results_<dataID>.csv";
	public static final String SUMMARY_HEADER = System.lineSeparator()+"Id,degree,product avg,product st.dev,reviewer avg,reviewer st.dev"+System.lineSeparator();
}