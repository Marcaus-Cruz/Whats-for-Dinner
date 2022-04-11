package edu.wwu.csci412.whatsfordinner;

public class Ingredient {
    /* Fields */
    private String name;

    // For potential future use cases
    //private String untitOfMeasurement;
    //private int quantity;
    //private Date dateOfEntry;

    /**Ingredient(String name)
     * Constructor.
     * Notes:
     *  - For now, an ingredient only needs a name
     *  - quantity and unitOfMeasurement may be added for comparision with recipe
     *  - dateOfEntry may be added in order to manage food expiration concerns
     */
    public Ingredient(String name) {
        this.name = name.toLowerCase();
    }

    /* Accessors */
    /** getName() -> name
     * accesses the name of the ingredient
     */
    public String getName() { return this.name; }

    /* Mutators */
    /** setName(String name)
     * update or set the name of the ingredient
     */
    public void setName(String name) { this.name = name; }

    /**compareTo(Ingredient other)
     * returns 0 if the ingredients are equal (based on name).
     * In practice, it compares the name Strings.
     */
    public int compareTo(Ingredient other) {
        return this.name.compareToIgnoreCase(other.getName());
    }
}
