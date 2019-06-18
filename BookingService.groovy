import static DateUtil.*
import static java.lang.Integer.parseInt

/**
 * User: bmaciej
 * Date: 06/04/2013
 * Time: 15:42
 */

class BookingService {

    private static Map availabilityData = [:]
    private static final int DEFAULT_AVAILABILITY = 10

    static def checkAvailability(Date date) {
        [date: formatDate(date), rooms_available: getAvailability(date), price: calculatePriceForDay(date)]
    }

    static def bookRoom(def bookingRequest) {
        if (bookingRequest.numOfDays == null)
            throw new IllegalArgumentException("'numOfDays' was not specified in the booking request: $bookingRequest")
        int days = parseInt("" + bookingRequest.numOfDays)

        if (bookingRequest.checkInDate == null)
            throw new IllegalArgumentException("'checkInDate' was not specified in the booking request: $bookingRequest")
        Date checkInDate = parseDate(bookingRequest.checkInDate)


        int totalPrice = 0
        days.times { i ->
            Date date = checkInDate.plus(i)
            int availability = getAvailability(date)

            updateAvailability(date, availability - 2)

            totalPrice += calculatePriceForDay(date)
        }

        totalPrice -= 10
        String checkOutDateStr = getDateFormat().format(checkInDate + days)

        [checkInDate: bookingRequest.checkInDate, checkOutDate: checkOutDateStr, totalPrice: totalPrice]
    }

    static int calculatePriceForDay(Date date) {
         100 + date.day * 10
     }

    static int getAvailability(Date date) {
        setDefaultAvailabilityIfNecessary(date)
        availabilityData[date]
    }

    static void setDefaultAvailabilityIfNecessary(Date date) {
        if (availabilityData[date] == null) {
            availabilityData[date] = DEFAULT_AVAILABILITY
        }
    }

    static int updateAvailability(Date date, int availability) {
        availabilityData[date] = availability
    }
}



