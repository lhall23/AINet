//AINet.java
// 	-Lee Hall Tue 16 Oct 2012 11:03:15 AM EDT
// 	Refactored from ImagePimp.java

import java.io.IOException;
import java.io.File;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.Dimension;
import java.awt.Image;
import java.text.ParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;


public class AINet {

    // Default values
    private static final String DEFAULT_TRAINING_FILE="test_data/ground_training.txt";
    private static final String DEFAULT_DATA_FILE="test_data/ground.txt";
    private static final int DEFAULT_POPULATION_SIZE=100;
    private static final int DEFAULT_MAX_BREEDING_ITER = 100;
    private static final int DEFAULT_DIMENSIONS = 3;

    //Logging 
    private static final Level LOG_LEVEL=Level.INFO;
    private static Logger log=null; 

    //RNG -- no need to have multiple copies of this.
    public static Random rand=new Random();

    //*FIXME*
    //These should not be statics, but a property of each instantiation, but
    //we'll deal with that later.
    
    //Dimensionality of the Antigen/Antibody vectors
    private static int Dimensions=DEFAULT_DIMENSIONS;
    //Maximum number of passes to attempt convergance 
    private static int MaxIter = DEFAULT_MAX_BREEDING_ITER;
    //Number of Antibodies in each population
    private static int BaseScale = DEFAULT_POPULATION_SIZE;
    //Control values for the breeding steps
    private static int diversityCount=BaseScale*3;
    private static int Clonal_BaseScale=BaseScale*3;


    // *FIXME* Not a global. This goes away with refactoring
    private static File input_file;

