package craigcrawl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class CoordinateTest
{

	@Test
	public void parseAddressComonents()
	{
		// ExtractCoordinates extract = new ExtractCoordinates();
		//
		// // extract.coordinateLookup(new BigDecimal(35.485300), new
		// BigDecimal(-97.537200));
		// extract.extractAll();

		String[] dictionary = new String[]
		{ "sq ft", "Sq. Ft.", "sqft", "Square Feet:", "square feet" };

		for (String word : dictionary)
		{
			System.out.println("[square feet] "+ word + "  distance = "
					+ StringUtils.getLevenshteinDistance(StringUtils.lowerCase(word), "square feet"));
			
			System.out.println("[sqft] "+ word + " distance = "
					+ StringUtils.getLevenshteinDistance(StringUtils.lowerCase(word), "sq ft"));	
		}
		
		

	}

}
