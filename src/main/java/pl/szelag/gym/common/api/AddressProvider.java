package pl.szelag.gym.common.api;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/** * Common contract for providing and formatting physical address data.
 */
public interface AddressProvider {

    /** * Returns the name of the street.
     * @return street name or null if not available
     */
    String getStreet();

    /** * Returns the building or house number.
     * @return street number
     */
    String getStreetNumber();

    /** * Returns the apartment or suite number.
     * @return home number or null/empty if not applicable
     */
    String getHomeNumber();

    /** * Returns the postal code.
     * @return postal code (e.g., "00-000")
     */
    String getPostCode();

    /** * Returns the city or town name.
     * @return city name
     */
    String getCity();

    /** * Returns a formatted address string.
     * @return formatted address or "No address data provided"
     */
    default String getFullAddress() {
        if (isAddressMissing()) return "No address data provided";

        String streetPart = Stream.of(getStreet(), getStreetNumber())
                .filter(this::isText)
                .collect(Collectors.joining(" "));

        if (isText(getHomeNumber())) {
            streetPart += (streetPart.isEmpty() ? "" : "/") + getHomeNumber();
        }

        String locationPart = Stream.of(getPostCode(), getCity())
                .filter(this::isText)
                .collect(Collectors.joining(" "));

        return Stream.of(streetPart, locationPart)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));
    }

    /** * Checks if all address fields are empty.
     * @return true if no address data is present
     */
    private boolean isAddressMissing() {
        return Stream.of(getStreet(), getStreetNumber(), getHomeNumber(), getPostCode(), getCity())
                .noneMatch(this::isText);
    }

    /** * Validates if the string contains actual text.
     * @param s string to check
     * @return true if the string is not null, not blank and not literal "null"
     */
    private boolean isText(String s) {
        return s != null && !s.isBlank() && !s.equalsIgnoreCase("null");
    }
}