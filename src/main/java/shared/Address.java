package shared;

import java.util.Objects;

public final class Address {

    private final String line1;
    private final String city;
    private final String postcode;
    private final String country;

    private Address(String line1, String city, String postcode, String country) {
        this.line1 = Objects.requireNonNull(line1);
        this.city = Objects.requireNonNull(city);
        this.postcode = Objects.requireNonNull(postcode);
        this.country = Objects.requireNonNull(country);
    }

    public static Address of(String line1, String city, String postcode, String country) {
        return new Address(line1, city, postcode, country);
    }

    public String line1() { return line1; }
    public String city() { return city; }
    public String postcode() { return postcode; }
    public String country() { return country; }
}
