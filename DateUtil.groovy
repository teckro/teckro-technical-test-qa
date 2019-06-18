import java.text.SimpleDateFormat
/**
 * User: bmaciej
 * Date: 04/04/2013
 * Time: 14:53
 */
class DateUtil {
    static def Date parseDate(dateStr) {
        try {
            def dateFormat = getDateFormat()
            dateFormat.parse(dateStr)
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or missing date '$dateStr'. Valid date format is: yyyy-mm-dd, e.g. 2013-04-20.")
        }
    }

    static SimpleDateFormat getDateFormat() {
        new SimpleDateFormat("yyyy-mm-dd")
    }

    static String formatDate(Date date) {
        getDateFormat().format(date)
    }
}