	public static void main (String[] args){
        String optarg_s="";
        File corpus_file=null;
        File output_file=null;
        String msg="";

        final String help = "Usage: \n\t-h (help)\n" +
            "\t-t FILENAME \tTraining set\n" +
            "\t-f FILENAME \tInput file\n" +
            "\t-o FILENAME \tOutput file for classified results\n" +
            "\t\tIf not specified, this goes to the console.\n" +
            "\t-s SCALE \tScale of the clonal population to generate\n" +
            "\t-i ITERATIONS \tNumber of Iterations to train Antibodies\n" +
            "\t-d DIMENSIONS \tNumber of dimensions in data set\n" +
            "\t-D          \tDEBUG\n";

                // Parse arguments... where's my beloved optarg?!
        int i=0;
        char opt=' ';

        log_init();

        while ((i < args.length) && args[i].length() > 0 &&
                (args[i].charAt(0) == '-')){
            opt = args[i++].charAt(1);
            optarg_s="";
            //Parse arguments that don't take options 
            switch(opt){
                case 'h':
                    System.out.print(help);
                    System.exit(1);
                case 'D':
                    System.out.println("Debugging enabled.");
                    log.setLevel(Level.FINE);
                    if (args[i-1].length() > 2 && 
                            args[i-1].charAt(2) == 'D'){
                        System.out.println("Extra Debugging enabled.");
                        log.setLevel(Level.FINER);
                    }
                    if (args[i-1].length() > 3 && 
                            args[i-1].charAt(3) == 'D'){
                        System.out.println("Super Extra Debugging enabled.");
                        log.setLevel(Level.FINEST);
                    }
                    continue;
            }

            //If we made it here, we need an argument
            if  ((i < args.length) && (args[i].charAt(0) != '-')){
                optarg_s=args[i++];
            } else {
                System.out.println(
                    String.format("Argument required for option %c.", opt));
                System.out.print(help);
                System.exit(1);
            }

            switch(opt){
                case 't':
                    msg=String.format("Loading training file \"%s\".", optarg_s);
                    log.info(msg);
                    corpus_file=new File(optarg_s); 
                    if (!corpus_file.canRead()){
                        System.out.printf(
                            "Can't read training file %s. Exiting.", 
                            corpus_file.getName());
                        System.exit(1);
                    }
                    break;
                case 'f':
                    msg=String.format(
                        "Loading data file \"%s\".", optarg_s);
                    log.info(msg);
                    input_file=new File(optarg_s); 
                    if (!input_file.canRead()){
                        msg=String.format(
                            "Can't read input file %s. Exiting.", 
                            input_file.getName());
                        System.out.println(msg);
                        System.exit(1);
                    }
                    break;
                case 'o':
                    msg=String.format(
                        "Creating output file \"%s\".", optarg_s);
                    log.info(msg);
                    output_file=new File(optarg_s); 
                    break;
                case 's':
                    msg=String.format(
                        "Setting scale to \"%s\".", optarg_s);
                    log.info(msg);
                    BaseScale=Integer.valueOf(optarg_s); 
                    diversityCount=BaseScale*3;
                    Clonal_BaseScale=BaseScale*3;
                    break;
                case 'i':
                    msg=String.format(
                        "Setting maximum iterations to \"%s\".", optarg_s);
                    log.info(msg);
                    MaxIter=Integer.valueOf(optarg_s); 
                    break;
                case 'd':
                    msg=String.format(
                        "Setting dimensions to \"%s\".", optarg_s);
                    log.info(msg);
                    Dimensions=Integer.valueOf(optarg_s); 
                    break;
                default:
                    System.out.print(help);
                    System.exit(1);
                    break;
            }
        }
        if (i < args.length && args[i].length() > 0){
            msg="Unused arguments: ";
            while (i < args.length){
                msg+=String.format("\"%s\",", args[i++]); 
            }
            msg=msg.substring(0,msg.length()-1) + ".";
            System.out.println(msg);
            System.out.print(help);
            System.exit(1);
        }

        System.out.printf("Logging level %s.\n", log.getLevel());

        if (corpus_file == null) {
            msg=String.format("Loading default training file \"%s\".", 
                DEFAULT_TRAINING_FILE);
            log.info(msg);
            corpus_file=new File(DEFAULT_TRAINING_FILE);
        } 
        if (input_file == null) {
            msg=String.format("Loading default data file \"%s\".", 
                DEFAULT_DATA_FILE);
            log.info(msg);
            input_file=new File(DEFAULT_DATA_FILE);
        } 


        /* 
         * Moved from setupAINet(), which should be passed the data in an array
         * format. Otherwise we have different codepaths for loading test data
         * files and loading images from the interface.
         */

        // Read the input data file
        BufferedReader reader;
        ArrayList<Antigen> inputData = new ArrayList<Antigen>();
        String line_in=null;
        try {
            reader=new BufferedReader(new FileReader(input_file));
            while((line_in=reader.readLine()) != null){
                if (line_in.equals("")){
                    log.fine("Blank line in input file skipped");
                    continue;
                }
                //Create an unclassified Antigen from the input row
                inputData.add(Antigen.valueOf(line_in,false));
            }
        }
        catch (FileNotFoundException e){
            msg=String.format("Failed to open data file %s", 
                input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        catch (IOException e){
            msg=String.format("Error %s reading file %s", 
                e, input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        catch (ParseException e){
            msg=String.format("Error %s reading file %s", 
                e, input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        
	    AINet ais = new AINet(inputData,corpus_file);  	
        log.info("Classification results: ");
        int m;
        if (output_file != null){  
            try {
                BufferedWriter writer=
                    new BufferedWriter(new FileWriter(output_file));
                for (m=0; m < ais.Whole_Ag.size(); m++){
                    writer.write(ais.Whole_Ag.get(m).toString());
                    writer.newLine();
                }
                writer.close();
            }
            catch (IOException e){
                msg=String.format("Error %s writing classification to %s.",
                    e, output_file.getName());
                System.out.println(msg);
                System.exit(1);
            }

        } else {
            for (m=0; m < ais.Whole_Ag.size(); m++){
                System.out.println(ais.Whole_Ag.get(m));
            }
        }
	}

    private static void log_init(){
        if (log!=null){
            log.fine("Logger already initialized. Skipping.");
            return;
        }

        log=Logger.getLogger("AINet");
        log.setLevel(LOG_LEVEL);
        
        //Turn off the parent console handler, which has an independent level
        //setting from this logger and can't be retrieved from child classes.
        
        log.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINEST);
        ch.setFormatter(new ReadableFormatter());
        log.addHandler(ch);


        log.fine("Initialized logging system");
        return;
    }

    private static class ReadableFormatter extends SimpleFormatter{
        public String format(LogRecord record){
            return String.format("<%s.%s>: %s\n", record.getSourceClassName(), 
                record.getSourceMethodName(), record.getMessage());
        }
    }

    private static int MaxValue=255;
    private static final int Initial_AbScale=1000;
    private static double supression_threshold = 0.09;
    private static double metadynamics_threshold = 0.7;

    /*
     * New Class vars
     */

    private List<Antigen> Whole_Ag;
    Dimension imageInDimension;
    int TRGB[][][];

    public static abstract class Cell {
        protected static final int DEFAULT_CLASS=0;
        private static int DEFAULT_VAL=MaxValue/2;
        //Regex to split input strings
        protected static String split_on= "[, \t]+";

        public double value[]=new double[Dimensions];
        public int classification;

        public Cell(double[] values, int classification){
            for(int i=0;i<Dimensions;i++){
                this.value[i]=values[i];
            }
            this.classification=classification;
        }

        public Cell(double[] values){
            this(values,DEFAULT_CLASS);
        }

        public Cell(int[] values){
            this(promote(values),DEFAULT_CLASS);
        }

        public Cell(){
            for(int i=0;i<Dimensions;i++){
                this.value[i]=DEFAULT_VAL;
            }
            this.classification=DEFAULT_CLASS;
        }

        public void randomize(){
           this.randomize(1.0); 
        }

        // Set each element of the vector to a random value between 
        // +/- mutation/2. A value of 1.0 (as is the default), should have an
        // equal chance of choosing every element between 0 and MaxValue if we
        // start at MaxValue/2
        public void randomize(double mutation){
            double delta;
            for(int i=0;i<Dimensions;i++){
                delta=(rand.nextDouble() - .5) * mutation* MaxValue ; 
                this.value[i]+=delta;
            }
        }

        public Cell findClosest(List<Cell> in_list){
            Cell max = null;
            for (Cell cur: in_list){
                if (max == null ||
                        (this.getAffinity(cur) > this.getAffinity(max))){
                    max=cur;
                }
            }
            return max;
        }
        

        public void setValue(int i, double f) {
            this.value[i]=f;
        }

        public String toString(){
            StringBuilder retString = new StringBuilder();
            for (int i=0; i < Dimensions; i++){
                retString.append(String.format("%3f ", this.value[i]));
            }
            retString.append(String.format("%d",this.classification));
            return retString.toString();
        }

        public double getAffinity(Cell neighbor){
            double EuclidianDistance = 0;
            for(int i=0;i<Dimensions;i++) {
                EuclidianDistance += 
                    Math.pow(Math.abs(this.value[i] - neighbor.value[i]),2);
            }
            return (1/Math.sqrt(EuclidianDistance));
        }

        //Promote an array of ints to an array of doubles
        private static double[] promote(int[] input){
            double[] output = new double[input.length];
            for (int i=0; i < input.length; i++){
                output[i]=input[i];
            }
            return output;
        }

    }
    
    // This is represents the vectors we will be classifying
    public static class Antigen extends Cell{

        public Antigen(){
            super();
        }
        public Antigen(double[] values, int classification){
            super(values,classification);
        }
        public Antigen(int[] values){
            super(values);
        }

        public void setValue(int i, double f) {
            this.value[i]=f;
        }

        public static Antigen valueOf(String input) throws ParseException{
            return valueOf(input,true);
        }

        public static Antigen valueOf(String input, boolean classified) 
                throws ParseException{
            String[] str_values;
            double[] num_values=new double[Dimensions];
            int classification;
            String msg;

            int expected_dim=Dimensions+1;
            if (!classified){
                expected_dim=Dimensions;
            }

            str_values=input.split(Cell.split_on); 
            //Check to see if we got a string with the right number of
            //dimensions plus a classification

            if (str_values.length != expected_dim){
                msg="Wrong number of values for ";
                if (!classified){
                    msg+="un";
                } 
                msg+="classified data string. " +
                    String.format("Expected %d, got %d in \"%s\".", 
                        expected_dim, str_values.length,input);
                throw new ParseException(msg,-1);
            }

            for(int i=0; i < Dimensions; i++){
                num_values[i]=Double.valueOf(str_values[i]);
            }
            if (classified) {
                classification=Integer.valueOf(str_values[Dimensions]);   
            } else {
                classification=DEFAULT_CLASS;
            }
            return new Antigen(num_values,classification);
        }
    }

    //These represent the vectors used to classify data
    public static class Antibody extends Cell implements Comparable<Antibody>{

        private double Affinity=0;

        //*FIXME* We probably don't need to instantiate this
        private Antigen Ag = new Antigen();

        public Antibody(){
            super();
        }

        public Antibody(Antibody clone){
            for(int i=0;i<Dimensions;i++){
                this.value[i]=clone.value[i];
            }
            this.classification=clone.classification;
            this.Affinity = clone.Affinity;
            this.Ag = clone.Ag;
        }

        public Antibody(double[] values){
            super(values);
        }

        public void setAntigen(Antigen Ag) {
            this.Ag=Ag;
        }

        public int compareTo(Antibody Ab){
            if (this.Affinity < Ab.Affinity)
                return -1;
            else if (this.Affinity > Ab.Affinity)
                return 1;
            else 
                assert this.Affinity == Ab.Affinity : 
                    "Math is broken in Antibody.compareTo()";
                return 0;
        }

        public String toString(){
            return String.format("%s %f",super.toString(), Affinity);
        }

        public static Antibody valueOf(String input) throws ParseException{
            String[] str_values;
            double[] num_values=new double[Dimensions];

            str_values=input.split(Cell.split_on); 
            //Check to see if we got a string with the right number of
            //dimensions 
            if (str_values.length != Dimensions){
                String msg= 
                    "Wrong number of dimensions for current AINet instance." +
                    String.format("Expected %d, got %d in \"%s\"", Dimensions, 
                        str_values.length,input);
                throw new ParseException(msg,-1);
            }

            for(int i=0; i < Dimensions; i++){
                num_values[i]=Double.valueOf(str_values[i]);
            }
            return new Antibody(num_values);
        }
    }

    // Find the percentage of Ag[] which is classified the same as the closest
    // element of Ab[]
    //Used to find the overall correctness
    public static double Whole_Affinity(Antibody []Ab,Antigen []Ag){
        log.warning("Whole_Affinity(Antibody[], Antigen[]) is deprecated");
        int correct=0;
        Antibody ab = null;
        for(int i=0;i<Ag.length;i++){
            //Antigen: findClosest(List<Antibody>) return Antibody
            for(int j=0;j<BaseScale;j++){
                if(j==0||(Ag[i].getAffinity(ab)<Ag[i].getAffinity(Ab[j]))){
                    ab=Ab[j];
                }
            }
            if(Ag[i].classification==ab.classification){
                correct++;
            }
        }
        return correct/(double)Ag.length;
    }

    // Find the percentage of ag_list<Antigen> which is classified the same as
    // the closest element of ab_list<Antibody>. 
    // Used to find the overall correctness
    // This replaces the old array based version
    public static double Whole_Affinity(List<Antibody> ab_list,
            List<Antigen> ag_list){
        int correct=0;
        Antibody closest_ab; 
        for(Antigen ag : ag_list){
            closest_ab = null;
            //Antigen: findClosest(List<Antibody>) return Antibody
            for(Antibody ab : ab_list){
                if(closest_ab == null ||
                        (ag.getAffinity(ab)<ag.getAffinity(closest_ab))){
                    closest_ab=ab;
                }
            }
            if(ag.classification==closest_ab.classification){
                correct++;
            }
        }
        return correct/(double)ag_list.size();
    }

    //Generate clone_population
    public void Clonal_Expansion(List<Antibody> AbBase,
            List<Antibody> clonal_population){

        int clone_count = 0, j = 0;

        //Generate clones for each antibody
        for(Antibody ab : AbBase) {
            // If Affinity is high, generate 3 clones, 
            // otherwise randomly choose 1 or 2
            if((ab.Affinity/2) > rand.nextDouble()) {
                clone_count = 3;
            } else {
                clone_count = rand.nextInt(2) + 1;
            }
            for(j = 0;j < clone_count; j++){
                clonal_population.add(new Antibody(ab));
            }
        }
    }

    //Mutate the clone population inversly proportional to affinity
    public void Affinity_Maturation(List<Antibody> clonal_population,
            List<Antigen> Training_Ag){

        for(Antibody ab: clonal_population){
            ab.randomize(ab.Affinity);
        }

        //After affinity maturation recalculate the affinity of clonal
        //population and the class it belongs to
        for(Antibody ab: clonal_population){
            //Cell: findClosest(List<Cell>) return Cell
            for(Antigen ag : Training_Ag) { 
                if(ab!=null&&ag!=null){
                    if(ab.Affinity < ag.getAffinity(ab)) {
                        ab.Affinity=ag.getAffinity(ab);
                        ab.classification=ag.classification;
                        ab.Ag=ag;
                    }
                }
            }
        }

       /* System.out.println("\nafter affinity maturation :\n" );
        for(int i = 0;i < clonal_population.size(); i++)
            System.out.println(clonal_population.get(i));
*/
       /* Antibody ab = new Antibody();
        for(int j = 0;j<clonal_population.size();j++){
            for(int k = 0;k<BaseScale;k++){
                if(clonal_population.get(j).Affinity == AbBase[k].Affinity){
                    clonal_population.remove(j);
                    if(j != 0)
                    j--;
                }
                else if(clonal_population.get(j).Affinity > AbBase[k].Affinity){
                    ab=new Antibody(AbBase[k]);
                    AbBase[k]=new Antibody(clonal_population.get(j));

                    if(Whole_Affinity(AbBase,Training_Ag)<correctness)
                             AbBase[k]=new Antibody(ab));
                }
            }
        }*/


    }

    //Remove those clones whose affinity is less than the natural threshold
    public static void Metadynamics(List<Antibody> clonal_population){

        ListIterator<Antibody> iter=clonal_population.listIterator();
        Antibody ab;
        while (iter.hasNext()){
            ab=iter.next();
            if ((1/ab.Affinity) > metadynamics_threshold){
                iter.remove();
            }
        }
    }

    //*FIXME* Why does this look identical to Network_Interaction_Supression()?
    // Clonal_Supression() Should operate only on the memory set, and
    // Network_Suppression should operate on the full Antigen set.
    //
    //Remove those clones whose affinity with each other is less than the 
    //supression threshold
    public static void Clonal_Supression(
            List<Antibody> clonal_population){
        
        String msg;
        msg=String.format("Clonal_Pop = %d.", clonal_population.size());
        log.finer(msg);

        Antibody ab1=null;
        for(ListIterator<Antibody> i = clonal_population.listIterator(); 
                i.hasNext(); ab1 = i.next()){ 
            if(ab1 == null) continue;
            for(Antibody ab2: clonal_population){
                if(ab2 == null) continue;
                //Since the iterator gives us the same ordering both times, 
                //this lets us avoid checking ab1(ab2) and ab2(ab1)
                if(ab1 == ab2) break;

                //If any element has an affinity less than the supression
                //threshold, remove it and go to the next one
                if(ab1.getAffinity(ab2) <= supression_threshold){
                    i.remove();
                    break;
                }
            }
        }

        msg=String.format("Clonal_Pop = %d.", clonal_population.size());
        log.finer(msg);
    }


    //concatenate the antibody base with the final clonal population
    //*FIXME* Just add AbBase to the final_clonal_population, we don't need a
    // new list here
    public static void Network_Reconstruction(
            List<Antibody> Reconstructed_Antibody_Pool,
            List<Antibody> final_clonal_population, List<Antibody> AbBase){

        String msg;
        msg=String.format("Ab pool size start: %d.", 
            Reconstructed_Antibody_Pool.size());
        log.finer(msg);

        Reconstructed_Antibody_Pool.addAll(AbBase);
        Reconstructed_Antibody_Pool.addAll(final_clonal_population);

        msg=String.format("Ab pool size end: %d.", 
            Reconstructed_Antibody_Pool.size());
        log.finer(msg);
    }

    //network supression
    // *FIXME* This is identical to Clonal_Supression
    public static void Network_Interaction_Supression(
            List<Antibody> Reconstructed_Antibody_Pool) {

        String msg;
        msg=String.format("Ab pool size start: %d.", 
            Reconstructed_Antibody_Pool.size());
        log.finer(msg);

        Antibody ab1=null;
        for(ListIterator<Antibody> i=
                Reconstructed_Antibody_Pool.listIterator(); i.hasNext(); 
                ab1=i.next()){
            if (ab1==null) continue;
            for(Antibody ab2 : Reconstructed_Antibody_Pool ){
                if (ab2==null) continue;
                //Since the iterator gives us the same ordering both times, 
                //this lets us avoid checking ab1(ab2) and ab2(ab1)
                if(ab1 == ab2) break;

                //If any element has an affinity less than the supression
                //threshold, remove it and go to the next one
                // *FIXME* I think this test may be backwards.
                if (ab1.getAffinity(ab2) >= supression_threshold) {
                    i.remove();
                    break;
                }
            }
        }

        msg=String.format("Ab pool size end: %d", 
            Reconstructed_Antibody_Pool.size());
        log.finer(msg);
    }


    //Introduce diversity to continue the while loop
    public void Introduce_Diversity(List<Antibody> final_Ab_Pool,
           List<Antigen> Training_Ag){

        Antibody ab;
        for(int i = 0;i<diversityCount;i++) {
            ab = new Antibody();
            ab.randomize();
            final_Ab_Pool.add(ab);
        }


        for (Antibody ab1: final_Ab_Pool) {
            if (ab1==null) continue;
            
            //*FIXME* findClosest()
            for (Antigen ag : Training_Ag){
                if (ag==null) continue;
                if(ab1.Affinity < ag.getAffinity(ab1)){
                    ab1.Affinity = ag.getAffinity(ab1);
                    ab1.classification= ag.classification;
                    ab1.Ag=ag;
                }
            }
        }
    }

    //Load the training set from a file and return it
    public static List<Antigen> get_training_set(File corpus_file){
        List<Antigen> Training_Ag = new ArrayList<Antigen>();
        String line_in=null;
        String msg;
        
        // Read the training data file

        try{
            BufferedReader reader = 
                new BufferedReader(new FileReader(corpus_file));
            while((line_in=reader.readLine()) != null){
                if (line_in.equals("")){
                    log.fine("Blank line in input file skipped");
                    continue;
                }
                Training_Ag.add(Antigen.valueOf(line_in));
            }
        }
        catch (FileNotFoundException e){
            msg=String.format("Failed to open data file %s", 
                input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        catch (IOException e){
            msg=String.format("Error %s reading file %s", 
                e, input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }
        catch (ParseException e){
            msg=String.format("Error %s reading file %s", 
                e, input_file.getName());
            System.out.println(msg);
            System.exit(1);
        }

        System.out.println("Dumping training set:");
        int i=0;
        for(Antigen ag: Training_Ag){
            i++;
            msg=String.format("[%d]: %s", i, ag);
            log.fine(msg);
        }
        return Training_Ag;
    }



    public void setupAINet(List<Antigen> Whole_Ag, List<Antigen> Training_Ag){

        String msg;

        List<Antibody> Initial_Ab=new ArrayList<Antibody>(Initial_AbScale);
        List<Antibody> AbBase=new ArrayList<Antibody>(BaseScale);
        List<Antibody> Reconstructed_Antibody_Pool = 
            new ArrayList<Antibody>(BaseScale+Clonal_BaseScale+diversityCount);
        List<Antibody> clonal_population = 
            new ArrayList<Antibody>(Clonal_BaseScale);

        //randomly generate all the antibodies

        Antibody temp_ab;
        for(int i=0;i<Initial_AbScale;i++){ 
            temp_ab=new Antibody();
            temp_ab.randomize();
            Initial_Ab.add(temp_ab);
        }

        // *FIXME* There should be a general 'Classify' method
        //For each antibody find the highest affinity with any antigen and the
        //class it belongs to
        for(Antibody ab: Initial_Ab){
            if (ab == null) continue;
            for(Antigen ag : Training_Ag){
                if (ag == null) continue;
                // findClosest()
                if(ab.Affinity<ag.getAffinity(ab)) {
                    ab.Affinity = ag.getAffinity(ab);
                    ab.classification=ag.classification;
                    ab.Ag=ag;
                }
            }
        }

        //Take the top 'AbScale' number of antibodies with highest affinity
        for(int i=0;i < BaseScale && i < Initial_Ab.size();i++) {
            AbBase.add(new Antibody(Initial_Ab.get(i)));
        }
        // *FIXME* 
        // Is this supposed to be bubble sort?
        // findClosest()
       
        Antibody highest=null;
        Antibody base_ab;
        ListIterator<Antibody> iter=AbBase.listIterator();
        while(iter.hasNext()) { 
            base_ab=iter.next();//j
            for(Antibody init_ab : Initial_Ab){ //i
                if(base_ab.Affinity<init_ab.Affinity) {
                    iter.set(new Antibody(init_ab));
                    highest = init_ab;
                }
            }
            if (highest != null) 
                highest.Affinity=0;
        }

        int iter_count = 0;
        double correctness_current_iteration = 0.0;
        // double correctness_previous_iteration = 0.0;

        while(correctness_current_iteration < 0.99 && 
                iter_count++ <= MaxIter){

            Clonal_Expansion(AbBase,clonal_population);
            log.fine("Size after clonal_expansion " +
                clonal_population.size());

            Affinity_Maturation(clonal_population,Training_Ag);
            log.fine("Size after Affinity maturation " +
                clonal_population.size());

            Metadynamics(clonal_population);
            log.fine("Size after metadynamics " +
                clonal_population.size());

            Clonal_Supression(clonal_population);
            log.fine("Size after clonal supression " +
                clonal_population.size());

            //*FIXME* This is a different data structure in the paper, because
            // they have a memory set and a breeding set of antibodies. That's
            // not the case with this implementation.
            Network_Reconstruction(Reconstructed_Antibody_Pool, 
                clonal_population, AbBase);
            log.fine("Size after Network reconstruction " +
                Reconstructed_Antibody_Pool.size());

            Network_Interaction_Supression(Reconstructed_Antibody_Pool);
            log.fine("Size after Network Supression " +
                Reconstructed_Antibody_Pool.size());

            Introduce_Diversity(Reconstructed_Antibody_Pool,
                Training_Ag);
            log.fine("Size after Diversity " +
                Reconstructed_Antibody_Pool.size());

            //Find the top 'baseScale' antibodies from the final
            //reconstructed antibody pool and repeate the loop
            //
            // *FIXME* Except this list isn't sorted, so it just selects some
            // of them at random

            int c=0;

            for(int i=0;i<AbBase.size() && 
                    i <Reconstructed_Antibody_Pool.size(); i++){
                AbBase.set(i, new Antibody(
                    Reconstructed_Antibody_Pool.get(i)));
            }
           
            // *FIXME*  
            //What does the magic number 10 do here? 
            //(Other than cause a crash when it's larger than BaseScale)
            //
            //This copies the element with the largest affinity from
            //Reconstructed_Antibody_Pool[10:-1] over each element in
            //Reconstructed_Antibody_Pool[0:10] that has a smaller
            //affinity, and sets the original Affinity to 0 

            for(int j=0;j<10 && j < AbBase.size();j++) {
                for(int i=10;i<Reconstructed_Antibody_Pool.size();
                        i++) {
                    if((AbBase.get(j).Affinity<
                        Reconstructed_Antibody_Pool.get(i).Affinity)){

                        AbBase.set(j,new Antibody( 
                            Reconstructed_Antibody_Pool.get(i)));
                        c=i;
                    }
                    Reconstructed_Antibody_Pool.get(c).Affinity = 0;
                } 
            }

            correctness_current_iteration = 
                Whole_Affinity(AbBase,Training_Ag);
            msg=String.format("Affinity after %d iterations: %f.", 
                iter_count, correctness_current_iteration);
            log.info(msg);
        }
        msg=String.format("Exited while loop (%d iterations).", iter_count);
        log.info(msg);

        for(int i=0;i<BaseScale;i++) {
            System.out.println(AbBase.get(i));
        }
            
        //  System.out.println("Whole Affinity = "
        //      +Whole_Affinity(AbBase,Whole_Ag));
        //  System.out.println("Total number of iterations = "+iter_count);
        //}//end of runainet

        /* 
         * This Classifies Whole_Ag. 
         *
         * *FIXME* 
         * The interior loop should be simplified into a single method call
         *
         */
        for(int i=0;i<Whole_Ag.size();i++) {
            temp_ab=new Antibody(AbBase.get(0));
            for(int j=1;j<AbBase.size();j++) {
                if(Whole_Ag.get(i).getAffinity(AbBase.get(j))>
                        Whole_Ag.get(i).getAffinity(temp_ab)){
                    Whole_Ag.get(i).classification=AbBase.get(j).classification;
                    temp_ab=new Antibody(AbBase.get(j));
                }
            }
        }
         
    }

    // *KILLME*
    public AINet(Image imageIn) throws Exception {
    //Pass this value downstream. We'd like to deprecate this constructor
    this(ImagePimp.pixelsArrayToTRGBArray(
            ImagePimp.imageToPixelsArray(imageIn), 
            ImagePimp.getImageDimension(imageIn)
        ));
    // this.imageInDimension=ImagePimp.getImageDimension(imageIn);
    // this.TRGB = ImagePimp.pixelsArrayToTRGBArray(ImagePimp.imageToPixelsArray(imageIn), imageInDimension);
    // This is for reading input from a text file.
    //Antigen [] Whole_Ag=new Antigen[AgScale];

    }

   
    // Take in a 3-d TRGB array and dump it into a 2-d array, 
    // passing it on the the eventual constructor 
    // This is currently stupidly inefficient, but these two legacy
    // constructors will go away as soon as we can actually test things
    // *KILLME*
    public AINet(int[][][] TRGB) throws Exception {
        this(TRGBto2dArray(TRGB), new File(DEFAULT_TRAINING_FILE));     
    }

    public static List<Antigen> TRGBto2dArray(int[][][] TRGB){
        int row_len=TRGB[0][0].length;
        int col_len=TRGB[0].length;
        List<Antigen> inputData = new ArrayList<Antigen>(Dimensions); 
        String msg=String.format("Rows: %d, Cols: %s", row_len, col_len);
        log.fine(msg);
        //There are 4 bands, but we don't care about the transparency
        int bands=3;
        int[] antigen_array= new int[bands];
        //Following loop from getResults() -- rowmajor or column major
        //flattening shouldn't matter as long as it's consistent
        for (int row=0; row < row_len; row++){
            for (int col=0; col < col_len; col++){
                for (int k=0; k < bands; k++){
                    antigen_array[k]=TRGB[k+1][col][row];
                }
                inputData.add(new Antigen(antigen_array));
            }
        }
        return inputData;
    }

    public AINet(List<Antigen> inputData, File corpus_file){
        String msg;
        //*KILLME* This makes sure that TRGB is initialized for getResults()
        //when this is the only constructor called. Since getResults should be
        //returning a flattened array anyway, the original dimensions are
        //irrelevant
        this.TRGB=new int[4][inputData.size()][1];

        log_init();

        Whole_Ag=inputData;

        List<Antigen> Training_Ag=get_training_set(corpus_file);
        //*FIXME* We should be checking this when we use it, not relying on a
        //global
        System.out.println("Antibodies:");
        for(Antigen ag: Training_Ag){
            msg=String.format("%s", ag);
            log.info(msg);
        }
        setupAINet(Whole_Ag,Training_Ag);
    }

    public int[] getResults(){
        String msg;
        int i=0;
        msg=String.format("Generating TRGB array of size %d,%d,%d\n",
            TRGB.length, TRGB[0].length,TRGB[0][0].length);
        log.info(msg);
        for(int row=0;row<TRGB[0][0].length;row++)
        for (int column = 0; column <TRGB[0].length; column++)
        {
            if(Whole_Ag.get(i) != null) {
                TRGB[0][column][row]=255;
                if(Whole_Ag.get(i).classification==1)
                {
                    TRGB[1][column][row]=255;//55;
                    TRGB[2][column][row]=222;//255;//128;
                    TRGB[3][column][row]=10;//78;
                }
                else if(Whole_Ag.get(i).classification==2)
                {
                    TRGB[1][column][row]=0;
                    TRGB[2][column][row]=0;//64;//75;
                    TRGB[3][column][row]=215;//78;
                }
                else if(Whole_Ag.get(i).classification==3)
                {
                    TRGB[1][column][row]=0;
                    TRGB[2][column][row]=215;
                    TRGB[3][column][row]=0;
                }
                else if(Whole_Ag.get(i).classification==4)
                {
                    TRGB[1][column][row]=215;
                    TRGB[2][column][row]=0;
                    TRGB[3][column][row]=0;
                }
                else if(Whole_Ag.get(i).classification==5)
                {
                    TRGB[1][column][row]=0;
                    TRGB[2][column][row]=10;
                    TRGB[3][column][row]=0;
                }
            } 
            i++;

        }

    /*for(int row=0;row<imageInDimension.getHeight();row++)
        for (int column = 0; column < imageInDimension.getWidth(); column++)
        {
            System.out.println(TRGB[1][column][row]+"  "+TRGB[2][column][row]+"  "+TRGB[3][column][row]+"  3");

        }
    System.out.println(imageInDimension.getHeight()*imageInDimension.getWidth());*/
    //return ImagePimp.pixelsArrayToImage(ImagePimp.TRGBArrayToPixelsArray(TRGB, imageInDimension), imageInDimension);
    return ImagePimp.TRGBArrayToPixelsArray(TRGB, 
        new Dimension(TRGB[0][0].length, TRGB[0].length));
}

}


