import CHG4343_Design_Project_CustomExcpetions.ArrayException;
import CHG4343_Design_Project_CustomExcpetions.InvalidArrayDataException;
import CHG4343_Design_Project_CustomExcpetions.LengthMismatch;
import CHG4343_Design_Project_CustomExcpetions.NumericalException;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract reaction class.
 */
public abstract class AbstractReaction {
    /* All variables announced as final since they should not change over the lifetime of reaction object
    * In other words, variables do not represent state, but rather "immutable characteristics" of the reaction. */
    private final ChemicalSpecies[] reactants;
    private final ChemicalSpecies[] products;
    private final double[] reactantsStoichiometry; // Stored as negative values
    private final double[] productsStoichiometry; // Stored as positive values
    /**
     *
     * @param reactants Array of ChemicalSpecies representing reactants.
     * @param products Array of ChemicalSpecies representing products.
     * @param reactantsStoichiometry Array of doubles representing reactants stoichiometric ratios.
     * @param productsStoichiometry Array of doubles representing product stoichiometric ratios.
     */
    public AbstractReaction(ChemicalSpecies[] reactants, ChemicalSpecies[] products, double[] reactantsStoichiometry,
                            double[] productsStoichiometry) {
        // Data validation.
        AbstractReaction.validateData(reactants, products, reactantsStoichiometry, productsStoichiometry);
        this.reactants = new ChemicalSpecies[reactants.length];
        this.products = new ChemicalSpecies[products.length];
        this.reactantsStoichiometry = new double[reactantsStoichiometry.length];
        this.productsStoichiometry = new double[productsStoichiometry.length];
        for(int i = 0; i < reactants.length; i++) {
            this.reactants[i] = reactants[i].clone();
            this.reactantsStoichiometry[i] = reactantsStoichiometry[i];
        }
        for(int i = 0; i < products.length; i++) {
            this.products[i] = products[i].clone();
            this.productsStoichiometry[i] = productsStoichiometry[i];
        }
    }
    public AbstractReaction(AbstractReaction source) throws NullPointerException {
        if(source == null) throw new NullPointerException("Source object in copy constructor of Reaction is null");
        this.reactants = new ChemicalSpecies[source.reactants.length];
        this.products = new ChemicalSpecies[source.products.length];
        this.reactantsStoichiometry = new double[source.reactantsStoichiometry.length];
        this.productsStoichiometry = new double[source.productsStoichiometry.length];
        for(int i = 0; i < this.reactants.length; i++) {
            this.reactants[i] = source.reactants[i].clone();
            this.reactantsStoichiometry[i] = source.reactantsStoichiometry[i];
        }
        for(int i = 0; i < this.products.length; i++) {
            this.products[i] = source.products[i].clone();
            this.productsStoichiometry[i] = source.productsStoichiometry[i];
        }
    }
    abstract public AbstractReaction clone();

    /* Accessor and Mutators */

    public ChemicalSpecies[] getReactants() {
        ChemicalSpecies[] tmpReactants = new ChemicalSpecies[this.reactants.length];
        for(int i = 0; i < this.reactants.length; i++) {
            tmpReactants[i] = this.reactants[i].clone();
        }
        return tmpReactants;
    }
    public ChemicalSpecies[] getProducts() {
        ChemicalSpecies[] tmpProducts = new ChemicalSpecies[this.products.length];
        for(int i = 0; i < this.products.length; i++) {
            tmpProducts[i] = this.products[i].clone();
        }
        return tmpProducts;
    }
    public double[] getReactantsStoichiometry() {
        double[] tmpReactantsStoichiometry = new double[this.reactantsStoichiometry.length];
        for(int i = 0; i < this.reactantsStoichiometry.length; i++) {
            tmpReactantsStoichiometry[i] = this.reactantsStoichiometry[i];
        }
        return tmpReactantsStoichiometry;
    }
    public double[] getProductsStoichiometry() {
        double[] tmpProductsStoichiometry = new double[this.reactantsStoichiometry.length];
        for(int i = 0; i < this.productsStoichiometry.length; i++) {
            tmpProductsStoichiometry[i] = this.productsStoichiometry[i];
        }
        return tmpProductsStoichiometry;
    }

    /**
     * Method returns a stoichiometry of species X in the system.
     * @param species ChemicalSpecies object.
     * @return stoichiometric ratio of species (0 if inert).
     */
    public double getStoichiometry(ChemicalSpecies species) {
        // Check if species is a reactant and return stoichiometry if it is.
        for(int i = 0; i < this.products.length; i++) {
            if(this.products[i].equals(species)) return this.productsStoichiometry[i];
        }
        // Check if species is a product and return stoichiometry if it is.
        for(int i = 0; i < this.reactants.length; i++) {
            if(this.reactants[i].equals(species)) return this.reactantsStoichiometry[i];
        }
        // return for inert species;
        return 0;
    }
    public boolean isReactant(ChemicalSpecies species) {
        for(int i = 0; i < this.reactants.length; i++) {
            if(this.reactants[i].equals(species)) return true;
        }
        return false;
    }
    public abstract double calculateRateConstant(ChemicalMixture mixture);
    public abstract double calculateReactionRate(ChemicalMixture mixture);
    public abstract Function generateRateExpression(ChemicalMixture mixture);
    /**
     * Custom validator
     */
    private static void validateData(ChemicalSpecies[] reactants, ChemicalSpecies[] products, double[] reactantsStoichiometry,
                                     double[] productsStoichiometry) {
        // Check for nulls
        if (reactants == null) throw new IllegalArgumentException("Invalid reactant array encountered while initializing chemical reaction object");
        if (products == null) throw new IllegalArgumentException("Invalid products array encountered while initializing chemical reaction object");
        if (reactantsStoichiometry == null) throw new IllegalArgumentException("Invalid reactant stoichiometry array encountered " +
                "while initializing chemical reaction object");
        if (productsStoichiometry == null) throw new IllegalArgumentException("Invalid product stoichiometry array encountered " +
                "while initializing chemical reaction object");
        // Check for length mismatches
        if (reactants.length != reactantsStoichiometry.length) {
            throw new ArrayException("Length of reactants array does not match the length of reactant stoichiometries array.");
        }
        if (products.length != productsStoichiometry.length) {
            throw new ArrayException("Length of products array does not match the length of product stoichiometries array.");
        }
        // Check for same species in reactants and products
        List<ChemicalSpecies> productList = Arrays.asList(products);
        for(int i = 0; i < reactants.length; i++) {
            if(productList.contains(reactants[i])) throw new ArrayException("Reactants and products arrays contain same specie");
        }
        // Check for invalid data in the arrays
        for (int i = 0; i < reactants.length; i++) {
            if (reactants[i] == null)
                throw new ArrayException("Reactant at index " + i + " is invalid.");
            if (0 < reactantsStoichiometry[i]) throw new ArrayException("Reactant stoichiometry at index " + i + " is invalid.");
        }
        for (int i = 0; i < products.length; i++) {
            if (products[i] == null)
                throw new ArrayException("Products at index " + i + " is invalid.");
            if (productsStoichiometry[i] < 0) throw new ArrayException("Product stoichiometry at index " + i + " is invalid.");
        }
    }
}
